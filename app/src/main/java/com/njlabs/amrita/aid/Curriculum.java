package com.njlabs.amrita.aid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class Curriculum extends AppCompatActivity {

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#5e98e9"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(department);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish(); //go back to the previous Activity
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
