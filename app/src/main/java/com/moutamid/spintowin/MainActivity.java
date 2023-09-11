package com.moutamid.spintowin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LuckyWheel luckyWheel;
    List<WheelItem> wheelItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckyWheel = findViewById(R.id.wheel);

        WheelItem wheelItem0 = new WheelItem(ResourcesCompat.getColor(getResources(),R.color.colorAccent, null),
        BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "10");

        wheelItemList.add(wheelItem0);

        WheelItem wheelItem1 = new WheelItem(ResourcesCompat.getColor(getResources(),R.color.primary, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize),"20");

        wheelItemList.add(wheelItem1);

        WheelItem wheelItem2 = new WheelItem(ResourcesCompat.getColor(getResources(),R.color.colorAccent, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize),"30");

        wheelItemList.add(wheelItem2);

        WheelItem wheelItem3 = new WheelItem(ResourcesCompat.getColor(getResources(),R.color.primary, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize),"40");

        wheelItemList.add(wheelItem3);

        WheelItem wheelItem4 = new WheelItem(ResourcesCompat.getColor(getResources(),R.color.secondary, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize),"50");

        wheelItemList.add(wheelItem4);

        luckyWheel.addWheelItems(wheelItemList);
    }


}