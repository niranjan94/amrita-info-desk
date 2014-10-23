package com.njlabs.amrita.aid.aums.classes;

import com.orm.SugarRecord;

public class CourseGradeData extends SugarRecord<CourseGradeData> {

    public String courseCode;
    public String courseTitle;
    public String type;
    public String grade;

    public CourseGradeData() {

    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}