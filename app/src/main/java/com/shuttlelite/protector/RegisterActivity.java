package com.shuttlelite.protector;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    private TextView occupantNumText, registerFailedMsg;
    private boolean isCheckBtnEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        occupantNumText = findViewById(R.id.occupant_number_text);
        registerFailedMsg = findViewById(R.id.register_failed_message);
        isCheckBtnEnable = true;

        findViewById(R.id.check_number_btn).setOnClickListener(view -> {
            if (!isCheckBtnEnable) {
                return;
            }
            isCheckBtnEnable = false;

            String occupantNumber = occupantNumText.getText().toString();

            if (occupantNumber.equals("")) {
                return;
            }
            String result = checkOccupantNumber(occupantNumber);

            if (result.equals(ResultMessage.SUCCESS)) {
                myAppInfo.setOccupantNumber();
                Toast.makeText(this, myAppInfo.getOccupantNumber(), Toast.LENGTH_SHORT).show();

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    registerFailedMsg.setText(ResultMessage.ERROR);
                                    return;
                                }

                                String token = task.getResult();
                                String resultMsg = registerFCMToken(token);

                                if (resultMsg.equals(ResultMessage.SUCCESS)) {
                                    Intent newIntent = new Intent(RegisterActivity.this, ChoiceActivity.class);
                                    startActivity(newIntent);
                                } else {
                                    registerFailedMsg.setText(resultMsg);
                                }
                                isCheckBtnEnable = true;
                            }
                        });
            } else {
                registerFailedMsg.setText(result);
                isCheckBtnEnable = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    private String checkOccupantNumber(String occupantNumber) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("occupantNumber", occupantNumber);

            MyHttpURLConnection myHttpURLConnection
                    = new MyHttpURLConnection(MyURL.CHECK_OCCUPANT_NUMBER, requestBody);

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
                boolean result = responseBody.getBoolean("result");

                if (result) {
                    if (myAppInfo.writeOccupantNumber(occupantNumber)) {
                        return ResultMessage.SUCCESS;
                    }
                } else {
                    return ResultMessage.WRONG_OCCUPANT_NUMBER;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResultMessage.ERROR;
    }

    private String registerFCMToken(String token) {
        JSONObject requestBody = new JSONObject();

        try {
            String occupantNumber = myAppInfo.getOccupantNumber();

            requestBody.put("occupantNumber", occupantNumber);
            requestBody.put("token", token);

            MyHttpURLConnection myHttpURLConnection
                    = new MyHttpURLConnection(MyURL.REGISTER_TOKEN, requestBody);

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
                boolean result = responseBody.getBoolean("result");

                if (result) {
                    return ResultMessage.SUCCESS;
                }
                return ResultMessage.REGISTER_TOKEN_ERROR;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResultMessage.ERROR;
    }
}
