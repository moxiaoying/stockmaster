package com.stock.master.api.request;

public class GetOrdersDataRequest extends BaseTradeRequest {

    public GetOrdersDataRequest(int userId) {
        super(userId);
    }

    @Override
    public String getMethod() {
        return TradeRequestMethod.GetOrdersDataRequest.value();
    }

    @Override
    public String toString() {
        return "GetOrdersDataRequest [" + super.toString() + "]";
    }

}
