package com.njlabs.amrita.aid.aums;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Size;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.bugs.BugReport;
import com.njlabs.amrita.aid.classes.CourseData;
import com.njlabs.amrita.aid.landing.Landing;
import com.njlabs.amrita.aid.util.ark.Security;
import com.njlabs.amrita.aid.util.ark.Util;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class Aums extends BaseActivity {

    private static long BackPress;

    private ProgressDialog dialog = null;
    public AumsClient client;

    // STUDENT DETAILS VARIABLES
    private String studentName = null;
    private String studentRollNo = null;
    private String studentCurrentSem = null;
    private String studentCurrentCGPA = null;

    private String studentHashId = null;

    private String username = null;
    private String password = null;

    private Boolean loggedIn = false;

    private String methodToCall = null;


    private Map<String,String> semesterMapping = new HashMap<>();

    int gradeRefIndex=1;
    int attendanceRefIndex=1;

    private String calledBy = "activity";
    private Context serviceContext;

    @NotEmpty(message = "Roll number is required")
    @Size(min = 16, max = 16, message = "Invalid roll number")
    EditText rollNoEditText;

    @NotEmpty(message = "Password is required")
    EditText passwordEditText;

    public Aums() {

    }

    public Aums(Context context, String calledBy, String username, String password) {
        loadSemesterMapping();
        this.calledBy = calledBy;
        serviceContext = context;
        this.username = username;
        this.password = password;
    }

    public Aums(Context context, String calledBy, String username, String password, ProgressDialog dialog, String methodToCall) {
        loadSemesterMapping();
        this.calledBy = calledBy;
        serviceContext = context;
        this.username = username;
        this.password = password;
        this.dialog = dialog;
        this.methodToCall = methodToCall;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLayout(R.layout.activity_aums, Color.parseColor("#e91e63"));

        rollNoEditText = (EditText) findViewById(R.id.roll_no);
        passwordEditText = (EditText) findViewById(R.id.pwd);

        serviceContext = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);    // ALERT DIALOG
        builder .setMessage("Amrita University does not provide an API for accessing AUMS data. " +
                "So, if any changes are made to the AUMS Website, please be patient while I try to catch up.")
                .setCancelable(true)
                .setIcon(R.drawable.ic_action_info_small)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();


        dialog = new ProgressDialog(serviceContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating your credentials ... ");
        SharedPreferences preferences = serviceContext.getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
        String RollNo = preferences.getString("RollNo", "");
        String encodedPassword = preferences.getString("Password","");
        if (!RollNo.equals("")) {
            ((EditText) findViewById(R.id.roll_no)).setText(RollNo);
            studentRollNo = RollNo;
            hideSoftKeyboard();
        }
        if(!encodedPassword.equals("")) {
            ((EditText)findViewById(R.id.pwd)).setText(Security.decrypt(encodedPassword,MainApplication.key));
            hideSoftKeyboard();
        }
        loadSemesterMapping();
    }

    public void loginStart(View view) {


        Validator validator = new Validator(this);
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                hideSoftKeyboard();
                SharedPreferences preferences = serviceContext.getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("RollNo", rollNoEditText.getText().toString());
                editor.putString("Password", Security.encrypt(passwordEditText.getText().toString(), MainApplication.key));
                editor.apply();

                username = rollNoEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                refreshSession();

                client = new AumsClient(serviceContext);
                GetSessionID("https://amritavidya.amrita.edu:8444",false);
                dialog.show();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(baseContext);

                    // Display error messages ;)
                    if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                    } else {
                        Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        validator.validate();

    }

    // INITIAL SESSION RECEIVER
    public void GetSessionID(String baseURL, Boolean retry) {

        if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null)) {
            if (retry) {
                dialog.setMessage("Starting a new session with a different server");
            } else
                dialog.setMessage("Starting a new Session !");
        }
        if (retry) {
            refreshSession();
            client = new AumsClient(serviceContext);
        }

        client.setBaseURL(baseURL);

        Ln.d("Starting Session");
        RequestParams params = new RequestParams();
        params.put("service", client.BASE_URL+"/aums/Jsp/Common/index.jsp");

        client.get("/cas/login", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Session ERROR. Code:%s Response:%s", statusCode, responseString);

                switch (client.BASE_URL) {
                    case "https://amritavidya.amrita.edu:8444":
                        GetSessionID("https://amritavidya1.amrita.edu:8444", true);
                        break;
                    case "https://amritavidya1.amrita.edu:8444":
                        GetSessionID("https://amritavidya2.amrita.edu:8444", true);
                        break;
                    default:
                        serverError();
                        closeSession(true);
                        break;
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String formAction;
                String lt;

                Document doc = Jsoup.parse(responseString);
                Element form = doc.select("#fm1").first();
                formAction = form.attr("action");

                Element hiddenInput = doc.select("input[name=lt]").first();
                lt = hiddenInput.attr("value");

                Ln.d("Session Done. action=%s lt=%s", formAction, lt);
                login(formAction, lt);
            }
        });

    }

    public void login(String formAction, String lt) {
        if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null)) {
            dialog.setMessage("Authenticating your credentials ...");
        }
        Ln.d("Start Login");
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("_eventId", "submit");
        params.put("lt", lt);
        params.put("submit", "LOGIN");

        client.post(formAction, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Login ERROR. Code:%s Response:%s",statusCode,responseString);
                serverError();
                closeSession(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Ln.d("Login Done.");
                Document doc = Jsoup.parse(responseString);
                Elements TableElements = doc.getElementsByTag("td");

                try{
                    Elements scripts = doc.select("script[language=JavaScript]");
                    String script = scripts.get(3).html();
                    BufferedReader bufReader = new BufferedReader(new StringReader(script));
                    String line = null;
                    while( (line=bufReader.readLine()) != null ) {
                        if(line.trim().startsWith("var myVar")){
                            studentHashId = line.split("\"")[1];
                        }
                    }
                } catch (Exception e){
                    Crashlytics.logException(e);
                }

                String Name = null;
                for (Element TableElement : TableElements) {
                    if (TableElement.attr("class").equals("style3") && TableElement.attr("width").equals("70%") && TableElement.attr("valign").equals("bottom")) {
                        Name = TableElement.text();
                    }
                }
                if (Name != null && !Name.equals("")) {
                    if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null))
                        dialog.setMessage("Authorization successful");
                    Name = Name.replace("Welcome ", "");
                    Name = Name.replace(")", "");
                    String[] result = Name.split("\\(");
                    studentName = result[0];
                    studentRollNo = result[1].toUpperCase();
                    getCGPA();
                }
                else
                {
                    Element form = doc.select("#fm1").first();
                    String formAction = form.attr("action");
                    Boolean retry = false;
                    try {
                        Map<String,String> actionQuery = Util.splitQuery(new URL(client.BASE_URL + formAction));
                        String service = actionQuery.get("service");
                        if(service.contains("amritavidya.amrita.edu"))
                        {
                            if(!client.BASE_URL.equals("https://amritavidya.amrita.edu:8444"))
                            {
                                retry = true;
                                GetSessionID("https://amritavidya.amrita.edu:8444",true);
                            }
                        }
                        else if(service.contains("amritavidya1.amrita.edu"))
                        {
                            if(!client.BASE_URL.equals("https://amritavidya1.amrita.edu:8444"))
                            {
                                retry = true;
                                GetSessionID("https://amritavidya1.amrita.edu:8444",true);
                            }
                        }
                        else if(service.contains("amritavidya2.amrita.edu"))
                        {
                            if(!client.BASE_URL.equals("https://amritavidya2.amrita.edu:8444"))
                            {
                                retry = true;
                                GetSessionID("https://amritavidya2.amrita.edu:8444",true);
                            }
                        }
                    } catch (UnsupportedEncodingException | MalformedURLException e) {
                        Crashlytics.logException(e);
                    }
                    if(!retry && (calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null))) {

                        Snackbar
                                .make(parentView, "Your credentials are incorrect.", Snackbar.LENGTH_LONG)
                                .show();

                        dialog.dismiss();
                    }
                }

            }
        });
    }
    public void getCGPA()
    {
        if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null))
            dialog.setMessage("Requesting Student Profile...");

        RequestParams params = new RequestParams();
        params.put("action", "UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN");
        params.put("isMenu", "true");
        client.get("/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("CGPA ERROR. Code:%s Response:%s", statusCode, responseString);
                serverError();
                closeSession(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Document doc = Jsoup.parse(responseString);
                Element CGPA = doc.select("td[width=19%].rowBG1").last();
                studentCurrentCGPA = CGPA.text().trim();
                getPhoto();
                DiplayDataView();
            }
        });
    }

    public void getPhoto()
    {

    }

    public void getPhotoFile(final int studentProfilePicProgress, final int studentProfilePic)
    {
        if(calledBy.equals("activity")) {
            RequestParams params = new RequestParams();
            params.put("action", "UMS-SRMHR_SHOW_PERSON_PHOTO");
            params.put("personId", studentHashId);

            client.get("/aums/FileUploadServlet", params, new FileAsyncHttpResponseHandler(serviceContext) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    findViewById(studentProfilePicProgress).setVisibility(View.GONE);
                    ImageView myImage = (ImageView) findViewById(studentProfilePic);
                    myImage.setVisibility(View.VISIBLE);
                    myImage.setImageResource(R.drawable.user_128);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    findViewById(studentProfilePicProgress).setVisibility(View.GONE);
                    if (file.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        ImageView myImage = (ImageView) findViewById(studentProfilePic);
                        myImage.setVisibility(View.VISIBLE);
                        myImage.setImageBitmap(myBitmap);
                    }
                }
            });
        }
    }


    public void DiplayDataView() {

        dialog.dismiss();
        setupLayout(R.layout.activity_aums_profile, Color.parseColor("#e91e63"));
        loggedIn = true;

        TextView StudentNameView = (TextView) findViewById(R.id.StudentName);
        TextView StudentRollNoView = (TextView) findViewById(R.id.StudentRollNo);
        TextView StudentCurrentCGPAView = (TextView) findViewById(R.id.StudentCurrentCGPA);

        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        StudentNameView.setText(WordUtils.capitalizeFully(studentName));
        StudentRollNoView.setText(studentRollNo);
        if(studentCurrentCGPA.equals("0.0")){
            StudentCurrentCGPAView.setText("N/A");
        } else {
            StudentCurrentCGPAView.setText(studentCurrentCGPA);
        }

        getSupportActionBar().setSubtitle("Logged in as " + WordUtils.capitalizeFully(studentName));

        findViewById(R.id.StudentProfilePicProgress).setVisibility(View.VISIBLE);
        getPhotoFile(R.id.StudentProfilePicProgress,R.id.StudentProfilePic);

    }
    public void getAttendance(final String semester)
    {
        Ln.d("Start getAttendance Stage 1");
        RequestParams params = new RequestParams();
        params.put("action", "UMS-ATD_INIT_ATDREPORTSTUD_SCREEN");
        params.put("isMenu", "true");
        client.get("/aums/Jsp/Attendance/AttendanceReportStudent.jsp", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Attendance Stage 1 ERROR. Code:%s Response:%s", statusCode, responseString);
                serverError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                getAttendanceStage2(semester);
            }
        });
    }
    public void getAttendanceStage2(String semester){
        Ln.d("Start getAttendance stage 2");
        // GET ACTUAL ATTENDACE DATA
        Log.d("SEMESTER","<"+semester+">");
        RequestParams params = new RequestParams();
        params.put("htmlPageTopContainer_selectSem", semester);
        params.put("Page_refIndex_hidden", attendanceRefIndex++);
        params.put("htmlPageTopContainer_selectCourse", "0");
        params.put("htmlPageTopContainer_selectType", "1");
        params.put("htmlPageTopContainer_hiddentSummary", "");
        params.put("htmlPageTopContainer_status", "");
        params.put("htmlPageTopContainer_action", "UMS-ATD_SHOW_ATDSUMMARY_SCREEN");
        params.put("htmlPageTopContainer_notify", "");
        client.setReferrer("/aums/Jsp/Attendance/AttendanceReportStudent.jsp");
        client.post("/aums/Jsp/Attendance/AttendanceReportStudent.jsp?action=UMS-ATD_INIT_ATDREPORTSTUD_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Attendance Stage 2 ERROR. Code:%s Response:%s", statusCode, responseString);
                serverError();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                client.removeReferrer();
                Ln.d("Got attendence");

                Ln.d(responseString);

                Document doc = Jsoup.parse(responseString);
                Element table = doc.select("table[width=75%] > tbody").first();
                Elements rows = table.select("tr:gt(0)");
                dialog.dismiss();
                if(rows.toString().equals("")) {
                    Snackbar.make(parentView,"No data available for the selected semester.",Snackbar.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getApplicationContext(), AumsAttendance.class);
                    i.putExtra("response", responseString);
                    startActivity(i);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
        });
    }

    public void getRegisteredCourses() {
        Ln.d("Start getRegisteredCourses Stage 1");
        dialog.setMessage("Getting a List of All Registered Courses...");
        RequestParams params = new RequestParams();
        params.put("action", "UMS-EVAL_REGSTUD_INIT_SCREEN");
        params.put("isMenu", "true");

        client.get("/aums/Jsp/StudentCourseRegistration/RegisteredStudentsReports.jsp", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("getRegisteredCourses Stage 1 ERROR. Code:%s Response:%s", statusCode, responseString);
                serverError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Ln.d("Start getRegisteredCourses stage 2");
                RequestParams params = new RequestParams();
                params.put("htmlPageTopContainer_selectStep", semesterMapping.get(studentCurrentSem));
                params.put("Page_refIndex_hidden", attendanceRefIndex++);
                params.put("htmlPageTopContainer_hidMaxMarks", "0");
                params.put("htmlPageTopContainer_hidRowCount", "0");
                params.put("htmlPageTopContainer_notify", "");
                params.put("htmlPageTopContainer_status", "");
                params.put("htmlPageTopContainer_action", "UMS-EVAL_REGSTUD_STEPCHANGE_SCREEN");

                client.post("/aums/Jsp/StudentCourseRegistration/RegisteredStudentsReports.jsp?action=UMS-EVAL_REGSTUD_INIT_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Ln.e("getRegisteredCourses Stage 2 ERROR. Code:%s Response:%s", statusCode, responseString);
                        serverError();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(calledBy.equals("activity")) {
                            Intent i = new Intent(getApplicationContext(), AumsAttendance.class);
                            i.putExtra("response", responseString);
                            startActivity(i);
                            overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        }
                        else {
                            Ln.d("Storing");
                            // STORE TO DATABASE AND SHOW NOTIFICATION
                            Document doc = Jsoup.parse(responseString);
                            Ln.d(responseString);
                            Element table = doc.select("table[width=75%] > tbody").first();
                            Elements rows = table.select("tr:gt(0)");

                            for(Element row : rows) {
                                Elements dataHolders = row.select("td > span");

                                CourseData adata = new CourseData();

                                if(dataHolders.size()>2) {
                                    adata.setCourseCode(dataHolders.get(0).text());
                                    adata.setCourseName(dataHolders.get(1).text());
                                    adata.setType(dataHolders.get(2).text());
                                    adata.setCredits(Float.valueOf(dataHolders.get(3).text()));

                                    adata.save();
                                }
                            }
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }


    private void getGrades(String semesterCode)
    {
        Ln.d("Start getGrades stage 2");
        // GET ACTUAL ATTENDACE DATA
        RequestParams params = new RequestParams();
        params.put("htmlPageTopContainer_selectStep", semesterCode);
        params.put("Page_refIndex_hidden", gradeRefIndex++);
        params.put("htmlPageTopContainer_hiddentblGrades", "");
        params.put("htmlPageTopContainer_status", "");
        params.put("htmlPageTopContainer_action", "UMS-EVAL_STUDPERFORMSURVEY_CHANGESEM_SCREEN");
        params.put("htmlPageTopContainer_notify", "");

        client.post("/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp?action=UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("getGrades Stage 2 ERROR. Code:%s Response:%s", statusCode, responseString);
                serverError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), AumsGrades.class);
                i.putExtra("response", responseString);
                startActivity(i);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });
    }
    public void OpenAttendance(View view) {
        final CharSequence[] items = {"1","2","Vacation 1","3","4","Vacation 2","5","6","Vacation 3","7","8","Vacation 4","9","10","Vacation 5","11","12","Vacation 6","13","14","15"};
        AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);
        builder.setCancelable(true);
        builder.setTitle("Select a Semester");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogList, int item) {
                dialog.setMessage("Getting your attendance");
                dialog.show();
                getAttendance(semesterMapping.get(items[item]));
            }
        });
        AlertDialog alert_d = builder.create();
        alert_d.show();

    }

    public void OpenGrades(View view) {

        final CharSequence[] items = {"1","2","Vacation 1","3","4","Vacation 2","5","6","Vacation 3","7","8","Vacation 4","9","10","Vacation 5","11","12","Vacation 6","13","14","15"};
        AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);
        builder.setCancelable(true);
        builder.setTitle("Select a Semester");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogList, int item) {
                dialog.setMessage("Getting your grades");
                dialog.show();
                getGrades(semesterMapping.get(items[item]));
            }
        });
        AlertDialog alert_d = builder.create();
        alert_d.show();
    }

    //
    // FORM RESET
    //
    public void reset(View view) {
        EditText RollNo = (EditText) findViewById(R.id.roll_no);
        EditText Password = (EditText) findViewById(R.id.pwd);
        RollNo.setText(null);
        Password.setText(null);
    }

    private Toast toast;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.aums, menu);
        return true;//return true so that the menu pop up is opened

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                exitAums();
                return true;
            case R.id.action_bug_report:

                Intent intent = new Intent(getApplicationContext(), BugReport.class);
                intent.putExtra("studentName",(studentName!=null?studentName:"Anonymous"));
                intent.putExtra("studentRollNo",(studentRollNo!=null?studentRollNo:"0"));
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    private void serverError()
    {
        if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null)) {

            Snackbar
                    .make(parentView, "Cannot connect to Server. Try again later.", Snackbar.LENGTH_LONG)
                    .show();

            if (dialog != null)
                dialog.dismiss();
        }

    }
    private void exitAums()
    {
        if (loggedIn) {
            if (BackPress + 2000 > System.currentTimeMillis()) {
                // need to cancel the toast here
                toast.cancel();
                closeSession(true);
                Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(serviceContext, Landing.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            } else {
                // ask user to press back button one more time to close app
                toast = Toast.makeText(getBaseContext(), "Press once again to Log Out of AUMS.", Toast.LENGTH_SHORT);
                toast.show();
            }
            BackPress = System.currentTimeMillis();
        } else {
            //closeSession(true);
            Intent intent = new Intent(serviceContext, Landing.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        }
    }

    private void closeSession(boolean clearAll)
    {
        client.closeClient();
        if(clearAll) {
            serviceContext.getSharedPreferences("CookiePrefsFile", 0).edit().clear().commit();
            String filePath = getApplicationContext().getFilesDir().getParent()+"/shared_prefs/CookiePrefsFile.xml";
            File deletePrefFile = new File(filePath );
            deletePrefFile.delete();
        }
    }

    private void refreshSession()
    {
        String filePath = getApplicationContext().getFilesDir().getParent()+"/shared_prefs/CookiePrefsFile.xml";
        File deletePrefFile = new File(filePath );
        deletePrefFile.delete();
    }

    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void loadSemesterMapping() {
        semesterMapping.clear();
        semesterMapping.put("1","7");
        semesterMapping.put("2","8");
        semesterMapping.put("Vacation 1","231");
        semesterMapping.put("3","9");
        semesterMapping.put("4","10");
        semesterMapping.put("Vacation 2","232");
        semesterMapping.put("5","11");
        semesterMapping.put("6","12");
        semesterMapping.put("Vacation 3","233");
        semesterMapping.put("7","13");
        semesterMapping.put("8","14");
        semesterMapping.put("Vacation 4","234");
        semesterMapping.put("9","72");
        semesterMapping.put("10","73");
        semesterMapping.put("Vacation 5","243");
        semesterMapping.put("11","138");
        semesterMapping.put("12","139");
        semesterMapping.put("Vacation 6","244");
        semesterMapping.put("13","177");
        semesterMapping.put("14","190");
        semesterMapping.put("15","219");
    }
}
