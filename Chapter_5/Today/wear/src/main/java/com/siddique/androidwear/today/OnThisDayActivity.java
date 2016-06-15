package com.siddique.androidwear.today;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;


public class OnThisDayActivity extends Activity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError;

    private static final String TAG = OnThisDayActivity.class.getName();

    private OnThisDay onThisDay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_this_day);

        if (onThisDay == null) {
            Toast.makeText(this, "Fetching from Wikipedia...", Toast.LENGTH_LONG).show();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } else {
            showOnThisDay(onThisDay);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError && onThisDay == null) {
            Log.i(TAG, "Connecting to Google Api Client");
            mGoogleApiClient.connect();
        } else {
            showOnThisDay(onThisDay);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to Data Api");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        sendMessage(Constants.ON_THIS_DAY_REQUEST, "OnThisDay".getBytes());
    }

    private void sendMessage(final String path, final byte[] data) {
        Log.i(TAG, "Sending message to path " + path);
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
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i(TAG, "###### onDataChanged");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

                String heading = dataMap.get(Constants.ON_THIS_DAY_DATA_ITEM_HEADER);
                ArrayList<String> listItems = dataMap.get(Constants.ON_THIS_DAY_DATA_ITEM_CONTENT);
                onThisDay = new OnThisDay(heading, listItems);

                showOnThisDay(onThisDay);
            }
        }
    }

    private void showOnThisDay(OnThisDay onThisDay) {
        TextView heading = (TextView) findViewById(R.id.on_this_day_heading);
        heading.setText(Html.fromHtml(onThisDay.getHeadingHtml()));

        TextView content = (TextView) findViewById(R.id.on_this_day_content);
        content.setText(Html.fromHtml(onThisDay.getListItemsHtml()));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed " + connectionResult);
        mResolvingError = true;
    }

}
