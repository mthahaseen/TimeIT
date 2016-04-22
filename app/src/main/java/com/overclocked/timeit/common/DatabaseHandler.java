package com.overclocked.timeit.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                + SWIPE_OUT_TIME + " INTEGER)";

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

    public void initializeWeekData(int weekNumber, int dayStart, int numberOfDays){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        calendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
        calendar.set(Calendar.DAY_OF_WEEK, dayStart);
        for(int i = 0 ; i < numberOfDays ; i++){
            if(i != 0){ calendar.add(Calendar.DATE, 1);}
            addSwipeData(sdf.format(calendar.getTime()), weekNumber, 0 , 0);
        }
    }

    public List<SwipeData> getSwipeData(int week)
    {
        List<SwipeData> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SWIPE + " where " + SWIPE_WEEK_NUMBER + "=" + week;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                SwipeData swipe =  new SwipeData();
                swipe.setSwipeDate(c.getString(c.getColumnIndex(SWIPE_DATE)));
                items.add(swipe);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return items;
    }

}
