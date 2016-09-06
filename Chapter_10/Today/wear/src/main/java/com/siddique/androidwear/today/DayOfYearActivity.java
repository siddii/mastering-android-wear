package com.siddique.androidwear.today;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DayOfYearActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_of_year);
        String dayOfYearDesc = getString(R.string.day_of_year_card_desc,
                TodayUtil.getDayOfYear(),
                TodayUtil.getDaysLeftInYear());
        TextView desc = (TextView) findViewById(R.id.day_of_year_desc);
        desc.setText(dayOfYearDesc);
    }
}
