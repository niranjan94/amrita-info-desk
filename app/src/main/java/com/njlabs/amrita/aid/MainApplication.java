package com.njlabs.amrita.aid;


import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.njlabs.amrita.aid.gpms.proxy.BackgroundSocketService;
import com.njlabs.amrita.aid.news.NewsModel;
import com.njlabs.amrita.aid.util.ark.logging.Ln;
import com.parse.Parse;
import com.parse.ParseInstallation;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {

    private Tracker mTracker;

    public static String key = "bdc0fabcbfa45a3506d1e66a6ff77596";
    public static String socketServer = "wss://socket.codezero.xyz";

    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "rh6SYwa5Gfxk9rBzIEZvXSloGRl50pMnockRYK5E", "DwA9WHCbzgXBfLMosxl32LPEhZGtEqe2jYVuXhCj");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        Iconify.with(new FontAwesomeModule());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/bariol_regular-webfont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        Ln.d("onCreate");

        Configuration dbConfiguration = new Configuration.Builder(this)
                .setDatabaseName("storage.db")
                .addModelClass(NewsModel.class)
                .create();

        ActiveAndroid.initialize(dbConfiguration);

        startService(new Intent(this, BackgroundSocketService.class));

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, BackgroundSocketService.class));
        }
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Ln.d("onTerminate");

        try {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, BackgroundSocketService.class));
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }



}
