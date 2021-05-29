package com.stock.master.api.request;

public class GetStockListRequest extends BaseTradeRequest {

    public GetStockListRequest(int userId) {
        super(userId);
    }

    @Override
    public String getMethod() {
        return TradeRequestMethod.GetStockList.value();
    }

    @Override
    public String toString() {
        return "GetStockListRequest [" + super.toString() + "]";
    }

}
