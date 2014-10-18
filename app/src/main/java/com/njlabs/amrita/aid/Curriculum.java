package com.njlabs.amrita.aid;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Curriculum extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String department = extras.getString("department");

        setContentView(R.layout.activity_curriculum);
        webView = (WebView) findViewById(R.id.CurriculumWeb);
        if (department.equals("Aerospace Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/aero.html");
        } else if (department.equals("Civil Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/civil.html");
        } else if (department.equals("Chemical Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/chem.html");
        } else if (department.equals("Computer Science Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/csci.html");
        } else if (department.equals("Electrical & Electronics Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/eee.html");
        } else if (department.equals("Electronics & Communication Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/ece.html");
        } else if (department.equals("Mechanical Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/mec.html");
        } else if (department.equals("Electronics & Instrumentation Engineering")) {
            webView.loadUrl("file:///android_asset/curriculum/eie.html");
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(department);
    }

}
