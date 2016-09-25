/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.client.Aums;
import com.njlabs.amrita.aid.aums.client.AumsServer;
import com.njlabs.amrita.aid.aums.responses.LoginResponse;
import com.njlabs.amrita.aid.aums.responses.SessionResponse;
import com.njlabs.amrita.aid.bugs.BugReport;
import com.njlabs.amrita.aid.landing.Landing;
import com.njlabs.amrita.aid.util.StringCallback;
import com.njlabs.amrita.aid.util.ark.Security;
import com.onemarker.ln.logger.Ln;
import com.njlabs.amrita.aid.util.okhttp.responses.FileResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.TextResponse;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class AumsActivity extends BaseActivity {

    private static long backPressTime;
    EditText rollNoEditText;
    EditText passwordEditText;
    MaterialSpinner spinner;
    private ProgressDialog dialog = null;
    private Aums aums;
    private SharedPreferences aumsPreferences;

    private boolean isLoggedIn = false;
    private Toast toast;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_aums, Color.parseColor("#e91e63"));

        rollNoEditText = (EditText) findViewById(R.id.roll_no);
        passwordEditText = (EditText) findViewById(R.id.pwd);

        List<String> campusDataSet = new LinkedList<>(Arrays.asList(
                "Ettimadai",
                "Amritapuri",
                "Bangalore",
                "Mysore",
                "AIMS",
                "Business schools",
                "ASAS Kochi"
        ));


        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems(campusDataSet);

        AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
        builder.setMessage("Amrita University does not provide an API for accessing AUMS data. " +
                "So, if any changes are made to the AUMS Website, please be patient while I try to catch up.")
                .setCancelable(true)
                .setIcon(R.drawable.ic_action_info_small)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        hideSoftKeyboard();
                    }
                });

        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        dialog = new ProgressDialog(baseContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating your credentials ... ");

        aumsPreferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
        String rollNo = aumsPreferences.getString("RollNo", "");
        String encodedPassword = aumsPreferences.getString("Password", "");
        spinner.setSelectedIndex(aumsPreferences.getInt("server_ordinal", 0));

        aums = new Aums(baseContext);

        if (!rollNo.equals("")) {
            ((EditText) findViewById(R.id.roll_no)).setText(rollNo);
            aums.studentRollNo = rollNo;
            hideSoftKeyboard();
        }

        if (!encodedPassword.equals("")) {
            ((EditText) findViewById(R.id.pwd)).setText(Security.decrypt(encodedPassword, MainApplication.key));
            hideSoftKeyboard();
        }

    }

    public void loginStart(View view) {

        isLoggedIn = false;

        boolean hasError = false;
        String rollNo = rollNoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (StringUtils.isEmpty(rollNo)) {
            hasError = true;
            rollNoEditText.setError("Your Roll Number is required");
        }

        if (StringUtils.isEmpty(password)) {
            hasError = true;
            passwordEditText.setError("Your AUMS password is required");
        }

        if (!hasError) {
            hideSoftKeyboard();
            SharedPreferences.Editor editor = aumsPreferences.edit();
            editor.putString("RollNo", rollNoEditText.getText().toString());
            editor.putString("Password", Security.encrypt(passwordEditText.getText().toString(), MainApplication.key));

            int serverOrdinal = spinner.getSelectedIndex();

            editor.putInt("server_ordinal", serverOrdinal);
            editor.apply();
            dialog.show();

            getSessionIdAndLogin(rollNo, password, AumsServer.Server.values()[serverOrdinal]);
        }
    }

    private void getSessionIdAndLogin(final String rollNo, final String password, AumsServer.Server server) {

        aums.logout();
        aums.switchServer(server);
        dialog.setMessage("Starting a new session ...");
        aums.getSessionId(new SessionResponse() {
            @Override
            public void onSuccess(String formAction, String lt) {
                dialog.setMessage("Logging into AUMS ...");
                aums.login(rollNo, password, formAction, lt, new LoginResponse() {
                    @Override
                    public void onSuccess(String name, String rollNo) {
                        isLoggedIn = true;
                        setupLayout(R.layout.activity_aums_profile, Color.parseColor("#e91e63"));
                        dialog.dismiss();

                        TextView nameView = (TextView) findViewById(R.id.student_name);
                        TextView rollNoView = (TextView) findViewById(R.id.student_roll_no);

                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }

                        nameView.setText(WordUtils.capitalizeFully(name));
                        rollNoView.setText(rollNo);

                        getSupportActionBar().setSubtitle("Logged in as " + WordUtils.capitalizeFully(name));

                        loadCgpa();
                        loadPhoto();

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AUMS");
                        bundle.putString(FirebaseAnalytics.Param.CHARACTER, name + " - " + rollNo);
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
                        tracker.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                    }

                    @Override
                    public void onFailedAuthentication() {
                        dialog.dismiss();
                        createSnackbar("Credentials were incorrect.");
                    }

                    @Override
                    public void onServerChanged(AumsServer.Server server) {
                        dialog.setMessage("Switching server ...");
                        getSessionIdAndLogin(rollNo, password, server);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        dialog.dismiss();
                        createSnackbar("An error occurred while connecting to the server.");
                        Ln.e(throwable);
                    }

                    @Override
                    public void onSiteStructureChange() {
                        dialog.dismiss();
                        createSnackbar("Site's structure has changed. Reported to the developer.");
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                dialog.dismiss();
                createSnackbar("An error occurred while connecting to the server.");
                Ln.e(throwable);
            }

            @Override
            public void onSiteStructureChange() {
                dialog.dismiss();
                createSnackbar("Site's structure has changed. Reported to the developer.");
            }
        });
    }

    private void loadCgpa() {
        final TextView cgpaView = (TextView) findViewById(R.id.student_cgpa);
        aums.getCGPA(new TextResponse() {
            @Override
            public void onSuccess(String cgpa) {
                cgpaView.setText(cgpa);
            }

            @Override
            public void onFailure(Throwable throwable) {
                cgpaView.setText("error");
                cgpaView.setTextColor(getResources().getColor(R.color.md_red_400));
                Ln.e(throwable);
            }

        });
    }

    private void loadPhoto() {
        aums.getPhotoFile(new FileResponse() {
            @Override
            public void onSuccess(File file) {
                findViewById(R.id.student_profile_pic_progress).setVisibility(View.GONE);
                ImageView myImage = (ImageView) findViewById(R.id.student_profile_pic);
                myImage.setVisibility(View.VISIBLE);
                Picasso.with(baseContext).load(file).error(R.drawable.user).into(myImage);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Ln.e(throwable);
                findViewById(R.id.student_profile_pic_progress).setVisibility(View.GONE);
                ImageView myImage = (ImageView) findViewById(R.id.student_profile_pic);
                myImage.setVisibility(View.VISIBLE);
                myImage.setImageResource(R.drawable.user);
            }
        });
    }

    public void openAttendance(View v) {
        final Intent intent = new Intent(baseContext, AttendanceActivity.class);
        intent.putExtra("server", aums.getServer());

        semesterPicker(new StringCallback() {
            @Override
            public void onFinish(String result) {
                intent.putExtra("semester", result);
                startActivity(intent);
            }
        });
    }

    public void openGrades(View v) {
        final Intent intent = new Intent(baseContext, GradesActivity.class);
        intent.putExtra("server", aums.getServer());

        semesterPicker(new StringCallback() {
            @Override
            public void onFinish(String result) {
                intent.putExtra("semester", result);
                startActivity(intent);
            }
        });
    }

    public void openMarks(View v) {
        final Intent intent = new Intent(baseContext, MarksActivity.class);
        intent.putExtra("server", aums.getServer());

        semesterPicker(new StringCallback() {
            @Override
            public void onFinish(String result) {
                intent.putExtra("semester", result);
                startActivity(intent);
            }
        });
    }

    public void openResources(View v) {
        final Intent intent = new Intent(baseContext, AumsResourcesActivity.class);
        intent.putExtra("server", aums.getServer());
        startActivity(intent);
    }

    private void semesterPicker(final StringCallback stringCallback) {
        final String[] items = {"1", "2", "Vacation 1", "3", "4", "Vacation 2", "5", "6", "Vacation 3", "7", "8", "Vacation 4", "9", "10", "Vacation 5", "11", "12", "Vacation 6", "13", "14", "15"};
        AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
        builder.setCancelable(true);
        builder.setTitle("Select a Semester");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stringCallback.onFinish(items[which]);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aums, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitAums();
                return true;
            case R.id.action_bug_report:
                Intent intent = new Intent(getApplicationContext(), BugReport.class);
                intent.putExtra("studentName", (aums.studentName != null ? aums.studentName : "Anonymous"));
                intent.putExtra("studentRollNo", (aums.studentRollNo != null ? aums.studentRollNo : "0"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        exitAums();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitAums();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitAums() {
        if (isLoggedIn) {
            if (backPressTime + 2000 > System.currentTimeMillis()) {
                toast.cancel();
                aums.logout();
                Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(baseContext, Landing.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                toast = Toast.makeText(getBaseContext(), "Press once again to Log Out of AUMS.", Toast.LENGTH_SHORT);
                toast.show();
            }
            backPressTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(baseContext, Landing.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aums != null) {
            try {
                aums.logout();
            } catch (Exception ignored) {

            }
        }
    }
}
