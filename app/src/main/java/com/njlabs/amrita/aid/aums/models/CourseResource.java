/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.models;

public class CourseResource {

    private String courseId;
    private String resourceFileName;
    private double bytesSize;

    public CourseResource() {
    }

    public CourseResource(String courseId, String resourceFileName) {
        this.courseId = courseId;
        this.resourceFileName = resourceFileName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getResourceFileName() {
        return resourceFileName;
    }

    public void setResourceFileName(String resourceFileName) {
        this.resourceFileName = resourceFileName;
    }

    public double getBytesSize() {
        return bytesSize;
    }

    public void setBytesSize(double bytesSize) {
        this.bytesSize = bytesSize;
    }
}
