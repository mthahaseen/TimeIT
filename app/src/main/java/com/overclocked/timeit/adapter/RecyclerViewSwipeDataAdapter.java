package com.overclocked.timeit.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.localytics.android.Localytics;
import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.activity.HomeActivity;
import com.overclocked.timeit.activity.TimeActivity;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.model.SwipeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Thahaseen on 4/22/2016.
 */
public class RecyclerViewSwipeDataAdapter extends RecyclerView.Adapter<RecyclerViewSwipeDataAdapter.SwipeViewHolder>{

    private List<SwipeData> lstSwipeData;
    private Context mContext;
    private SharedPreferences preferences;
    private OnLongListener onLongListener;
    private int weekNumber;
    private final static int FADE_DURATION = 500;// in milliseconds

    public RecyclerViewSwipeDataAdapter(Context context, List<SwipeData> itemList, int weekNumber) {
        this.lstSwipeData = itemList;
        this.mContext = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.weekNumber = weekNumber;
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public SwipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.swipe_data_item, null);
        SwipeViewHolder viewHolder = new SwipeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SwipeViewHolder viewHolder, int i) {
        SwipeData item = lstSwipeData.get(i);
        viewHolder.txtSwipeDate.setText(item.getSwipeDate());
        if(item.getSwipeDate().equals(AppUtil.getDateAsText(Calendar.getInstance()))){
            if (item.getSwipeInTime() != 0 && item.getSwipeOutTime() != 0) {
                viewHolder.imgReminder.setVisibility(View.GONE);
            }else{
                if (item.getReminder() == 1) {
                    viewHolder.imgReminder.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notifications_active_white_18dp));
                } else {
                    viewHolder.imgReminder.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notifications_off_white_18dp));
                }
            }
        }else{
            viewHolder.imgReminder.setVisibility(View.GONE);
        }
        if(item.getSwipeInTime() == 0){
            viewHolder.txtSwipeInTime.setText("-- : --");
        }else{
            viewHolder.txtSwipeInTime.setText(AppUtil.convertMillisToHoursMinutes(item.getSwipeInTime()));
        }
        if(item.getSwipeOutTime() == 0){
            viewHolder.txtSwipeOutTime.setText("-- : --");
        }else{
            viewHolder.txtSwipeOutTime.setText(AppUtil.convertMillisToHoursMinutes(item.getSwipeOutTime()));
        }
        if(item.getHoliday() == 0) {
            if (item.getSwipeInTime() == 0 && item.getSwipeOutTime() == 0) {
                viewHolder.txtSwipeDifference.setText("00 h 00 m");
                viewHolder.txtSwipeStatus.setBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
            } else if (item.getSwipeInTime() != 0 && item.getSwipeOutTime() == 0) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
                try {
                    calendar.setTime(sdf.parse(item.getSwipeDate()));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                Calendar calendar1 = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,calendar1.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE,calendar1.get(Calendar.MINUTE));
                Long diff = calendar.getTimeInMillis() - item.getSwipeInTime();
                viewHolder.txtSwipeDifference.setText(AppUtil.convertMillisToHours(diff) + " h " +
                        AppUtil.convertMillisToMinutes(diff) + " m");
                viewHolder.txtSwipeStatus.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_orange_light));
            } else if (item.getSwipeInTime() != 0 && item.getSwipeOutTime() != 0) {
                Long diff = item.getSwipeOutTime() - item.getSwipeInTime();
                viewHolder.txtSwipeDifference.setText(AppUtil.convertMillisToHours(diff) + " h " +
                        AppUtil.convertMillisToMinutes(diff) + " m");
                viewHolder.txtSwipeStatus.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
            }
            viewHolder.imgSwipeType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_access_time_white_48dp));
        }else{
            viewHolder.txtSwipeDifference.setText("HOLIDAY!!");
            viewHolder.imgSwipeType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_weekend_white_48dp));
        }
        setScaleAnimation(viewHolder.itemView);
    }

    public void setOnLongListener(OnLongListener onLongListener) {
        this.onLongListener = onLongListener;
    }

    @Override
    public int getItemCount() {
        return lstSwipeData.size();
    }

    public class SwipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        @Bind(R.id.txtSwipeDate) protected TextView txtSwipeDate;
        @Bind(R.id.txtSwipeInTime) protected  TextView txtSwipeInTime;
        @Bind(R.id.txtSwipeOutTime) protected  TextView txtSwipeOutTime;
        @Bind(R.id.txtSwipeDifference) protected TextView txtSwipeDifference;
        @Bind(R.id.txtSwipeStatus) protected TextView txtSwipeStatus;
        @Bind(R.id.imgSwipeType) protected ImageView imgSwipeType;
        @Bind(R.id.imgReminder) protected ImageView imgReminder;

        public SwipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SWIPE_DATA_CLICK);
            if(!AppController.getInstance().getDatabaseHandler().isSwipeDateHoliday(lstSwipeData.get(getAdapterPosition()).getSwipeDate())) {
                Intent i = new Intent(mContext, TimeActivity.class);
                i.putExtra("swipeData", lstSwipeData.get(getAdapterPosition()));
                i.putExtra("weekNumber", weekNumber);
                mContext.startActivity(i);
                ((HomeActivity) (mContext)).finish();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SWIPE_DATA_LONG_CLICK);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            if(!AppController.getInstance().getDatabaseHandler().isSwipeDateHoliday(lstSwipeData.get(getAdapterPosition()).getSwipeDate())) {
                alertDialog.setMessage("Mark this as holiday?");
                alertDialog.setTitle("Holiday");
                alertDialog.setIcon(R.drawable.ic_weekend_black_48dp);
            }else{
                alertDialog.setMessage("Mark this as work day?");
                alertDialog.setTitle("Work Day");
                alertDialog.setIcon(R.drawable.ic_query_builder_black_48dp);
            }
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(!AppController.getInstance().getDatabaseHandler().isSwipeDateHoliday(lstSwipeData.get(getAdapterPosition()).getSwipeDate())) {
                        AppController.getInstance().getDatabaseHandler().updateSwipeDateAsHoliday(lstSwipeData.get(getAdapterPosition()).getSwipeDate());
                    }else{
                        AppController.getInstance().getDatabaseHandler().updateSwipeDateAsWorkDay(lstSwipeData.get(getAdapterPosition()).getSwipeDate());
                    }
                    onLongListener.onLongClicked(getAdapterPosition());
                    dialog.cancel();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
            return true;
        }
    }

    public interface OnLongListener {
        void onLongClicked(int position);
    }

}
