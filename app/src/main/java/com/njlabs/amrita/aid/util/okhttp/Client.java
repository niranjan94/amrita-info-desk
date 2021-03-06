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

package com.njlabs.amrita.aid.util.okhttp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.njlabs.amrita.aid.util.TLSSocketFactory;
import com.njlabs.amrita.aid.util.okhttp.extras.MapQuery;
import com.njlabs.amrita.aid.util.okhttp.extras.PersistentCookieStore;
import com.njlabs.amrita.aid.util.okhttp.extras.RequestParams;
import com.njlabs.amrita.aid.util.okhttp.responses.FileResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.RawResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.TextResponse;
import com.onemarker.ln.logger.Ln;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

abstract public class Client {

    public String BASE_URL;
    protected OkHttpClient client;
    protected String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/47.0.2526.106 Chrome/47.0.2526.106 Safari/537.36";
    protected Context context;
    protected String referrer = null;
    private ProgressResponseBody.ProgressListener progressListener = null;
    private SharedPreferences cookiePrefs;
    private HashMap<String, String> customHeaders;

    Interceptor interceptor = new Interceptor() {
        @SuppressLint("DefaultLocale")
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            if (setLoggingEnabled()) {
                Request request = chain.request();
                long t1 = System.nanoTime();
                Ln.d(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
                Response response = chain.proceed(request);
                ResponseBody responseBody = response.body();
                String responseBodyString = response.body().string();
                long t2 = System.nanoTime();
                Ln.d(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                Ln.d(responseBodyString);

                Map<String, ?> cookies = cookiePrefs.getAll();
                for (String key : cookies.keySet()) {
                    Ln.d(key + "::" + cookies.get(key));
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
    };

    public Client(Context context) {
        this.context = context;
    }

    public void powerUp() throws NoSuchAlgorithmException, KeyManagementException {
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context, getCookieFile()), CookiePolicy.ACCEPT_ALL);
        cookiePrefs = context.getSharedPreferences(getCookieFile(), 0);
        OkHttpClient.Builder builder;

        if (shouldVerifySSL()) {
            builder = new OkHttpClient.Builder();
        } else {
            builder = getUnsecuredOkHttpBuilder();
        }

        if (progressListener != null) {
            builder.addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }


        OkHttpClient.Builder clientBuilder = builder
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .readTimeout(0, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        URL url = null;
                        try {
                            url = new URL(getAbsoluteUrl(""));
                        } catch (MalformedURLException e) {
                            Ln.e(e);
                        }

                        String origin = "https://www.google.com/";
                        if (url != null) {
                            origin = url.getProtocol() + "://" + url.getHost();
                        }

                        Request.Builder newRequest = request.newBuilder();

                        newRequest.addHeader("Origin", origin);
                        newRequest.addHeader("User-Agent", userAgent);

                        if (referrer != null) {
                            newRequest.addHeader("Referer", referrer);
                        } else {
                            newRequest.removeHeader("Referer");
                        }

                        if (customHeaders != null) {
                            for (String name : customHeaders.keySet()) {
                                newRequest.addHeader(name, customHeaders.get(name));
                            }
                        }

                        return chain.proceed(newRequest.build());
                    }
                })
                .connectTimeout(0, TimeUnit.SECONDS);
        client = clientBuilder.build();
    }

    public void setProgressListener(ProgressResponseBody.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void removeReferrer() {
        this.referrer = null;
    }

    public void setCustomHeaders(HashMap<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    public void addCustomHeader(String name, String value) {
        if(this.customHeaders == null) {
            this.setCustomHeaders(new HashMap<String, String>());
        }
        this.customHeaders.put(name, value);
    }

    public void removeCustomHeaders() {
        if(this.customHeaders != null) {
            this.customHeaders.clear();
        }
    }

    public void get(String path, RequestParams params, final TextResponse responseHandler) {

        String queryString = "?";
        if (params != null) {
            queryString += MapQuery.urlEncode(params);
        }

        Request request = new Request.Builder()
                .url(getAbsoluteUrl(path) + queryString).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        responseHandler.onFailure(e);
                    }
                });
                Ln.e(e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseString = response.body().string();
                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            responseHandler.onSuccess(responseString);
                        } else {
                            responseHandler.onFailure(new IOException(responseString));
                        }

                    }
                });
            }
        });
    }

    public void get(String path, RequestParams params, final RawResponse responseHandler) {
        String queryString = "?";
        if (params != null) {
            queryString += MapQuery.urlEncode(params);
        }

        Request request = new Request.Builder()
                .url(getAbsoluteUrl(path) + queryString).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        responseHandler.onFailure(e);
                    }
                });
                Ln.e(e);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    responseHandler.onSuccess(response);
                } else {
                    runOnCorrectThread(new Runnable() {
                        @Override
                        public void run() {
                            responseHandler.onFailure(new IOException(call.request().toString()));
                        }
                    });
                }
            }
        });
    }

    public void get(String path, RequestParams params, final FileResponse responseHandler) {

        String queryString = "?";
        if (params != null) {
            queryString += MapQuery.urlEncode(params);
        }

        Request request = new Request.Builder()
                .url(getAbsoluteUrl(path) + queryString).build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        responseHandler.onFailure(e);
                    }
                });
                Ln.e(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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

                try {
                    response.body().close();
                    inputStream.close();
                } catch (Exception e) {
                    Ln.e(e);
                }

                outputStream.close();

                final File finalCacheFile = cacheFile;
                final Response finalResponse = response;

                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalResponse.isSuccessful()) {
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

    public void get(String path, RawResponse responseHandler) {
        get(path, null, responseHandler);
    }

    public void post(String path, RequestParams params, final TextResponse responseHandler) {

        FormBody.Builder formBody = new FormBody.Builder();

        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            formBody.add((String) pair.getKey(), (String) pair.getValue());
            iterator.remove();
        }

        Request request = new Request.Builder()
                .url(getAbsoluteUrl(path))
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        responseHandler.onFailure(e);
                    }
                });
                Ln.e(e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseString = response.body().string();
                runOnCorrectThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            responseHandler.onSuccess(responseString);
                        } else {
                            responseHandler.onFailure(new IOException(responseString));
                        }
                    }
                });
            }
        });
    }

    protected String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void setBaseURL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    public void resetClient() {
        cookiePrefs.edit().clear().apply();
    }

    public void runOnCorrectThread(Runnable runnable) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(runnable);
        } else {
            runnable.run();
        }
    }

    private OkHttpClient.Builder getUnsecuredOkHttpBuilder() {
        OkHttpClient.Builder builder;
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

            builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sc.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

        } catch (Exception e) {
            builder = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
        }
        return builder;
    }

    abstract protected String getCookieFile();

    abstract protected boolean setLoggingEnabled();

    abstract protected boolean shouldVerifySSL();
}
