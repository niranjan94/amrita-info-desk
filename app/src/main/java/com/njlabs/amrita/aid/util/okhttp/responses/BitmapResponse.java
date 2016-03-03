/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util.okhttp.responses;

import android.graphics.Bitmap;

public abstract class BitmapResponse extends BaseResponse {
    public abstract void onSuccess(Bitmap image);
}
