/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
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
