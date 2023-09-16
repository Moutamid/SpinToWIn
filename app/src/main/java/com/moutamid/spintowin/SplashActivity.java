package com.moutamid.spintowin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Stash.getBoolean(Constants.IS_TERMS_ACCEPTED, false)) {
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this, GuidelineActivity.class));
        }
    }
}
