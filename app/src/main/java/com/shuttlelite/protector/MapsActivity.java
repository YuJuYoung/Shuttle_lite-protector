package com.shuttlelite.protector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String SHUTTLE_MARKER_TITLE = "현재 셔틀 위치";
    private static final String CALLING_ERROR = "전화번호 정보를 찾을 수 없습니다.";

    private GoogleMap mMap;
    private Marker shuttleMarker = null;

    private final class MapBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyAction.SHUTTLE_LOCATION_RECEIVED)) {
                if (mMap != null) {
                    Bundle bundle = intent.getBundleExtra("location");

                    float latitude = bundle.getFloat("latitude");
                    float longitude = bundle.getFloat("longitude");

                    if (shuttleMarker != null) {
                        shuttleMarker.remove();
                    }
                    setMarker(latitude, longitude);
                }
            }
        }
    }

    private MapBroadcastReceiver receiver;
    private MyAppInfo appInfo = MyAppInfo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoadingActivity.activity != null) {
            LoadingActivity.activity.finish();
        }

        setContentView(R.layout.activity_maps);

        receiver = new MapBroadcastReceiver();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton callingBtn = findViewById(R.id.calling_btn);

        findViewById(R.id.calling_btn).setOnClickListener(view -> {
            String phoneNumber = appInfo.getDriverPhoneNumber();

            if (phoneNumber != null) {
                Intent newIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)); // 운전기사 전화번호
                startActivity(newIntent);
            } else {
                callingBtn.setContentDescription(CALLING_ERROR);
                Toast.makeText(this, CALLING_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        float latitude = intent.getFloatExtra("latitude", 0);
        float longitude = intent.getFloatExtra("longitude", 0);
        setMarker(latitude, longitude);

        IntentFilter filter = new IntentFilter(MyAction.SHUTTLE_LOCATION_RECEIVED);
        registerReceiver(receiver, filter);
    }

    private void setMarker(float latitude, float longitude) {
        if (shuttleMarker != null) {
            shuttleMarker.remove();
        }

        LatLng location = new LatLng(latitude, longitude);
        shuttleMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(location)
                        .title(SHUTTLE_MARKER_TITLE)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }
}