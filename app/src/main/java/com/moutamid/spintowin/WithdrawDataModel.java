package com.moutamid.spintowin;

public class WithdrawDataModel {
    private int WithdrawalAmount;
    private String MobileNumber;

    public WithdrawDataModel() {
        // Default constructor required for Firebase
    }

    public WithdrawDataModel(String numberwithdraw, Integer withdrawamnt) {
        MobileNumber = numberwithdraw;
        WithdrawalAmount = withdrawamnt;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public int getWithdrawalAmount() {
        return WithdrawalAmount;
    }

    public void setWithdrawalAmount(int withdrawalAmount) {
        WithdrawalAmount = withdrawalAmount;
    }
}
