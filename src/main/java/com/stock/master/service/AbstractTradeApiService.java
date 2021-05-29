package com.stock.master.service;

import com.alibaba.fastjson.TypeReference;
import com.stock.master.api.TradeResultVo;
import com.stock.master.api.request.*;
import com.stock.master.api.response.*;

public abstract class AbstractTradeApiService implements TradeApiService {

    @Override
    public TradeResultVo<GetAssetsResponse> getAsserts(GetAssetsRequest request) {
        return send(request, new TypeReference<GetAssetsResponse>() {});
    }

    @Override
    public TradeResultVo<SubmitResponse> submit(SubmitRequest request) {
        return send(request, new TypeReference<SubmitResponse>() {});
    }

    @Override
    public TradeResultVo<RevokeResponse> revoke(RevokeRequest request) {
        return send(request, new TypeReference<RevokeResponse>() {});
    }

    @Override
    public TradeResultVo<GetStockListResponse> getStockList(GetStockListRequest request) {
        return send(request, new TypeReference<GetStockListResponse>() {});
    }

    @Override
    public TradeResultVo<GetOrdersDataResponse> getOrdersData(GetOrdersDataRequest request) {
        return send(request, new TypeReference<GetOrdersDataResponse>() {});
    }

    @Override
    public TradeResultVo<GetDealDataResponse> getDealData(GetDealDataRequest request) {
        return send(request, new TypeReference<GetDealDataResponse>() {});
    }

    @Override
    public TradeResultVo<GetHisDealDataResponse> getHisDealData(GetHisDealDataRequest request) {
        return send(request, new TypeReference<GetHisDealDataResponse>() {});
    }

    @Override
    public TradeResultVo<GetHisOrdersDataResponse> getHisOrdersData(GetHisOrdersDataRequest request) {
        return send(request, new TypeReference<GetHisOrdersDataResponse>() {});
    }

    public abstract <T> TradeResultVo<T> send(BaseTradeRequest request, TypeReference<T> responseType);

}
