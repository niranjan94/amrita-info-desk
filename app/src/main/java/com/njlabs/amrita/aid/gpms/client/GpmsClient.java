/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.client;

import android.content.Context;

import com.njlabs.amrita.aid.util.okhttp.Client;
import com.njlabs.amrita.aid.util.okhttp.extras.PersistentCookieStore;

import java.net.URLEncoder;

public class GpmsClient extends Client {

    public String PROXY_URL = "https://anokha.amrita.edu/glype/browse.php?b=4";
    public String BASE_URL = "http://gpms.ettimadai.net/gpis/student";

    public String COOKIE_FILE = PersistentCookieStore.GPMS_COOKIE_PREFS;

    public GpmsClient(Context context) {
        super(context);
    }

    public GpmsClient(Context context, String COOKIE_FILE) {
        super(context);
        this.COOKIE_FILE = COOKIE_FILE;
    }

    @Override
    protected String getAbsoluteUrl(String relativeUrl) {
        if(isProxyOn()) {
            return PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl);
        } else {
            return getUnproxiedUrl(relativeUrl);
        }

    }

    public String getUnproxiedUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    @Override
    protected String getCookieFile() {
        return COOKIE_FILE;
    }

    @Override
    protected boolean setLoggingEnabled() {
        return false;
    }

    @Override
    protected boolean shouldVerifySSL() {
        return false;
    }

    protected boolean isProxyOn() {
        return false;
    }
}
