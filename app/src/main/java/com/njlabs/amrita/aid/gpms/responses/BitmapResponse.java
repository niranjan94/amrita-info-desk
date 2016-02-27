/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.responses;

import android.graphics.Bitmap;

import java.io.File;

public abstract class BitmapResponse {
    public abstract void onSuccess(Bitmap image);
    public abstract void onFailure(File file, Throwable throwable);
}
