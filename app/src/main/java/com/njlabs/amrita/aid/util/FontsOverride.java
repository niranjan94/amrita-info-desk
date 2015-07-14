/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util;

import android.content.Context;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.Field;

public final class FontsOverride {

    public static void setDefaultFont(Context context) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");
        replaceFont("DEFAULT", regular);
        replaceFont("MONOSPACE", regular);
        replaceFont("SANS_SERIF", regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,  final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}