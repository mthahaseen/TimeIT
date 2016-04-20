package com.overclocked.timeit.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.overclocked.timeit.model.Days;

import java.util.ArrayList;
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

    // Days Table Columns names
    private static final String DAY_ID = "id";
    private static final String DAY_NAME = "name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_DAYS + "("
                + DAY_ID + " INTEGER,"
                + DAY_NAME + " TEXT)";

        db.execSQL(CREATE_CATEGORY_TABLE);

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

}
