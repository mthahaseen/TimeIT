package com.overclocked.timeit.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.overclocked.timeit.model.Days;
import com.overclocked.timeit.model.SwipeData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Thahaseen on 4/13/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;
    SharedPreferences preferences;

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "timeit.db";

    // Table names
    private static final String TABLE_DAYS = "days";
    private static final String TABLE_SWIPE = "swipe";

    // Days Table Columns names
    private static final String DAY_ID = "id";
    private static final String DAY_NAME = "name";

    // Swipe Table Columns names
    private static final String SWIPE_WEEK_NUMBER = "week";
    private static final String SWIPE_DATE = "swipedate";
    private static final String SWIPE_IN_TIME= "intime";
    private static final String SWIPE_OUT_TIME= "outtime";
    private static final String SWIPE_HOLIDAY= "holiday";
    private static final String SWIPE_REMINDER= "reminder";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DAYS_TABLE = "CREATE TABLE " + TABLE_DAYS + "("
                + DAY_ID + " INTEGER,"
                + DAY_NAME + " TEXT)";

        db.execSQL(CREATE_DAYS_TABLE);

        String CREATE_SWIPE_TABLE = "CREATE TABLE " + TABLE_SWIPE + "("
                + SWIPE_DATE + " TEXT,"
                + SWIPE_WEEK_NUMBER + " INTEGER,"
                + SWIPE_IN_TIME + " INTEGER,"
                + SWIPE_OUT_TIME + " INTEGER,"
                + SWIPE_HOLIDAY + " INTEGER,"
                + SWIPE_REMINDER + " INTEGER)";

        db.execSQL(CREATE_SWIPE_TABLE);

        // Insert days id and values initially
        db.execSQL("insert into days values(1,'Sunday');");
        db.execSQL("insert into days values(2,'Monday');");
        db.execSQL("insert into days values(3,'Tuesday');");
        db.execSQL("insert into days values(4,'Wednesday');");
        db.execSQL("insert into days values(5,'Thursday');");
        db.execSQL("insert into days values(6,'Friday');");
        db.execSQL("insert into days values(7,'Saturday');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Days> getDays(int exclude)
    {
        List<Days> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DAYS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                if(exclude == 0){
                    Days d = new Days();
                    d.setDayId((c.getInt(c.getColumnIndex(DAY_ID))));
                    d.setDayName((c.getString(c.getColumnIndex(DAY_NAME))));
                    items.add(d);
                }else {
                    if (c.getInt(c.getColumnIndex(DAY_ID)) != exclude) {
                        Days d = new Days();
                        d.setDayId((c.getInt(c.getColumnIndex(DAY_ID))));
                        d.setDayName((c.getString(c.getColumnIndex(DAY_NAME))));
                        items.add(d);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return items;
    }

    public String getDay(int dayId){
        String dayName = "";
        String selectQuery = "SELECT * FROM " + TABLE_DAYS + " where " + DAY_ID + "=" +dayId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            dayName = c.getString(c.getColumnIndex(DAY_NAME));
        }
        c.close();
        db.close();
        return dayName;
    }

    public void addSwipeData(String swipeDate, int week, int startMillis, int endMillis){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SWIPE_DATE, swipeDate);
        contentValues.put(SWIPE_WEEK_NUMBER, week);
        contentValues.put(SWIPE_IN_TIME, startMillis);
        contentValues.put(SWIPE_OUT_TIME, endMillis);
        contentValues.put(SWIPE_HOLIDAY, 0);
        contentValues.put(SWIPE_REMINDER, 0);
        db.insert(TABLE_SWIPE, null, contentValues);
        db.close();
    }

    public boolean isWeekEntryAvailable(int week){
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_WEEK_NUMBER + "=" + week;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean isTodayHoliday(){
        boolean result = false;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_DATE + "='" + sdf.format(calendar.getTime()) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            if(c.getInt(c.getColumnIndex(SWIPE_HOLIDAY)) == 0){
                result = false;
            }else{
                result = true;
            }
        }
        return result;
    }

    public boolean isTodayCheckInDone(){
        boolean result = false;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_DATE + "='" + sdf.format(calendar.getTime()) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            if(c.getInt(c.getColumnIndex(SWIPE_IN_TIME)) == 0){
                result = false;
            }else{
                result = true;
            }
        }
        return result;
    }

    public boolean isTodayCheckOutDone(){
        boolean result = false;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_DATE + "='" + sdf.format(calendar.getTime()) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            if(c.getInt(c.getColumnIndex(SWIPE_OUT_TIME)) == 0){
                result = false;
            }else{
                result = true;
            }
        }
        return result;
    }

    public boolean isSwipeDateHoliday(String swipeDate){
        boolean result = false;
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_DATE + "='" + swipeDate + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            if(c.getInt(c.getColumnIndex(SWIPE_HOLIDAY)) == 1){
                result = true;
            }else{
                result = false;
            }
        }
        return result;
    }

    public void initializeWeekData(int weekNumber, int year, int dayStart, int numberOfDays){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_WEEK, dayStart);
        for(int i = 0 ; i < numberOfDays ; i++){
            if(i != 0){ calendar.add(Calendar.DATE, 1);}
            addSwipeData(sdf.format(calendar.getTime()), weekNumber, 0 , 0);
        }
    }

    public List<SwipeData> getSwipeData(int week) {
        List<SwipeData> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_WEEK_NUMBER + "=" + week;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                SwipeData swipe =  new SwipeData();
                swipe.setSwipeDate(c.getString(c.getColumnIndex(SWIPE_DATE)));
                swipe.setSwipeInTime(c.getLong(c.getColumnIndex(SWIPE_IN_TIME)));
                swipe.setSwipeOutTime(c.getLong(c.getColumnIndex(SWIPE_OUT_TIME)));
                swipe.setHoliday(c.getInt(c.getColumnIndex(SWIPE_HOLIDAY)));
                swipe.setReminder(c.getInt(c.getColumnIndex(SWIPE_REMINDER)));
                items.add(swipe);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return items;
    }

    public int getWhichDay(int week, String swipeDate) {
        int which = 0;
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_WEEK_NUMBER + "=" + week;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                which = which + 1;
                if(c.getString(c.getColumnIndex(SWIPE_DATE)).equals(swipeDate)){
                   break;
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return which;
    }

    public SwipeData getOneSwipeData(String swipeDate){
        SwipeData swipe =  new SwipeData();
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_DATE + "='" + swipeDate + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
                swipe.setSwipeDate(c.getString(c.getColumnIndex(SWIPE_DATE)));
                swipe.setSwipeInTime(c.getLong(c.getColumnIndex(SWIPE_IN_TIME)));
                swipe.setSwipeOutTime(c.getLong(c.getColumnIndex(SWIPE_OUT_TIME)));
                swipe.setHoliday(c.getInt(c.getColumnIndex(SWIPE_HOLIDAY)));
                swipe.setReminder(c.getInt(c.getColumnIndex(SWIPE_REMINDER)));
        }
        c.close();
        db.close();
        return swipe;
    }

    public Long getWeeklyAverage(int week) {
        int count = 0;
        Long totalDiffTime = 0L;
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_WEEK_NUMBER + "=" + week;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                if(c.getInt(c.getColumnIndex(SWIPE_HOLIDAY))!=1) {
                    if (c.getLong(c.getColumnIndex(SWIPE_IN_TIME)) != 0 && c.getLong(c.getColumnIndex(SWIPE_OUT_TIME)) != 0) {
                        totalDiffTime = totalDiffTime + (c.getLong(c.getColumnIndex(SWIPE_OUT_TIME)) - c.getLong(c.getColumnIndex(SWIPE_IN_TIME)));
                        count = count + 1;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        if(count == 0){
            return 0L;
        }else{
            return totalDiffTime/count;
        }
    }

    public Long calculateTodayTarget(int week) {
        int count = 0;
        Long totalDiffTime = 0L;
        Long target = 0L;
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_WEEK_NUMBER + "=" + week;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                if(c.getInt(c.getColumnIndex(SWIPE_HOLIDAY))!=1) {
                    if (c.getLong(c.getColumnIndex(SWIPE_IN_TIME)) != 0 && c.getLong(c.getColumnIndex(SWIPE_OUT_TIME)) != 0) {
                        totalDiffTime = totalDiffTime + (c.getLong(c.getColumnIndex(SWIPE_OUT_TIME)) - c.getLong(c.getColumnIndex(SWIPE_IN_TIME)));
                        count = count + 1;
                    }
                }
            } while (c.moveToNext());
        }
        if(count == 0){
            return 0L;
        }else{
            int temp = c.getCount() - count;
            if(temp == 0) {
                target = getWeeklyAverage(week);
            }else {
                Long configAverage = preferences.getLong(AppConstants.PREF_AVG_SWIPE_MILLIS, 0L);
                target = ((configAverage * c.getCount()) - totalDiffTime) / temp;
            }
        }
        c.close();
        db.close();
        return target;
    }

    public void updateSwipeInTime(String swipeDate, Long timeInMillis) {
        String Query = "update swipe set intime =" + timeInMillis + " where swipedate='" + swipeDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
        db.close();
    }

    public void updateSwipeOutTime(String swipeDate, Long timeInMillis) {
        String Query = "update swipe set outtime =" + timeInMillis + " where swipedate='" + swipeDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
        db.close();
    }

    public void updateSwipeDateAsHoliday(String swipeDate) {
        String Query = "update swipe set holiday = 1,intime = 0,outtime = 0 where swipedate='" + swipeDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
        db.close();
    }

    public void updateSwipeDateAsWorkDay(String swipeDate) {
        String Query = "update swipe set holiday = 0 where swipedate='" + swipeDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
        db.close();
    }

    public void updateSwipeDateReminder(String swipeDate, int reminder) {
        String Query = "update swipe set reminder = "+reminder+" where swipedate='" + swipeDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
        db.close();
    }

}
