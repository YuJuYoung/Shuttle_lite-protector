package com.shuttlelite.protector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    private static final String RECEIVE_ERROR = "차량이 운행중이 아니거나 위치를 수신할 수 없습니다";

    public static Activity activity;

    private LoadingBroadcastReceiver receiver;
    private boolean received = false;

    private final class LoadingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyAction.SHUTTLE_LOCATION_RECEIVED)) {
                received = true;

                Bundle bundle = intent.getBundleExtra("location");

                float latitude = bundle.getFloat("latitude");
                float longitude = bundle.getFloat("longitude");

                Intent newIntent = new Intent(LoadingActivity.this, MapsActivity.class);
                newIntent.putExtra("latitude", latitude);
                newIntent.putExtra("longitude", longitude);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(newIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        activity = this;
        receiver = new LoadingBroadcastReceiver();

        TextView message = findViewById(R.id.loading_message);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!received && message != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            message.setText(RECEIVE_ERROR);
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, 6000);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(MyAction.SHUTTLE_LOCATION_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

}
