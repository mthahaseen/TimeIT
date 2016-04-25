package com.overclocked.timeit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.adapter.RecyclerViewSwipeDataAdapter;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.model.SwipeData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.txtToday) TextView txtToday;
    @Bind(R.id.lblSwipe) TextView lblSwipe;
    @Bind(R.id.recyclerViewSwipeData) RecyclerView recyclerViewSwipeData;
    @Bind(R.id.fabCheckInOut) FloatingActionButton fabCheckInOut;
    SharedPreferences preferences;
    List<SwipeData> lstSwipe = new ArrayList<>();
    RecyclerViewSwipeDataAdapter recyclerViewSwipeDataAdapter;
    int weekNumber = AppUtil.getWeekNumberOfTodayDate();
    boolean isCheckInDone = false;
    boolean isCheckOutDone = false;
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
            final DateFormat df = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
            txtToday.setText(AppUtil.getDateAsText(calendar));
            if (calendar.get(Calendar.DAY_OF_WEEK) < preferences.getInt(AppConstants.PREF_START_DAY, 1)) {
                weekNumber = weekNumber - 1;
                if (!AppController.getInstance().getDatabaseHandler().isWeekEntryAvailable(weekNumber)) {
                    AppController.getInstance().getDatabaseHandler().initializeWeekData(weekNumber, preferences.getInt(AppConstants.PREF_START_DAY, 1), preferences.getInt(AppConstants.PREF_END_DAY, 7));
                }
                lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
            } else {
                if (!AppController.getInstance().getDatabaseHandler().isWeekEntryAvailable(weekNumber)) {
                    AppController.getInstance().getDatabaseHandler().initializeWeekData(weekNumber, preferences.getInt(AppConstants.PREF_START_DAY, 1), preferences.getInt(AppConstants.PREF_END_DAY, 7));
                }
                lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
            }
            if(AppController.getInstance().getDatabaseHandler().isTodayCheckInDone()){
                if(AppController.getInstance().getDatabaseHandler().isTodayCheckOutDone()){
                    fabCheckInOut.setImageResource(R.drawable.ic_done_all_white_48dp);
                    lblSwipe.setText("<3 <3 <3");
                    isCheckOutDone = true;
                }else {
                    fabCheckInOut.setImageResource(R.drawable.ic_arrow_back_white_48dp);
                    lblSwipe.setText(AppConstants.SWIPE_OUT);
                    isCheckInDone = true;
                }
            }else{
                fabCheckInOut.setImageResource(R.drawable.ic_arrow_forward_white_48dp);
                lblSwipe.setText(AppConstants.SWIPE_IN);
                isCheckInDone = false;
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
            recyclerViewSwipeData.setLayoutManager(linearLayoutManager);
            recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe);
            recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
            fabCheckInOut.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isCheckOutDone) {
                        Calendar calendar = Calendar.getInstance();
                        if (!isCheckInDone) {
                            AppController.getInstance().getDatabaseHandler().updateSwipeInTime(df.format(calendar.getTime()), calendar.getTimeInMillis());
                            fabCheckInOut.setImageResource(R.drawable.ic_arrow_back_white_48dp);
                            lblSwipe.setText(AppConstants.SWIPE_OUT);
                            isCheckInDone = true;
                        } else {
                            AppController.getInstance().getDatabaseHandler().updateSwipeOutTime(df.format(calendar.getTime()), calendar.getTimeInMillis());
                            fabCheckInOut.setImageResource(R.drawable.ic_done_all_white_48dp);
                            lblSwipe.setText("<3 <3 <3");
                            isCheckOutDone = true;
                        }
                        lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
                        recyclerViewSwipeData.removeAllViews();
                        recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe);
                        recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
                    } else {
                        Toast.makeText(HomeActivity.this, "All Caught up. Have a great day!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
        recyclerViewSwipeDataAdapter.notifyDataSetChanged();
    }
}
