/*
 * MIT License
 *
 * Copyright (c) 2016 Niranjan Rajendran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.njlabs.amrita.aid.aums.models;


import com.google.firebase.crash.FirebaseCrash;

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
        } catch (Exception e) {
            FirebaseCrash.report(e);
            this.total = 0;
        }
    }

    public void setAttended(String attended) {
        try {
            this.attended = Math.round(Float.valueOf(attended));
            this.bunked = this.total - this.attended;
        } catch (Exception e) {
            FirebaseCrash.report(e);
            this.attended = 0;
            this.bunked = 0;
        }

    }

    public void setPercentage(String percentage) {
        try {
            this.percentage = Float.parseFloat(percentage);
        } catch (Exception e) {
            FirebaseCrash.report(e);
            this.percentage = 0;
        }
    }
}