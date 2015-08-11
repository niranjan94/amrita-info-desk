package com.njlabs.amrita.aid.info;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;

public class Curriculum extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String department = extras.getString("department");

        setupLayout(R.layout.activity_curriculum, Color.parseColor("#5e98e9"));
        WebView webView = (WebView) findViewById(R.id.CurriculumWeb);

        if (department != null) {
            switch (department) {
                case "Aerospace Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/aero.html");
                    break;
                case "Civil Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/civil.html");
                    break;
                case "Chemical Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/chem.html");
                    break;
                case "Computer Science Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/csci.html");
                    break;
                case "Electrical & Electronics Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/eee.html");
                    break;
                case "Electronics & Communication Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/ece.html");
                    break;
                case "Mechanical Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/mec.html");
                    break;
                case "Electronics & Instrumentation Engineering":
                    webView.loadUrl("file:///android_asset/curriculum/eie.html");
                    break;
                default:
                    webView.loadUrl("file:///android_asset/curriculum/aero.html");
                    break;
            }
        }
        getSupportActionBar().setSubtitle(department);
    }
}
