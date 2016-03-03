/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util.okhttp.responses;

import okhttp3.Response;

public abstract class RawResponse extends BaseResponse {
    public abstract void onSuccess(Response response);
}
