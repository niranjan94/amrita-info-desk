/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.bunker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.Aums;

public class AttendanceManager extends BaseActivity {

    Aums aums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_under_construction, Color.parseColor("#009688"));

       /* SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
        String RollNo = preferences.getString("RollNo", "");
        String encodedPassword = preferences.getString("Password","");

        if (RollNo != null && RollNo != "") {
            ((FormEditText) findViewById(R.id.roll_no)).setText(RollNo);
        }
        if(encodedPassword != null && encodedPassword != "") {
            ((FormEditText)findViewById(R.id.pwd)).setText(Security.decrypt(encodedPassword,MainApplication.key));
        }*/

    }

    public void configureAuto(View v)
    {
/*
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
            editor.apply();

            aums = new Aums(this,"data_hook",RollNo.getText().toString(),Password.getText().toString(),dialog,"getRegisteredCourses");
            aums.GetSessionID("https://amritavidya.amrita.edu:8444", true);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Ln.d("Process over");
                }
            });
        }*/
    }

    public void exit(View v)
    {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
