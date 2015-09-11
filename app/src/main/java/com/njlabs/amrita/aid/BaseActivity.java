/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

public class BaseActivity extends AppCompatActivity {

    public Context baseContext;
    public Toolbar toolbar;
    public View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseContext = this;
    }

    public void setupLayout(int layoutRef, int primaryColor){
        setContentView(layoutRef);
        setupToolbar(null);
        toolbar.setBackgroundColor(primaryColor);
        setStatusbarColor(primaryColor);
        setRecentHeaderColor(primaryColor);
        parentView = (View) toolbar.getParent();
    }

    public void setupLayout(int layoutRef){
        setContentView(layoutRef);
        setupToolbar(null);
        parentView = (View) toolbar.getParent();
    }

    public void setupLayout(int layoutRef, String title){
        setContentView(layoutRef);
        setupToolbar(title);
        parentView = (View) toolbar.getParent();
    }

    public void setupLayoutNoActionBar(int layoutRef){
        setContentView(layoutRef);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRecentHeaderColor(int color){
        if(isLollipop()){
            ActivityInfo activityInfo = null;
            String title = "Amrita Info Desk";
            try {
                activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                title = activityInfo.loadLabel(getPackageManager()).toString();
            } catch (PackageManager.NameNotFoundException e) {
                Crashlytics.logException(e);
            }
            this.setTaskDescription(new ActivityManager.TaskDescription(title, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), color));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(title!=null){
            getSupportActionBar().setTitle(title);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void setStatusbarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(darkenColor(color));
        }
    }

    public int darkenColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public boolean isLollipop(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish(); //go back to the previous Activity
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
