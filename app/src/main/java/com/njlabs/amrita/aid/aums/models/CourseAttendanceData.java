/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.models;

import com.crashlytics.android.Crashlytics;

/**
 * Created by Niranjan on 22-10-2014.
 */

public class CourseAttendanceData {
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
        try {
            this.total = Math.round(Float.valueOf(total));
        } catch(Exception e){
            Crashlytics.logException(e);
            this.total = 0;
        }
    }

    public void setAttended(String attended) {
        try{
            this.attended = Math.round(Float.valueOf(attended));
            this.bunked = this.total-this.attended;
        } catch (Exception e){
            Crashlytics.logException(e);
            this.attended = 0;
            this.bunked = 0;
        }

    }

    public void setPercentage(String percentage) {
        try{
            this.percentage = Float.parseFloat(percentage);
        } catch(Exception e){
            Crashlytics.logException(e);
            this.percentage = 0;
        }
    }
}