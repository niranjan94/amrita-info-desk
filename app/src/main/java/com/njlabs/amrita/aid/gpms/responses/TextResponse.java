/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.responses;

public abstract class TextResponse extends BaseResponse {
    public abstract void onSuccess(String responseString);
}
