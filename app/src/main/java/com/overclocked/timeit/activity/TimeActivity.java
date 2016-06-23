package com.overclocked.timeit.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.Localytics;
import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.fragments.TimePickerDialogFragment;
import com.overclocked.timeit.model.SwipeData;
import com.overclocked.timeit.receiver.NotificationPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeActivity extends AppCompatActivity implements TimePickerDialogFragment.onTimeSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.txtSwipeInTime) TextView txtSwipeInTime;
    @Bind(R.id.txtSwipeOutTime) TextView txtSwipeOutTime;
    @Bind(R.id.txtAvgHours) TextView txtAvgHours;
    @Bind(R.id.txtAvgMinutes) TextView txtAvgMinutes;
    @Bind(R.id.imgEditSwipeIn) ImageView imgEditSwipeIn;
    @Bind(R.id.imgEditSwipeOut) ImageView imgEditSwipeOut;
    @Bind(R.id.btnReminder) Button btnReminder;

    int inHour = 0;
    int inMinutes = 0;
    int outHour = 0;
    int outMinutes = 0;
    SharedPreferences preferences;
    SwipeData swipeData;
    int weekNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(TimeActivity.this);
        swipeData = getIntent().getExtras().getParcelable("swipeData");
        weekNumber = getIntent().getExtras().getInt("weekNumber");
        getSupportActionBar().setTitle(swipeData.getSwipeDate());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#009688"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#004D40"));
        }
        if(swipeData.getSwipeInTime() == 0){
            txtSwipeInTime.setText("-- : --");
        }else{
            txtSwipeInTime.setText(AppUtil.convertMillisToHoursMinutes(swipeData.getSwipeInTime()));
        }
        if(swipeData.getSwipeOutTime() == 0){
            txtSwipeOutTime.setText("-- : --");
        }else{
            txtSwipeOutTime.setText(AppUtil.convertMillisToHoursMinutes(swipeData.getSwipeOutTime()));
        }
        Localytics.tagScreen(AppConstants.LOCALYTICS_TAG_SCREEN_TIME_EDIT);
        updateTimeDiff();
        if(swipeData.getSwipeInTime() != 0) {
            inHour = AppUtil.convertSwipeTimeToHours(swipeData.getSwipeInTime());
            inMinutes = AppUtil.convertSwipeTimeToMinutes(swipeData.getSwipeInTime());
        }
        if(swipeData.getSwipeOutTime() != 0) {
            outHour = AppUtil.convertSwipeTimeToHours(swipeData.getSwipeOutTime());
            outMinutes = AppUtil.convertSwipeTimeToMinutes(swipeData.getSwipeOutTime());
        }
        imgEditSwipeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_IN_TIME_EDIT_CLICK);
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                Bundle args = new Bundle();
                args.putInt("hour", inHour);
                args.putInt("minute", inMinutes);
                args.putBoolean("isInTime", true);
                timePickerDialogFragment.setArguments(args);
                timePickerDialogFragment.show(getSupportFragmentManager(), "timerDialog");
            }
        });
        imgEditSwipeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_OUT_TIME_EDIT_CLICK);
                if(!txtSwipeInTime.getText().toString().equals("-- : --")) {
                    TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("hour", outHour);
                    args.putInt("minute", outMinutes);
                    args.putBoolean("isInTime", false);
                    timePickerDialogFragment.setArguments(args);
                    timePickerDialogFragment.show(getSupportFragmentManager(), "timerDialog");
                }else{
                    Toast.makeText(TimeActivity.this,"Select Swipe In time first.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        final Calendar calendar = Calendar.getInstance();
        if(swipeData.getSwipeDate().equals(AppUtil.getDateAsText(calendar))){
            btnReminder.setVisibility(View.VISIBLE);
            if (swipeData.getReminder() == 1) {
                btnReminder.setText("Remove Swipe Out Reminder");
                btnReminder.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_clear_white_24dp), null, null, null);
            } else {
                btnReminder.setText("Set Swipe Out Reminder");
                btnReminder.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_done_white_24dp), null, null, null);
            }
        }else{
            btnReminder.setVisibility(View.GONE);
        }
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            if (swipeData.getSwipeInTime() != 0) {
                final Calendar calendar = Calendar.getInstance();
                if (swipeData.getReminder() == 1) {
                    AppController.getInstance().getDatabaseHandler().updateSwipeDateReminder(swipeData.getSwipeDate(), 0);
                    cancelCheckOutNotification();
                    btnReminder.setText("Set Swipe Out Reminder");
                    btnReminder.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_done_white_24dp), null, null, null);
                } else {
                    if(swipeData.getSwipeOutTime()!=0 && swipeData.getSwipeOutTime() < calendar.getTimeInMillis()){
                        Toast.makeText(TimeActivity.this,"Omg! You have elapsed swipe out time.",Toast.LENGTH_SHORT).show();
                    }else {
                        scheduleCheckOutNotification();
                        AppController.getInstance().getDatabaseHandler().updateSwipeDateReminder(swipeData.getSwipeDate(), 1);
                        btnReminder.setText("Remove Swipe Out Reminder");
                        btnReminder.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_clear_white_24dp), null, null, null);
                    }
                }
                swipeData = AppController.getInstance().getDatabaseHandler().getOneSwipeData(swipeData.getSwipeDate());
            }else{
                Toast.makeText(TimeActivity.this,"Select Swipe In time first",Toast.LENGTH_SHORT).show();
            }
        }});
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
        Intent i = new Intent(TimeActivity.this,HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onTimeSelected(int hour, int minute, boolean isInTime){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        try {
            calendar.setTime(sdf.parse(swipeData.getSwipeDate()));
        }catch (ParseException e){
            e.printStackTrace();
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        if(isInTime){
            inHour = hour;
            inMinutes = minute;
            AppController.getInstance().getDatabaseHandler().updateSwipeInTime(swipeData.getSwipeDate(), calendar.getTimeInMillis());
        }else{
            outHour = hour;
            outMinutes = minute;
            AppController.getInstance().getDatabaseHandler().updateSwipeOutTime(swipeData.getSwipeDate(), calendar.getTimeInMillis());
        }
        String newTime = AppUtil.showTimeInTwelveHours(hour, minute);
        if(isInTime){
            txtSwipeInTime.setText(newTime);
        }else{
            txtSwipeOutTime.setText(newTime);
        }
        swipeData = AppController.getInstance().getDatabaseHandler().getOneSwipeData(swipeData.getSwipeDate());
        updateTimeDiff();
        if(swipeData.getReminder() == 1) {
            updateNotification();
        }
    }

    public void updateTimeDiff(){
        if(swipeData.getSwipeInTime() == 0 && swipeData.getSwipeOutTime() == 0){
            txtAvgHours.setText("00");
            txtAvgMinutes.setText("00");
        }else if(swipeData.getSwipeInTime() != 0 && swipeData.getSwipeOutTime() == 0){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
            try {
                calendar.setTime(sdf.parse(swipeData.getSwipeDate()));
            }catch (ParseException e){
                e.printStackTrace();
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,calendar1.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE,calendar1.get(Calendar.MINUTE));
            Long diff = calendar.getTimeInMillis() - swipeData.getSwipeInTime();
            txtAvgHours.setText(AppUtil.convertMillisToHours(diff));
            txtAvgMinutes.setText(AppUtil.convertMillisToMinutes(diff));
        }else if(swipeData.getSwipeInTime() != 0 && swipeData.getSwipeOutTime() != 0){
            Long diff = swipeData.getSwipeOutTime() - swipeData.getSwipeInTime();
            txtAvgHours.setText(AppUtil.convertMillisToHours(diff));
            txtAvgMinutes.setText(AppUtil.convertMillisToMinutes(diff));
        }
    }

    public void updateNotification(){
        if(swipeData.getSwipeInTime() !=0) {
            cancelCheckOutNotification();
            scheduleCheckOutNotification();
        }
    }

    private void scheduleCheckOutNotification() {
        Calendar calendar = Calendar.getInstance();
        long futureInMillis =  SystemClock.elapsedRealtime() + getCountDownTimeInMillis(AppUtil.getDateAsText(calendar));
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,"swipeOut");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 85128 , notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void cancelCheckOutNotification(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,"swipeOut");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 85128, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public Long getCountDownTimeInMillis(String SwipeDate){
        Long count = 0L;
        SwipeData item = AppController.getInstance().getDatabaseHandler().getOneSwipeData(SwipeDate);
        if (item.getSwipeInTime() != 0 && item.getSwipeOutTime() == 0) {
            Long dailyTargetMillis = AppController.getInstance().getDatabaseHandler().calculateTodayTarget(weekNumber);
            Calendar calendar = Calendar.getInstance();
            Long diff = calendar.getTimeInMillis() - item.getSwipeInTime();
            if(dailyTargetMillis!= 0L) {
                count = dailyTargetMillis - diff;
            }else{
                count = preferences.getLong(AppConstants.PREF_AVG_SWIPE_MILLIS,0L) - diff;
            }
        }
        return count;
    }
}
