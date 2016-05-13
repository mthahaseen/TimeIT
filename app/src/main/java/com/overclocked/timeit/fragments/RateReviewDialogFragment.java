package com.overclocked.timeit.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.localytics.android.Localytics;
import com.overclocked.timeit.R;
import com.overclocked.timeit.common.AppConstants;

/**
 * Created by Thahaseen on 5/5/2016.
 */
public class RateReviewDialogFragment extends DialogFragment {

    public RateReviewDialogFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate_review, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TextView txtRate = (TextView) view.findViewById(R.id.txtRate);
        TextView txtNoThanks = (TextView) view.findViewById(R.id.txtNoThanks);
        txtRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_RATING_NOW_CLICK);
                getDialog().dismiss();
                //Take to play store
                Intent intentRate = new Intent(Intent.ACTION_VIEW);
                intentRate.setData(Uri.parse(AppConstants.TIME_IT_MARKET_URI));
                startActivity(intentRate);
            }
        });
        txtNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_RATING_NO_THANKS_CLICK);
                getDialog().dismiss();
            }
        });
        return view;
    }
}
