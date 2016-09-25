package com.njlabs.amrita.aid;


import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.njlabs.amrita.aid.news.NewsModel;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {

    public static String key = "bdc0fabcbfa45a3506d1e66a6ff77596";
    private FirebaseAnalytics mFirebaseAnalytics;

    public void onCreate() {
        super.onCreate();

        Iconify.with(new FontAwesomeModule());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/bariol_regular-webfont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        Configuration dbConfiguration = new Configuration.Builder(this)
                .setDatabaseName("storage.db")
                .addModelClass(NewsModel.class)
                .create();

        ActiveAndroid.initialize(dbConfiguration);
    }

    synchronized public FirebaseAnalytics getDefaultTracker() {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        return mFirebaseAnalytics;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
