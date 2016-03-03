/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.util.okhttp.responses.BaseResponse;

public abstract class AumsBaseResponse extends BaseResponse {
    public abstract void onSiteStructureChange();
}
