package com.overclocked.timeit.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.AppUtil;

import java.util.Calendar;

/**
 * Created by Thahaseen on 5/5/2016.
 */
public class TutorialTwoDialogFragment extends DialogFragment{

    public TutorialTwoDialogFragment(){}
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_two, container);
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
        TextView txtSwipeDate = (TextView) view.findViewById(R.id.txtSwipeDate);
        final Calendar calendar = Calendar.getInstance();
        txtSwipeDate.setText(AppUtil.getDateAsText(calendar));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(AppConstants.PREF_TUTORIAL_TWO, 1);
        editor.commit();
    }
}
