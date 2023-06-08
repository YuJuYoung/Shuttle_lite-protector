package com.shuttlelite.protector;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ChoiceActivity extends AppCompatActivity {

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();
    private boolean shuttleBtnEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        shuttleBtnEnable = true;

        findViewById(R.id.shuttle_btn).setOnClickListener(view -> {
            if (!shuttleBtnEnable) {
                return;
            }
            shuttleBtnEnable = false;

            setDriverPhoneNumber();

            Intent newIntent = new Intent(this, LoadingActivity.class);
            startActivity(newIntent);

            shuttleBtnEnable = true;
        });

        findViewById(R.id.NFC_btn).setOnClickListener(view -> {
            Intent newIntent = new Intent(this, CardActivity.class);
            startActivity(newIntent);
        });
    }

    private void setDriverPhoneNumber() {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("occupantNumber", myAppInfo.getOccupantNumber());

            MyHttpURLConnection myHttpURLConnection
                    = new MyHttpURLConnection(MyURL.GET_DRIVER_INFO, requestBody);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    myHttpURLConnection.request();
                }
            });

            thread.start();
            thread.join(8000);

            JSONObject responseBody = myHttpURLConnection.getResponseBody();

            if (responseBody != null) {
                String phoneNumber = responseBody.getString("driverPhoneNumber");
                myAppInfo.setDriverPhoneNumber(phoneNumber);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}
