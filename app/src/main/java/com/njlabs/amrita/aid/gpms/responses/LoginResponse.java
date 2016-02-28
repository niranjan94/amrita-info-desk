/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.responses;

public abstract class LoginResponse extends SuccessResponse {
    public abstract void onFailedAuthentication();
}
