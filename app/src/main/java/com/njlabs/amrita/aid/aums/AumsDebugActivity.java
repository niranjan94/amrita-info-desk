/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;

public class AumsDebugActivity extends BaseActivity {
    @Override
    public void init(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        String response = null;
        if (extras != null) {
            response = extras.getString("response");
        }

        setupLayout(R.layout.activity_aums_data, Color.parseColor("#e91e63"));

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadData(response,"text/html","UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);

    }
}
