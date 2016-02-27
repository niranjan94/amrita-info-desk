/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.responses;

public abstract class SuccessResponse {
    public abstract void onSuccess();
    public abstract void onFailure(String response, Throwable throwable);

}
