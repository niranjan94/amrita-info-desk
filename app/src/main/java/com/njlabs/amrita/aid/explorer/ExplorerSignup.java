package com.njlabs.amrita.aid.explorer;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.njlabs.amrita.aid.R;
import com.onemarker.ark.logging.Ln;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ExplorerSignup extends AppCompatActivity {

    public String mobile_num = null;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_signup);


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
                SuperToast superToast = new SuperToast(ExplorerSignup.this);
                superToast.setDuration(SuperToast.Duration.LONG);
                superToast.setAnimations(SuperToast.Animations.FLYIN);
                superToast.setBackground(SuperToast.Background.RED);
                superToast.setTextColor(Color.WHITE);
                superToast.setText("Cannot connect to Server. Try again later.");
                superToast.show();
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
                    // TODO ACRA.getErrorReporter().handleException(e);
                }
                if (status.equals("success")) {
                    ////
                    //// STORE DATA TO SHARED PREFERENCES
                    ////
                    SharedPreferences preferences = getSharedPreferences("pref", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("explorer_is_registered", "yes");
                    editor.putString("mobile_number", mobile_num);

                    // commit the edits
                    editor.commit();
                    //// START INTENT
                    Intent done = new Intent(ExplorerSignup.this, Explorer.class);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    done.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(done);

                } else if (status.equals("noreg")) {
                    SuperToast superToast = new SuperToast(ExplorerSignup.this);
                    superToast.setDuration(SuperToast.Duration.LONG);
                    superToast.setAnimations(SuperToast.Animations.FLYIN);
                    superToast.setBackground(SuperToast.Background.RED);
                    superToast.setTextColor(Color.WHITE);
                    superToast.setText("You are not registered ! Please register now.");
                    superToast.show();
                } else {

                    SuperToast superToast = new SuperToast(ExplorerSignup.this);
                    superToast.setDuration(SuperToast.Duration.LONG);
                    superToast.setAnimations(SuperToast.Animations.FLYIN);
                    superToast.setBackground(SuperToast.Background.RED);
                    superToast.setTextColor(Color.WHITE);
                    superToast.setText("Server Error ! Please try again later.");
                    superToast.show();
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
        // Check for any missed fields
        if (name == null || name == "" || email_id == null || email_id == "" || mobile == null || mobile == "" || name.equals("") || email_id.equals("") || mobile.equals("")) {
            // Missing feilds . Warn user
            SuperToast superToast = new SuperToast(ExplorerSignup.this);
            superToast.setDuration(SuperToast.Duration.LONG);
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setBackground(SuperToast.Background.BLACK);
            superToast.setTextColor(Color.WHITE);
            superToast.setText("Please fill in all the details.");
            superToast.show();
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

                if (value == null || value.equals("") || value.equals(" ") || value.length() < 10) {

                        SuperToast superToast = new SuperToast(ExplorerSignup.this);
                        superToast.setDuration(SuperToast.Duration.LONG);
                        superToast.setAnimations(SuperToast.Animations.FLYIN);
                        superToast.setBackground(SuperToast.Background.BLACK);
                        superToast.setTextColor(Color.WHITE);
                        superToast.setText("Please fill in your mobile number.");
                        superToast.show();

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
