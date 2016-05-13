package com.overclocked.timeit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.localytics.android.Localytics;
import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.adapter.RecyclerViewCompanyAdapter;
import com.overclocked.timeit.common.AppConstants;
import com.overclocked.timeit.model.Company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class    CompanySelectActivity extends AppCompatActivity {
    @Bind(R.id.recyclerViewCompany) RecyclerView recyclerViewCompany;
    @Bind(R.id.toolbar) Toolbar toolbar;
    private ProgressDialog mProgress;
    List<Company> lstCompany = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_select);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Your Company");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Localytics.tagScreen(AppConstants.LOCALYTICS_TAG_SCREEN_COMPANY_SELECT);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CompanySelectActivity.this,2);
        recyclerViewCompany.setLayoutManager(gridLayoutManager);
        if(AppController.getInstance().getConnectionDetector().isConnectingToInternet()){
            mProgress = new ProgressDialog(CompanySelectActivity.this);
            mProgress.setCancelable(false);
            mProgress.setMessage("Loading...");
            mProgress.show();
            getCompanyDetails();
        }else{
            Toast.makeText(CompanySelectActivity.this,AppConstants.MSG_NO_INTERNET ,Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    public void getCompanyDetails(){
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                AppConstants.URL_GET_COMPANY_FEED, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getInt(AppConstants.KEY_SUCCESS) != 0) {
                            JSONArray feeds = response.getJSONArray(AppConstants.KEY_COMPANY_FEED);
                            Log.i("feed_length ",""+feeds.length());
                            for (int i = 0; i < feeds.length(); i++) {
                                JSONObject f = feeds.getJSONObject(i);
                                Company company = new Company();
                                company.setCompanyLogo(f.getString("companyLogo"));
                                company.setCompanyName(f.getString("companyName"));
                                company.setCompanyTag(f.getString("companyTag"));
                                lstCompany.add(company);
                            }
                            populateRecyclerView();
                            mProgress.dismiss();
                        }
                    } catch (JSONException e) {e.printStackTrace();}
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Get_Company", "Error: " + error.getMessage());
                mProgress.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    public void populateRecyclerView(){
        RecyclerViewCompanyAdapter recyclerViewCompanyAdapter = new RecyclerViewCompanyAdapter(CompanySelectActivity.this,lstCompany);
        recyclerViewCompany.setAdapter(recyclerViewCompanyAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CompanySelectActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

}
