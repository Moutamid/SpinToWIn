package com.moutamid.spintowin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;

public class GuidelineActivity extends AppCompatActivity {
    public Integer currentAvail;
    public Integer exchangeRate;
    public Integer maxAvail;
    public Integer withdrawLimit;
    boolean manualVisible;
    String androidId, merchantAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guideline);

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
    }

    public void startBtnClick(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);

        mainIntent.putExtra("currentAvail", currentAvail);
        mainIntent.putExtra("exchangeRate", exchangeRate);
        mainIntent.putExtra("maxAvail", maxAvail);
        mainIntent.putExtra("withdrawLimit", withdrawLimit);
        mainIntent.putExtra("manualVisible", manualVisible);
        mainIntent.putExtra("merchantAPI", merchantAPI);
        mainIntent.putExtra("androidId", androidId);

        Stash.put(Constants.IS_TERMS_ACCEPTED, true);
        startActivity(mainIntent);
    }

    public void declineBtnClick(View view) {
        finish();
    }
}