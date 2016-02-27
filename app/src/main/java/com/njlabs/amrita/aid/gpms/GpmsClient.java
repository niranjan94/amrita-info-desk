/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import java.net.URLEncoder;

public class GpmsClient {

    public String PROXY_URL = "https://anokha.amrita.edu/glype/browse.php?b=4";
    public String BASE_URL = "http://gpms.ettimadai.net/gpis/student";

    private AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    public PersistentCookieStore cookieStore;

    public GpmsClient(Context context) {
        cookieStore = new PersistentCookieStore(context);
        client.setCookieStore(cookieStore);
        client.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3");
        client.setEnableRedirects(true, true, true);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        Ln.d(PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl));
        return PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl);
    }

    public void setBaseURL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    public void closeClient() {
        if(cookieStore!=null)
            cookieStore.clear();
    }
}
