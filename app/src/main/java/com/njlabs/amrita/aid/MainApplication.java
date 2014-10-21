package com.njlabs.amrita.aid;

import android.app.Application;

import com.njlabs.amrita.aid.util.FontsOverride;
import com.parse.Parse;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formKey = "",
        formUri = "https://njlabs.cloudant.com/acra-aid/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="irstannewentiefuttoordst",
        formUriBasicAuthPassword="L8EmIHU6gGovaab1R6lhLtXB",
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false,
        resToastText = R.string.crash_toast_text
        )

public class MainApplication extends Application {

    public static double currentVersion = 2.0;
    public void onCreate() 
    {
        super.onCreate();
        Parse.initialize(this, "rh6SYwa5Gfxk9rBzIEZvXSloGRl50pMnockRYK5E", "DwA9WHCbzgXBfLMosxl32LPEhZGtEqe2jYVuXhCj");
        ACRA.init(this);
        FontsOverride.setDefaultFont(this);
    }
}
