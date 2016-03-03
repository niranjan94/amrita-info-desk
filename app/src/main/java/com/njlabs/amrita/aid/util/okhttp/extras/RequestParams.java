/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util.okhttp.extras;

import java.util.HashMap;

@SuppressWarnings("unused")
public class RequestParams extends HashMap<String, String> {

    public String put(String key, int value) {
        return this.put(key, String.valueOf(value));
    }

    public String put(String key, double value) {
        return this.put(key, String.valueOf(value));
    }

    public String put(String key, float value) {
        return this.put(key, String.valueOf(value));
    }

    public String put(String key, boolean value) {
        return this.put(key, String.valueOf(value));
    }

    public String put(String key, long value) {
        return this.put(key, String.valueOf(value));
    }

    public String put(String key, char value) {
        return this.put(key, String.valueOf(value));
    }

    public String put(String key, Object value) {
        return this.put(key, String.valueOf(value));
    }
}
