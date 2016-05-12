package com.overclocked.timeit.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.overclocked.timeit.R;

public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Time It", "Maintain Work Life Balance", R.drawable.work_life_balance, Color.parseColor("#7DCCB6")));
        addSlide(AppIntroFragment.newInstance("Get Notified", "Get notified on when you should leave for the day by maintaining your average swipe hours of the week.", R.drawable.notify, Color.parseColor("#50B9CD")));
        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(IntroActivity.this,TimeConfigureActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }
}
