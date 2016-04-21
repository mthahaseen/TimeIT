package com.overclocked.timeit.common;

/**
 * Created by Thahaseen on 4/20/2016.
 */
public class AppConstants {

    public static final int MAX_AVG_SWIPE_HOURS = 11;
    public static final int MIN_AVG_SWIPE_HOURS = 7;
    public static final int MIN_AVG_SWIPE_MINUTES = 0;
    public static final int MAX_AVG_SWIPE_MINUTES = 59;

    public static final String PREF_COMPANY_NAME = "companyName";
    public static final String PREF_COMPANY_LOGO = "companyLogo";
    public static final String PREF_INITIAL_TIME_CONFIG = "initialTimeConfig";
    public static final String PREF_AVG_SWIPE_MILLIS = "averageSwipeMillis";
    public static final String PREF_START_DAY = "startDay";
    public static final String PREF_END_DAY = "endDay";
    public static final String PREF_APPROX_OFFICE_IN_TIME_MILLIS = "approxOfficeInTimeMillis";
    public static final String PREF_APPROX_OFFICE_OUT_TIME_MILLIS = "approxOfficeOutTimeMillis";


    public static final String MSG_NO_INTERNET  = "Trouble connecting to network";

    public static final String URL_GET_COMPANY_FEED  = "http://petmire.ap-southeast-1.elasticbeanstalk.com/companyfeed.json";

    public static final String KEY_SUCCESS  = "success";
    public static final String KEY_COMPANY_FEED  = "companyFeed";
}