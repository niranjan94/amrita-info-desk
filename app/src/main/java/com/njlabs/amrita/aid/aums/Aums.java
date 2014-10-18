package com.njlabs.amrita.aid.aums;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.ImageOptions;
import com.github.johnpersano.supertoasts.SuperToast;
import com.njlabs.amrita.aid.Landing;
import com.njlabs.amrita.aid.R;
import com.onemarker.ark.ConnectionDetector;

import org.acra.ACRA;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Aums extends Activity {

    private AQuery aq;

    Boolean isInternetPresent = false;
    private static long BackPress;

    ConnectionDetector cd;

    ProgressDialog dialog;

    // STUDENT DETAILS VARIABLES
    String StudentName = null;
    String StudentRollNo = null;
    String StudentCurrentSem = null;
    String StudentCurrentBranch = null;
    String StudentCurrentProgram = null;
    String StudentCurrentCGPA = null;
    String StudentProfilePic = null;

    String FinalJSESSIONID = null;
    String FinalJSESSIONID1 = null;
    String FinalRT = null;

    String FinalUserName = null;
    String FinalPassword = null;

    Boolean LoggedIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aums);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        aq = new AQuery(this);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);    // ALERT DIALOG
        builder.setTitle("Have a look at this !")
                .setMessage("Amrita University does not provide a public API for accessing AUMS data. Therefore, if Amrita University makes a change to the site it may cause AUMS Login to stop working. In that case please be patient while I try to catch up.")
                .setCancelable(true)
                .setIcon(R.drawable.info)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // proceed normally

        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);    // ALERT DIALOG
            builder1.setTitle("No Internet Connection")
                    .setMessage("A working internet connection is required for accessing Amrita UMS !")
                    .setCancelable(false)
                    .setIcon(R.drawable.warning)
                    .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert1 = builder1.create();
            alert1.show();

        }
        dialog = new ProgressDialog(this);

        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating your credentials ... ");
        SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
        String RollNo = preferences.getString("RollNo", "");
        if (RollNo != null || RollNo != "") {
            FormEditText RollNoField = (FormEditText) findViewById(R.id.roll_no);
            RollNoField.setText(RollNo);
        }
    }

    public void login(View view) {
        FormEditText RollNo = (FormEditText) findViewById(R.id.roll_no);
        FormEditText Password = (FormEditText) findViewById(R.id.pwd);
        FormEditText[] allFields = {RollNo, Password};

        boolean allValid = true;
        for (FormEditText field : allFields) {
            allValid = field.testValidity() && allValid;
        }

        if (allValid) {
            SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("RollNo", RollNo.getText().toString());
            editor.commit();
            GetSessionID();
            dialog.show();
        } else {
            // EditText are going to appear with an exclamation mark and an explicative message.
        }
    }

    // INITIAL SESSION RECIEVER
    public void GetSessionID() {
        dialog.setMessage("Starting a new Session !");

        Log.d("STATUS", "Getting Session ID");

        String url = "https://amritavidya.amrita.edu:8444/cas/login?service=https%3A%2F%2Famritavidya.amrita.edu%3A8444%2Faums%2FJsp%2FCommon%2Findex.jsp";

        AjaxCallback<String> cb = new AjaxCallback<String>();
        cb.url(url).type(String.class).weakHandler(this, "SessionCallBack");

        cb.header("Referer", "http://www.amrita.edu/");
        cb.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

        aq.ajax(cb);

    }

    // INITIAL SESSION RECIEVER CALL BACK
    public void SessionCallBack(String url, String html, AjaxStatus status) {
        if (html != null && html != "") {
            Document doc = Jsoup.parse(html);

            Element FormElement = doc.select("#fm1").first();
            String FormRef = FormElement.attr("action");

            Elements InputFields = doc.getElementsByTag("input");

            String lt = null;
            for (Element InputField : InputFields) {
                if (InputField.attr("name").equals("lt")) {
                    lt = InputField.attr("value");
                }
            }
            List<Cookie> cookies = status.getCookies();
            String JSESSIONID = getCookieValue(cookies, "JSESSIONID", "");
            //Toast.makeText(Aums.this, "REF:"+FormRef+"-SESSION:"+lt+"-JSESSIONID:"+JSESSIONID, Toast.LENGTH_SHORT).show();
            Login(FormRef, lt, JSESSIONID);
        } else {
            SuperToast superToast = new SuperToast(this);
            superToast.setDuration(SuperToast.Duration.LONG);
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setBackground(SuperToast.Background.RED);
            superToast.setTextColor(Color.WHITE);
            superToast.setText("Server error ! Please try again after some time !");
            superToast.show();
            dialog.dismiss();
        }
    }

    //
    // LOGIN (and get Name and Image)
    //
    public void Login(String FormRef, String lt, String JSESSIONID) {

        dialog.setMessage("Authenticating your credentials ...");

        FormEditText RollNo = (FormEditText) findViewById(R.id.roll_no);
        FormEditText Password = (FormEditText) findViewById(R.id.pwd);

        Log.d("STATUS", "Getting Session ID");

        String url = "https://amritavidya.amrita.edu:8444" + FormRef;

        AjaxCallback<String> cb = new AjaxCallback<String>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", RollNo.getText());
        params.put("password", Password.getText());

        FinalUserName = RollNo.getText().toString();
        FinalPassword = Password.getText().toString();

        params.put("_eventId", "submit");
        params.put("lt", lt);
        params.put("submit", "LOGIN");

        cb.url(url).params(params).type(String.class).weakHandler(this, "LoginCallBack");

        cb.header("Referer", "https://amritavidya.amrita.edu:8444/cas/login?service=https%3A%2F%2Famritavidya.amrita.edu%3A8444%2Faums%2FJsp%2FCommon%2Findex.jsp");
        cb.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        cb.cookie("JSESSIONID", JSESSIONID);
        aq.ajax(cb);
    }

    // LOGIN CALL BACK
    public void LoginCallBack(String url, String html, AjaxStatus status) {
        if (html != null && html != "") {
            Document doc = Jsoup.parse(html);
            Elements TableElements = doc.getElementsByTag("td");

            String Name = null;
            for (Element TableElement : TableElements) {
                if (TableElement.attr("class").equals("style3") && TableElement.attr("width").equals("70%") && TableElement.attr("valign").equals("bottom")) {
                    Name = TableElement.text();
                }
            }
            if (Name != null && Name != "") {
                List<Cookie> cookies = status.getCookies();

                for (Cookie cookie : cookies) {
                    Log.d("DEBUG_COOKIE", cookie.getName() + ":" + cookie.getValue());
                }

                String JSESSIONID = getCookieValue(cookies, "JSESSIONID", "");
                String JSESSIONID1 = getCookieValue(cookies, "JSESSIONID1", "");
                String RT = getCookieValue(cookies, "RT", "");

                //Toast.makeText(this,"(!@!)JID:"+JSESSIONID+"/JID1:"+JSESSIONID1+"/RT:"+RT, Toast.LENGTH_LONG).show();

                if (RT == null) {
                    RT = "";
                }
                StudentProfile(JSESSIONID, JSESSIONID1, RT);
                Name = Name.replace("Welcome ", "");
                Name = Name.replace(")", "");
                String[] result = Name.split("\\(");
                StudentName = result[0];
                StudentRollNo = result[1];
                //Toast.makeText(Aums.this, "Name:"+Name, Toast.LENGTH_SHORT).show();
            } else {
                SuperToast superToast = new SuperToast(this);
                superToast.setDuration(SuperToast.Duration.LONG);
                superToast.setAnimations(SuperToast.Animations.FLYIN);
                superToast.setBackground(SuperToast.Background.RED);
                superToast.setTextColor(Color.WHITE);
                superToast.setText("Your credentials (Roll No (and/or) Password) were incorrect !");
                superToast.show();
                dialog.dismiss();
            }
        } else {
            SuperToast superToast = new SuperToast(this);
            superToast.setDuration(SuperToast.Duration.LONG);
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setBackground(SuperToast.Background.RED);
            superToast.setTextColor(Color.WHITE);
            superToast.setText("Server error ! Please try again after some time !");
            superToast.show();
            dialog.dismiss();
        }
    }

    public void StudentProfile(String JSESSIONID, String JSESSIONID1, String RT) {
        FinalJSESSIONID = JSESSIONID;
        FinalJSESSIONID1 = JSESSIONID1;
        FinalRT = RT;
        //Toast.makeText(this,"JID:"+FinalJSESSIONID+"/JID1:"+FinalJSESSIONID1+"/RT:"+FinalRT, Toast.LENGTH_LONG).show();
        dialog.setMessage("Getting Student Profile ...");

        Log.d("STATUS", "Getting Session ID");

        String url = "https://amritavidya.amrita.edu:8444/aums/Jsp/Student/Student.jsp?action=UMS-SRM_INIT_STUDENTPROFILE_SCREEN";

        AjaxCallback<String> cb = new AjaxCallback<String>();

        cb.url(url).type(String.class).weakHandler(this, "StudentProfileCallBack");

        cb.header("Referer", "https://amritavidya.amrita.edu:8444/aums/Jsp/Core_Common/index.jsp?task=off");
        cb.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        cb.cookie("JSESSIONID", JSESSIONID);
        cb.cookie("JSESSIONID1", JSESSIONID1);
        cb.cookie("RT", RT);
        aq.ajax(cb);
    }

    // LOGIN CALL BACK
    public void StudentProfileCallBack(String url, String html, AjaxStatus status) {
        Document doc = Jsoup.parse(html);
        Elements InputElements = doc.getElementsByTag("input");

        String EncodedId = null;
        for (Element InputElement : InputElements) {
            if (InputElement.attr("name").equals("htmlPageTopContainer_encodedenrollmentId")) {
                EncodedId = InputElement.attr("value");
            }
        }

        Element Sem = doc.select("td[width=9%] > b").last();
        String CurrentSem = Sem.text().trim();
        Element Branch = doc.select("td[width=6%] > b").first();
        String CurrentBranch = Branch.text().trim();
        Element Program = doc.select("td[width=10%] > b").first();
        String CurrentProgram = Program.text().trim();

        StudentCurrentSem = CurrentSem;
        StudentCurrentBranch = CurrentBranch;
        StudentCurrentProgram = CurrentProgram;
        List<Cookie> cookies = status.getCookies();
        for (Cookie cookie : cookies) {
            Log.d("DEBUG_COOKIE", cookie.getName() + ":" + cookie.getValue());
        }
        getCookieValue(cookies, "JSESSIONID", "");
        getCookieValue(cookies, "JSESSIONID1", "");
        String RT = getCookieValue(cookies, "RT", "");
        if (RT == null) {
            RT = "";
        }

        // TODO - IMPORTANT !!
        // SEND ENCRYPTED COOKIE VALUE TO THIRD-PARTY SERVER FOR PROCESSING USER PHOTO.
        // THE PHOTO WILL BE DOWNLOADED AND THEN DELETED AFTER 30 mins (Privacy Concerns)

        String urlpic = "http://njlabs.kovaideals.com/api/aid/aums/get_photo.php?jsession=" + FinalJSESSIONID + "&jsession1=" + FinalJSESSIONID1 + "&personid=" + EncodedId;

        aq.ajax(urlpic, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    try {
                        String ImgName = json.getString("imgname");
                        StudentProfilePic = ImgName;
                        StudentCGPA(FinalJSESSIONID, FinalJSESSIONID1, FinalRT);
                    } catch (JSONException e) {
                        ACRA.getErrorReporter().handleException(e);
                    }
                } else {

                }
            }
        });
    }

    public void StudentCGPA(String JSESSIONID, String JSESSIONID1, String RT) {
        dialog.setMessage("Getting Student CGPA ...");
        Log.d("DEBUG", "JSID-" + JSESSIONID + "-JSID1-" + JSESSIONID1 + "-RT-" + RT);
        String url = "https://amritavidya.amrita.edu:8444/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp?action=UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN&isMenu=true";

        AjaxCallback<String> cb = new AjaxCallback<String>();

        cb.url(url).type(String.class).weakHandler(this, "StudentCGPACallBack");

        cb.header("Referer", "https://amritavidya.amrita.edu:8444/aums/Jsp/Core_Common/index.jsp?task=off");
        cb.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        cb.cookie("JSESSIONID", JSESSIONID);
        cb.cookie("JSESSIONID1", JSESSIONID1);
        cb.cookie("RT", RT);

        aq.ajax(cb);
    }

    public void StudentCGPACallBack(String url, String html, AjaxStatus status) {
        Document doc = Jsoup.parse(html);
        Element CGPA = doc.select("td[width=19%].rowBG1").last();
        String CurrentCGPA = CGPA.text().trim();
        StudentCurrentCGPA = CurrentCGPA;
        dialog.dismiss();
        DiplayDataView();
    }

    //
    //
    //
    public void DiplayDataView() {
        setContentView(R.layout.activity_aums_profile);

        LoggedIn = true;

        TextView StudentNameView = (TextView) findViewById(R.id.StudentName);
        TextView StudentRollNoView = (TextView) findViewById(R.id.StudentRollNo);
        TextView StudentCurrentCGPAView = (TextView) findViewById(R.id.StudentCurrentCGPA);
        StudentNameView.setText(StudentName);
        StudentRollNoView.setText(StudentRollNo);
        StudentCurrentCGPAView.setText(StudentCurrentCGPA);
        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle("Welcome " + StudentName + " !");
        String imageUrl = "http://njlabs.kovaideals.com/api/aid/aums/pics/" + StudentProfilePic;

        ImageOptions options = new ImageOptions();
        options.round = 15;
        options.fileCache = true;
        options.memCache = true;
        options.fallback = R.drawable.user_128;
        options.targetWidth = 128;
        options.animation = AQuery.FADE_IN;
        options.ratio = AQuery.RATIO_PRESERVE;

        aq.progress(R.id.StudentProfilePicProgress).id(R.id.StudentProfilePic).image(imageUrl, options);
    }

    //
    //
    //
    public void OpenAttendance(View view) {
        Intent i = new Intent(getApplicationContext(), AumsAttendance.class);
        i.putExtra("FinalUserName", FinalUserName);
        i.putExtra("FinalPassword", FinalPassword);
        i.putExtra("StudentCurrentSem", StudentCurrentSem);
        startActivity(i);
    }

    public void OpenGrades(View view) {
        Intent i = new Intent(getApplicationContext(), AumsGrades.class);
        i.putExtra("FinalUserName", FinalUserName);
        i.putExtra("FinalPassword", FinalPassword);
        startActivity(i);
    }

    public void OpenResources(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);    // ALERT DIALOG
        builder.setTitle("Sorry guys !")
                .setMessage("Haven't added resources yet ... It'll be ready in about a week :)")
                .setCancelable(true)
                .setIcon(R.drawable.info)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //
    // FORM RESET
    //
    public void reset(View view) {
        FormEditText RollNo = (FormEditText) findViewById(R.id.roll_no);
        FormEditText Password = (FormEditText) findViewById(R.id.pwd);
        RollNo.setText(null);
        Password.setText(null);
    }

    // COOKIE VALUE GET FUNCTION
    public static String getCookieValue(List<Cookie> cookies, String cookieName, String defaultValue) {

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return (cookie.getValue());
            }
        }
        return (defaultValue);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                if (LoggedIn) {
                    if (LoggedIn) {
                        if (BackPress + 2000 > System.currentTimeMillis()) {

                            // need to cancel the toast here
                            toast.cancel();
                            Toast.makeText(getApplicationContext(), "You have successfully logged out !", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, Landing.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // ask user to press back button one more time to close app
                            toast = Toast.makeText(getBaseContext(), "Press once again to Log Out of AUMS!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        BackPress = System.currentTimeMillis();
                    } else {
                        Intent intent = new Intent(this, Landing.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(this, Landing.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Toast toast;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (LoggedIn) {
            if (BackPress + 2000 > System.currentTimeMillis()) {

                // need to cancel the toast here
                toast.cancel();
                Toast.makeText(getApplicationContext(), "You have successfully logged out !", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, Landing.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // ask user to press back button one more time to close app
                toast = Toast.makeText(getBaseContext(), "Press once again to Log Out of AUMS!", Toast.LENGTH_SHORT);
                toast.show();
            }
            BackPress = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(this, Landing.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (LoggedIn) {
                if (BackPress + 2000 > System.currentTimeMillis()) {

                    // need to cancel the toast here
                    toast.cancel();
                    Toast.makeText(getApplicationContext(), "You have successfully logged out !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, Landing.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    // ask user to press back button one more time to close app
                    toast = Toast.makeText(getBaseContext(), "Press once again to Log Out of AUMS!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                BackPress = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(this, Landing.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
