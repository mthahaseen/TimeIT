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

import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppConstants;

/**
 * Created by Thahaseen on 5/5/2016.
 */
public class TutorialOneDialogFragment extends DialogFragment {

    public TutorialOneDialogFragment(){}
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_one, container);
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
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
        editor.putInt(AppConstants.PREF_TUTORIAL_ONE, 1);
        editor.commit();
        onTutorialOneDismissListener onTutorialOneDismissListener = (onTutorialOneDismissListener) getActivity();
        onTutorialOneDismissListener.onTutorialOneDismissed();
    }

    public interface onTutorialOneDismissListener {
        void onTutorialOneDismissed();
    }
}
