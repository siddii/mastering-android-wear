package com.siddique.androidwear.today;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TodayMobileActivity extends Activity implements DataApi.DataListener,
        MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = TodayMobileActivity.class.getName();

    private int CONNECTION_TIME_OUT_MS = 15000;
    private TextView connectionStatusText = null;


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

        connectionStatusText = (TextView) findViewById(R.id.connectionStatusView);
    }

    private void sendMessageToWearable(final String path, final byte[] data) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node : nodes.getNodes()) {
                            Wearable.MessageApi
                                    .sendMessage(mGoogleApiClient, node.getId(), path, data);
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        super.onStop();
    }

    public void setWearableConnectionStatus(boolean status) {
        connectionStatusText.setText(Boolean.toString(status));
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);

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
                        setWearableConnectionStatus(nodes.size() > 0);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Ignore
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Failed to connect to Google Play Services");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
//        if (messageEvent.getPath().equals(RESET_QUIZ_PATH)) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    resetQuiz(null);
//                }
//            });
//        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // Need to freeze the dataEvents so they will exist later on the UI thread
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DataEvent event : events) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem())
                                .getDataMap();

                    }
                }
            }
        });

    }
}
