package com.moutamid.spintowin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fxn.stash.Stash;

import java.util.Random;

public class GuidelineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guideline);
    }

    public void startBtnClick(View view) {
        Intent intent= new Intent(GuidelineActivity.this, MainActivity.class);
        Stash.put(Constants.IS_TERMS_ACCEPTED, true);
        startActivity(intent);
    }

    public void declineBtnClick(View view) {
        finish();
    }
}