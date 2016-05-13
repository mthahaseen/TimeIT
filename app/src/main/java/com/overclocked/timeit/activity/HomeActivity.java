package com.overclocked.timeit.activity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.localytics.android.Localytics;
import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.adapter.RecyclerViewSwipeDataAdapter;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.common.VerticalSpaceItemDecoration;
import com.overclocked.timeit.fragments.RateReviewDialogFragment;
import com.overclocked.timeit.fragments.ShareDialogFragment;
import com.overclocked.timeit.fragments.TutorialOneDialogFragment;
import com.overclocked.timeit.fragments.TutorialTwoDialogFragment;
import com.overclocked.timeit.model.SwipeData;
import com.overclocked.timeit.receiver.NotificationPublisher;

import java.io.IOException;
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
    @Bind(R.id.imgShare) ImageView imgShare;
    @Bind(R.id.adView) AdView adView;
    SharedPreferences preferences;
    List<SwipeData> lstSwipe = new ArrayList<>();
    RecyclerViewSwipeDataAdapter recyclerViewSwipeDataAdapter;
    int weekNumber = AppUtil.getWeekNumberOfTodayDate();
    boolean isCheckInDone = false;
    boolean isCheckOutDone = false;
    CountDownTimer cTimer = null;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final String TAG = "GCM";
    Context context;
    String regid;

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
            context = getApplicationContext();
            Localytics.tagScreen(AppConstants.LOCALYTICS_TAG_SCREEN_HOME);
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
                if(AppController.getInstance().getConnectionDetector().isConnectingToInternet()){
                    AdRequest adRequest = new AdRequest.Builder()
                            .build();
                    adView.loadAd(adRequest);
                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                recyclerViewSwipeData.setPadding(15, 15, 15, 15);
                            }else{
                                recyclerViewSwipeData.setPadding(5, 5, 5, 5);
                            }
                        }
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                recyclerViewSwipeData.setPadding(15, 15, 15, 150);
                            }else {
                                recyclerViewSwipeData.setPadding(5, 5, 5, 100);
                            }
                        }
                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                            Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_ADS_CLICK);
                        }
                    });
                }
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    recyclerViewSwipeData.addItemDecoration(new VerticalSpaceItemDecoration(AppConstants.VERTICAL_ITEM_SPACE));
                }else{
                    recyclerViewSwipeData.addItemDecoration(new VerticalSpaceItemDecoration(AppConstants.VERTICAL_ITEM_SPACE_PRE_LOLLIPOP));
                }
                recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe, weekNumber);
                recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
                recyclerViewSwipeDataAdapter.setOnLongListener(this);
                fabCheckInOut.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!AppController.getInstance().getDatabaseHandler().isTodayHoliday()) {
                            if (!isCheckInDone) {
                                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SWIPE_IN_LONG_CLICK);
                            }else{
                                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SWIPE_OUT_LONG_CLICK);
                            }
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
                    Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_RATING_DIALOG_CLICK);
                    RateReviewDialogFragment rateReviewDialogFragment = new RateReviewDialogFragment();
                    rateReviewDialogFragment.show(getSupportFragmentManager(),"rateReviewDialogFragment");
                }
            });
            imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SHARING_DIALOG_CLICK);
                    ShareDialogFragment shareDialogFragment = new ShareDialogFragment();
                    shareDialogFragment.show(getSupportFragmentManager(),"shareDialogFragment");
                }
            });
            if (checkPlayServices()) {
                regid = getRegistrationId(context);
                if (regid.isEmpty()) {
                    registerInBackground();
                } else {
                    Log.i(TAG, "GCM Already registred!");
                }
            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
                Toast.makeText(this, "Install play service to proceed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLongClicked(int position) {
        lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
        recyclerViewSwipeData.removeAllViews();
        recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe, weekNumber);
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
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cTimer!=null)
            cTimer.cancel();
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    public void startCountDown(String swipeDate){
        Long count = getCountDownTimeInMillis(swipeDate);
        if (count != 0L) {
            cTimer = new CountDownTimer(count, 1000) {
                public void onTick(long millisUntilFinished) {
                    txtCountDown.setText("Leave in "+AppUtil.convertMillisToHoursMinutesSeconds(millisUntilFinished));
                }
                public void onFinish() {}
            };
            cTimer.start();
        }
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
                if (!isCheckInDone) {
                    Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SWIPE_IN_CLICK);
                }else{
                    Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SWIPE_OUT_CLICK);
                }
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
                lblSwipe.setText(AppConstants.SWIPE_OUT);
                isCheckInDone = true;
                setWeeklyAverage();
                setDailyTarget();
                startCountDown(AppUtil.getDateAsText(calendar));
                scheduleCheckOutNotification();
                AppController.getInstance().getDatabaseHandler().updateSwipeDateReminder(df.format(calendar.getTime()),1);
                ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.flipping);
                anim.setTarget(fabCheckInOut);
                anim.setDuration(1500);
                anim.start();
                fabCheckInOut.setImageResource(R.drawable.ic_arrow_back_white_48dp);
                fabCheckInOut.setBackgroundResource(R.drawable.rounded_button_swipe_out);
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
                cancelCheckOutNotification();
                AppController.getInstance().getDatabaseHandler().updateSwipeDateReminder(df.format(calendar.getTime()),0);
            }
            lstSwipe = AppController.getInstance().getDatabaseHandler().getSwipeData(weekNumber);
            recyclerViewSwipeData.removeAllViews();
            recyclerViewSwipeDataAdapter = new RecyclerViewSwipeDataAdapter(HomeActivity.this, lstSwipe, weekNumber);
            recyclerViewSwipeData.setAdapter(recyclerViewSwipeDataAdapter);
            recyclerViewSwipeDataAdapter.setOnLongListener(this);
        } else {
            Toast.makeText(HomeActivity.this, "All Caught up. Have a great day!", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleCheckOutNotification() {
        Calendar calendar = Calendar.getInstance();
        long futureInMillis = calendar.getTimeInMillis() + getCountDownTimeInMillis(AppUtil.getDateAsText(calendar));
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(HomeActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    public void registerInBackground(){
        new AsyncTask<Void, Void, String>() {
            String token = "";
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    token =
                            InstanceID.getInstance(HomeActivity.this).getToken(AppConstants.SENDER_ID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i("GCM", "Device registered, registration ID= " + token);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                Localytics.setPushRegistrationId(token);
                storeRegistrationId(token);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences(HomeActivity.this);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}
