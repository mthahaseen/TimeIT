package com.overclocked.timeit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.fragments.DaysDialogFragment;
import com.overclocked.timeit.fragments.TimePickerDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeConfigureActivity extends FragmentActivity implements DaysDialogFragment.onDaySelectListener, TimePickerDialogFragment.onTimeSelectedListener {

    @Bind(R.id.txtSwipeHours) TextView txtSwipeHours;
    @Bind(R.id.txtSwipeMinutes) TextView txtSwipeMinutes;
    @Bind(R.id.txtWorkDayStart) TextView txtWorkDayStart;
    @Bind(R.id.txtWorkDayEnd) TextView txtWorkDayEnd;
    @Bind(R.id.editInTime) Button editInTime;
    @Bind(R.id.editOutTime) Button editOutTime;
    @Bind(R.id.txtInTime) TextView txtInTime;
    @Bind(R.id.txtOutTime) TextView txtOutTime;

    int dayStart = 2;
    int dayEnd = 6;

    int officeInHour = 9;
    int officeInMinute = 30;
    int officeOutHour = 18;
    int officeOutMinute = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_configure);
        ButterKnife.bind(this);
        txtWorkDayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaysDialogFragment dialog = new DaysDialogFragment();
                Bundle args = new Bundle();
                args.putBoolean("isStart", true);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "daysDialog");
            }
        });
        txtWorkDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaysDialogFragment dialog = new DaysDialogFragment();
                Bundle args = new Bundle();
                args.putBoolean("isStart", false);
                args.putInt("exclude", dayStart);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(),"daysDialog");
            }
        });
        editInTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                Bundle args = new Bundle();
                args.putInt("hour",officeInHour);
                args.putInt("minute", officeInMinute);
                args.putBoolean("isInTime", true);
                timePickerDialogFragment.setArguments(args);
                timePickerDialogFragment.show(getSupportFragmentManager(),"timerDialog");
            }
        });
        editOutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                Bundle args = new Bundle();
                args.putInt("hour",officeOutHour);
                args.putInt("minute",officeOutMinute);
                args.putBoolean("isInTime", false);
                timePickerDialogFragment.setArguments(args);
                timePickerDialogFragment.show(getSupportFragmentManager(),"timerDialog");
            }
        });
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
        Toast.makeText(TimeConfigureActivity.this, "Work Days: "+getDaysDifference(dayStart,dayEnd), Toast.LENGTH_SHORT).show();
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
        showTime(hour,minute,isInTime);
    }

    public int getDaysDifference(int start, int end){
        if(start < end){
            return (end - start) + 1;
        }else{
            return  (7 - start) + 1 + end;
        }
    }

    public void showTime(int hour, int min , boolean isInTime) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        String minutes;
        if(String.valueOf(min).length()==1){
            minutes = "0" + min;
        }else{
            minutes = ""+min;
        }
        if(isInTime) {
            txtInTime.setText(new StringBuilder().append(hour).append(" : ").append(minutes)
                    .append(" ").append(format));
        }else{
            txtOutTime.setText(new StringBuilder().append(hour).append(" : ").append(minutes)
                    .append(" ").append(format));
        }
    }

}
