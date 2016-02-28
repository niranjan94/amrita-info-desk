/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import android.graphics.Bitmap;

import java.io.File;

public abstract class BitmapResponse extends BaseResponse {
    public abstract void onSuccess(Bitmap image);
}
