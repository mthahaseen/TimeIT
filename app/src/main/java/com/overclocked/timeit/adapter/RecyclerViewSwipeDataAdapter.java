package com.overclocked.timeit.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.overclocked.timeit.R;
import com.overclocked.timeit.model.SwipeData;

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

    }

    @Override
    public int getItemCount() {
        return (null != lstSwipeData ? lstSwipeData.size() : 0);
    }

    public class SwipeViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.txtSwipeDate) protected TextView txtSwipeDate;

        public SwipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
