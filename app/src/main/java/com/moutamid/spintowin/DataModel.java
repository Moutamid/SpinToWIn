package com.moutamid.spintowin;

public class DataModel {
    private String RequestId;
    private int CurrentAvail;
    private int ExchangeRate;
    private int MaxAvail;
    private int withdrawLimit;
    private boolean manualVisible;
    private String MerchantAPI;

    public DataModel() {

    }

    public int getCurrentAvail() {
        return CurrentAvail;
    }

    public void setCurrentAvail(int currentAvail) {
        this.CurrentAvail = currentAvail;
    }

    public int getExchangeRate() {
        return ExchangeRate;
    }

    public void setExchangeRate(int exchangeRate) {
        this.ExchangeRate = exchangeRate;
    }

    public int getMaxAvail() {
        return MaxAvail;
    }

    public void setMaxAvail(int maxAvail) {
        this.MaxAvail = maxAvail;
    }

    public int getWithdrawLimit() {
        return withdrawLimit;
    }

    public void setWithdrawLimit(int withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public boolean isManualVisible() {
        return manualVisible;
    }

    public void setManualVisible(boolean manualVisible) {
        this.manualVisible = manualVisible;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getMerchantAPI() {
        return MerchantAPI;
    }

    public void setMerchantAPI(String merchantAPI) {
        MerchantAPI = merchantAPI;
    }
}

