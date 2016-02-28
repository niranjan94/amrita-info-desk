/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

public abstract class BaseResponse {
    public abstract void onFailure(Throwable throwable);
    public abstract void onSiteStructureChange();
}
