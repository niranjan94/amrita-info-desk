/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.bunker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.andreabaccega.widget.FormEditText;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.Aums;
import com.onemarker.ark.Security;

public class AttendanceManager extends Activity {

    Aums aums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_manager_login);

        SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
        String RollNo = preferences.getString("RollNo", "");
        String encodedPassword = preferences.getString("Password","");

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (RollNo != null && RollNo != "") {
            ((FormEditText) findViewById(R.id.roll_no)).setText(RollNo);
        }
        if(encodedPassword != null && encodedPassword != "") {
            ((FormEditText)findViewById(R.id.pwd)).setText(Security.decrypt(encodedPassword,MainApplication.key));
        }

    }

    public void configureAuto(View v)
    {
        final FormEditText RollNo = (FormEditText) findViewById(R.id.roll_no);
        final FormEditText Password = (FormEditText) findViewById(R.id.pwd);
        FormEditText[] allFields = {RollNo, Password};

        boolean allValid = true;
        for (FormEditText field : allFields) {
            allValid = field.testValidity() && allValid;
        }

        if (allValid) {

            ProgressDialog dialog;
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Authenticating your credentials ... ");
            dialog.show();
            SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("RollNo", RollNo.getText().toString());
            editor.putString("Password", Security.encrypt(Password.getText().toString(), MainApplication.key));
            editor.commit();

            aums = new Aums(this,"data_hook",RollNo.getText().toString(),Password.getText().toString(),dialog,"getRegisteredCourses");
            aums.GetSessionID("https://amritavidya.amrita.edu:8444", true);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
        }
    }

    public void exit(View v)
    {
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item){
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
