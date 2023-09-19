package com.moutamid.spintowin;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    DatabaseReference configDataRef = Constants.databaseReference().child("configData");
    DatabaseReference userDataRef = Constants.databaseReference().child("userData");
    public Integer currentAvail, exchangeRate, maxAvail, withdrawLimit;
    boolean manualVisible;
    DataModel dataModel;
    String androidId, merchantAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DatabaseReference query = userDataRef.child(androidId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataModel = dataSnapshot.getValue(DataModel.class);
                    if (dataModel != null) {
                        currentAvail = dataModel.getCurrentAvail();

                        handleDataInitialization();
                    }
                } else {
                    createData(androidId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
            }
        });
    }

    private void handleDataInitialization() {
        configDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataModel = dataSnapshot.getValue(DataModel.class);
                    if (dataModel != null) {
                        exchangeRate = dataModel.getExchangeRate();
                        maxAvail = dataModel.getMaxAvail();
                        withdrawLimit = dataModel.getWithdrawLimit();
                        manualVisible = dataModel.isManualVisible();
                        merchantAPI = dataModel.getMerchantAPI();

                        if (Stash.getBoolean(Constants.IS_TERMS_ACCEPTED, false)) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("currentAvail", currentAvail);
                            intent.putExtra("exchangeRate", exchangeRate);
                            intent.putExtra("maxAvail", maxAvail);
                            intent.putExtra("withdrawLimit", withdrawLimit);
                            intent.putExtra("manualVisible", manualVisible);
                            intent.putExtra("merchantAPI", merchantAPI);
                            intent.putExtra("androidId", androidId);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, GuidelineActivity.class);
                            intent.putExtra("currentAvail", currentAvail);
                            intent.putExtra("exchangeRate", exchangeRate);
                            intent.putExtra("maxAvail", maxAvail);
                            intent.putExtra("withdrawLimit", withdrawLimit);
                            intent.putExtra("manualVisible", manualVisible);
                            intent.putExtra("merchantAPI", merchantAPI);
                            intent.putExtra("androidId", androidId);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(SplashActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error Fetching Data");
            }
        });
    }

    private void createData(String androidId) {
        DatabaseReference androidIdRef = userDataRef.child(androidId);
        androidIdRef.child("currentAvail").setValue(0);
        currentAvail = 0;
    }
}
