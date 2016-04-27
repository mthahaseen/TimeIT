package com.overclocked.timeit.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.overclocked.timeit.R;
import com.overclocked.timeit.activity.HomeActivity;
import com.overclocked.timeit.activity.TimeActivity;
import com.overclocked.timeit.common.AppUtil;
import com.overclocked.timeit.model.SwipeData;

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

    public RecyclerViewSwipeDataAdapter(Context context, List<SwipeData> itemList) {
        this.lstSwipeData = itemList;
        this.mContext = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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
        if(item.getSwipeInTime() == 0 && item.getSwipeOutTime() == 0){
            viewHolder.txtSwipeDifference.setText("00 h 00 m");
            viewHolder.txtSwipeStatus.setBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }else if(item.getSwipeInTime() != 0 && item.getSwipeOutTime() == 0){
            Calendar calendar = Calendar.getInstance();
            Long diff = calendar.getTimeInMillis() - item.getSwipeInTime();
            viewHolder.txtSwipeDifference.setText(AppUtil.convertMillisToHours(diff) + " h " +
            AppUtil.convertMillisToMinutes(diff) + " m");
            viewHolder.txtSwipeStatus.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_orange_light));
        }else if(item.getSwipeInTime() != 0 && item.getSwipeOutTime() != 0){
            Long diff = item.getSwipeOutTime() - item.getSwipeInTime();
            viewHolder.txtSwipeDifference.setText(AppUtil.convertMillisToHours(diff) + " h " +
                    AppUtil.convertMillisToMinutes(diff) + " m");
            viewHolder.txtSwipeStatus.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return lstSwipeData.size();
    }

    public class SwipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.txtSwipeDate) protected TextView txtSwipeDate;
        @Bind(R.id.txtSwipeInTime) protected  TextView txtSwipeInTime;
        @Bind(R.id.txtSwipeOutTime) protected  TextView txtSwipeOutTime;
        @Bind(R.id.txtSwipeDifference) protected TextView txtSwipeDifference;
        @Bind(R.id.txtSwipeStatus) protected TextView txtSwipeStatus;

        public SwipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(mContext, TimeActivity.class);
            i.putExtra("swipeData",lstSwipeData.get(getAdapterPosition()));
            mContext.startActivity(i);
            ((HomeActivity) (mContext)).finish();
        }
    }

}
