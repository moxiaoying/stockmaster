package com.stock.master.service;

import com.stock.master.api.TradeResultVo;
import com.stock.master.api.request.*;
import com.stock.master.api.response.*;

public interface TradeApiService {

    /**
     * 我的资产
     */
    TradeResultVo<GetAssetsResponse> getAsserts(GetAssetsRequest request);

    /**
     * 挂单
     */
    TradeResultVo<SubmitResponse> submit(SubmitRequest request);

    /**
     * 撤单
     */
    TradeResultVo<RevokeResponse> revoke(RevokeRequest request);

    /**
     * 我的持仓
     */
    TradeResultVo<GetStockListResponse> getStockList(GetStockListRequest request);

    /**
     * 当日委托
     */
    TradeResultVo<GetOrdersDataResponse> getOrdersData(GetOrdersDataRequest request);

    /**
     * 当日成交
     */
    TradeResultVo<GetDealDataResponse> getDealData(GetDealDataRequest request);

    /**
     * 登录
     */
    TradeResultVo<AuthenticationResponse> authentication(AuthenticationRequest request);

    /**
     * 历史成交
     */
    TradeResultVo<GetHisDealDataResponse> getHisDealData(GetHisDealDataRequest request);

    /**
     * 历史委托
     */
    TradeResultVo<GetHisOrdersDataResponse> getHisOrdersData(GetHisOrdersDataRequest request);

}
