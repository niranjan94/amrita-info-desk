/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.aums.models.CourseMarkData;

import java.util.List;

public abstract class MarksResponse extends AumsBaseResponse {
    public abstract void onSuccess(List<CourseMarkData> markDataList);
    public abstract void onDataUnavailable();
}
