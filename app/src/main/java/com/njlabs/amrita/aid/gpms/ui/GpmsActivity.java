/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import com.jakewharton.picasso.OkHttp3Downloader;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.bugs.BugReport;
import com.njlabs.amrita.aid.gpms.client.AbstractGpms;
import com.njlabs.amrita.aid.gpms.client.Gpms;
import com.njlabs.amrita.aid.gpms.models.Relay;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.landing.Landing;
import com.njlabs.amrita.aid.util.Identifier;
import com.njlabs.amrita.aid.util.ark.Security;
import com.onemarker.ln.logger.Ln;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class GpmsActivity extends BaseActivity {

    private static long backPress;
    public String identifier;
    public ArrayList<Relay> relays;
    AbstractGpms gpms;
    EditText rollNoEditText;
    EditText passwordEditText;
    SharedPreferences preferences;
    private ProgressDialog dialog;
    private String studentRollNo;
    private String studentName;
    private Boolean loggedIn = false;
    private Toast toast;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_gpms_login, Color.parseColor("#009688"));
        rollNoEditText = (EditText) findViewById(R.id.roll_no);
        passwordEditText = (EditText) findViewById(R.id.pwd);


        AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
        builder.setMessage("Amrita University does not provide an API for accessing GPMS data. " +
                "So, if any changes are made to the GPMS Website, please be patient while I try to catch up.")
                .setCancelable(true)
                .setIcon(R.drawable.ic_action_info_small)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        hideSoftKeyboard();
                        showConnectToAmritaAlert();
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
        preferences = getSharedPreferences("gpms_prefs", Context.MODE_PRIVATE);
        String rollNo = preferences.getString("roll_no", "");
        String encodedPassword = preferences.getString("password", "");
        if (!rollNo.equals("")) {
            rollNoEditText.setText(rollNo);
            studentRollNo = rollNo;
            hideSoftKeyboard();
        } else {
            SharedPreferences aumsPrefs = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
            String aumsRollNo = aumsPrefs.getString("RollNo", "");
            if (!aumsRollNo.equals("")) {
                rollNoEditText.setText(aumsRollNo);
                studentRollNo = aumsRollNo;
                hideSoftKeyboard();
            }
        }

        if (!encodedPassword.equals("")) {
            passwordEditText.setText(Security.decrypt(encodedPassword, MainApplication.key));
            hideSoftKeyboard();
        }
    }

    private void initialiseGpms() {
        gpms = new Gpms(baseContext);
    }

    private void showConnectToAmritaAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
        builder.setMessage("Connect to the Amrita WiFi if possible, for a reliable connection to the GPMS Server.")
                .setCancelable(true)
                .setIcon(R.drawable.ic_action_info_small)
                .setPositiveButton("Ok. I'll connect.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("No. I won't.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        initialiseGpms();
                        hideSoftKeyboard();
                    }
                });

        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!Identifier.isConnectedToAmrita(baseContext)) {
            alert.show();
        } else {
            initialiseGpms();
        }
    }

    public void reset(View v) {
        rollNoEditText.setText("");
        passwordEditText.setText("");
    }

    public void loginStart(View view) {
        boolean hasError = false;
        final String rollNo = rollNoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (StringUtils.isEmpty(rollNo)) {
            hasError = true;
            rollNoEditText.setError("Your Roll Number is required");
        }

        if (StringUtils.isEmpty(password)) {
            hasError = true;
            passwordEditText.setError("Your GPMS password is required");
        }

        if (!hasError) {
            hideSoftKeyboard();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("roll_no", rollNoEditText.getText().toString());
            editor.putString("password", Security.encrypt(passwordEditText.getText().toString(), MainApplication.key));
            editor.apply();
            dialog.show();

            gpms.logout();
            gpms.login(rollNo, password, new InfoResponse() {

                @Override
                public void onSuccess() {
                    showError();
                }

                @Override
                public void onFailedAuthentication() {
                    showIncorrectError();
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(String regNo, String name, String hostel, String roomNo, String mobile, String email, String photoUrl, String numPasses) {
                    setupLayout(R.layout.activity_gpms_profile, Color.parseColor("#009688"));
                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.roll_no)).setText(regNo);
                    ((TextView) findViewById(R.id.mobile)).setText(mobile);
                    ((TextView) findViewById(R.id.hostel)).setText(hostel + " - " + roomNo);
                    getSupportActionBar().setSubtitle("Logged in as " + name);
                    studentName = name;
                    ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
                    Picasso picasso = getUnsecuredPicassoDownloader();
                    picasso.load(photoUrl).error(R.drawable.user).into(profilePic);
                    hideProgress();
                    loggedIn = true;

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "GPMS");
                    bundle.putString(FirebaseAnalytics.Param.CHARACTER, name + " - " + regNo);
                    tracker.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                }

                @Override
                public void onFailure(Throwable throwable) {
                    Ln.e(throwable);
                    showError();
                }
            });
        }
    }

    public void openApply(View v) {
        final String[] items = {"Day Pass", "Home Pass"};
        AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
        builder.setCancelable(true);
        builder.setTitle("Pass type ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogList, int item) {
                Intent intent = new Intent(baseContext, PassApplyActivity.class);
                intent.putExtra("pass_type", items[item]);
                if (relays != null && relays.size() > 0 && identifier != null) {
                    intent.putParcelableArrayListExtra("relays", relays);
                    intent.putExtra("identifier", identifier);
                }
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void openPassStatus(View v) {
        Intent pendingPassActivityIntent = new Intent(baseContext, PendingPassActivity.class);
        if (relays != null && relays.size() > 0 && identifier != null) {
            pendingPassActivityIntent.putParcelableArrayListExtra("relays", relays);
            pendingPassActivityIntent.putExtra("identifier", identifier);
        }
        startActivity(pendingPassActivityIntent);
    }

    public void openPassesHistory(View v) {

        Intent passHistoryActivityIntent = new Intent(baseContext, PassHistoryActivity.class);
        if (relays != null && relays.size() > 0 && identifier != null) {
            passHistoryActivityIntent.putParcelableArrayListExtra("relays", relays);
            passHistoryActivityIntent.putExtra("identifier", identifier);
        }
        startActivity(passHistoryActivityIntent);
    }

    private void showError() {
        Snackbar.make(parentView, "Cannot connect to Server. Try again later.", Snackbar.LENGTH_LONG).show();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void showIncorrectError() {
        Snackbar.make(parentView, "Credentials Incorrect.", Snackbar.LENGTH_LONG).show();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public Picasso getUnsecuredPicassoDownloader() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }


                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());

            Picasso.Builder builder = new Picasso.Builder(baseContext).listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso arg0, Uri arg1, Exception ex) {
                    ex.printStackTrace();

                }
            });

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sc.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();

            Downloader downloader = new OkHttp3Downloader(client);
            return builder.downloader(downloader).build();
        } catch (Exception ignored) {
            Picasso.Builder builder = new Picasso.Builder(baseContext).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso arg0, Uri arg1, Exception ex) {
                    ex.printStackTrace();
                }
            });
            return builder.build();
        }

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
                exitGpms();
                return true;
            case R.id.action_bug_report:
                Intent intent = new Intent(getApplicationContext(), BugReport.class);
                intent.putExtra("studentName", (studentName != null ? studentName : "Anonymous"));
                intent.putExtra("studentRollNo", (studentRollNo != null ? studentRollNo : "0"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        exitGpms();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitGpms();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitGpms() {
        if (loggedIn) {
            if (backPress + 2000 > System.currentTimeMillis()) {
                toast.cancel();
                gpms.logout();
                Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(baseContext, Landing.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                toast = Toast.makeText(getBaseContext(), "Press once again to Log Out of GPMS.", Toast.LENGTH_SHORT);
                toast.show();
            }
            backPress = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(baseContext, Landing.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
