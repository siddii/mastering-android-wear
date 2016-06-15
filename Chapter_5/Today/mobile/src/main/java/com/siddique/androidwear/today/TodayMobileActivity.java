package com.siddique.androidwear.today;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TodayMobileActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = TodayMobileActivity.class.getName();

    private int CONNECTION_TIME_OUT_MS = 15000;
    private TextView devicesConnectedTextView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.i(TAG, "Creating Google Api Client");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        devicesConnectedTextView = (TextView) findViewById(R.id.devicesConnected);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Google Api Client Connected");

        new Thread(new Runnable() {

            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                final List<Node> nodes = result.getNodes();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(TAG, "Connected devices = " + nodes.size());
                        devicesConnectedTextView.setText(String.valueOf(nodes.size()));
                    }
                });
            }
        }).start();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Failed to connect to Google Play Services");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Failed to connect to Google Play Services");
    }
}
