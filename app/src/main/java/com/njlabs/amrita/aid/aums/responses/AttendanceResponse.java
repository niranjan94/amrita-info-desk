/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.aums.models.CourseAttendanceData;

import java.util.List;

public abstract class AttendanceResponse extends AumsBaseResponse {
    public abstract void onSuccess(List<CourseAttendanceData> attendanceDataList);

    public abstract void onDataUnavailable();
}
