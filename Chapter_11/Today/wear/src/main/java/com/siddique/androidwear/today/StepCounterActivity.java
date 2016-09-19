package com.siddique.androidwear.today;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CardFrame;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class StepCounterActivity extends WearableActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    // Steps counted since the last reboot
    private int mSteps = 0;

    private static final String TAG = StepCounterActivity.class.getName();

    private BoxInsetLayout stepCounterLayout;
    private CardFrame cardFrame;
    private TextView title, desc;

    /**
     * Since the handler (used in active mode) can't wake up the processor when the device is in
     * ambient mode and undocked, we use an Alarm to cover ambient mode updates when we need them
     * more frequently than every minute. Remember, if getting updates once a minute in ambient
     * mode is enough, you can do away with the Alarm code and just rely on the onUpdateAmbient()
     * callback.
     */
    private AlarmManager mAmbientStateAlarmManager;
    private PendingIntent mAmbientStatePendingIntent;

    /**
     * This custom handler is used for updates in "Active" mode. We use a separate static class to
     * help us avoid memory leaks.
     */
    private final Handler mActiveModeUpdateHandler = new UpdateHandler(this);

    /**
     * Custom 'what' for Message sent to Handler.
     */
    private static final int MSG_UPDATE_SCREEN = 0;

    /**
     * Milliseconds between updates based on state.
     */
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_step_counter);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        setAmbientEnabled();

        mAmbientStateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientStateIntent = new Intent(getApplicationContext(), DailyTotalActivity.class);

        mAmbientStatePendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0 /* requestCode */,
                ambientStateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        stepCounterLayout = (BoxInsetLayout) findViewById(R.id.step_counter_layout);
        cardFrame = (CardFrame) findViewById(R.id.step_counter_card_frame);
        title = (TextView) findViewById(R.id.daily_step_count_title);
        desc = (TextView) findViewById(R.id.daily_step_count_desc);

        refreshDisplayAndSetNextUpdate();
    }

    /**
     * This is mostly triggered by the Alarms we set in Ambient mode and informs us we need to
     * update the screen (and process any data).
     */
    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent(): " + intent);
        super.onNewIntent(intent);

        setIntent(intent);

        refreshDisplayAndSetNextUpdate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);
        mAmbientStateAlarmManager.cancel(mAmbientStatePendingIntent);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG,
                "onSensorChanged - " + event.values[0]);
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.i(TAG,
                    "Total step count: " + mSteps);

            mSteps = (int) event.values[0];

            refreshDisplayAndSetNextUpdate();
        }
    }

    private void refreshStepCount() {
        desc.setText(getString(R.string.daily_step_count_desc, mSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG,
                "onAccuracyChanged - " + sensor);
    }

    /**
     * Prepares UI for Ambient view.
     */
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        Log.d(TAG, "onEnterAmbient()");
        super.onEnterAmbient(ambientDetails);


        /** Clears Handler queue (only needed for updates in active mode). */
        mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);

        /**
         * Following best practices outlined in WatchFaces API (keeping most pixels black,
         * avoiding large blocks of white pixels, using only black and white,
         * and disabling anti-aliasing anti-aliasing, etc.)
         */

        stepCounterLayout.setBackgroundColor(Color.BLACK);
        cardFrame.setBackgroundColor(Color.BLACK);

        desc.setTextColor(Color.WHITE);
        desc.getPaint().setAntiAlias(false);

        title.setTextColor(Color.WHITE);
        title.getPaint().setAntiAlias(false);

        refreshDisplayAndSetNextUpdate();
    }

    /**
     * Updates UI in Ambient view (once a minute). Because we need to update UI sooner than that
     * (every ~20 seconds), we also use an Alarm. However, since the processor is awake for this
     * callback, we might as well call refreshDisplayAndSetNextUpdate() to update screen and reset
     * the Alarm.
     * <p/>
     * If you are happy with just updating the screen once a minute in Ambient Mode (which will be
     * the case a majority of the time), then you can just use this method and remove all
     * references/code regarding Alarms.
     */
    @Override
    public void onUpdateAmbient() {
        Log.d(TAG, "onUpdateAmbient()");
        super.onUpdateAmbient();

        refreshDisplayAndSetNextUpdate();
    }

    /**
     * Prepares UI for Active view (non-Ambient).
     */
    @Override
    public void onExitAmbient() {
        Log.d(TAG, "onExitAmbient()");
        super.onExitAmbient();

        /** Clears out Alarms since they are only used in ambient mode. */
        mAmbientStateAlarmManager.cancel(mAmbientStatePendingIntent);

        stepCounterLayout.setBackgroundResource(R.drawable.jogging);
        cardFrame.setBackgroundColor(Color.WHITE);

        desc.setTextColor(Color.BLACK);
        desc.getPaint().setAntiAlias(true);

        title.setTextColor(Color.BLACK);
        title.getPaint().setAntiAlias(true);

        refreshDisplayAndSetNextUpdate();
    }

    /**
     * Loads data/updates screen (via method), but most importantly, sets up the next refresh
     * (active mode = Handler and ambient mode = Alarm).
     */
    private void refreshDisplayAndSetNextUpdate() {

        Log.i(TAG, "Refresh display and set next update ");

        refreshStepCount();

        long timeMs = System.currentTimeMillis();

        if (isAmbient()) {
            /** Calculate next trigger time (based on state). */
            long delayMs = AMBIENT_INTERVAL_MS - (timeMs % AMBIENT_INTERVAL_MS);
            long triggerTimeMs = timeMs + delayMs;

            /**
             * Note: Make sure you have set activity launchMode to singleInstance in the manifest.
             * Otherwise, it is easy for the AlarmManager launch intent to open a new activity
             * every time the Alarm is triggered rather than reusing this Activity.
             */
            mAmbientStateAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    mAmbientStatePendingIntent);

        } else {
            /** Calculate next trigger time (based on state). */
            long delayMs = ACTIVE_INTERVAL_MS - (timeMs % ACTIVE_INTERVAL_MS);

            mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);
            mActiveModeUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SCREEN, delayMs);
        }
    }

    /**
     * Handler separated into static class to avoid memory leaks.
     */
    private static class UpdateHandler extends Handler {
        private final WeakReference<StepCounterActivity> mMainActivityWeakReference;

        public UpdateHandler(StepCounterActivity reference) {
            mMainActivityWeakReference = new WeakReference<StepCounterActivity>(reference);
        }

        @Override
        public void handleMessage(Message message) {
            StepCounterActivity mainActivity = mMainActivityWeakReference.get();

            if (mainActivity != null) {
                switch (message.what) {
                    case MSG_UPDATE_SCREEN:
                        mainActivity.refreshDisplayAndSetNextUpdate();
                        break;
                }
            }
        }
    }
}
