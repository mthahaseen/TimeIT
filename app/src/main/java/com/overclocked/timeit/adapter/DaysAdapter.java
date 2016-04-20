package com.overclocked.timeit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.overclocked.timeit.model.Days;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Thahaseen on 4/20/2016.
 */
public class DaysAdapter  extends ArrayAdapter<Days> {

    List<Days> lstdays;
    Context context;

    public DaysAdapter(Context context, List<Days> items){
        super(context, android.R.layout.simple_list_item_1, items);
        this.lstdays = items;
        this.context = context;
    }

    class ViewHolder {
        @Bind(android.R.id.text1) TextView txtDayName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final Days rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            ButterKnife.bind(holder, convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDayName.setText(rowItem.getDayName());

        return convertView;
    }

}
