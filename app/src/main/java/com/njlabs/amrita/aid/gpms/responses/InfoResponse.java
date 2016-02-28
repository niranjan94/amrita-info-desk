/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.responses;

public abstract class InfoResponse extends LoginResponse {
    public abstract void onSuccess(String regNo, String name, String hostel, String roomNo, String mobile, String email, String photoUrl, String numPasses);
}
