package com.njlabs.amrita.aid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.njlabs.amrita.aid.util.ark.logging.Ln;
import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseInstallation;

import org.onepf.opfpush.OPFPush;
import org.onepf.opfpush.configuration.Configuration;
import org.onepf.opfpush.gcm.GCMProvider;
import org.onepf.opfpush.listener.SimpleEventListener;
import org.onepf.opfutils.OPFLog;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends SugarApp {

    private Tracker mTracker;

    public static String key = "bdc0fabcbfa45a3506d1e66a6ff77596";
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

        OPFLog.setEnabled(BuildConfig.DEBUG, true);
        final Configuration configuration = new Configuration.Builder()
                .addProviders(new GCMProvider(this, getString(R.string.gcm_defaultSenderId)))
                .setSelectSystemPreferred(true)
                .setEventListener(new SimpleEventListener() {
                    @Override
                    public void onMessage(@NonNull Context context, @NonNull String providerName, Bundle extras) {
                        Ln.d("GCM Message received");
                    }

                    @Override
                    public void onRegistered(@NonNull Context context, @NonNull String providerName, @NonNull String registrationId) {
                        Ln.d("GCM Message registered");
                    }
                })
                .build();

        OPFPush.init(this, configuration);
        OPFPush.getHelper().register();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
