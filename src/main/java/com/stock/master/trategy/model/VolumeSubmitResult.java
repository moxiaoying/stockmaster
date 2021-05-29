package com.stock.master.trategy.model;


import com.stock.master.api.request.SubmitRequest;

public class VolumeSubmitResult extends SubmitRequest {

    private String tradeCode;

    public VolumeSubmitResult(int userId) {
        super(userId);
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

}
