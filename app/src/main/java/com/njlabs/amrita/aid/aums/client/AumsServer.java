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

package com.njlabs.amrita.aid.aums.client;

@SuppressWarnings("ALL")
public class AumsServer {

    public static String get(Server identifier) {
        switch (identifier) {
            case ETTIMADAI:
                return "https://amritavidya.amrita.edu:8444";
            case ETTIMADAI_ONE:
                return "https://amritavidya1.amrita.edu:8444";
            case ETTIMADAI_TWO:
                return "https://amritavidya2.amrita.edu:8444";
            case AMRITAPURI:
                return "https://aums-students-am.amrita.edu:8443";
            case BANGALORE:
                return "https://aums-blr.amrita.edu:8444";
            case MYSORE:
                return "https://amritavidya-am-student.amrita.edu:8444";
            case AIMS:
                return "https://amritavidya-aims.amrita.edu:8444";
            case BUSINESS:
                return "https://amritavidya-am-student.amrita.edu:8444";
            case ASAS_KOCHI:
                return "https://amritavidya-am-student.amrita.edu:8444";
            default:
                return null;
        }
    }

    public static enum Server {
        ETTIMADAI, AMRITAPURI, BANGALORE, MYSORE, AIMS, BUSINESS, ASAS_KOCHI, ETTIMADAI_ONE, ETTIMADAI_TWO
    }
}
