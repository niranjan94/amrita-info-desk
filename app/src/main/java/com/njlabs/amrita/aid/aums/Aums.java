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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.njlabs.amrita.aid.Landing;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.classes.CourseAttendanceData;
import com.njlabs.amrita.aid.aums.classes.CourseData;
import com.onemarker.ark.Security;
import com.onemarker.ark.Util;
import com.onemarker.ark.logging.Ln;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class Aums extends ActionBarActivity {

    private static long BackPress;

    ProgressDialog dialog = null;
    AumsClient client;

    // STUDENT DETAILS VARIABLES
    private String StudentName = null;
    private String StudentRollNo = null;
    private String StudentCurrentSem = null;
    private String StudentCurrentBranch = null;
    private String StudentCurrentProgram = null;
    private String StudentCurrentCGPA = null;

    private String username = null;
    private String password = null;

    private Boolean LoggedIn = false;
    private Boolean gotAttendance = false;
    private Boolean gradesOnce=false;

    private String methodToCall = null;

    private String getAttendanceResponse;

    private Map<String,String> semesterMapping = new HashMap<String, String>();

    int gradeRefIndex=1;
    int attendanceRefIndex=1;

    private String calledBy = "activity";
    private Context serviceContext;

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
        setContentView(R.layout.activity_aums);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e91e63"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serviceContext = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);    // ALERT DIALOG
        builder .setMessage("Amrita University does not provide an API for accessing AUMS data. " +
                "So, if any changes are made to the AUMS Website, please be patient while I try to catch up.")
                .setCancelable(true)
                .setIcon(R.drawable.info)
                .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
        // check for Internet status
       /* if ((new ConnectionDetector(getApplicationContext())).isConnectingToInternet()) {

        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(serviceContext);    // ALERT DIALOG
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
        }*/
        dialog = new ProgressDialog(serviceContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating your credentials ... ");
        SharedPreferences preferences = serviceContext.getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
        String RollNo = preferences.getString("RollNo", "");
        String encodedPassword = preferences.getString("Password","");
        if (RollNo != null && RollNo != "") {
            ((FormEditText) findViewById(R.id.roll_no)).setText(RollNo);
        }
        if(encodedPassword != null && encodedPassword != "") {
            ((FormEditText)findViewById(R.id.pwd)).setText(Security.decrypt(encodedPassword,MainApplication.key));
        }
        loadSemesterMapping();
    }

    public void loginStart(View view) {

        final FormEditText RollNo = (FormEditText) findViewById(R.id.roll_no);
        final FormEditText Password = (FormEditText) findViewById(R.id.pwd);
        FormEditText[] allFields = {RollNo, Password};

        boolean allValid = true;
        for (FormEditText field : allFields) {
            allValid = field.testValidity() && allValid;
        }

        if (allValid) {

            final CharSequence[] items = {"amritavidya.amrita.edu (recommended)","amritavidya1.amrita.edu","amritavidya2.amrita.edu"};
            AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);
            builder.setTitle("Select a server to use");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogList, int item) {

                    SharedPreferences preferences = serviceContext.getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("RollNo", RollNo.getText().toString());
                    editor.putString("Password", Security.encrypt(Password.getText().toString(), MainApplication.key));
                    editor.commit();

                    username = RollNo.getText().toString();
                    password = Password.getText().toString();

                    refreshSession();

                    client = new AumsClient(serviceContext);

                    switch(item)
                    {
                        case 0:
                            GetSessionID("https://amritavidya.amrita.edu:8444",false);
                            break;
                        case 1:
                            GetSessionID("https://amritavidya1.amrita.edu:8444",false);
                            break;
                        case 2:
                            GetSessionID("https://amritavidya2.amrita.edu:8444",false);
                            break;
                        default:
                            GetSessionID("https://amritavidya.amrita.edu:8444",false);
                    }

                    dialog.show();
                }
            });
            AlertDialog alert_d = builder.create();
            alert_d.show();

        } else {

        }
    }

    // INITIAL SESSION RECEIVER
    public void GetSessionID(String baseURL, Boolean retry) {

        if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null)) {
            if (retry == true) {
                dialog.setMessage("Starting a new session with a different server");
            } else
                dialog.setMessage("Starting a new Session !");
        }
        if (retry == true) {
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

                if(client.BASE_URL.equals("https://amritavidya.amrita.edu:8444"))
                {
                    GetSessionID("https://amritavidya1.amrita.edu:8444",true);
                }
                else if(client.BASE_URL.equals("https://amritavidya1.amrita.edu:8444"))
                {
                    GetSessionID("https://amritavidya2.amrita.edu:8444",true);
                }
                else
                {
                    serverError();
                    closeSession(true);
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
                // VERIFY SUCCESSFUL AUTHENTICATION
                Document doc = Jsoup.parse(responseString);
                Elements TableElements = doc.getElementsByTag("td");

                String Name = null;
                for (Element TableElement : TableElements) {
                    if (TableElement.attr("class").equals("style3") && TableElement.attr("width").equals("70%") && TableElement.attr("valign").equals("bottom")) {
                        Name = TableElement.text();
                    }
                }
                if (Name != null && Name != "") {
                    if(calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null))
                        dialog.setMessage("Authorization successful");
                    Name = Name.replace("Welcome ", "");
                    Name = Name.replace(")", "");
                    String[] result = Name.split("\\(");
                    StudentName = result[0];
                    StudentRollNo = result[1].toUpperCase();
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
                    } catch (UnsupportedEncodingException e) {

                    } catch (MalformedURLException e) {

                    }
                    if(retry==false && (calledBy.equals("activity")||(calledBy.equals("data_hook")&&dialog!=null))) {

                        SuperToast superToast = new SuperToast(serviceContext);
                        superToast.setDuration(SuperToast.Duration.LONG);
                        superToast.setAnimations(SuperToast.Animations.FLYIN);
                        superToast.setBackground(SuperToast.Background.RED);
                        superToast.setTextColor(Color.WHITE);
                        superToast.setText("Your credentials (Roll No (and/or) Password) were incorrect !");
                        superToast.show();
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
                StudentCurrentCGPA = CGPA.text().trim();
                getPhoto();
            }
        });
    }

    public void getPhoto()
    {
        RequestParams params = new RequestParams();
        params.put("action", "UMS-SRM_INIT_STUDENTPROFILE_SCREEN");

        client.get("/aums/Jsp/Student/Student.jsp", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("CGPA ERROR. Code:%s Response:%s",statusCode,responseString);
                serverError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Document doc = Jsoup.parse(responseString);
                Elements InputElements = doc.getElementsByTag("input");

                String EncodedId = null;
                for (Element InputElement : InputElements) {
                    if (InputElement.attr("name").equals("htmlPageTopContainer_encodedenrollmentId")) {
                        EncodedId = InputElement.attr("value");
                    }
                }

                Element Sem = doc.select("td[width=9%] > b").last();
                StudentCurrentSem = Sem.text().trim();
                Element Branch = doc.select("td[width=6%] > b").first();
                StudentCurrentBranch = Branch.text().trim();
                Element Program = doc.select("td[width=10%] > b").first();
                StudentCurrentProgram = Program.text().trim();

                if(calledBy.equals("activity")) {
                    SharedPreferences preferences = serviceContext.getSharedPreferences("com.njlabs.amrita.aid_preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name", WordUtils.capitalizeFully(StudentName));
                    editor.putString("campus", "Coimbatore");
                    editor.commit();
                    DiplayDataView(EncodedId);
                }
                if(methodToCall!=null) {
                    Method method = null;
                    try {
                        method = ((Object) Aums.this).getClass().getMethod(methodToCall);
                    } catch (SecurityException e) {

                    } catch (NoSuchMethodException e) {

                    }
                    try {
                        method.invoke(Aums.this);
                    } catch (IllegalArgumentException e) {
                        
                    } catch (IllegalAccessException e) {
                        
                    } catch (InvocationTargetException e) {
                        
                    } catch (NullPointerException e) {

                    }
                }
            }
        });
    }

    public void getPhotoFile(String encodedID, final int studentProfilePicProgress, final int studentProfilePic)
    {
        if(calledBy.equals("activity")) {
            RequestParams params = new RequestParams();
            params.put("action", "SHOW_STUDENT_PHOTO");
            params.put("encodedenrollmentId", encodedID);

            client.get("/aums/FileUploadServlet", params, new FileAsyncHttpResponseHandler(serviceContext) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    serverError();
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

    //
    //
    //
    public void DiplayDataView(String EncodedId) {

        dialog.dismiss();
        setContentView(R.layout.activity_aums_profile);
        LoggedIn = true;

        TextView StudentNameView = (TextView) findViewById(R.id.StudentName);
        TextView StudentRollNoView = (TextView) findViewById(R.id.StudentRollNo);
        TextView StudentCurrentCGPAView = (TextView) findViewById(R.id.StudentCurrentCGPA);

        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        StudentNameView.setText(WordUtils.capitalizeFully(StudentName));
        StudentRollNoView.setText(StudentRollNo);
        StudentCurrentCGPAView.setText(StudentCurrentCGPA);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e91e63"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Welcome " + StudentName + " !");

        findViewById(R.id.StudentProfilePicProgress).setVisibility(View.VISIBLE);
        getPhotoFile(EncodedId,R.id.StudentProfilePicProgress,R.id.StudentProfilePic);


    }

    //
    //
    //

    public void getAttendance()
    {
        if(gotAttendance)
        {
            if(calledBy.equals("activity")) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), AumsAttendance.class);
                i.putExtra("response", getAttendanceResponse);
                startActivity(i);
            }
        }
        else {

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
                    getAttendanceStage2(StudentCurrentSem);
                }
            });
        }
    }
    public void getAttendanceStage2(String semester){
        Ln.d("Start getAttendance stage 2");
        // GET ACTUAL ATTENDACE DATA
        RequestParams params = new RequestParams();
        params.put("htmlPageTopContainer_selectSem", semesterMapping.get(semester));
        params.put("Page_refIndex_hidden", attendanceRefIndex++);
        params.put("htmlPageTopContainer_selectCourse", "0");
        params.put("htmlPageTopContainer_selectType", "1");
        params.put("htmlPageTopContainer_hiddentSummary", "");
        params.put("htmlPageTopContainer_status", "");
        params.put("htmlPageTopContainer_action", "UMS-ATD_SHOW_ATDSUMMARY_SCREEN");
        params.put("htmlPageTopContainer_notify", "");

        client.post("/aums/Jsp/Attendance/AttendanceReportStudent.jsp?action=UMS-ATD_INIT_ATDREPORTSTUD_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Attendance Stage 2 ERROR. Code:%s Response:%s", statusCode, responseString);
                serverError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Ln.d("Got attendence");

                gotAttendance = true;
                getAttendanceResponse = responseString;

                Document doc = Jsoup.parse(responseString);
                Element table = doc.select("table[width=75%] > tbody").first();
                Elements rows = table.select("tr:gt(0)");

                if(rows.toString().equals(""))
                {
                    getAttendanceStage2(String.valueOf(Integer.parseInt(StudentCurrentSem)-1));
                }
                else {
                    dialog.dismiss();
                    if (calledBy.equals("activity")) {
                        Intent i = new Intent(getApplicationContext(), AumsAttendance.class);
                        i.putExtra("response", responseString);
                        startActivity(i);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    } else {
                        Ln.d("Storing");

                        // STORE TO DATABASE AND SHOW NOTIFICATION
                        CourseAttendanceData.deleteAll(CourseAttendanceData.class);

                        for (Element row : rows) {
                            Elements dataHolders = row.select("td > span");

                            CourseAttendanceData adata = new CourseAttendanceData();

                            adata.setCourseCode(dataHolders.get(0).text());
                            adata.setCourseTitle(dataHolders.get(1).text());
                            adata.setTotal(dataHolders.get(5).text());
                            adata.setAttended(dataHolders.get(6).text());
                            adata.setPercentage(dataHolders.get(7).text());

                            adata.save();
                        }
                        // SEND NOTIFICATIONS

                    }

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
                params.put("htmlPageTopContainer_selectStep", semesterMapping.get(StudentCurrentSem));
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

                        gotAttendance = true;
                        getAttendanceResponse = responseString;
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
   /* public void getGrades(final String semesterCode)
    {
        if(true)
        {
            Ln.d("Already pinged. Proceed to Stage 2 directly");
            getGradesStage2(semesterCode);
        }
        else {
            Ln.d("Start getGrades Stage 1");
            RequestParams params = new RequestParams();
            params.put("action", "UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN");
            params.put("isMenu", "true");

            client.get("/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp", params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Ln.e("getGrades Stage 1 ERROR. Code:%s Response:%s", statusCode, responseString);
                    serverError();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Ln.d(responseString);
                    getGradesStage2(semesterCode);
                }
            });
        }
    }*/

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
                gradesOnce = true;
                Intent i = new Intent(getApplicationContext(), AumsGrades.class);
                i.putExtra("response", responseString);
                startActivity(i);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });
    }
    public void OpenAttendance(View view) {
        dialog.setMessage("Getting your attendance summary");
        dialog.show();
        getAttendance();
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
        FormEditText RollNo = (FormEditText) findViewById(R.id.roll_no);
        FormEditText Password = (FormEditText) findViewById(R.id.pwd);
        RollNo.setText(null);
        Password.setText(null);
    }

    private Toast toast;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                exitAums();
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
            SuperToast superToast = new SuperToast(serviceContext);
            superToast.setDuration(SuperToast.Duration.LONG);
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setBackground(SuperToast.Background.RED);
            superToast.setTextColor(Color.WHITE);
            superToast.setText("Cannot connect to Server. Try again later.");
            superToast.show();
            if (dialog != null)
                dialog.dismiss();
        }

    }
    private void exitAums()
    {
        if (LoggedIn) {
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
            File deletePrefFile = new File("/data/data/com.njlabs.amrita.aid/shared_prefs/CookiePrefsFile.xml");
            deletePrefFile.delete();
        }
    }

    private void refreshSession()
    {
        serviceContext.getSharedPreferences("CookiePrefsFile", 0).edit().clear().commit();
        File deletePrefFile = new File("/data/data/com.njlabs.amrita.aid/shared_prefs/CookiePrefsFile.xml");
        deletePrefFile.delete();
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
