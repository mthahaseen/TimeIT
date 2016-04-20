package com.overclocked.timeit.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.adapter.DaysAdapter;
import com.overclocked.timeit.model.Days;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thahaseen on 4/20/2016.
 */
public class DaysDialogFragment extends DialogFragment {

    ListView mylist;
    boolean isStart = false;
    List<Days> lstDays = new ArrayList<>();
    int dayStart = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.days_dialog_fragment, null, false);
        mylist = (ListView) view.findViewById(R.id.listDays);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isStart = getArguments().getBoolean("isStart");
        if(!isStart){
            dayStart = getArguments().getInt("exclude");
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!isStart){
            lstDays = AppController.getInstance().getDatabaseHandler().getDays(dayStart);
        }else{
            lstDays = AppController.getInstance().getDatabaseHandler().getDays(0);
        }
        DaysAdapter adapter = new DaysAdapter(getActivity(), lstDays);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                onDaySelectListener listener = (onDaySelectListener) getActivity();
                listener.onDaySelected(lstDays.get(i).getDayId(),lstDays.get(i).getDayName(),isStart);
            }
        });
    }

    public interface onDaySelectListener {
        void onDaySelected(int dayId, String dayName, boolean isStart);
    }
}
