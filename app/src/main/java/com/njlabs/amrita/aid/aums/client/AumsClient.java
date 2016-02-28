/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.njlabs.amrita.aid.BuildConfig;
import com.njlabs.amrita.aid.aums.responses.FileResponse;
import com.njlabs.amrita.aid.aums.responses.TextResponse;
import com.njlabs.amrita.aid.util.MapQuery;
import com.njlabs.amrita.aid.util.PersistentCookieStore;
import com.njlabs.amrita.aid.util.RequestParams;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AumsClient {

    public String BASE_URL = AumsServer.get(AumsServer.Server.ETTIMADAI);
    private OkHttpClient client;
    private String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3";
    private Context context;
    private String referer = null;

    public AumsClient(Context context) {

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context, PersistentCookieStore.AUMS_COOKIE_PREFS), CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .readTimeout(0, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        if(BuildConfig.DEBUG && false) {

                            Request request = chain.request();

                            long t1 = System.nanoTime();

                            Ln.d(String.format("Sending request %s on %s%n%s",
                                    request.url(), chain.connection(), request.headers()));

                            Response response = chain.proceed(request);

                            ResponseBody responseBody = response.body();
                            String responseBodyString = response.body().string();

                            if(BuildConfig.DEBUG) {
                                long t2 = System.nanoTime();
                                Ln.d(String.format("Received response for %s in %.1fms%n%s",
                                        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                                Ln.d(responseBodyString);
                            }

                            return response.newBuilder()
                                    .body(ResponseBody.create(responseBody.contentType(), responseBodyString.getBytes()))
                                    .headers(response.headers())
                                    .protocol(response.protocol())
                                    .handshake(response.handshake())
                                    .build();

                        } else {
                            return chain.proceed(chain.request());
                        }
                    }
                })
                .connectTimeout(0, TimeUnit.SECONDS)
                .build();

        this.context = context;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public void removeReferer() {
        this.referer = null;
    }

    public void get(String path, RequestParams params, final TextResponse responseHandler) {

        String queryString = "?";
        if(params != null) {
            queryString += MapQuery.urlEncode(params);
        }

        Request.Builder request = new Request.Builder()
                .url(getAbsoluteUrl(path) + queryString)
                .header("User-Agent", userAgent);

        if(referer != null) {
            request.addHeader("Referer", getAbsoluteUrl(referer));
        } else {
            request.removeHeader("Referer");
        }

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

    public void get(String path, RequestParams params, final FileResponse responseHandler) {

        String queryString = "?";
        if(params != null) {
            queryString += MapQuery.urlEncode(params);
        }

        Request.Builder request = new Request.Builder()
                .url(getAbsoluteUrl(path) + queryString)
                .header("User-Agent", userAgent);

        if(referer != null) {
            request.addHeader("Referer", getAbsoluteUrl(referer));
        } else {
            request.removeHeader("Referer");
        }

        client.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseHandler.onFailure(e);
                Ln.e(e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                InputStream inputStream = response.body().byteStream();
                FileOutputStream outputStream;
                File cacheFile = new File(context.getCacheDir() + "/okhttp/");
                cacheFile.mkdirs();
                cacheFile = new File(cacheFile + "/" + DateTime.now().getMillis() + ".cache");
                outputStream = new FileOutputStream(cacheFile);

                int read;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                response.body().close();
                inputStream.close();
                outputStream.close();

                final File finalCacheFile = cacheFile;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()) {
                            responseHandler.onSuccess(finalCacheFile);
                        } else {
                            responseHandler.onFailure(new IOException());
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

        if(referer != null) {
            request.addHeader("Referer", getAbsoluteUrl(referer));
        } else {
            request.removeHeader("Referer");
        }


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

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void setBaseURL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }


    public void resetClient() {
        SharedPreferences cookiePrefs = context.getSharedPreferences(PersistentCookieStore.AUMS_COOKIE_PREFS, 0);
        cookiePrefs.edit().clear().apply();
    }
}
