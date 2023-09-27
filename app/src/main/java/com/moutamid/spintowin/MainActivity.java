package com.moutamid.spintowin;

import android.content.Intent;
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
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);

        currentBal = findViewById(R.id.currentAmnt);
        remainingBal = findViewById(R.id.amntLeft);
        withdraw_btn = findViewById(R.id.withdraw_btn);
        withdraw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    RazorpayClient razorpay = new RazorpayClient("rzp_test_D0iZEk51VDPgK5", "FnQgV1J62ckAmaHl5T3LqcHT");
                                    JSONObject paymentLinkRequest = new JSONObject();
                                    paymentLinkRequest.put("amount", 1000);
                                    paymentLinkRequest.put("currency", "INR");
                                    paymentLinkRequest.put("accept_partial", true);
                                    paymentLinkRequest.put("first_min_partial_amount", 100);
                                    paymentLinkRequest.put("expire_by", 1691097057);
                                    paymentLinkRequest.put("reference_id", "TS1989");
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
                                    PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);
                                }
                                catch (Exception e) {
                                    Log.d("error", e.getMessage());
                                }


                            }
                        }, 1000);



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


    public boolean limitReached() {
        return currentAvail >= maxAvail;
    }

    public void withdrawBtnClick(View view) {
//        loadRewardedVideoAd();

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


}
