/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.njlabs.amrita.aid.gpms.responses.TextResponse;
import com.njlabs.amrita.aid.util.MapQuery;
import com.njlabs.amrita.aid.util.PersistentCookieStore;
import com.njlabs.amrita.aid.util.RequestParams;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GpmsClient {

    public String PROXY_URL = "https://anokha.amrita.edu/glype/browse.php?b=4";
    public String BASE_URL = "http://gpms.ettimadai.net/gpis/student";

    private OkHttpClient client;
    private String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3";

    private Context context;

    public GpmsClient(Context context) {

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context, PersistentCookieStore.GPMS_COOKIE_PREFS), CookiePolicy.ACCEPT_ALL);

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }


                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());

            client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .sslSocketFactory(sc.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .build();

        } catch (Exception e) {

            client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .build();

        }
        this.context = context;

    }

    public void get(String path, RequestParams params, final TextResponse responseHandler) {

        String queryString = "?";
        if(params != null) {
            queryString += MapQuery.urlEncode(params);
        }

        Request.Builder request = new Request.Builder()
                .url(getAbsoluteUrl(path) + queryString)
                .header("User-Agent", userAgent);


        client.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseHandler.onFailure(e);
                Ln.e(e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseString = response.body().string();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()) {
                            responseHandler.onSuccess(responseString);
                        } else {
                            responseHandler.onFailure(new IOException(responseString));
                        }
                    }
                });
            }
        });
    }

    public void get(String path, TextResponse responseHandler) {
        get(path, null, responseHandler);
    }

    public void post(String path, RequestParams params, final TextResponse responseHandler) {

        FormBody.Builder formBody = new FormBody.Builder();

        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            formBody.add((String) pair.getKey(), (String) pair.getValue());
            iterator.remove();
        }

        Request.Builder request = new Request.Builder()
                .url(getAbsoluteUrl(path))
                .header("User-Agent", userAgent)
                .post(formBody.build());

        client.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseHandler.onFailure(e);
                Ln.e(e);
            }

            @Override
            public void onResponse(Call call,final Response response) throws IOException {
                final String responseString = response.body().string();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()) {
                            responseHandler.onSuccess(responseString);
                        } else {
                            responseHandler.onFailure(new IOException(responseString));
                        }
                    }
                });
            }
        });
    }

    private String getAbsoluteUrl(String relativeUrl) {
        Ln.d(PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl));
        return PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl);
    }

    public void setBaseURL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    public void closeClient() {
        SharedPreferences cookiePrefs = context.getSharedPreferences(PersistentCookieStore.GPMS_COOKIE_PREFS, 0);
        cookiePrefs.edit().clear().apply();
    }
}
