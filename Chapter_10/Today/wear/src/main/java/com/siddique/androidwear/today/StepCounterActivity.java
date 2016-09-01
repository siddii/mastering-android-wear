package com.siddique.androidwear.today;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StepCounterActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    // Steps counted since the last reboot
    private int mSteps = 0;

    private static final String TAG = StepCounterActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_step_counter);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        refreshStepCount();
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

            refreshStepCount();
        }
    }

    private void refreshStepCount() {
        TextView desc = (TextView) findViewById(R.id.daily_step_count_desc);
        desc.setText(getString(R.string.daily_step_count_desc, mSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG,
                "onAccuracyChanged - " + sensor);
    }
}
