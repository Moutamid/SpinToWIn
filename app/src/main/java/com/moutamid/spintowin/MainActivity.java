    package com.moutamid.spintowin;

    import android.content.Intent;
    import android.os.Bundle;
    import android.provider.Settings;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    import rubikstudio.library.LuckyWheelView;
    import rubikstudio.library.model.LuckyItem;

    public class MainActivity extends AppCompatActivity {
        String TAG = "MainActivityLogs";
        DataModel dataModel;
        TextView currentBal, remainingBal;
        public Integer currentAvail, exchangeRate, maxAvail, withdrawLimit, remainingAmnt;
        String points, merchantAPI, androidId;
        boolean manualVisible;
        LuckyWheelView luckyWheelView;
        DatabaseReference configDataRef = Constants.databaseReference().child("configData");
        DatabaseReference userDataRef = Constants.databaseReference().child("userData");
        List<LuckyItem> data = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);

            currentBal = findViewById(R.id.currentAmnt);
            remainingBal = findViewById(R.id.amntLeft);

            luckyWheelView.setTouchEnabled(false);

            fetchData();

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
            luckyWheelView.setRound(10);

            luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
                @Override
                public void LuckyRoundItemSelected(int index) {
                    String pointsReceivedValue = data.get(index).topText;

                    Toast.makeText(MainActivity.this, "You won " + pointsReceivedValue + " points", Toast.LENGTH_SHORT).show();

                    Integer points = Integer.valueOf(pointsReceivedValue);
                    currentAvail = points + currentAvail;
                    remainingAmnt = maxAvail - currentAvail;

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

        public void fetchData() {

            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Android ID: " + androidId);
            DatabaseReference query = userDataRef.child(androidId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataModel = dataSnapshot.getValue(DataModel.class);
                        if (dataModel != null) {
                            currentAvail = dataModel.getCurrentAvail();

                            // Update the UI elements here
                            currentBal.setText(String.valueOf(currentAvail));
                            remainingAmnt = maxAvail - currentAvail;
                            remainingBal.setText(String.valueOf(remainingAmnt));
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

        private void createData(String androidId) {
            DatabaseReference androidIdRef = userDataRef.child(androidId);
            androidIdRef.child("currentAvail").setValue(0);
            currentBal.setText(String.valueOf(currentAvail));
            remainingAmnt = maxAvail - currentAvail;
            remainingBal.setText(String.valueOf(remainingAmnt));
        }


        public void spinBtnClick(View view) {
            if (!limitReached()) {
                Random random = new Random();
                points = String.valueOf(random.nextInt(6));
                luckyWheelView.startLuckyWheelWithTargetIndex(Integer.parseInt(points) + 1);
            }
            else {
                Toast.makeText(MainActivity.this, "Limit Reached. Withdraw First To Play", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean limitReached() {
            return currentAvail >= maxAvail;
        }

        public void withdrawBtnClick(View view){
            if (limitReached())
            {
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("CurrentAvail", currentAvail);
                intent.putExtra("MaxAvail", maxAvail);
                intent.putExtra("ExchangeRate", exchangeRate);
                intent.putExtra("WithdrawLimit", withdrawLimit);
                intent.putExtra("manualVisible", manualVisible);
                intent.putExtra("MerchantAPI", merchantAPI);
                startActivity(intent);
            }
            else {
                Toast.makeText(MainActivity.this, "You have not reached Minimum withdraw limit", Toast.LENGTH_SHORT).show();
            }
        }
    }