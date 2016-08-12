package com.siddique.androidwear.today;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class DayOfYearActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_of_year);
        Calendar calendar = Calendar.getInstance();
        String dayOfYearDesc = getString(R.string.day_of_year_card_desc,
                calendar.get(Calendar.DAY_OF_YEAR),
                calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));
        TextView desc = (TextView) findViewById(R.id.day_of_year_desc);
        desc.setText(dayOfYearDesc);
    }
}
