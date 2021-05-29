package com.stock.master.trategy.handle;

import com.stock.master.api.TradeResultVo;
import com.stock.master.api.request.GetDealDataRequest;
import com.stock.master.api.request.RevokeRequest;
import com.stock.master.api.request.SubmitRequest;
import com.stock.master.api.response.GetDealDataResponse;
import com.stock.master.api.response.RevokeResponse;
import com.stock.master.api.response.SubmitResponse;
import com.stock.master.exception.ServiceException;
import com.stock.master.model.po.TradeOrder;
import com.stock.master.model.po.TradeRule;
import com.stock.master.service.MessageService;
import com.stock.master.service.StockService;
import com.stock.master.service.TradeApiService;
import com.stock.master.service.TradeService;
import com.stock.master.trategy.model.VolumeStrategyInput;
import com.stock.master.trategy.model.VolumeStrategyResult;
import com.stock.master.trategy.model.VolumeSubmitResult;
import com.stock.master.utils.StockConsts.StockType;
import com.stock.master.utils.StockUtil;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component("volumeStrategyHandler")
public class VolumeStrategyHandler extends BaseStrategyHandler<VolumeStrategyInput, VolumeStrategyResult> {

    private final Logger logger = LoggerFactory.getLogger(VolumeStrategyHandler.class);

    @Autowired
    private MessageService messageServicve;

    @Autowired
    private TradeApiService tradeApiService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private StockService stockService;

    @Override
    public VolumeStrategyInput queryInput() {
        final int UserId = 1;
        TradeResultVo<GetDealDataResponse> dealData = tradeApiService.getDealData(new GetDealDataRequest(UserId));
        if (!dealData.isSuccess()) {
            throw new ServiceException("execute VolumeStrategyService error: " + dealData.getMessage());
        }
        List<GetDealDataResponse> dealDataList = mergeDealList(dealData.getData());
        List<TradeOrder> tradeOrderList = tradeService.getTodayTradeOrderList();

        VolumeStrategyInput input = new VolumeStrategyInput(UserId);
        input.setDealDataList(dealDataList);
        input.setTradeOrderList(tradeOrderList);

        return input;
    }

    @Override
    public VolumeStrategyResult handle(VolumeStrategyInput input) {
        List<GetDealDataResponse> dealDataList = input.getDealDataList();
        List<TradeOrder> tradeOrderList = input.getTradeOrderList();
        int userId = input.getUserId();

        ArrayList<RevokeRequest> revokeList = new ArrayList<>();
        ArrayList<VolumeSubmitResult> submitList = new ArrayList<>();

        for (GetDealDataResponse getDealDataResponse : dealDataList) {
            String stockCode = getDealDataResponse.getZqdm();
            TradeRule tradeRule = tradeService.getTradeRuleByStockCode(stockCode);
            if (tradeRule != null) {
                int amount = Integer.parseInt(getDealDataResponse.getCjsl());
                double price = Double.parseDouble(getDealDataResponse.getCjjg());
                String cjbh = getDealDataResponse.getCjbh();
                String wtbh = getDealDataResponse.getWtbh();
                double rate = tradeRule.getRate().doubleValue();
                boolean isHandle = false;
                if (SubmitRequest.B.equals(getDealDataResponse.getMmlb())) {
                    double tradePrice = (int) (price * (1 + rate) * getPrecision(stockCode)) / getPrecision(stockCode);
                    isHandle = setNeedSubmit(tradeOrderList, SubmitRequest.S, stockCode, amount, tradePrice, cjbh, submitList, userId);
                }
                double tradePrice = (int) (price * (1 - rate) * getPrecision(stockCode)) / getPrecision(stockCode);
                boolean submitResult = setNeedSubmit(tradeOrderList, SubmitRequest.B, stockCode, amount, tradePrice, cjbh, submitList, userId);
                if (submitResult) {
                    isHandle = true;
                }
                if (isHandle) {
                    setNeedRevoke(tradeOrderList, wtbh, revokeList, userId);
                }
            }
        }

        VolumeStrategyResult result = new VolumeStrategyResult();
        result.setRevokeList(revokeList);
        result.setSubmitList(submitList);
        return result;
    }

    @Override
    public void handleResult(VolumeStrategyInput input, VolumeStrategyResult result) {
        List<RevokeRequest> revokeList = result.getRevokeList();
        List<VolumeSubmitResult> submitList = result.getSubmitList();

        revokeList.forEach(request -> {
            logger.info("revoek request: {}", request);
            TradeResultVo<RevokeResponse> resultVo = tradeApiService.revoke(request);
            logger.info("revoek response: {}", resultVo);
            if (!resultVo.isSuccess()) {
                logger.error(resultVo.getMessage());
                messageServicve.send(String.format("revoke error. request: %s, response: %s", request, resultVo.getMessage()));
            }
        });

        submitList.forEach(request -> {
            TradeResultVo<SubmitResponse> saleResultVo = trade(request);
            if (saleResultVo.isSuccess()) {
                TradeOrder tradeOrder = new TradeOrder();
                tradeOrder.setTradeCode(request.getTradeCode());
                tradeOrder.setStockCode(request.getStockCode());
                tradeOrder.setPrice(BigDecimal.valueOf(request.getPrice()));
                tradeOrder.setVolume(request.getAmount());
                tradeOrder.setTradeType(request.getTradeType());
                tradeOrder.setEntrustCode(saleResultVo.getData().get(0).getWtbh());
                tradeService.saveTradeOrder(tradeOrder);
            }
        });

    }

