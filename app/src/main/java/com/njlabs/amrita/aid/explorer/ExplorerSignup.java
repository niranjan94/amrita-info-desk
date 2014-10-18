package com.njlabs.amrita.aid.explorer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.njlabs.amrita.aid.Landing;
import com.njlabs.amrita.aid.R;
import com.onemarker.ark.ConnectionDetector;

import org.acra.ACRA;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ExplorerSignup extends Activity {
    public String mobile_num = null;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_signup);
        getActionBar().hide();
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // proceed normally
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
        } else {

            // Internet connection is not present
            // Ask user to connect to Internet
            Builder builder = new Builder(this);    // ALERT DIALOG
            builder.setTitle("No Internet Connection")
                    .setMessage("A working internet connection is required for using Amrita Explorer !")
                    .setCancelable(false)
                    .setIcon(R.drawable.warning)
                    .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getBaseContext(), Landing.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    ////
    //// ASYNC TASK
    ////
    class PostRequestTask extends AsyncTask<ArrayList<String>, String, String> {
        private ProgressDialog dialog;
        @SuppressWarnings("unused")
        private Activity activity;
        private Builder dialog_alert;

        public PostRequestTask(Activity activity) {
            this.activity = activity;
            this.dialog = new ProgressDialog(activity);
            ///// ALERT DIALOG
            this.dialog_alert = new Builder(activity);

        }

        protected void onPreExecute() {
            this.dialog.setTitle("Communicating with my server");
            this.dialog.setCancelable(false);
            this.dialog.setMessage("Get ready to experience something wonderful !");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<String>... uri) {
            ArrayList<String> passed = uri[0];
            String Url = passed.get(0).toString();
            String name = passed.get(1).toString();
            String mobile = passed.get(2).toString();
            String email_id = passed.get(3).toString();

            HttpResponse response;
            String responseString = null;
            // Creating HTTP client
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpClient httpClient = new DefaultHttpClient(params);
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(Url);

            // Building post parameters
            // key and value pair

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
            nameValuePair.add(new BasicNameValuePair("name", name));
            nameValuePair.add(new BasicNameValuePair("mobile", mobile));
            nameValuePair.add(new BasicNameValuePair("email", email_id));
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String deviceid = telephonyManager.getDeviceId();
            nameValuePair.add(new BasicNameValuePair("deviceid", deviceid));
            mobile_num = mobile;

            // Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                ACRA.getErrorReporter().handleException(e);
            }

            // Making HTTP Request
            try {
                response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());

                }

            } catch (ClientProtocolException e) {
                // writing exception to log
                //progress.dismiss();
                ACRA.getErrorReporter().handleException(e);
            } catch (IOException e) {
                // writing exception to log
                //progress.dismiss();
                ACRA.getErrorReporter().handleException(e);

            }
            return responseString;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            result = result.trim().toLowerCase().toString();
            String status = null;
            String message = null;
            Log.d("HTTP PUSH RESULT", "..." + result);
            try {
                JSONObject json = new JSONObject(result);
                status = json.getString("status").trim();
                message = json.getString("message").trim();
            } catch (JSONException e) {
                ACRA.getErrorReporter().handleException(e);
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
                this.dialog_alert.setTitle("Oops !")
                        .setIcon(R.drawable.info)
                        .setMessage("Sorry ! You are not registered ! Please register now !")
                        .setCancelable(false)
                        .setPositiveButton("Got it :)", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = this.dialog_alert.create();
                alert.show();
            } else {
                this.dialog_alert.setTitle("Oops !")
                        .setIcon(R.drawable.info)
                        .setMessage("Looks like there was an error registering you !\n\nPlease try again later...\n\nERROR MESSAGE :" + result + "\n\nPlease file a bug report with this error\n")
                        .setCancelable(false)
                        .setPositiveButton("Oh Crap !", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = this.dialog_alert.create();
                alert.show();
            }


        }
    }

    public void go_back(View view) {


    }

    public void go_in(View view) {


    }

    ////////////
    ////////////
    @SuppressWarnings("unchecked")
    public void start(View view) {
        ////
        //// NAME ////
        ////
        EditText text = (EditText) findViewById(R.id.name);
        String name = text.getText().toString().trim();
        ////
        //// MOBILE /////
        ////
        text = (EditText) findViewById(R.id.mobile);
        String mobile = text.getText().toString().trim();
        ////
        //// EMAIL ////
        ////
        text = (EditText) findViewById(R.id.email_id);
        String email_id = text.getText().toString().trim();
        // CHeck for any missed fields
        if (name == null || name == "" || email_id == null || email_id == "" || mobile == null || mobile == "" || name.equals("") || email_id.equals("") || mobile.equals("")) {
            // Missing feilds . Warn user
            Builder builder = new Builder(this);    // ALERT DIALOG
            builder.setMessage("Please fill in all fields before starting...")
                    .setCancelable(false)
                    .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            PostRequestTask reqtask = new PostRequestTask(this);
            ArrayList<String> stringList = new ArrayList<String>(); //Generic ArrayList to Store only String objects
            stringList.add("http://njlabs.kovaideals.com/api/aid/explorer.php?type=new");
            stringList.add(name);
            stringList.add(mobile);
            mobile_num = mobile;
            stringList.add(email_id);
            reqtask.execute(stringList);

        }

    }

    @SuppressWarnings("unchecked")
    public void already_registered(final View view) {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        String mobile_no = mTelephonyMgr.getLine1Number();
        if (mobile_no != null && mobile_no.length() < 10) {
            final Builder alert = new Builder(this);
            alert.setTitle("Mobile Number ?");
            alert.setMessage("Please enter your 10 digit mobile number for verification");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setHint("10-digit mobile number");
            alert.setView(input);

            alert.setPositiveButton("Let's get started !", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString().trim();

                    if (value == null || value.equals("") || value.equals(" ") || value.length() < 10) {
                        final Builder builder = new Builder(view.getContext());    // ALERT DIALOG
                        builder.setTitle("Oops !")
                                .setMessage("Please fill in your mobile number before starting...")
                                .setCancelable(false)
                                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();

                                    }
                                });
                        builder.show();
                    } else {
                        PostRequestTask reqtask = new PostRequestTask(ExplorerSignup.this);
                        ArrayList<String> stringList = new ArrayList<String>(); //Generic ArrayList to Store only String objects
                        stringList.add("http://njlabs.kovaideals.com/api/aid/explorer.php?type=check");
                        stringList.add("name");
                        stringList.add(value);
                        mobile_num = value;
                        stringList.add("email");
                        reqtask.execute(stringList);

                    }
                }
            });
            alert.show();
        } else {
            PostRequestTask reqtask = new PostRequestTask(ExplorerSignup.this);
            ArrayList<String> stringList = new ArrayList<String>(); //Generic ArrayList to Store only String objects
            stringList.add("http://njlabs.kovaideals.com/api/aid/explorer.php?type=check");
            stringList.add("name");
            mobile_num = mobile_no;
            stringList.add(mobile_no);
            stringList.add("email");
            reqtask.execute(stringList);
        }

    }

}
