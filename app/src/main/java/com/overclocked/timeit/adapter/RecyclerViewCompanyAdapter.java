package com.overclocked.timeit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.overclocked.timeit.R;
import com.overclocked.timeit.activity.TimeConfigureActivity;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.common.ScaleImageView;
import com.overclocked.timeit.model.Company;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Thahaseen on 4/21/2016.
 */
public class RecyclerViewCompanyAdapter extends RecyclerView.Adapter<RecyclerViewCompanyAdapter.CompanyViewHolder>{

    private List<Company> companyList;
    private Context mContext;
    private SharedPreferences preferences;

    public RecyclerViewCompanyAdapter(Context context, List<Company> itemList) {
        this.companyList = itemList;
        this.mContext = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.company_select_item, null);
        CompanyViewHolder viewHolder = new CompanyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder viewHolder, int i) {
        Company item = companyList.get(i);
        Picasso.with(mContext).load(item.getCompanyLogo())
                .into(viewHolder.imgViewCompany);
        viewHolder.txtCompanyName.setText(item.getCompanyName());

    }

    @Override
    public int getItemCount() {
        return (null != companyList ? companyList.size() : 0);
    }


    public class CompanyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.imgViewCompany) protected ScaleImageView imgViewCompany;
        @Bind(R.id.txtCompanyName) protected TextView txtCompanyName;

        public CompanyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.PREF_COMPANY_NAME, companyList.get(getAdapterPosition()).getCompanyName());
            editor.putString(AppConstants.PREF_COMPANY_LOGO, companyList.get(getAdapterPosition()).getCompanyLogo());
            editor.commit();
            Intent i = new Intent(mContext, TimeConfigureActivity.class);
            mContext.startActivity(i);
            ((Activity) mContext).finish();
        }
    }
}
