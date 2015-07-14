/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.classes;

import com.orm.SugarRecord;

/**
 * Created by Niranjan on 22-10-2014.
 */
public class CourseData extends SugarRecord<CourseData> {
    String courseCode;
    String courseName;
    String type;
    float credits;

    public CourseData() {
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getCredits() {
        return credits;
    }

    public void setCredits(float credits) {
        this.credits = credits;
    }
}
