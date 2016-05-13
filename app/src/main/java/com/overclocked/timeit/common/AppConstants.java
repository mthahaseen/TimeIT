package com.overclocked.timeit.common;

/**
 * Created by Thahaseen on 4/20/2016.
 */
public class AppConstants {

    public static final String SENDER_ID = "997911950305";
    public static final String TIME_IT_MARKET_URI = "market://details?id=com.overclocked.timeit";
    public static final String TIME_IT_MARKET_TINY_URL = "https://goo.gl/PVZs0m";

    public static final int MAX_AVG_SWIPE_HOURS = 11;
    public static final int MIN_AVG_SWIPE_HOURS = 7;
    public static final int MIN_AVG_SWIPE_MINUTES = 0;
    public static final int MAX_AVG_SWIPE_MINUTES = 59;
    public static final int VERTICAL_ITEM_SPACE = 20;
    public static final int VERTICAL_ITEM_SPACE_PRE_LOLLIPOP = 5;

    //Preferences
    public static final String PREF_COMPANY_NAME = "companyName";
    public static final String PREF_COMPANY_LOGO = "companyLogo";
    public static final String PREF_INITIAL_TIME_CONFIG = "initialTimeConfig";
    public static final String PREF_AVG_SWIPE_MILLIS = "averageSwipeMillis";
    public static final String PREF_START_DAY = "startDay";
    public static final String PREF_END_DAY = "endDay";
    public static final String PREF_DAY_DIFFERENCE = "dayDifference";
    public static final String PREF_APPROX_OFFICE_IN_TIME_MILLIS = "approxOfficeInTimeMillis";
    public static final String PREF_APPROX_OFFICE_OUT_TIME_MILLIS = "approxOfficeOutTimeMillis";
    public static final String PREF_TUTORIAL_ONE = "tutorialOne";
    public static final String PREF_TUTORIAL_TWO = "tutorialTwo";

    public static final String SWIPE_IN = "Swipe In";
    public static final String SWIPE_OUT = "Swipe Out";

    public static final String SWIPE_DATE_FORMAT = "EEEE, d-MMM-yyyy";
    public static final String MSG_NO_INTERNET  = "Trouble connecting to network";

    public static final String URL_GET_COMPANY_FEED  = "http://thaha.tradly.co/timeit/api/companyfeed.json";

    public static final String KEY_SUCCESS  = "success";
    public static final String KEY_COMPANY_FEED  = "companyFeed";

    //Localytics Screen Tags
    public static final String LOCALYTICS_TAG_SCREEN_HOME  = "Home";
    public static final String LOCALYTICS_TAG_SCREEN_INTRO  = "Intro";
    public static final String LOCALYTICS_TAG_SCREEN_COMPANY_SELECT  = "CompanySelect";
    public static final String LOCALYTICS_TAG_SCREEN_TIME_CONFIGURE  = "TimeConfigure";
    public static final String LOCALYTICS_TAG_SCREEN_TIME_EDIT  = "TimeEdit";

    //Localytics Event Tags
    public static final String LOCALYTICS_TAG_EVENT_COMPANY  = "CompanySelect";
    public static final String LOCALYTICS_TAG_EVENT_SWIPE_IN_CLICK  = "SwipeInClick";
    public static final String LOCALYTICS_TAG_EVENT_SWIPE_IN_LONG_CLICK  = "SwipeInLongClick";
    public static final String LOCALYTICS_TAG_EVENT_SWIPE_OUT_CLICK  = "SwipeOutClick";
    public static final String LOCALYTICS_TAG_EVENT_SWIPE_OUT_LONG_CLICK  = "SwipeOutLongClick";
    public static final String LOCALYTICS_TAG_EVENT_SWIPE_DATA_CLICK  = "SwipeDataClick";
    public static final String LOCALYTICS_TAG_EVENT_SWIPE_DATA_LONG_CLICK  = "SwipeDataLongClick";
    public static final String LOCALYTICS_TAG_EVENT_RATING_DIALOG_CLICK  = "RatingDialogClick";
    public static final String LOCALYTICS_TAG_EVENT_RATING_NOW_CLICK  = "RateNowClick";
    public static final String LOCALYTICS_TAG_EVENT_RATING_NO_THANKS_CLICK  = "RateNoThanksClick";
    public static final String LOCALYTICS_TAG_EVENT_SHARING_DIALOG_CLICK  = "SharingDialogClick";
    public static final String LOCALYTICS_TAG_EVENT_SHARE_NOW_CLICK  = "ShareNowClick";
    public static final String LOCALYTICS_TAG_EVENT_SHARE_LATER_CLICK  = "ShareLaterClick";
    public static final String LOCALYTICS_TAG_EVENT_ADS_CLICK  = "AdsClick";
    public static final String LOCALYTICS_TAG_EVENT_IN_TIME_EDIT_CLICK = "InTimeEditClick";
    public static final String LOCALYTICS_TAG_EVENT_OUT_TIME_EDIT_CLICK = "OutTimeEditClick";
}
