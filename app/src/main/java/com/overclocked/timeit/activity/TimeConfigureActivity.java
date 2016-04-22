package com.overclocked.timeit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.fragments.DaysDialogFragment;
import com.overclocked.timeit.fragments.TimePickerDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeConfigureActivity extends AppCompatActivity implements DaysDialogFragment.onDaySelectListener, TimePickerDialogFragment.onTimeSelectedListener {

    @Bind(R.id.txtSwipeHours) TextView txtSwipeHours;
    @Bind(R.id.txtSwipeMinutes) TextView txtSwipeMinutes;
    @Bind(R.id.txtWorkDayStart) TextView txtWorkDayStart;
    @Bind(R.id.txtWorkDayEnd) TextView txtWorkDayEnd;
    @Bind(R.id.editInTime) ImageView editInTime;
    @Bind(R.id.editOutTime) ImageView editOutTime;
    @Bind(R.id.editStartDay) ImageView editStartDay;
    @Bind(R.id.editEndDay) ImageView editEndDay;
    @Bind(R.id.txtInTime) TextView txtInTime;
    @Bind(R.id.txtOutTime) TextView txtOutTime;
    @Bind(R.id.btnTimeConfigDone) Button btnTimeConfigDone;
    @Bind(R.id.toolbar) Toolbar toolbar;

    int dayStart = 2;
    int dayEnd = 6;

    int officeInHour = 9;
    int officeInMinute = 30;
    int officeOutHour = 18;
    int officeOutMinute = 30;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_configure);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Time Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(TimeConfigureActivity.this);
        txtWorkDayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDay();
            }
        });
        txtWorkDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDay();
            }
        });
        editStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDay();
            }
        });
        editEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDay();
            }
        });
        txtInTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officeInTime();
            }
        });
        txtOutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officeOutTime();
            }
        });
        editInTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officeInTime();
            }
        });
        editOutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officeOutTime();
            }
        });
        btnTimeConfigDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtInTime.getText().toString().equals("Set Time")){
                    Toast.makeText(TimeConfigureActivity.this,"Set your approximate office in time",Toast.LENGTH_SHORT).show();
                }else if(txtOutTime.getText().toString().equals("Set Time")){
                    Toast.makeText(TimeConfigureActivity.this,"Set your approximate office out time",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(AppConstants.PREF_INITIAL_TIME_CONFIG, true);
                    editor.putLong(AppConstants.PREF_AVG_SWIPE_MILLIS, AppUtil.convertHoursMinutesToMillis(Integer.parseInt(txtSwipeHours.getText().toString()), Integer.parseInt(txtSwipeMinutes.getText().toString())));
                    editor.putInt(AppConstants.PREF_START_DAY, dayStart);
                    editor.putInt(AppConstants.PREF_END_DAY, dayEnd);
                    editor.putLong(AppConstants.PREF_APPROX_OFFICE_IN_TIME_MILLIS, AppUtil.convertHoursMinutesToMillis(officeInHour, officeInMinute));
                    editor.putLong(AppConstants.PREF_APPROX_OFFICE_OUT_TIME_MILLIS, AppUtil.convertHoursMinutesToMillis(officeOutHour, officeOutMinute));
                    editor.putInt(AppConstants.PREF_DAY_DIFFERENCE, getDaysDifference(dayStart, dayEnd));
                    editor.commit();
                    AppController.getInstance().getDatabaseHandler().initializeWeekData(AppUtil.getWeekNumberOfTodayDate(), dayStart, getDaysDifference(dayStart, dayEnd));
                    AppController.getInstance().getDatabaseHandler().initializeWeekData(AppUtil.getWeekNumberOfTodayDate() - 1, dayStart, getDaysDifference(dayStart, dayEnd));
                    AppController.getInstance().getDatabaseHandler().initializeWeekData(AppUtil.getWeekNumberOfTodayDate() + 1, dayStart, getDaysDifference(dayStart, dayEnd));
                    Intent intent = new Intent(TimeConfigureActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void officeOutTime(){
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt("hour", officeOutHour);
        args.putInt("minute", officeOutMinute);
        args.putBoolean("isInTime", false);
        timePickerDialogFragment.setArguments(args);
        timePickerDialogFragment.show(getSupportFragmentManager(), "timerDialog");
    }

    public void officeInTime(){
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt("hour", officeInHour);
        args.putInt("minute", officeInMinute);
        args.putBoolean("isInTime", true);
        timePickerDialogFragment.setArguments(args);
        timePickerDialogFragment.show(getSupportFragmentManager(), "timerDialog");
    }

    public void startDay(){
        DaysDialogFragment dialog = new DaysDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("isStart", true);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "daysDialog");
    }

    public void endDay(){
        DaysDialogFragment dialog = new DaysDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("isStart", false);
        args.putInt("exclude", dayStart);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),"daysDialog");
    }

    public void averageHoursClick(View v){
        int hours = Integer.parseInt(txtSwipeHours.getText().toString());
        if(v.getId() == R.id.btnIncreaseSwipeHours){
            if(hours < AppConstants.MAX_AVG_SWIPE_HOURS){txtSwipeHours.setText(String.valueOf(hours+1));}
        }else if(v.getId() == R.id.btnReduceSwipeHours){
            if(hours > AppConstants.MIN_AVG_SWIPE_HOURS){ txtSwipeHours.setText(String.valueOf(hours-1));}
        }
    }

    public void averageMinutesClick(View v){
        int hours = Integer.parseInt(txtSwipeHours.getText().toString());
        int minutes = Integer.parseInt(txtSwipeMinutes.getText().toString());
        if(v.getId() == R.id.btnIncreaseSwipeMinutes){
            if(minutes < AppConstants.MAX_AVG_SWIPE_MINUTES){txtSwipeMinutes.setText(String.valueOf(minutes+1));}
            if(minutes == AppConstants.MAX_AVG_SWIPE_MINUTES && hours < AppConstants.MAX_AVG_SWIPE_HOURS){
                txtSwipeHours.setText(String.valueOf(hours+1));
                txtSwipeMinutes.setText(String.valueOf(0));
            }
        }else if(v.getId() == R.id.btnReduceSwipeMinutes){
            if(minutes > AppConstants.MIN_AVG_SWIPE_MINUTES){ txtSwipeMinutes.setText(String.valueOf(minutes-1));}
            if(minutes == AppConstants.MIN_AVG_SWIPE_MINUTES && hours > AppConstants.MIN_AVG_SWIPE_HOURS){
                txtSwipeHours.setText(String.valueOf(hours-1));
                txtSwipeMinutes.setText(String.valueOf(59));
            }
        }
    }

    @Override
    public void onDaySelected(int dayId, String dayName, boolean isStart){
        if(isStart) {
            dayStart = dayId;
            txtWorkDayStart.setText(dayName);
            if(dayEnd == dayId){
                if(dayId == 7){
                    txtWorkDayEnd.setText(AppController.getInstance().getDatabaseHandler().getDay(1));
                    dayEnd = 1;
                }else{
                    txtWorkDayEnd.setText(AppController.getInstance().getDatabaseHandler().getDay(dayId+1));
                    dayEnd = dayId + 1;
                }
            }
        }else{
            if(dayStart == dayId){
                if(dayId == 1){
                    txtWorkDayEnd.setText(AppController.getInstance().getDatabaseHandler().getDay(7));
                    dayEnd = 7;
                }else{
                    txtWorkDayEnd.setText(AppController.getInstance().getDatabaseHandler().getDay(dayId-1));
                    dayEnd = dayId - 1;
                }
            }else{
                dayEnd = dayId;
                txtWorkDayEnd.setText(dayName);
            }
        }
        //Toast.makeText(TimeConfigureActivity.this, "Work Days: "+getDaysDifference(dayStart,dayEnd), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSelected(int hour, int minute, boolean isInTime){
        if(isInTime){
            officeInHour = hour;
            officeInMinute = minute;
        }else{
            officeOutHour = hour;
            officeOutMinute = minute;
        }
        String newTime = AppUtil.showTimeInTwelveHours(hour, minute);
        if(isInTime){
            txtInTime.setText(newTime);
        }else{
            txtOutTime.setText(newTime);
        }
    }

    public int getDaysDifference(int start, int end){
        if(start < end){
            return (end - start) + 1;
        }else{
            return  (7 - start) + 1 + end;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TimeConfigureActivity.this, CompanySelectActivity.class);
        startActivity(intent);
        finish();
    }
}
