package com.overclocked.timeit.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.fragments.TimePickerDialogFragment;
import com.overclocked.timeit.model.SwipeData;

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

    int inHour = 0;
    int inMinutes = 0;
    int outHour = 0;
    int outMinutes = 0;

    SwipeData swipeData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        swipeData = getIntent().getExtras().getParcelable("swipeData");
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
        updateTimeDiff();
        inHour = (int)(swipeData.getSwipeInTime()/(1000*3600));
        inMinutes = (int) ((swipeData.getSwipeInTime()/60)%60);
        outHour = (int)(swipeData.getSwipeOutTime()/(1000*3600));
        outMinutes = (int) ((swipeData.getSwipeOutTime()/60)%60);
        imgEditSwipeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                Bundle args = new Bundle();
                args.putInt("hour", outHour);
                args.putInt("minute", outMinutes);
                args.putBoolean("isInTime", false);
                timePickerDialogFragment.setArguments(args);
                timePickerDialogFragment.show(getSupportFragmentManager(), "timerDialog");
            }
        });
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
    }

    public void updateTimeDiff(){
        if(swipeData.getSwipeInTime() == 0 && swipeData.getSwipeOutTime() == 0){
            txtAvgHours.setText("00");
            txtAvgMinutes.setText("00");
        }else if(swipeData.getSwipeInTime() != 0 && swipeData.getSwipeOutTime() == 0){
            Calendar calendar = Calendar.getInstance();
            Long diff = calendar.getTimeInMillis() - swipeData.getSwipeInTime();
            txtAvgHours.setText(AppUtil.convertMillisToHours(diff));
            txtAvgMinutes.setText(AppUtil.convertMillisToMinutes(diff));
        }else if(swipeData.getSwipeInTime() != 0 && swipeData.getSwipeOutTime() != 0){
            Long diff = swipeData.getSwipeOutTime() - swipeData.getSwipeInTime();
            txtAvgHours.setText(AppUtil.convertMillisToHours(diff));
            txtAvgMinutes.setText(AppUtil.convertMillisToMinutes(diff));
        }
    }
}
