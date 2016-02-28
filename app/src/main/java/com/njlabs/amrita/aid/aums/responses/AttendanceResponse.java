/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.responses;

import com.njlabs.amrita.aid.classes.CourseAttendanceData;

import java.util.List;

public abstract class AttendanceResponse extends BaseResponse {
    public abstract void onSuccess(List<CourseAttendanceData> attendanceDataList);
    public abstract void onDataUnavailable();
}
