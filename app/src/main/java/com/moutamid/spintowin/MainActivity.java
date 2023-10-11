package com.moutamid.spintowin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.razorpay.Checkout;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Transfer;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView currentBal, remainingBal;
    public Integer currentAvail, exchangeRate, maxAvail, withdrawLimit, remainingAmnt;
    String points, merchantAPI, androidId;
    boolean manualVisible;
    LuckyWheelView luckyWheelView;
    DatabaseReference userDataRef = Constants.databaseReference().child("userData");
    List<LuckyItem> data = new ArrayList<>();
    Button withdraw_btn;
    RazorpayClient razorpay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);

        currentBal = findViewById(R.id.currentAmnt);
        remainingBal = findViewById(R.id.amntLeft);
        withdraw_btn = findViewById(R.id.withdraw_btn);
//        try {
//            razorpay = new RazorpayClient("rzp_test_D0iZEk51VDPgK5", "FnQgV1J62ckAmaHl5T3LqcHT");
//        } catch (RazorpayException e) {
//        }

        Checkout.preload(getApplicationContext());


        withdraw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MyAsyncTask myAsyncTasks = new MyAsyncTask();
//                myAsyncTasks.execute();
                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_D0iZEk51VDPgK5"); // Replace with your Razorpay API Key


                JSONObject options = new JSONObject();
                try {
                    options.put("name", "Your App Name");
                    options.put("description", "Payment request for XYZ");
                    options.put("currency", "INR"); // Replace with the desired currency
                    options.put("amount", "10000"); // Replace with the amount in paise (e.g., 10000 for ₹100.00)
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                checkout.open(MainActivity.this, options);


            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentAvail = extras.getInt("currentAvail");
            exchangeRate = extras.getInt("exchangeRate");
            maxAvail = extras.getInt("maxAvail");
            withdrawLimit = extras.getInt("withdrawLimit");
            manualVisible = extras.getBoolean("manualVisible");
            merchantAPI = extras.getString("merchantAPI");
            androidId = extras.getString("androidId");
        }

        currentBal.setText(String.valueOf(currentAvail));
        remainingAmnt = maxAvail - currentAvail;

        if (remainingAmnt < 0) {
            remainingBal.setText("0");
        } else {
            remainingBal.setText(String.valueOf(remainingAmnt));
        }

        luckyWheelView.setTouchEnabled(false);

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "+100 Points";
        luckyItem1.icon = R.drawable.coin_prize;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "+200 Points";
        luckyItem2.icon = R.drawable.coin_prize2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "+300 Points";
        luckyItem3.icon = R.drawable.coin_prize;
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "+400 Points";
        luckyItem4.icon = R.drawable.coin_prize2;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "+500 Points";
        luckyItem5.icon = R.drawable.coin_prize;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "+1000 Points";
        luckyItem6.icon = R.drawable.coin_prize2;
        data.add(luckyItem6);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(5);

        luckyWheelView.setLuckyRoundItemSelectedListener(index -> {
            String pointsReceivedValue = data.get(index).topText;
            String numericPart = pointsReceivedValue.replaceAll("[^0-9]", "");

            if (!numericPart.isEmpty()) {
                Integer points = Integer.parseInt(numericPart);
                currentAvail = points + currentAvail;
                remainingAmnt = maxAvail - currentAvail;

                Toast.makeText(MainActivity.this, "You won " + numericPart + " points", Toast.LENGTH_SHORT).show();

                currentBal.setText(String.valueOf(currentAvail));
                remainingBal.setText(String.valueOf(remainingAmnt));

                updateDataInFirebase(currentAvail);
            }
        });
    }

    private void updateDataInFirebase(Integer currentAvail) {
        DatabaseReference userRef = userDataRef.child(androidId);
        userRef.child("currentAvail").setValue(currentAvail);
    }

    public void spinBtnClick(View view) {
        if (!limitReached()) {
            Random random = new Random();
            int[] availablePoints = {100, 200, 300, 400, 500, 1000};

            int maxPoints = remainingAmnt;

            List<Integer> allowedPoints = new ArrayList<>();
            for (int value : availablePoints) {
                if (value <= maxPoints) {
                    allowedPoints.add(value);
                }
            }

            if (!allowedPoints.isEmpty()) {
                int randomIndex = random.nextInt(allowedPoints.size());
                luckyWheelView.startLuckyWheelWithTargetIndex(randomIndex);
            }
        } else {
            Toast.makeText(MainActivity.this, "Limit Reached. Withdraw First To Play", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean limitReached()
    {
        return currentAvail >= maxAvail;
    }

    public void withdrawBtnClick()
    {

//            loadRewardedVideoAd();

//            if (limitReached())
//            {
//                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
//                intent.putExtra("CurrentAvail", currentAvail);
//                intent.putExtra("MaxAvail", 20);
//                intent.putExtra("ExchangeRate", exchangeRate);
//                intent.putExtra("WithdrawLimit", withdrawLimit);
//                intent.putExtra("manualVisible", manualVisible);
//                intent.putExtra("MerchantAPI", "rzp_test_D0iZEk51VDPgK5");
//                startActivity(intent);
//            }
//            else {
//                Toast.makeText(MainActivity.this, "You have not reached Minimum withdraw limit", Toast.LENGTH_SHORT).show();
//            }


//        Checkout checkout = new Checkout();
//
//        // set your id as below
//        checkout.setKeyID("rzp_test_D0iZEk51VDPgK5");
//        // set image
//        checkout.setImage(R.drawable.ic_logo);
//        int amount = Math.round(Float.parseFloat("10") * 100);
//
//
//        // initialize json object
//        JSONObject object = new JSONObject();
//        try {
//            // to put name
//            object.put("name", "Fiza");
//
//            // put description
//            object.put("description", "Test payment");
//
//            // to set theme color
//            object.put("theme.color", "");
//
//            // put the currency
//            object.put("currency", "INR");
//
//            // put amount
//            object.put("amount", amount);
//
//            // put mobile number
//            object.put("prefill.contact", "9284064503");
//
//            // put email
//            object.put("prefill.email", "chaitanyamunje@gmail.com");
//
//            // open razorpay to checkout activity
//            checkout.open(MainActivity.this, object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.d("payment", object+"");


    }

    public class MyAsyncTask extends AsyncTask<String, JSONObject, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject paymentLinkRequest = new JSONObject();

            // implement API in background and store the response in current variable
            try {
                paymentLinkRequest.put("amount", 1000);
                paymentLinkRequest.put("currency", "INR");
                paymentLinkRequest.put("accept_partial", true);
                paymentLinkRequest.put("first_min_partial_amount", 100);
//                paymentLinkRequest.put("expire_by", 1691097057);
                paymentLinkRequest.put("reference_id", "TS2793");
                paymentLinkRequest.put("description", "Payment for policy no #23456");
                JSONObject customer = new JSONObject();
                customer.put("name", "+919000090000");
                customer.put("contact", "Gaurav Kumar");
                customer.put("email", "gaurav.kumar@example.com");
                paymentLinkRequest.put("customer", customer);
                JSONObject notify = new JSONObject();
                notify.put("sms", true);
                notify.put("email", true);
                paymentLinkRequest.put("notify", notify);
                paymentLinkRequest.put("reminder_enable", true);
                JSONObject notes = new JSONObject();
                notes.put("policy_name", "Jeevan Bima");
                paymentLinkRequest.put("notes", notes);
                paymentLinkRequest.put("callback_url", "https://example-callback-url.com/");
                paymentLinkRequest.put("callback_method", "get");
                Log.d("json", "json " + paymentLinkRequest.toString());

//                String transferId = "trf_EAznuJ9cDLnF7Y";
//                JSONObject transferRequest = new JSONObject();
//                transferRequest.put("amount","100");
//                JSONObject notes = new JSONObject();
//                notes.put("branch","Acme Corp Bangalore North");
//                notes.put("name","Gaurav Kumar");
//                transferRequest.put("notes",notes);
//                razorpay.transfers.reversal(transferId,transferRequest);

                razorpay.paymentLink.create(paymentLinkRequest);
                return paymentLinkRequest;
            } catch (Exception e) {
                Log.d("error", "error" + e);
                return paymentLinkRequest;
            }


        }

        @Override
        protected void onPostExecute(JSONObject s) {
            progressDialog.dismiss();
            Log.d("json", "json on post method " + s.toString());
//            try {
////                razorpay.paymentLink.create(s);
//            } catch (RazorpayException e) {
//                Log.d("error", "error" + e.getMessage());
//            }

        }
    }

}
