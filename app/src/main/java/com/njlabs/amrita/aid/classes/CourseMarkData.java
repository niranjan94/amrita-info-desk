/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.classes;

public class CourseMarkData {

    public String courseCode = null;
    public String mark = null;
    public String exam = null;

    public CourseMarkData() {

    }

    public CourseMarkData(String exam) {
        this.exam = exam;
    }

    public CourseMarkData(String courseCode, String mark, String exam) {
        this.courseCode = courseCode;
        this.mark = mark;
        this.exam = exam;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }
}