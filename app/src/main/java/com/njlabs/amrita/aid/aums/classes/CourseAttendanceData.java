/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.classes;

import com.orm.SugarRecord;

/**
 * Created by Niranjan on 22-10-2014.
 */

public class CourseAttendanceData extends SugarRecord<CourseAttendanceData> {
    public String courseCode;
    public String courseTitle;
    public int total;
    public int attended;
    public int bunked;
    public float percentage;

    public CourseAttendanceData() {

    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setTotal(String total) {
        this.total = Integer.parseInt(total);
    }

    public void setAttended(String attended) {
        this.attended = Integer.parseInt(attended);
        this.bunked = this.total-this.attended;
    }

    public void setPercentage(String percentage) {
        this.percentage = Float.parseFloat(percentage);
    }
}