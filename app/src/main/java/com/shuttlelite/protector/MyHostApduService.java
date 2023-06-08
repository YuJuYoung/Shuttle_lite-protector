package com.shuttlelite.protector;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import java.nio.charset.StandardCharsets;

public class MyHostApduService extends HostApduService {

    public static final String MY_AID = BuildConfig.MY_AID;

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        Intent newIntent = new Intent(MyAction.NFC_SEND_COMPLETED);
        sendBroadcast(newIntent);

        return myAppInfo.getOccupantNumber().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void onDeactivated(int i) {

    }
}
