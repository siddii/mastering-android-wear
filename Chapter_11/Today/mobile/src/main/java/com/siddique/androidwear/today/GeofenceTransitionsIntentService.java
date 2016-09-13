/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.siddique.androidwear.today;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Set;


/**
 * Listens for geofence transition changes.
 */
public class GeofenceTransitionsIntentService extends IntentService {


    private static final String TAG = GeofenceTransitionsIntentService.class.getName();

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     *
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Location changed " + intent);

        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {

            int transitionType = geoFenceEvent.getGeofenceTransition();
            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);

            Log.i(TAG, "Notifying home todo items");
            String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                    .getRequestId();

            switch (triggeredGeoFenceId) {
                case Constants.HOME_GEOFENCE_ID:
                    if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                        Log.i(TAG, "Notifying home todo items");
                        notifyTodoItems(notificationManager, "Home", Constants.HOME_TODO_NOTIFICATION_ID, R.drawable.white_house);
                    }
                    break;
                case Constants.WORK_GEOFENCE_ID:
                    if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                        Log.i(TAG, "Notifying work todo items");
                        notifyTodoItems(notificationManager, "Work", Constants.WORK_TODO_NOTIFICATION_ID, R.drawable.capitol_hill);
                    }
                    break;
            }
        }
    }

    private void notifyTodoItems(NotificationManagerCompat notificationManager, String todoItemType, int notificationId, int background) {
        Set<String> todoItems = TodoItems.readItems(this, todoItemType);
        Intent viewIntent = new Intent(this, TodoMobileActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_today_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                getResources(), background))
                        .setContentTitle(todoItems.size() + " " + todoItemType + " todo items found!")
                        .setContentText(todoItems.toString()    )
                        .setContentIntent(viewPendingIntent);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
