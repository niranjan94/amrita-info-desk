package com.njlabs.amrita.aid;

import com.crashlytics.android.Crashlytics;
import com.njlabs.amrita.aid.util.FontsOverride;
import com.orm.SugarApp;
import com.parse.Parse;

public class MainApplication extends SugarApp {

    public static double currentVersion = 2.0;
    public static String key = "bdc0fabcbfa45a3506d1e66a6ff77596";
    public void onCreate() 
    {
        super.onCreate();
        Crashlytics.start(this);
        Parse.initialize(this, "rh6SYwa5Gfxk9rBzIEZvXSloGRl50pMnockRYK5E", "DwA9WHCbzgXBfLMosxl32LPEhZGtEqe2jYVuXhCj");
        FontsOverride.setDefaultFont(this);
    }
}
