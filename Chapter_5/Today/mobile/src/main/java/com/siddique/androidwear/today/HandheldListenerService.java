package com.siddique.androidwear.today;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HandheldListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = HandheldListenerService.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Created");

        if (null == mGoogleApiClient) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            Log.i(TAG, "GoogleApiClient created");
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Connecting to GoogleApiClient..");
        }
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "Destroyed");

        if (null != mGoogleApiClient) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
                Log.i(TAG, "GoogleApiClient disconnected");
            }
        }

        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "onConnectionSuspended called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed called");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "onConnected called");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.i(TAG, "Data Changed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.i(TAG, "Message received" + messageEvent);

        if (Constants.ON_THIS_DAY_REQUEST.equals(messageEvent.getPath())) {
            //read Today's content from Wikipedia
            getOnThisDayContentFromWikipedia();
        }
    }

    private void getOnThisDayContentFromWikipedia() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://en.wikipedia.org/wiki/Special:FeedItem/onthisday/" + DATE_FORMAT.format(new Date()) + "000000/en";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Wikipedia response  = " + response);
                        Document doc = Jsoup.parse(response);
                        Element heading = doc.select("h1").first();
                        Log.i(TAG, "Heading node = " + heading);


                        if (heading != null) {
                            Log.i(TAG, "Wikipedia page heading = " + heading);

                            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.ON_THIS_DAY_DATA_ITEM_HEADER);
                            DataMap dataMap = dataMapRequest.getDataMap();

                            // We add a timestamp to make this dataMap 'dirty'. This lets the wearable get updates.
                            dataMap.putLong(Constants.ON_THIS_DAY_TIMESTAMP, new Date().getTime());
                            dataMap.putString(Constants.ON_THIS_DAY_DATA_ITEM_HEADER, heading.text());

                            Element listNode = doc.select("ul").first();

                            if (listNode != null) {
                                Elements itemNodes = listNode.select("li");
                                int size = itemNodes.size();
                                ArrayList<String> items = new ArrayList<String>();
                                for (int i = 0; i < size; i++) {
                                    items.add(itemNodes.get(i).text());
                                }
                                dataMap.putStringArrayList(Constants.ON_THIS_DAY_DATA_ITEM_CONTENT, items);
                            }

                            Log.i(TAG, "Sending dataMap request ...");
                            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, dataMapRequest.asPutDataRequest());
                            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                @Override
                                public void onResult(final DataApi.DataItemResult result) {
                                    if (result.getStatus().isSuccess()) {
                                        Log.d(TAG, "Data item set: " + result.getDataItem().getUri());
                                    }
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error reading online content = " + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i(TAG, "Peer Connected " + peer.getDisplayName());
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Peer Disconnected " + peer.getDisplayName());
    }
}
