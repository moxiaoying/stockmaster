package com.stock.master.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.stock.master.api.TradeResultVo;
import com.stock.master.api.request.AuthenticationRequest;
import com.stock.master.api.request.BaseTradeRequest;
import com.stock.master.api.response.AuthenticationResponse;
import com.stock.master.client.TradeClient;
import com.stock.master.model.po.TradeMethod;
import com.stock.master.model.po.TradeUser;
import com.stock.master.service.AbstractTradeApiService;
import com.stock.master.service.TradeService;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class TradeApiServiceImpl extends AbstractTradeApiService {

    private final Logger logger = LoggerFactory.getLogger(TradeApiServiceImpl.class);

    private static final List<String> IgnoreList = Arrays.asList("class", "userId", "method");

    private final ResponseParser revokeResponseParser = new ResponseParser() {
        @Override
        public <T> TradeResultVo<T> parse(String content, TypeReference<T> responseType) {
            TradeResultVo<T> resultVo = new TradeResultVo<>();
            resultVo.setData(Collections.emptyList());
            resultVo.setStatus(TradeResultVo.STATUS_SUCCESS);
            resultVo.setMessage(content);
            return resultVo;
        }
    };

    private final ResponseParser defaultReponseParser = new ResponseParser() {
        @Override
        public <T> TradeResultVo<T> parse(String content, TypeReference<T> responseType) {
            TradeResultVo<T> resultVo = JSON.parseObject(content, new TypeReference<TradeResultVo<T>>() {});
            if (resultVo.isSuccess()) {
                List<T> list = resultVo.getData();
                ArrayList<T> newList = new ArrayList<>();
                if (list != null) {
                    list.forEach(d -> {
                        String text = JSON.toJSONString(d);
                        T t = JSON.parseObject(text, responseType);
                        newList.add(t);
                    });
                }
                resultVo.setData(newList);
            } else {
                resultVo.setData(Collections.emptyList());
            }
            return resultVo;
        }
    };

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeClient tradeClient;

    @Override
    public <T> TradeResultVo<T> send(BaseTradeRequest request, TypeReference<T> responseType) {
        String url = getUrl(request);
        Map<String, Object>  params = getParams(request);
        Map<String, String> header = getHeader(request);

        logger.debug("trade {} request: {}", request.getMethod(), params);
        String content = tradeClient.send(url, params, header);
        logger.debug("trade {} response: {}", request.getMethod(), content);

        ResponseParser responseParse = getResponseParser(request);
        return responseParse.parse(content, responseType);
    }

    @Override
    public TradeResultVo<AuthenticationResponse> authentication(AuthenticationRequest request) {
        TradeMethod tradeMethod = tradeService.getTradeMethodByName(request.getMethod());
        TradeUser tradeUser = tradeService.getTradeById(request.getUserId());

        Map<String, Object> params = getParams(request);
        params.put("userId", tradeUser.getAccountId());
        try {
            tradeClient.openSession();
            String content = tradeClient.sendNewInstance(tradeMethod.getUrl(), params);
            ResponseParser responseParse = defaultReponseParser;
            TradeResultVo<AuthenticationResponse> resultVo = responseParse.parse(content, new TypeReference<AuthenticationResponse>() {});
            if (resultVo.isSuccess()) {
                TradeMethod authCheckTradeMethod = tradeService.getTradeMethodByName(BaseTradeRequest.TradeRequestMethod.AuthenticationCheckRequest.value());

                String content2 = tradeClient.sendNewInstance(authCheckTradeMethod.getUrl(), new HashMap<>());
                String validateKey = getValidateKey(content2);

                AuthenticationResponse response = new AuthenticationResponse();
                response.setCookie(tradeClient.getCurrentCookie());
                response.setValidateKey(validateKey);
                resultVo.setData(Arrays.asList(response));
            }
            return resultVo;
        } finally {
            tradeClient.destoryCurrentSession();
        }
    }

    private String getValidateKey(String content) {
        String key = "input id=\"em_validatekey\" type=\"hidden\" value=\"";
        int inputBegin = content.indexOf(key) + key.length();
        int inputEnd = content.indexOf("\" />", inputBegin);
        String validateKey = content.substring(inputBegin, inputEnd);
        return validateKey;
    }

    private ResponseParser getResponseParser(BaseTradeRequest request) {
        if (BaseTradeRequest.TradeRequestMethod.RevokeRequest.value().equals(request.getMethod())) {
            return revokeResponseParser;
        }
        return defaultReponseParser ;
    }

    private Map<String, Object> getParams(BaseTradeRequest request) {
        Map<Object, Object> beanMap = new BeanMap(request);
        HashMap<String, Object> params = new HashMap<>();
        beanMap.entrySet().stream().filter(entry -> !TradeApiServiceImpl.IgnoreList.contains(entry.getKey()))
                .forEach(entry -> params.put(String.valueOf(entry.getKey()), entry.getValue()));
        return params;
    }

    private String getUrl(BaseTradeRequest request) {
        TradeMethod tradeMethod = tradeService.getTradeMethodByName(request.getMethod());
        TradeUser tradeUser = tradeService.getTradeById(request.getUserId());
        String url = tradeMethod.getUrl();
        return url.replace("${validatekey}", tradeUser.getValidateKey()) ;
    }

    private Map<String, String> getHeader(BaseTradeRequest request) {
        TradeUser tradeUser = tradeService.getTradeById(request.getUserId());

        HashMap<String, String> header = new HashMap<>();
        header.put("cookie", tradeUser.getCookie());
        return header;
    }

    private static interface ResponseParser {
        <T> TradeResultVo<T> parse(String content, TypeReference<T> responseType);
    }

}
