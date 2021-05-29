package com.stock.master.trategy.model;


import com.stock.master.api.response.GetDealDataResponse;
import com.stock.master.model.po.TradeOrder;

import java.util.List;

public class VolumeStrategyInput extends BaseStrategyInput {

    public VolumeStrategyInput(int userId) {
        super(userId);
    }

    private List<GetDealDataResponse> dealDataList;

    private List<TradeOrder> tradeOrderList;

    public List<GetDealDataResponse> getDealDataList() {
        return dealDataList;
    }

    public void setDealDataList(List<GetDealDataResponse> dealDataList) {
        this.dealDataList = dealDataList;
    }

    public List<TradeOrder> getTradeOrderList() {
        return tradeOrderList;
    }

    public void setTradeOrderList(List<TradeOrder> tradeOrderList) {
        this.tradeOrderList = tradeOrderList;
    }



}
