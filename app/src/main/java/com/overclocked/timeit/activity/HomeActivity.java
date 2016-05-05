package com.overclocked.timeit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.adapter.RecyclerViewSwipeDataAdapter;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.common.VerticalSpaceItemDecoration;
import com.overclocked.timeit.fragments.RateReviewDialogFragment;
import com.overclocked.timeit.fragments.TutorialOneDialogFragment;
import com.overclocked.timeit.fragments.TutorialTwoDialogFragment;
import com.overclocked.timeit.model.SwipeData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements RecyclerViewSwipeDataAdapter.OnLongListener, TutorialOneDialogFragment.onTutorialOneDismissListener {

    @Bind(R.id.txtToday) TextView txtToday;
    @Bind(R.id.txtTarget) TextView txtTarget;
    @Bind(R.id.txtCountDown) TextView txtCountDown;
    @Bind(R.id.lblSwipe) TextView lblSwipe;
    @Bind(R.id.txtAvgHours) TextView txtAvgHours;
    @Bind(R.id.txtAvgMinutes) TextView txtAvgMinutes;
    @Bind(R.id.txtTargetHour) TextView txtTargetHour;
    @Bind(R.id.txtTargetMinutes) TextView txtTargetMinutes;
    @Bind(R.id.recyclerViewSwipeData) RecyclerView recyclerViewSwipeData;
    @Bind(R.id.fabCheckInOut) ImageButton fabCheckInOut;
    @Bind(R.id.rlWeekend) RelativeLayout rlWeekend;
    @Bind(R.id.imgLogo) ImageView imgLogo;
    SharedPreferences preferences;
    List<SwipeData> lstSwipe = new ArrayList<>();
    RecyclerViewSwipeDataAdapter recyclerViewSwipeDataAdapter;
    int weekNumber = AppUtil.getWeekNumberOfTodayDate();
    boolean isCheckInDone = false;
    boolean isCheckOutDone = false;
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        if(preferences.getInt(AppConstants.PREF_START_DAY, 0) == 0){
            Intent i = new Intent(HomeActivity.this,IntroActivity.class);
            startActivity(i);
            finish();
        }else {
            ButterKnife.bind(this);
            final Calendar calendar = Calendar.getInstance();
            txtToday.setText(AppUtil.getDateAsText(calendar));
            if(AppUtil.isTodayOutOfSelectedDays(calendar.get(Calendar.DAY_OF_WEEK),preferences.getInt(AppConstants.PREF_START_DAY, 1), preferences.getInt(AppConstants.PREF_END_DAY, 7))){
                fabCheckInOut.setImageResource(R.drawable.ic_done_all_white_48dp);
                fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_done);
                lblSwipe.setText("<3 <3");
                Long weeklyAverageMillis = preferences.getLong(AppConstants.PREF_AVG_SWIPE_MILLIS,0L);
                txtAvgHours.setText(AppUtil.convertMillisToHours(weeklyAverageMillis));
                txtAvgMinutes.setText(AppUtil.convertMillisToMinutes(weeklyAverageMillis));
                recyclerViewSwipeData.setVisibility(View.GONE);
                rlWeekend.setVisibility(View.VISIBLE);
                txtTargetHour.setText("<3");
                txtTargetMinutes.setText("<3");
                txtTarget.setText("Happy Weekend");
            }else {
                rlWeekend.setVisibility(View.GONE);
                recyclerViewSwipeData.setVisibility(View.VISIBLE);
                if (calendar.get(Calendar.DAY_OF_WEEK) < preferences.getInt(AppConstants.PREF_START_DAY, 1)) {
                    weekNumber = weekNumber - 1;
                    if (!AppController.getInstance().getDatabaseHandler().isWeekEntryAvailable(weekNumber)) {
                        AppController.getInstance().getDatabaseHandler().initializeWeekData(weekNumber, calendar.get(Calendar.YEAR), preferences.getInt(AppConstants.PREF_START_DAY, 1), AppUtil.getDaysDifference(preferences.getInt(AppConstants.PREF_START_DAY, 1), preferences.getInt(AppConstants.PREF_END_DAY, 7)));
                    }
                    lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
                } else {
                    if (!AppController.getInstance().getDatabaseHandler().isWeekEntryAvailable(weekNumber)) {
                        AppController.getInstance().getDatabaseHandler().initializeWeekData(weekNumber, calendar.get(Calendar.YEAR), preferences.getInt(AppConstants.PREF_START_DAY, 1), AppUtil.getDaysDifference(preferences.getInt(AppConstants.PREF_START_DAY, 1), preferences.getInt(AppConstants.PREF_END_DAY, 7)));
                    }
                    lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
                }
                setTopViewData();
                setWeeklyAverage();
                setDailyTarget();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
                recyclerViewSwipeData.setLayoutManager(linearLayoutManager);
                recyclerViewSwipeData.addItemDecoration(new VerticalSpaceItemDecoration(AppConstants.VERTICAL_ITEM_SPACE));
                recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe);
                recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
                recyclerViewSwipeDataAdapter.setOnLongListener(this);
                fabCheckInOut.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!AppController.getInstance().getDatabaseHandler().isTodayHoliday()) {
                            checkInOut();
                        }
                        return true;
                    }
                });
                fabCheckInOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!AppController.getInstance().getDatabaseHandler().isTodayHoliday()) {
                            if (!isCheckOutDone) {
                                showAlertDialog();
                            }
                        }
                    }
                });
            }
            if(preferences.getInt(AppConstants.PREF_TUTORIAL_ONE,0) != 1){
                TutorialOneDialogFragment tutorialOneDialogFragment = new TutorialOneDialogFragment();
                tutorialOneDialogFragment.show(getSupportFragmentManager(), "tutorialOneDialogFragment");
            }
            imgLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RateReviewDialogFragment rateReviewDialogFragment = new RateReviewDialogFragment();
                    rateReviewDialogFragment.show(getSupportFragmentManager(),"rateReviewDialogFragment");
                }
            });
        }
    }

    @Override
    public void onLongClicked(int position) {
        lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
        recyclerViewSwipeData.removeAllViews();
        recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe);
        recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
        recyclerViewSwipeDataAdapter.setOnLongListener(this);
        recyclerViewSwipeData.scrollToPosition(position);
        setTopViewData();
        setWeeklyAverage();
        setDailyTarget();
        if(AppController.getInstance().getDatabaseHandler().isTodayHoliday()) {
            if(cTimer!=null) cTimer.cancel();
            txtCountDown.setText("");
        }
    }

    @Override
    public void onTutorialOneDismissed() {
        if(preferences.getInt(AppConstants.PREF_TUTORIAL_TWO,0) != 1){
            TutorialTwoDialogFragment tutorialTwoDialogFragment = new TutorialTwoDialogFragment();
            tutorialTwoDialogFragment.show(getSupportFragmentManager(), "tutorialTwoDialogFragment");
        }
    }

    public void setTopViewData(){
        if (!AppController.getInstance().getDatabaseHandler().isTodayHoliday()) {
            if (AppController.getInstance().getDatabaseHandler().isTodayCheckInDone()) {
                if (AppController.getInstance().getDatabaseHandler().isTodayCheckOutDone()) {
                    fabCheckInOut.setImageResource(R.drawable.ic_done_all_white_48dp);
                    fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_done);
                    lblSwipe.setText(":-) :-)");
                    isCheckOutDone = true;
                } else {
                    fabCheckInOut.setImageResource(R.drawable.ic_arrow_back_white_48dp);
                    fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_out);
                    lblSwipe.setText(AppConstants.SWIPE_OUT);
                    isCheckInDone = true;
                    final Calendar calendar = Calendar.getInstance();
                    startCountDown(AppUtil.getDateAsText(calendar));
                }
            } else {
                fabCheckInOut.setImageResource(R.drawable.ic_arrow_forward_white_48dp);
                fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_in);
                lblSwipe.setText(AppConstants.SWIPE_IN);
                isCheckInDone = false;
            }
        }else{
            fabCheckInOut.setImageResource(R.drawable.ic_done_all_white_48dp);
            fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_done);
            lblSwipe.setText("Holiday");
        }
    }

    public void setWeeklyAverage(){
        Long weeklyAverageMillis = AppController.getInstance().getDatabaseHandler().getWeeklyAverage(weekNumber);
        txtAvgHours.setText(AppUtil.convertMillisToHours(weeklyAverageMillis));
        txtAvgMinutes.setText(AppUtil.convertMillisToMinutes(weeklyAverageMillis));
    }

    public void setDailyTarget(){
        Long dailyTargetMillis = AppController.getInstance().getDatabaseHandler().calculateTodayTarget(weekNumber);
        if(dailyTargetMillis != 0L) {
            txtTargetHour.setText(AppUtil.convertMillisToHours(dailyTargetMillis));
            txtTargetMinutes.setText(AppUtil.convertMillisToMinutes(dailyTargetMillis));
        }else{
            txtTargetHour.setText(AppUtil.convertMillisToHours(preferences.getLong(AppConstants.PREF_AVG_SWIPE_MILLIS,0L)));
            txtTargetMinutes.setText(AppUtil.convertMillisToMinutes(preferences.getLong(AppConstants.PREF_AVG_SWIPE_MILLIS,0L)));
        }
        txtTarget.setText("Today's Target");
        Calendar calendar = Calendar.getInstance();
        SwipeData item = AppController.getInstance().getDatabaseHandler().getOneSwipeData(AppUtil.getDateAsText(calendar));
        if(item.getSwipeInTime()!=0 && item.getSwipeOutTime()!=0){
            if(AppController.getInstance().getDatabaseHandler().getWhichDay(weekNumber,AppUtil.getDateAsText(calendar))
                    < preferences.getInt(AppConstants.PREF_DAY_DIFFERENCE,0)){
                txtTarget.setText("Tomorrow's Target");
            }else if(AppController.getInstance().getDatabaseHandler().getWhichDay(weekNumber,AppUtil.getDateAsText(calendar))
                    == preferences.getInt(AppConstants.PREF_DAY_DIFFERENCE,0)){
                txtTargetHour.setText("<3");
                txtTargetMinutes.setText("<3");
                txtTarget.setText("Happy Weekend");
            }else if(AppController.getInstance().getDatabaseHandler().getWhichDay(weekNumber,AppUtil.getDateAsText(calendar)) == 0){
                txtTargetHour.setText("<3");
                txtTargetMinutes.setText("<3");
                txtTarget.setText("Happy Weekend");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        if(!AppUtil.isTodayOutOfSelectedDays(calendar.get(Calendar.DAY_OF_WEEK),preferences.getInt(AppConstants.PREF_START_DAY, 1), preferences.getInt(AppConstants.PREF_END_DAY, 7))){
            setWeeklyAverage();
            setDailyTarget();
            lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
            recyclerViewSwipeDataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cTimer!=null)
            cTimer.cancel();
    }

    public void startCountDown(String swipeDate){
        Long count = 0L;
            SwipeData item = AppController.getInstance().getDatabaseHandler().getOneSwipeData(swipeDate);
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
            if (count != 0L) {
                cTimer = new CountDownTimer(count, 1000) {
                    public void onTick(long millisUntilFinished) {
                        txtCountDown.setText("Leave in "+AppUtil.convertMillisToHoursMinutesSeconds(millisUntilFinished));
                    }

                    public void onFinish() {

                    }
                };
                cTimer.start();
            }
    }

    public void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        if(!isCheckInDone) {
            alertDialog.setTitle("Swipe In");
            alertDialog.setMessage("Are you sure you want to swipe in now?");
            alertDialog.setIcon(R.drawable.ic_arrow_forward_black_48dp);
        }else{
            alertDialog.setTitle("Swipe Out");
            alertDialog.setMessage("Are you sure you want to swipe out now?");
            alertDialog.setIcon(R.drawable.ic_arrow_back_black_48dp);
        }
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                checkInOut();
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void checkInOut(){
        if (!isCheckOutDone) {
            Calendar calendar = Calendar.getInstance();
            final DateFormat df = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
            if (!isCheckInDone) {
                AppController.getInstance().getDatabaseHandler().updateSwipeInTime(df.format(calendar.getTime()), calendar.getTimeInMillis());
                fabCheckInOut.setImageResource(R.drawable.ic_arrow_back_white_48dp);
                fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_out);
                lblSwipe.setText(AppConstants.SWIPE_OUT);
                isCheckInDone = true;
                setWeeklyAverage();
                setDailyTarget();
                startCountDown(AppUtil.getDateAsText(calendar));
            } else {
                AppController.getInstance().getDatabaseHandler().updateSwipeOutTime(df.format(calendar.getTime()), calendar.getTimeInMillis());
                fabCheckInOut.setImageResource(R.drawable.ic_done_all_white_48dp);
                fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_done);
                lblSwipe.setText(":-) :-)");
                isCheckOutDone = true;
                setWeeklyAverage();
                setDailyTarget();
                if(cTimer!=null) cTimer.cancel();
                txtCountDown.setText("");
            }
            lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
            recyclerViewSwipeData.removeAllViews();
            recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe);
            recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
            recyclerViewSwipeDataAdapter.setOnLongListener(this);
        } else {
            Toast.makeText(HomeActivity.this, "All Caught up. Have a great day!", Toast.LENGTH_SHORT).show();
        }
    }
}
