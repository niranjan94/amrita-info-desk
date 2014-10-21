/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.loopj.android.http.*;
import com.njlabs.amrita.aid.R;
import com.onemarker.ark.logging.Ln;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class AttendanceFlow extends Activity {

    String username="CB.EN.U4AEE12029";
    String password="asmi1995adminnasa1995admin";
    String semester="5";

    AumsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_flow);
        client = new AumsClient(this);
        getSession();
    }
    public void getSession()
    {
        Ln.d("Starting Session");
        RequestParams params = new RequestParams();
        params.put("service", "https://amritavidya2.amrita.edu:8444/aums/Jsp/Common/index.jsp");

        client.get("/cas/login", params, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Session ERROR. Code:%s Response:%s",statusCode,responseString);
                client.cookieStore.clear();
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

                Ln.d("Session Done. action=%s lt=%s",formAction,lt);
                login(formAction, lt);
            }
        });

    }

    public void login(String formAction, String lt)
    {
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
                client.cookieStore.clear();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Ln.d("Login Done.");
                //getAttendance();
                //getCGPA();
                getPhoto();
            }
        });
    }

    public void getAttendance()
    {
        Ln.d("Start getAttendance Stage 1");
        RequestParams params = new RequestParams();
        params.put("action", "UMS-ATD_INIT_ATDREPORTSTUD_SCREEN");
        params.put("isMenu", "true");

        client.get("/aums/Jsp/Attendance/AttendanceReportStudent.jsp", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("Attendance Stage 1 ERROR. Code:%s Response:%s",statusCode,responseString);
                client.cookieStore.clear();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Ln.d("Start getAttendance stage 2");
                // GET ACTUAL ATTENDACE DATA
                RequestParams params = new RequestParams();
                params.put("htmlPageTopContainer_selectSem", "11");
                params.put("Page_refIndex_hidden", "3");
                params.put("htmlPageTopContainer_selectCourse", "0");
                params.put("htmlPageTopContainer_selectType", "1");
                params.put("htmlPageTopContainer_hiddentSummary", "");
                params.put("htmlPageTopContainer_status", "");
                params.put("htmlPageTopContainer_action", "UMS-ATD_SHOW_ATDSUMMARY_SCREEN");
                params.put("htmlPageTopContainer_notify", "");

                client.post("/aums/Jsp/Attendance/AttendanceReportStudent.jsp?action=UMS-ATD_INIT_ATDREPORTSTUD_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Ln.e("Attendance Stage 2 ERROR. Code:%s Response:%s",statusCode,responseString);
                        client.cookieStore.clear();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                        ((WebView) findViewById(R.id.webView)).getSettings().setJavaScriptEnabled(true);
                        ((WebView) findViewById(R.id.webView)).loadData(responseString, "text/html", "UTF-8");
                        client.cookieStore.clear();
                    }
                });
            }
        });
    }

    public void getCGPA()
    {
        RequestParams params = new RequestParams();
        params.put("action", "UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN");
        params.put("isMenu", "true");
        client.get("/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Ln.e("CGPA ERROR. Code:%s Response:%s", statusCode, responseString);
                client.cookieStore.clear();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Document doc = Jsoup.parse(responseString);
                Element CGPA = doc.select("td[width=19%].rowBG1").last();
                String CurrentCGPA = CGPA.text().trim();

                ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                ((WebView) findViewById(R.id.webView)).getSettings().setJavaScriptEnabled(true);
                ((WebView) findViewById(R.id.webView)).loadData(CurrentCGPA, "text/plain", "UTF-8");
                client.cookieStore.clear();
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
                client.cookieStore.clear();
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
                String CurrentSem = Sem.text().trim();
                Element Branch = doc.select("td[width=6%] > b").first();
                String CurrentBranch = Branch.text().trim();
                Element Program = doc.select("td[width=10%] > b").first();
                String CurrentProgram = Program.text().trim();



                String html = "<html><body>" +
                        "Encoded ID:" + EncodedId +
                        "<br>" +
                        "Semester:" + CurrentSem +
                        "<br>" +
                        "Branch:" + CurrentBranch+
                        "<br>" +
                        "Program:" + CurrentProgram +
                        "<br>";


                getPhotoFile(EncodedId);

                ((WebView) findViewById(R.id.webView)).getSettings().setJavaScriptEnabled(true);
                ((WebView) findViewById(R.id.webView)).loadData(html, "text/plain", "UTF-8");
                client.cookieStore.clear();
            }
        });
    }

    public void getPhotoFile(String encodedID)
    {
        RequestParams params = new RequestParams();
        params.put("action", "SHOW_STUDENT_PHOTO");
        params.put("encodedenrollmentId", encodedID);

        client.get("/aums/FileUploadServlet", params, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                if(file.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ImageView myImage = (ImageView) findViewById(R.id.imageView);
                    myImage.setVisibility(View.VISIBLE);
                    myImage.setImageBitmap(myBitmap);

                }
            }
        });
    }



}
