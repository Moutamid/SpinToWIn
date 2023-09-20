package com.moutamid.spintowin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import android.util.Base64;

public class WithdrawActivity extends AppCompatActivity {
    String numberwithdraw, merchantAPI, username;
    EditText numberWithdraw, withdrawAmnt, userName;
    Integer currentAvail, maxAvail, exchangeRate, withdrawLimit, withdrawamnt, remainingAmnt;
    CardView razorPay, manualPayment;
    boolean manualvisible, rpayOn, mpayOn;
    ImageView rpaytick, mpaytick;
    TextView myTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        myTotal = findViewById(R.id.myTotal);
        numberWithdraw = findViewById(R.id.mobileNumber);
        withdrawAmnt = findViewById(R.id.withdrawAmnt);
        razorPay = findViewById(R.id.razorPay);
        manualPayment = findViewById(R.id.manualPayment);
        userName = findViewById(R.id.userName);
        rpaytick = findViewById(R.id.rpaytick);
        mpaytick = findViewById(R.id.mpaytick);

        currentAvail = getIntent().getIntExtra("CurrentAvail", 0);
        maxAvail = getIntent().getIntExtra("MaxAvail", 0);
        exchangeRate = getIntent().getIntExtra("ExchangeRate", 0);
        withdrawLimit = getIntent().getIntExtra("WithdrawLimit", 0);
        manualvisible = getIntent().getBooleanExtra("manualVisible", false);
        merchantAPI = getIntent().getStringExtra("MerchantAPI");

        withdrawAmnt.setText(String.valueOf(maxAvail));
        withdrawAmnt.setEnabled(false);

        if (manualvisible) {
            manualPayment.setVisibility(View.VISIBLE);
        } else {
            manualPayment.setVisibility(View.INVISIBLE);
        }

        myTotal.setText(String.valueOf(currentAvail));
    }

    public void withdrawSubmitBtnClick(View view) {
        numberwithdraw = String.valueOf(numberWithdraw.getText());
        withdrawamnt = maxAvail;

        if (numberwithdraw.isEmpty()) {
            Toast.makeText(this, "Please enter a number to withdraw", Toast.LENGTH_SHORT).show();
            return;
        }

        if (canWithdraw()) {
            if (mpayOn) {

                WithdrawDataModel withdrawalRequest = new WithdrawDataModel(username, numberwithdraw, withdrawamnt);
                DatabaseReference reference = Constants.databaseReference().child("WithdrawRequests");
                String requestId = reference.push().getKey();
                reference.child(requestId).setValue(withdrawalRequest);


                Toast.makeText(this, "Withdrawal request submitted!", Toast.LENGTH_SHORT).show();
                currentAvail -= withdrawamnt;
                remainingAmnt = maxAvail - currentAvail;
                myTotal.setText(String.valueOf(currentAvail));

                recordWithdrawal();

                updateDataInFirebase(currentAvail);
                numberWithdraw.setText("");
                mpaytick.setVisibility(View.GONE);
            } else if (rpayOn) {
                byte[] byteData = Base64.decode(merchantAPI, Base64.DEFAULT);
                String decodedAPI = new String(byteData);

                Toast.makeText(this, "Merchant API : " + decodedAPI, Toast.LENGTH_LONG).show();

                currentAvail -= withdrawamnt;
                remainingAmnt = maxAvail - currentAvail;
                myTotal.setText(String.valueOf(currentAvail));

                recordWithdrawal();

                numberWithdraw.setText("");
                rpaytick.setVisibility(View.GONE);
                updateDataInFirebase(currentAvail);
            } else {
                Toast.makeText(this, "Select Payment Method", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You have reached the daily withdrawal limit", Toast.LENGTH_SHORT).show();
            numberWithdraw.setText("");

        }
    }

    private void updateDataInFirebase(Integer currentAvail) {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DatabaseReference userDataRef = Constants.databaseReference().child("userData");
        DatabaseReference userRef = userDataRef.child(androidId);
        userRef.child("currentAvail").setValue(currentAvail);
    }


    public void recordWithdrawal() {
        SharedPreferences preferences = getSharedPreferences("WithdrawalPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int withdrawalsMade = preferences.getInt("withdrawalsMade", 0);
        editor.putInt("withdrawalsMade", withdrawalsMade + 1);

        editor.putLong("lastWithdrawalTimestamp", System.currentTimeMillis());
        editor.apply();
    }


    public boolean canWithdraw() {
        SharedPreferences preferences = getSharedPreferences("WithdrawalPrefs", MODE_PRIVATE);
        long lastWithdrawalTimestamp = preferences.getLong("lastWithdrawalTimestamp", 0);
        long currentTime = System.currentTimeMillis();

        boolean timeLimitPassed = currentTime - lastWithdrawalTimestamp >= (24 * 60 * 60 * 1000);

        int withdrawalsMade = preferences.getInt("withdrawalsMade", 0);
        boolean withinWithdrawLimit = withdrawalsMade < withdrawLimit;

        if (timeLimitPassed) {
            // Reset withdrawalsMade to 0 when the time limit has passed
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("withdrawalsMade", 0);
            editor.apply();
        }

        return timeLimitPassed && withinWithdrawLimit;
    }


    public void rpayClicked(View view) {
        if (!rpayOn) {
            rpaytick.setVisibility(View.VISIBLE);
            rpayOn = true;
        } else {
            rpaytick.setVisibility(View.GONE);
            rpayOn = false;
        }
    }

    public void mpayClicked() {
        if (!mpayOn) {
            mpaytick.setVisibility(View.VISIBLE);
            mpayOn = true;
        } else {
            mpaytick.setVisibility(View.GONE);
            mpayOn = false;
        }
    }
}