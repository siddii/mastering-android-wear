package com.siddique.androidwear.today;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Wearable listener service for data layer messages
 * Created by michaelHahn on 1/11/15.
 */
public class ListenerService extends WearableListenerService {

    private static final String TAG = ListenerService.class.getName();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "#### onMessageReceived ");
        if (messageEvent.getPath().startsWith("/today")) {
            final String message = new String(messageEvent.getData());
            Log.i(TAG, "Message path received on watch is: " + messageEvent.getPath());
            Log.i(TAG, "Message received on watch is: " + message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}

