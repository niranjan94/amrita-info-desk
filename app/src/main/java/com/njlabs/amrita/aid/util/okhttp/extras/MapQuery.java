/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util.okhttp.extras;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class MapQuery {

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String urlEncode(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncode(entry.getKey().toString()),
                    urlEncode(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }
}
