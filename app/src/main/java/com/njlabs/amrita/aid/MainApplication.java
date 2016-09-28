/*
 * MIT License
 *
 * Copyright (c) 2016 Niranjan Rajendran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
