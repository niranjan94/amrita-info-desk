/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.aums.client.AumsServer;

public abstract class LoginResponse extends AumsBaseResponse {
    public abstract void onSuccess(String name, String rollNo);

    public abstract void onFailedAuthentication();

    public abstract void onServerChanged(AumsServer.Server server);
}
