package com.overclocked.timeit.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.overclocked.timeit.R;

/**
 * Created by Thahaseen on 5/5/2016.
 */
public class RateReviewDialogFragment extends DialogFragment {

    public RateReviewDialogFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate_review, container);
        TextView txtRate = (TextView) view.findViewById(R.id.txtRate);
        TextView txtNoThanks = (TextView) view.findViewById(R.id.txtNoThanks);
        txtRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                //Take to play store
            }
        });
        txtNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }
}
