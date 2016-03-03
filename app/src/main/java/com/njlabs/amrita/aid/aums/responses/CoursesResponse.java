/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.aums.models.CourseData;

import java.util.List;

public abstract class CoursesResponse extends AumsBaseResponse {
    public abstract void onSuccess(List<CourseData> courseDataList);
}
