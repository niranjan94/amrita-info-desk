package com.njlabs.amrita.aid;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseInstallation;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends SugarApp {

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
    }
}
