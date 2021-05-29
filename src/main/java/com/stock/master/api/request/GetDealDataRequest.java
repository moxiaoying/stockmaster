package com.stock.master.api.request;

public class GetDealDataRequest extends BaseTradeRequest {

    public GetDealDataRequest(int userId) {
        super(userId);
    }

    @Override
    public String getMethod() {
        return TradeRequestMethod.GetDealDataRequest.value();
    }

    @Override
    public String toString() {
        return "GetDealDataRequest [" + super.toString() + "]";
    }

}
