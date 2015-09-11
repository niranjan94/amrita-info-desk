package com.njlabs.amrita.aid.explorer;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ExplorerSignup extends BaseActivity {

    public String mobile_num = null;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_explorer_signup, Color.parseColor("#3f51b5"));

            TelephonyManager mTelephonyMgr;
            mTelephonyMgr = (TelephonyManager)
                    getSystemService(Context.TELEPHONY_SERVICE);
            String mobile_no = mTelephonyMgr.getLine1Number();
            EditText mobile = (EditText) findViewById(R.id.mobile);
            if (mobile_no == null || mobile_no.length() < 10) {
                mobile.setHint("Mobile number (with +91)");
                mobile.setEnabled(true);
                mobile.setInputType(InputType.TYPE_CLASS_TEXT);
                mobile.setFocusable(true);
            } else {
                mobile.setText(mobile_no);
                mobile.setEnabled(false);
                mobile.setInputType(InputType.TYPE_NULL);
                mobile.setFocusable(false);
            }

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating your credentials ... ");

    }

    public void signup(String name, String mobile, String email, String type)
    {
        dialog.setMessage("Contacting Server ...");
        dialog.show();
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("mobile", mobile);
        params.put("email", email);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = telephonyManager.getDeviceId();

        params.put("deviceid", deviceid);
        if(type.equals("new"))
        {
            dialog.setMessage("Signing up ...");
        }
        else
        {
            dialog.setMessage("Verifying your existing registration data ...");
        }

        ExplorerClient.post("/explorer.php?type="+type, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                dialog.dismiss();
                Ln.e("ERROR: "+statusCode+" MESSAGE: "+responseString);
                Snackbar
                        .make(parentView, "Cannot connect to Server. Try again later.", Snackbar.LENGTH_LONG)
                        .show();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Ln.d(response.toString());
                dialog.dismiss();
                String status = null;
                String message = null;
                try {
                    status = response.getString("status").trim();
                    message = response.getString("message").trim();
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
                if (status != null) {
                    switch (status) {
                        case "success":
                            ////
                            //// STORE DATA TO SHARED PREFERENCES
                            ////
                            SharedPreferences preferences = getSharedPreferences("pref", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("explorer_is_registered", "yes");
                            editor.putString("mobile_number", mobile_num);

                            // commit the edits
                            editor.apply();
                            //// START INTENT
                            Intent done = new Intent(ExplorerSignup.this, Explorer.class);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            done.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(done);

                            break;
                        case "noreg":
                            Snackbar
                                    .make(parentView, "You have not yet registered. Please register now", Snackbar.LENGTH_LONG)
                                    .show();
                            break;

                        default:
                            Snackbar
                                    .make(parentView, "Cannot connect to Server. Try again later.", Snackbar.LENGTH_LONG)
                                    .show();
                            break;

                    }
                }
            }

        });

    }

    public void goBack(View view) {
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @SuppressWarnings("unchecked")
    public void start(View view) {

        EditText text = (EditText) findViewById(R.id.name);
        String name = text.getText().toString().trim();

        text = (EditText) findViewById(R.id.mobile);
        String mobile = text.getText().toString().trim();

        text = (EditText) findViewById(R.id.email_id);
        String email_id = text.getText().toString().trim();
        if (name.equals("") || email_id.equals("") || mobile.equals("") || name.equals("") || email_id.equals("") || mobile.equals("")) {
            Snackbar
                    .make(parentView, "Please fill in all the details.", Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            mobile_num = mobile;
            signup(name,mobile,email_id,"new");
        }
    }

    @SuppressWarnings("unchecked")
    public void alreadyRegistered(final View view) {

        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogEntryView = factory.inflate(R.layout.dialog_input, null);
        final Builder alert = new Builder(this);
        alert.setView(dialogEntryView);

        alert.setPositiveButton("Let's get started !", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = ((EditText)dialogEntryView.findViewById(R.id.mobile)).getText().toString().trim();
                if (value.equals("") || value.equals(" ") || value.length() < 10) {
                    Snackbar
                            .make(parentView, "Please fill in your mobile number.", Snackbar.LENGTH_SHORT)
                            .show();

                } else {
                        signup("name",value,"email","check");
                    }
                }
            });
            alert.show();


    }
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