    private void setNeedRevoke(List<TradeOrder> tradeOrderList, String wtbh, List<RevokeRequest> revokeList, int userId) {
        String needRevokeWtbh = getNeedRevokeWtbh(tradeOrderList, wtbh);
        if (!StringUtils.isEmpty(needRevokeWtbh)) {
            String revokes = String.format("%s_%s", DateUtils.formatDate(new Date(), "yyyyMMdd"), needRevokeWtbh);
            RevokeRequest request = new RevokeRequest(userId);
            request.setRevokes(revokes);
            revokeList.add(request);
        }
    }

    private boolean setNeedSubmit(List<TradeOrder> tradeOrderList, String tradeType, String stockCode,
            int amount, double tradePrice, String cjbh, List<VolumeSubmitResult> submitList, int userId) {
        TradeOrder tradeOrderInDb = getByCondition(tradeOrderList, v -> cjbh.equals(v.getTradeCode())
                && tradeType.equals(v.getTradeType()));
        if (tradeOrderInDb == null) {
            VolumeSubmitResult request = new VolumeSubmitResult(userId);
            request.setAmount(amount);
            request.setPrice(tradePrice);
            request.setTradeType(tradeType);
            request.setStockCode(stockCode);
            request.setTradeCode(cjbh);
            submitList.add(request);
            return true;
        }
        return false;
    }

    private String getNeedRevokeWtbh(List<TradeOrder> tradeOrderList, String wtbh) {
        TradeOrder tradeOrder = getByCondition(tradeOrderList, v -> wtbh.equals(v.getEntrustCode()));
        if (tradeOrder == null) {
            return null;
        }

        TradeOrder relatedTradeOrder = getByCondition(tradeOrderList,
                v -> tradeOrder.getTradeCode().equals(v.getTradeCode())
                        && !tradeOrder.getTradeType().equals(v.getTradeType()));
        return relatedTradeOrder != null ? relatedTradeOrder.getEntrustCode() : null;
    }

    private TradeResultVo<SubmitResponse> trade(SubmitRequest request) {
        logger.info("submit request: {}", request);
        TradeResultVo<SubmitResponse> tradeResultVo = tradeApiService.submit(request);
        logger.info("submit response: {}", tradeResultVo);
        String name = stockService.getStockByFullCode(StockUtil.getFullCode(request.getStockCode())).getName();
        if (!tradeResultVo.isSuccess()) {
            logger.error(tradeResultVo.getMessage());
        }
        String body = String.format("%s %s %d %.02f %s", request.getTradeType(), name, request.getAmount(), request.getPrice(), tradeResultVo.getMessage() == null ? "" : tradeResultVo.getMessage());
        try {
            messageServicve.send(body);
        } catch (Exception e) {
            logger.error("send message error", e);
        }
        return tradeResultVo;
    }

    private TradeOrder getByCondition(List<TradeOrder> tradeOrderList, Predicate<TradeOrder> predicate) {
        Optional<TradeOrder> optional = tradeOrderList.stream().filter(predicate).findAny();
        return optional.orElse(null);
    }

    /**
     * merge the partial-deal list
     */
    private List<GetDealDataResponse> mergeDealList(List<GetDealDataResponse> data) {
        LinkedHashMap<String, GetDealDataResponse> map = new LinkedHashMap<>();
        for (GetDealDataResponse getDealDataResponse : data) {
            String wtbh = getDealDataResponse.getWtbh();
            GetDealDataResponse response = map.get(wtbh);
            if (response == null) {
                response = mergeDeal(null, getDealDataResponse);
                map.put(wtbh, response);
            } else {
                mergeDeal(response, getDealDataResponse);
            }
        }
        return map.values().stream().filter(v -> v.getCjsl().equals(v.getWtsl())).collect(Collectors.toList());
    }

    private GetDealDataResponse mergeDeal(GetDealDataResponse response, GetDealDataResponse getDealDataResponse) {
        if (response == null) {
            response = new GetDealDataResponse();
            response.setCjbh(getDealDataResponse.getCjbh());
            response.setCjjg(getDealDataResponse.getCjjg());
            response.setCjsj(getDealDataResponse.getCjsj());
            response.setCjsl(getDealDataResponse.getCjsl());
            response.setMmlb(getDealDataResponse.getMmlb());
            response.setWtbh(getDealDataResponse.getWtbh());
            response.setWtsl(getDealDataResponse.getWtsl());
            response.setZqdm(getDealDataResponse.getZqdm());
        } else {
            int cjsl = Integer.parseInt(response.getCjsl());
            int cjsl2 = Integer.parseInt(getDealDataResponse.getCjsl());
            response.setCjsl(String.valueOf(cjsl + cjsl2));
        }
        return response;
    }

    private double getPrecision(String code) {
        int type = StockUtil.getStockType(null, code);
        if (type == StockType.ETF.value()) {
            return 1000;
        }
        return 100;
    }

}
