    package com.moutamid.spintowin;

    import static android.util.Log.d;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.res.ResourcesCompat;

    import android.content.Intent;
    import android.graphics.BitmapFactory;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.bluehomestudio.luckywheel.LuckyWheel;
    import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
    import com.bluehomestudio.luckywheel.WheelItem;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    public class MainActivity extends AppCompatActivity {
        String TAG = "MainActivityLogs";
        TextView currentBal, remainingBal;
        DataModel dataModel;
        public Integer currentAvail, exchangeRate, maxAvail, withdrawLimit, remainingAmnt;
        String points;
        boolean manualVisible;

        LuckyWheel luckyWheel;
        List<WheelItem> wheelItemList = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            luckyWheel = findViewById(R.id.wheel);
            currentBal = findViewById(R.id.currentAmnt);
            remainingBal = findViewById(R.id.amntLeft);

            fetchData();

            WheelItem wheelItem0 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.primary, null),
                    BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "+100  Coins");

            wheelItemList.add(wheelItem0);

            WheelItem wheelItem1 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.secondary, null),
                    BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "+200  Coins");

            wheelItemList.add(wheelItem1);

            WheelItem wheelItem2 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.primary, null),
                    BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "+300  Coins");

            wheelItemList.add(wheelItem2);

            WheelItem wheelItem3 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.secondary, null),
                    BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "+400  Coins");

            wheelItemList.add(wheelItem3);

            WheelItem wheelItem4 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.primary, null),
                    BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "+500  Coins");
            wheelItemList.add(wheelItem4);

            WheelItem wheelItem5 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.secondary, null),
                    BitmapFactory.decodeResource(getResources(), R.drawable.coin_prize), "+1000  Coins");
            wheelItemList.add(wheelItem5);

            luckyWheel.addWheelItems(wheelItemList);

            luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
                @Override
                public void onReachTarget() {
                    WheelItem itemselected = wheelItemList.get(Integer.parseInt(points));
                    String pointsRecv = itemselected.text;

                    String numericPart = pointsRecv.replaceAll("[^0-9]", "");
                    int pointsReceivedValue = Integer.parseInt(numericPart);

                    Toast.makeText(MainActivity.this, "You won " + pointsReceivedValue + " points", Toast.LENGTH_SHORT).show();

                    currentAvail = Integer.valueOf(pointsReceivedValue + currentAvail);
                    remainingAmnt = remainingAmnt - currentAvail;

                    currentBal.setText(String.valueOf(currentAvail));
                    remainingBal.setText(String.valueOf(remainingAmnt));

                    updateDataInFirebase(currentAvail, remainingAmnt);
                }
            });
        }

        private void updateDataInFirebase(Integer currentAvail, Integer remainingAmnt) {
            DatabaseReference reference = Constants.databaseReference();
            if (dataModel != null) {
                dataModel.setCurrentAvail(currentAvail);
                dataModel.setRemainingAmnt(remainingAmnt);
                reference.setValue(dataModel);
            }
        }

        public void fetchData() {
            DatabaseReference reference = Constants.databaseReference();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataModel = dataSnapshot.getValue(DataModel.class);
                        if (dataModel != null) {
                            currentAvail = dataModel.getCurrentAvail();
                            exchangeRate = dataModel.getExchangeRate();
                            maxAvail = dataModel.getMaxAvail();
                            withdrawLimit = dataModel.getWithdrawLimit();
                            remainingAmnt = dataModel.getRemainingAmnt();
                            manualVisible = dataModel.isManualVisible();

                            currentBal.setText(String.valueOf(currentAvail));
                            remainingBal.setText(String.valueOf(remainingAmnt));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Error Fetching Data");
                }
            });
        }

        public void spinBtnClick(View view) {
            if (!limitReached()) {
                Random random = new Random();
                points = String.valueOf(random.nextInt(6));
                luckyWheel.rotateWheelTo(Integer.parseInt(points) + 1);
            }
            else {
                Toast.makeText(MainActivity.this, "Today's Limit Reached.", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean limitReached() {
            if (currentAvail != maxAvail) {
                return true;
            } else {
                return false;
            }
        }

        public void withdrawBtnClick(View view){
            if (limitReached())
            {
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("CurrentAvail", currentAvail);
                intent.putExtra("MaxAvail", maxAvail);
                intent.putExtra("ExchangeRate", exchangeRate);
                intent.putExtra("WithdrawLimit", withdrawLimit);
                intent.putExtra("ManualVisible", manualVisible);
                startActivity(intent);
            }
            else {
                Toast.makeText(MainActivity.this, "You have not reached Minimum withdraw limit", Toast.LENGTH_SHORT).show();
            }
        }
    }