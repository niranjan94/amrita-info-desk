/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.responses;

import com.njlabs.amrita.aid.gpms.models.PendingEntry;

import java.util.List;

public abstract class PendingResponse extends GpmsBaseResponse {
    public abstract void onSuccess(List<PendingEntry> pendingEntries);
}
