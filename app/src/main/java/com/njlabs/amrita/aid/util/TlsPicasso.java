package com.njlabs.amrita.aid.util;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.njlabs.amrita.aid.util.okhttp.OkHttpTools;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

public class TlsPicasso {

    private static Picasso instance = null;

    protected TlsPicasso() {
        // Exists only to defeat instantiation.
    }

    public static Picasso getInstance(Context context) {
        if(instance == null) {
            OkHttpClient client = OkHttpTools.enableTls12OnPreLollipop(new OkHttpClient.Builder()).build();
            instance = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
        }
        return instance;
    }
}
