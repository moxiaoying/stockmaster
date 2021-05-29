package com.stock.master.api.request;

public class RevokeRequest extends BaseTradeRequest {

    private String revokes;

    public RevokeRequest(int userId) {
        super(userId);
    }

    public String getRevokes() {
        return revokes;
    }

    public void setRevokes(String revokes) {
        this.revokes = revokes;
    }

    @Override
    public String getMethod() {
        return TradeRequestMethod.RevokeRequest.value();
    }

    @Override
    public String toString() {
        return "RevokeRequest [revokes=" + revokes + ", " + super.toString() + "]";
    }

}
