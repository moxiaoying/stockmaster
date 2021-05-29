package com.stock.master.trategy.model;


import com.stock.master.api.request.RevokeRequest;

import java.util.List;

public class VolumeStrategyResult {

    private List<RevokeRequest> revokeList;
    private List<VolumeSubmitResult> submitList;

    public List<RevokeRequest> getRevokeList() {
        return revokeList;
    }

    public void setRevokeList(List<RevokeRequest> revokeList) {
        this.revokeList = revokeList;
    }

    public List<VolumeSubmitResult> getSubmitList() {
        return submitList;
    }

    public void setSubmitList(List<VolumeSubmitResult> submitList) {
        this.submitList = submitList;
    }

}
