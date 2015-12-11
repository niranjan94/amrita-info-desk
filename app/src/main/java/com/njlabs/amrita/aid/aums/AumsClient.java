package com.njlabs.amrita.aid.aums;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;


public class AumsClient {

    public String BASE_URL = "https://amritavidya.amrita.edu:8444";
    private AsyncHttpClient client = new AsyncHttpClient(8444,8444);
    public PersistentCookieStore cookieStore;

    public AumsClient(Context context) {
        cookieStore = new PersistentCookieStore(context);
        client.setCookieStore(cookieStore);
        client.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3");
        client.setEnableRedirects(true, true, true);
    }

    public void setReferrer(String referrer){
        client.addHeader("Referer",getAbsoluteUrl(referrer));
    }

    public void removeReferrer(){
        client.removeHeader("Referer");
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
        return BASE_URL + relativeUrl;
    }

    public void setBaseURL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    public void closeClient() {
        if(cookieStore!=null)
            cookieStore.clear();
    }
}
