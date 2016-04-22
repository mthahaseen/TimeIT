package com.overclocked.timeit.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.adapter.RecyclerViewCompanyAdapter;
import com.overclocked.timeit.adapter.RecyclerViewSwipeDataAdapter;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.model.SwipeData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.txtToday) TextView txtToday;
    @Bind(R.id.recyclerViewSwipeData) RecyclerView recyclerViewSwipeData;
    SharedPreferences preferences;
    List<SwipeData> lstSwipe = new ArrayList<>();
    int weekNumber = AppUtil.getWeekNumberOfTodayDate();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        ButterKnife.bind(this);
        Calendar calendar = Calendar.getInstance();
        txtToday.setText(AppUtil.getDateAsText(calendar));
        if(calendar.get(Calendar.DAY_OF_WEEK) <= preferences.getInt(AppConstants.PREF_START_DAY, 1)){
            weekNumber = weekNumber - 1;
            lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
        }else{
            lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
        }
        /*for(int i = 0 ; i < preferences.getInt(AppConstants.PREF_DAY_DIFFERENCE,2) ; i++){
            SwipeData swipeData = new SwipeData();
            if(i != 0){ calendar.add(Calendar.DATE, 1);}
            swipeData.setSwipeDate(sdf.format(calendar.getTime()));
            lstSwipe.add(swipeData);
        }*/
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(HomeActivity.this);
        recyclerViewSwipeData.setLayoutManager(linearLayoutManager);
        RecyclerViewSwipeDataAdapter recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this,lstSwipe);
        recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
        //Toast.makeText(HomeActivity.this,String.valueOf(AppController.getInstance().getDatabaseHandler().isWeekEntryAvailable(weekNumber)),Toast.LENGTH_SHORT).show();
    }


}
