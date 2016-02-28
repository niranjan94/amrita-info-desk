/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

public abstract class SessionResponse extends BaseResponse {
    public abstract void onSuccess(String formAction, String lt);
}
