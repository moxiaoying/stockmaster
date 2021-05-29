package com.stock.master.api.request;

public class GetAssetsRequest extends BaseTradeRequest {

    public GetAssetsRequest(int userId) {
        super(userId);
    }

    @Override
    public String getMethod() {
        return TradeRequestMethod.GetAssertsRequest.value();
    }

    @Override
    public String toString() {
        return "GetAssetsRequest [" + super.toString() + "]";
    }

}
