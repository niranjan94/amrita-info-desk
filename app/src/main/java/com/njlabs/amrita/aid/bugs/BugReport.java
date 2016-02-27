/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.bugs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.parse.ParseInstallation;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BugReport extends BaseActivity {

    private String studentName;
    private String studentRollNo;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_bug_report, Color.parseColor("#5B96E7"));

        studentName = getIntent().getStringExtra("studentName");
        studentRollNo = getIntent().getStringExtra("studentRollNo");

        ((TextView) findViewById(R.id.install_id_view)).setText("Installation ID: "+ ParseInstallation.getCurrentInstallation().getInstallationId());
    }

    public void sendReport(View v){
        String additionalInfo = ((EditText) findViewById(R.id.additional_info)).getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Exception e = new Exception("AUMS Error Reported by "+studentName+" ("+studentRollNo+") on "+currentDateandTime+". Additional Info: "+ additionalInfo);
        Crashlytics.logException(e);
        Toast.makeText(getBaseContext(), "The error has been reported. Thank you for helping us improve the app.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
