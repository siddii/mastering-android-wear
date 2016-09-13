package com.siddique.androidwear.today;

import java.util.Calendar;

/**
 * Created by siddique on 9/5/16.
 */
public class TodayUtil {

    public static int getDayOfYear() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    public static int getDaysLeftInYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
    }
}
