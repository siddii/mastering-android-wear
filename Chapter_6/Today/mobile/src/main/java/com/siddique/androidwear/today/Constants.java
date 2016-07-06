package com.siddique.androidwear.today;

public class Constants {

    public static final String ON_THIS_DAY_REQUEST = "/today/onThisDayRequest";
    public static final String ON_THIS_DAY_TIMESTAMP = "/today/requestTimestamp";

    public static final String ON_THIS_DAY_DATA_ITEM_HEADER = "/today/onThisDayHeader";
    public static final String ON_THIS_DAY_DATA_ITEM_CONTENT = "/today/onThisDayContent";


    //Geofence parameters for 'Home' (Whitehouse)
    public static final String HOME_GEOFENCE_ID = "1";
    public static final double HOME_LATITUDE = 38.897885;
    public static final double HOME_LONGITUDE = -77.036541;

    //Geofence parameters for 'Work' (Capitol Hill)
    public static final String WORK_GEOFENCE_ID = "2";
    public static final double WORK_LATITUDE = 38.886050;
    public static final double WORK_LONGITUDE = -76.999621;

    public static int API_CLIENT_CONNECTION_TIME_OUT_MS = 15000;


    public static final int HOME_TODO_NOTIFICATION_ID = 10;
    public static final int WORK_TODO_NOTIFICATION_ID = 20;
}
