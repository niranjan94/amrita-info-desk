/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms;

import android.content.Context;

import com.njlabs.amrita.aid.util.okhttp.Client;
import com.njlabs.amrita.aid.util.okhttp.extras.PersistentCookieStore;

import java.net.URLEncoder;

public class GpmsClient extends Client {

    public String PROXY_URL = "https://anokha.amrita.edu/glype/browse.php?b=4";
    public String BASE_URL = "http://gpms.ettimadai.net/gpis/student";

    public GpmsClient(Context context) {
        super(context);
    }

    @Override
    protected String getAbsoluteUrl(String relativeUrl) {
        return PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl);
    }

    public String getUnproxiedUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    @Override
    protected String getCookieFile() {
        return PersistentCookieStore.GPMS_COOKIE_PREFS;
    }

    @Override
    protected boolean setLoggingEnabled() {
        return false;
    }

    @Override
    protected boolean shouldVerifySSL() {
        return false;
    }
}
