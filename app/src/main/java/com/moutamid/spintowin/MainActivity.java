    package com.moutamid.spintowin;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.google.firebase.database.DatabaseReference;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    import rubikstudio.library.LuckyWheelView;
    import rubikstudio.library.model.LuckyItem;

    public class MainActivity extends AppCompatActivity {
        TextView currentBal, remainingBal;
        public Integer currentAvail, exchangeRate, maxAvail, withdrawLimit, remainingAmnt;
        String points, merchantAPI, androidId;
        boolean manualVisible;
        LuckyWheelView luckyWheelView;
        DatabaseReference userDataRef = Constants.databaseReference().child("userData");
        List<LuckyItem> data = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);

            currentBal = findViewById(R.id.currentAmnt);
            remainingBal = findViewById(R.id.amntLeft);

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
            remainingBal.setText(String.valueOf(remainingAmnt));

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
            luckyWheelView.setRound(10);

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
                points = String.valueOf(random.nextInt(5));
                luckyWheelView.startLuckyWheelWithTargetIndex(Integer.parseInt(points));
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