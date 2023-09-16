package com.moutamid.spintowin;

public class WithdrawDataModel {
    private int WithdrawalAmount;
    private String MobileNumber;
    private String UserName;

    public WithdrawDataModel() {
        // Default constructor required for Firebase
    }

    public WithdrawDataModel(String username, String numberwithdraw, Integer withdrawamnt) {
        UserName = username;
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

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
