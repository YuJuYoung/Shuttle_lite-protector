package com.shuttlelite.protector;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    private class CardBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG_COMPLETED = "태그 완료";

        @Override
        public void onReceive(Context context, Intent intent) {
            notice.setText(TAG_COMPLETED);
            image.setImageResource(R.drawable.ic_baseline_done_24);
            image.setContentDescription(TAG_COMPLETED);
        }
    }

    private NfcAdapter nfcAdapter;
    private CardEmulation cardEmulation;
    private CardBroadcastReceiver receiver;

    private ComponentName componentName;
    private List<String> AIDList;

    private TextView notice;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (!nfcAdapter.isEnabled()) {
            showNFCAlertDialog();
        }
        cardEmulation = CardEmulation.getInstance(nfcAdapter);
        receiver = new CardBroadcastReceiver();

        componentName = new ComponentName(this, MyHostApduService.class);
        AIDList = Arrays.asList(MyHostApduService.MY_AID);

        notice = findViewById(R.id.NFC_notice);
        image = findViewById(R.id.image);
    }

    @Override
    public void onResume() {
        super.onResume();
        cardEmulation.registerAidsForService(componentName, "other", AIDList);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyAction.NFC_SEND_COMPLETED);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        cardEmulation.removeAidsForService(componentName, "other");
        unregisterReceiver(receiver);
    }

    private void showNFCAlertDialog() {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(this);

        DialogBuilder
                .setTitle("NFC가 비활성화 되어있음")
                .setMessage("NFC를 활성화 시켜야합니다.")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                    }
                });
        AlertDialog dialog = DialogBuilder.create();
        dialog.show();
    }

}
