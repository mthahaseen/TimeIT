package com.overclocked.timeit.fragments;

import android.content.Intent;
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
public class ShareDialogFragment extends DialogFragment {

    public ShareDialogFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TextView txtShare = (TextView) view.findViewById(R.id.txtShare);
        TextView txtLater = (TextView) view.findViewById(R.id.txtLater);
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SHARE_NOW_CLICK);
                getDialog().dismiss();
                //Share Intent
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "I found TimeIt App useful to maintain work life balance. You should definitely try it out. Download Now "+ AppConstants.TIME_IT_MARKET_TINY_URL);
                startActivity(Intent.createChooser(intent,"Share Time It"));
            }
        });
        txtLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localytics.tagEvent(AppConstants.LOCALYTICS_TAG_EVENT_SHARE_LATER_CLICK);
                getDialog().dismiss();
            }
        });
        return view;
    }
}
