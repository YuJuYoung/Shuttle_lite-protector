package com.shuttlelite.protector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        Bundle bundle = new Bundle();

        bundle.putFloat("latitude", Float.parseFloat(data.get("latitude")));
        bundle.putFloat("longitude", Float.parseFloat(data.get("longitude")));

        Intent newIntent = new Intent(MyAction.SHUTTLE_LOCATION_RECEIVED);
        newIntent.putExtra("location", bundle);

        sendBroadcast(newIntent);
    }

}
