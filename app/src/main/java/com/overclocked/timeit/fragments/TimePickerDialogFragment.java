package com.overclocked.timeit.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

/**
 * Created by Thahaseen on 4/20/2016.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private int timeHour;
    private int timeMinute;
    boolean isInTime = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        timeHour = bundle.getInt("hour");
        timeMinute = bundle.getInt("minute");
        isInTime = bundle.getBoolean("isInTime");
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                onTimeSelectedListener listener = (onTimeSelectedListener) getActivity();
                listener.onTimeSelected(hourOfDay, minute, isInTime);
                timeHour = hourOfDay;
                timeMinute = minute;
            }
        };
        return new TimePickerDialog(getActivity(), listener, timeHour, timeMinute, false);
    }

    public interface onTimeSelectedListener {
        void onTimeSelected(int hour, int minute, boolean isInTime);
    }
}
