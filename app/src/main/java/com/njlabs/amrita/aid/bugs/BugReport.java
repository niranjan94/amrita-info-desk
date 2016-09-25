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

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BugReport extends BaseActivity {

    private String studentName;
    private String studentRollNo;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_bug_report, Color.parseColor("#5B96E7"));

        studentName = getIntent().getStringExtra("studentName");
        studentRollNo = getIntent().getStringExtra("studentRollNo");

        ((TextView) findViewById(R.id.install_id_view)).setText("Installation ID: " + FirebaseInstanceId.getInstance().getId());
    }

    public void sendReport(View v) {
        String additionalInfo = ((EditText) findViewById(R.id.additional_info)).getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.US);
        String currentDateandTime = sdf.format(new Date());
        Exception e = new Exception("AUMS Error Reported by " + studentName + " (" + studentRollNo + ") on " + currentDateandTime + ". Additional Info: " + additionalInfo);
        FirebaseCrash.report(e);
        Toast.makeText(getBaseContext(), "The error has been reported. Thank you for helping us improve the app.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
