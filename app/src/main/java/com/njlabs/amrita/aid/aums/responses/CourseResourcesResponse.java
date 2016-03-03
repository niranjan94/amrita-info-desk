/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.aums.models.CourseResource;

import java.util.List;

public abstract class CourseResourcesResponse extends AumsBaseResponse {
    public abstract void onSuccess(List<CourseResource> courseResourceList);
}
