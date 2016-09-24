/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.njlabs.amrita.aid.aums.models.CourseAttendanceData;
import com.njlabs.amrita.aid.aums.models.CourseData;
import com.njlabs.amrita.aid.aums.models.CourseGradeData;
import com.njlabs.amrita.aid.aums.models.CourseMarkData;
import com.njlabs.amrita.aid.aums.models.CourseResource;
import com.njlabs.amrita.aid.aums.responses.AttendanceResponse;
import com.njlabs.amrita.aid.aums.responses.CourseResourcesResponse;
import com.njlabs.amrita.aid.aums.responses.CoursesResponse;
import com.njlabs.amrita.aid.aums.responses.GradesResponse;
import com.njlabs.amrita.aid.aums.responses.LoginResponse;
import com.njlabs.amrita.aid.aums.responses.MarksResponse;
import com.njlabs.amrita.aid.aums.responses.SessionResponse;
import com.njlabs.amrita.aid.util.ark.Util;
import com.njlabs.amrita.aid.util.okhttp.ProgressResponseBody;
import com.njlabs.amrita.aid.util.okhttp.extras.RequestParams;
import com.njlabs.amrita.aid.util.okhttp.responses.FileResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.RawResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.TextResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

@SuppressWarnings("unused")
public class Aums {

    public AumsClient client;
    private int gradeRefIndex = 1;
    private int attendanceRefIndex = 1;
    private int markRefIndex = 1;
    public String studentRollNo = null;
    public String studentName = null;
    private String studentHashId = null;
    private Context context;
    private Map<String, String> semesterMapping;

    public Aums(Context context) {
        this(context, null);
    }

    public Aums(Context context, ProgressResponseBody.ProgressListener progressListener) {
        this.context = context;
        client = new AumsClient(context);
        client.setProgressListener(progressListener);
        client.powerUp();
        semesterMapping = new HashMap<>();
        loadSemesterMapping();
    }

    @SuppressLint("CommitPrefEdits")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void logout() {
        client.resetClient();
    }

    public void switchServer(AumsServer.Server server) {
        client.setBaseURL(AumsServer.get(server));
        client.resetClient();
    }

    public void setServer(AumsServer.Server server) {
        client.setBaseURL(AumsServer.get(server));
    }

    public void setServer(String serverUrl) {
        client.setBaseURL(serverUrl);
    }

    public String getServer() {
        return client.BASE_URL;
    }

    public void getSessionId(final SessionResponse response) {
        RequestParams params = new RequestParams();
        params.put("service", client.BASE_URL + "/aums/Jsp/Common/index.jsp");

        client.get("/cas/login", params, new TextResponse() {
            @Override
            public void onSuccess(String responseString) {
                Document doc = Jsoup.parse(responseString);
                Element form = doc.select("#fm1").first();
                Element hiddenInput = doc.select("input[name=lt]").first();
                try {
                    response.onSuccess(form.attr("action"), hiddenInput.attr("value"));
                } catch (Exception e) {
                    client = new AumsClient(context);
                    response.onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }

        });
    }

    public void login(String rollNo, String password, String formAction, String lt, final LoginResponse response) {
        RequestParams params = new RequestParams();
        params.put("username", rollNo);
        params.put("password", password);
        params.put("_eventId", "submit");
        params.put("lt", lt);
        params.put("submit", "LOGIN");

        client.post(formAction, params, new TextResponse() {

            @Override
            public void onSuccess(String responseString) {
                Document doc = Jsoup.parse(responseString);
                Elements TableElements = doc.getElementsByTag("td");

                try {
                    Elements scripts = doc.select("script[language=JavaScript]");
                    String script = scripts.get(3).html();
                    BufferedReader bufReader = new BufferedReader(new StringReader(script));
                    String line;
                    while ((line = bufReader.readLine()) != null) {
                        if (line.trim().startsWith("var myVar")) {
                            studentHashId = line.split("\"")[1];
                        }
                    }
                } catch (Exception ignored) {

                }

                String name = null;
                for (Element tableElement : TableElements) {
                    if (tableElement.attr("class").equals("style3") && tableElement.attr("width").equals("70%") && tableElement.attr("valign").equals("bottom")) {
                        name = tableElement.text();
                    }
                }
                if (name != null && !name.equals("")) {

                    try {
                        name = name.replace("Welcome ", "");
                        name = name.replace(")", "");
                        String[] result = name.split("\\(");
                        studentName = result[0];
                        studentRollNo = result[1].toUpperCase();
                        response.onSuccess(studentName, studentRollNo);
                    } catch (Exception e) {
                        response.onSiteStructureChange();
                    }

                } else {
                    Element form = doc.select("#fm1").first();
                    String formAction = form.attr("action");
                    Boolean retry = false;
                    try {
                        Map<String, String> actionQuery = Util.splitQuery(new URL(client.BASE_URL + formAction));
                        String service = actionQuery.get("service");
                        if (service.contains("amritavidya.amrita.edu")) {
                            if (!client.BASE_URL.equals(AumsServer.get(AumsServer.Server.ETTIMADAI))) {
                                retry = true;
                                response.onServerChanged((AumsServer.Server.ETTIMADAI));
                            }
                        } else if (service.contains("amritavidya1.amrita.edu")) {
                            if (!client.BASE_URL.equals(AumsServer.get(AumsServer.Server.ETTIMADAI_ONE))) {
                                retry = true;
                                response.onServerChanged((AumsServer.Server.ETTIMADAI_ONE));
                            }
                        } else if (service.contains("amritavidya2.amrita.edu")) {
                            if (!client.BASE_URL.equals(AumsServer.get(AumsServer.Server.ETTIMADAI_TWO))) {
                                retry = true;
                                response.onServerChanged((AumsServer.Server.ETTIMADAI_TWO));
                            }
                        }
                    } catch (UnsupportedEncodingException | MalformedURLException e) {
                        response.onFailure(e);
                        return;
                    }

                    if (!retry) {
                        response.onFailedAuthentication();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }
        });
    }

    public void getCGPA(final TextResponse response) {
        RequestParams params = new RequestParams();
        params.put("action", "UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN");
        params.put("isMenu", "true");
        client.get("/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp", params, new TextResponse() {

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {
                Document doc = Jsoup.parse(responseString);
                try {
                    Element CGPA = doc.select("td[width=19%].rowBG1").last();
                    response.onSuccess(CGPA.text().trim());
                } catch (Exception e) {
                    response.onFailure(e);
                }
            }
        });

    }

    public void getPhotoFile(final FileResponse response) {
        RequestParams params = new RequestParams();
        params.put("action", "UMS-SRMHR_SHOW_PERSON_PHOTO");
        params.put("personId", studentHashId);

        client.get("/aums/FileUploadServlet", params, new FileResponse() {
            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }

            @Override
            public void onSuccess(File file) {
                if (file.exists()) {
                    response.onSuccess(file);
                } else {
                    response.onFailure(new IOException(file + "does not exist"));
                }
            }
        });
    }

    public void getAttendance(final String semester, final AttendanceResponse response) {
        RequestParams params = new RequestParams();
        params.put("action", "UMS-ATD_INIT_ATDREPORTSTUD_SCREEN");
        params.put("isMenu", "true");
        client.get("/aums/Jsp/Attendance/AttendanceReportStudent.jsp", params, new TextResponse() {

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {

                RequestParams params = new RequestParams();
                params.put("htmlPageTopContainer_selectSem", semesterMapping.get(semester));
                params.put("Page_refIndex_hidden", attendanceRefIndex++);
                params.put("htmlPageTopContainer_selectCourse", "0");
                params.put("htmlPageTopContainer_selectType", "1");
                params.put("htmlPageTopContainer_hiddentSummary", "");
                params.put("htmlPageTopContainer_status", "");
                params.put("htmlPageTopContainer_action", "UMS-ATD_SHOW_ATDSUMMARY_SCREEN");
                params.put("htmlPageTopContainer_notify", "");
                client.setReferer("/aums/Jsp/Attendance/AttendanceReportStudent.jsp");
                client.post("/aums/Jsp/Attendance/AttendanceReportStudent.jsp?action=UMS-ATD_INIT_ATDREPORTSTUD_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextResponse() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        response.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(String finalResponseString) {
                        client.removeReferer();

                        Document doc = Jsoup.parse(finalResponseString);

                        try {
                            Element table = doc.select("table[width=75%] > tbody").first();
                            Elements rows = table.select("tr:gt(0)");
                            if (rows.toString().equals("")) {
                                response.onDataUnavailable();
                            } else {
                                rows = table.select("tr");
                                List<CourseAttendanceData> attendanceData = new ArrayList<>();
                                int index = 0;
                                for (Element row : rows) {
                                    index++;
                                    if ((index & 1) == 0) {
                                        Elements dataHolders = row.select("td > span");
                                        CourseAttendanceData courseAttendanceData = new CourseAttendanceData();
                                        courseAttendanceData.setCourseCode(dataHolders.get(0).text());
                                        courseAttendanceData.setCourseTitle(dataHolders.get(1).text());
                                        courseAttendanceData.setTotal(dataHolders.get(5).text());
                                        courseAttendanceData.setAttended(dataHolders.get(6).text());
                                        courseAttendanceData.setPercentage(dataHolders.get(7).text());
                                        attendanceData.add(courseAttendanceData);
                                    }
                                }
                                response.onSuccess(attendanceData);
                            }
                        } catch (Exception e) {

                            response.onSiteStructureChange();
                        }
                    }
                });
            }
        });
    }

    public void getGrades(String semester, final GradesResponse response) {
        RequestParams params = new RequestParams();
        params.put("htmlPageTopContainer_selectStep", semesterMapping.get(semester));
        params.put("Page_refIndex_hidden", gradeRefIndex++);
        params.put("htmlPageTopContainer_hiddentblGrades", "");
        params.put("htmlPageTopContainer_status", "");
        params.put("htmlPageTopContainer_action", "UMS-EVAL_STUDPERFORMSURVEY_CHANGESEM_SCREEN");
        params.put("htmlPageTopContainer_notify", "");

        client.post("/aums/Jsp/StudentGrade/StudentPerformanceWithSurvey.jsp?action=UMS-EVAL_STUDPERFORMSURVEY_INIT_SCREEN&isMenu=true&pagePostSerialID=0", params, new TextResponse() {

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {

                List<CourseGradeData> courseGradeDataList = new ArrayList<>();
                String sgpa = null;
                Document doc = Jsoup.parse(responseString);

                try {
                    Element PublishedState = doc.select("input[name=htmlPageTopContainer_status]").first();
                    if (PublishedState.attr("value").equals("Result Not Published.")) {
                        response.onDataUnavailable();
                    } else {
                        Element table = doc.select("table[width=75%] > tbody").first();
                        Elements rows = table.select("tr:gt(0)");

                        for (Element row : rows) {
                            Elements dataHolders = row.select("td > span");

                            CourseGradeData courseGradeData = new CourseGradeData();

                            if (dataHolders.size() > 2) {
                                courseGradeData.setCourseCode(dataHolders.get(1).text());
                                courseGradeData.setCourseTitle(dataHolders.get(2).text());
                                courseGradeData.setType(dataHolders.get(4).text());
                                courseGradeData.setGrade(dataHolders.get(5).text());
                                courseGradeDataList.add(courseGradeData);
                            } else {
                                try {
                                    sgpa = dataHolders.get(1).text();
                                    if (sgpa == null || sgpa.trim().equals("null")) {
                                        response.onDataUnavailable();
                                        return;
                                    }
                                } catch (Exception e) {
                                    sgpa = "N/A";
                                }
                            }
                        }

                        response.onSuccess(sgpa, courseGradeDataList);
                    }
                } catch (Exception e) {
                    response.onSiteStructureChange();
                }
            }
        });
    }

    public void getMarks(final String semester, final MarksResponse marksResponse) {
        RequestParams params = new RequestParams();
        params.put("action", "UMS-EVAL_STUDMARKVIEW_INIT_SCREEN");
        params.put("isMenu", "true");
        client.get("/aums/Jsp/Marks/ViewPublishedMark.jsp", params, new TextResponse() {

            @Override
            public void onFailure(Throwable throwable) {
                marksResponse.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {
                RequestParams params = new RequestParams();
                params.put("htmlPageTopContainer_selectStep", semesterMapping.get(semester));
                params.put("Page_refIndex_hidden", markRefIndex++);
                params.put("htmlPageTopContainer_status", "");
                params.put("htmlPageTopContainer_action", "UMS-EVAL_STUDMARKVIEW_SELSEM_SCREEN");
                params.put("htmlPageTopContainer_notify", "I");

                client.post("/aums/Jsp/Marks/ViewPublishedMark.jsp?action=UMS-EVAL_STUDMARKVIEW_INIT_SCREEN&isMenu=true", params, new TextResponse() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        marksResponse.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(String responseString) {
                        ArrayList<String> subjects = new ArrayList<>();
                        List<CourseMarkData> markDataList = new ArrayList<>();
                        Document doc = Jsoup.parse(responseString);

                        try {
                            Element table = doc.select("table[width=75%]").first();

                            if(table == null) {
                                marksResponse.onDataUnavailable();
                                return;
                            }

                            Elements rows = table.select("tr");
                            Elements headerRowCells = rows.get(0).select("td");

                            for(int i=3; i < headerRowCells.size(); i++){
                                Element cell = headerRowCells.get(i);
                                if(cell.text().trim().length()>0){
                                    subjects.add(cell.text().trim());
                                }
                            }

                            if(subjects.size() == 0) {
                                marksResponse.onDataUnavailable();
                                return;
                            }

                            for(int i=1; i < rows.size(); i++) {
                                boolean hasMarks = false;
                                Elements cells = rows.get(i).select("td");
                                String exam = cells.get(0).text();
                                int k = 0;
                                for(int j=3; j < cells.size(); j++){
                                    Element cell = cells.get(j);
                                    String mark = cell.text();
                                    if(isNumeric(mark)) {
                                        hasMarks = true;
                                    }
                                    k++;
                                }
                                if(hasMarks) {
                                    markDataList.add(new CourseMarkData(exam));
                                    k = 0;
                                    for(int j=3; j < cells.size(); j++){
                                        Element cell = cells.get(j);
                                        String mark = cell.text();
                                        if(isNumeric(mark)) {
                                            markDataList.add(new CourseMarkData(subjects.get(k), mark, exam));
                                        }
                                        k++;
                                    }
                                }
                            }

                            if(markDataList.size() > 0) {
                                marksResponse.onSuccess(markDataList);
                            } else {
                                marksResponse.onDataUnavailable();
                            }

                        } catch (Exception e) {
                            marksResponse.onFailure(e);
                        }
                    }
                });
            }
        });
    }

    public void getCourses(final CoursesResponse response){
        RequestParams params = new RequestParams();
        params.put("action", "UMS-EVAL_CLASSHEADER_SCREEN_INIT");
        client.get("/aums/Jsp/DefineComponent/ClassHeader.jsp", params, new TextResponse() {
            @Override
            public void onSuccess(String responseString) {

                Document doc = Jsoup.parse(responseString);
                Element tr = doc.select("body > form > table > tbody > tr").first();

                Elements mainCells = tr.select("td[width=\"21%\"]");
                mainCells.remove(0);
                Elements options = tr.select("td > select > option");
                final List<CourseData> courseDataList = new ArrayList<>();
                for (Element mainCell: mainCells) {

                    String fullName = mainCell.select("a").first().text().trim();
                    String fullUrl = mainCell.select("a").first().attr("href").trim();

                    String[] fullNameArray = fullName.split("\\.");
                    String[] fullUrlArray = fullUrl.split("/");

                    CourseData courseData = new CourseData();
                    courseData.setCourseCode(fullNameArray[fullNameArray.length - 1]);
                    courseData.setId(fullUrlArray[fullUrlArray.length - 1]);
                    courseData.setType("regular");

                    for (String fullNameArrayPart: fullNameArray) {
                        if(fullNameArrayPart.trim().equals("Re")) {
                            courseData.setType("re_reg");
                            break;
                        }
                    }

                    courseDataList.add(courseData);
                }

                for (Element option: options) {
                    if(!option.attr("value").equals("0")) {
                        String fullName = option.text().trim();
                        String[] fullNameArray = fullName.split("\\.");
                        CourseData courseData = new CourseData();
                        courseData.setCourseCode(fullNameArray[fullNameArray.length - 1]);
                        courseData.setId(option.attr("value").trim());
                        courseData.setType("regular");

                        for (String fullNameArrayPart: fullNameArray) {
                            if(fullNameArrayPart.trim().equals("Re")) {
                                courseData.setType("re_reg");
                                break;
                            }
                        }
                        courseDataList.add(courseData);
                    }
                }

                final int[] responded = {0};
                for (final CourseData courseData: courseDataList) {
                    getCourseNameFromId(courseData.getId(), new TextResponse() {
                        @Override
                        public void onSuccess(String responseString) {
                            responded[0]++;
                            courseData.setCourseName(responseString);
                            if(responded[0] == courseDataList.size()) {
                                response.onSuccess(courseDataList);
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            responded[0]++;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }
        });
    }

    private void getCourseNameFromId(String id, final TextResponse textResponse) {
        client.get("/access/site/" + id + "/", new TextResponse() {
            @Override
            public void onSuccess(String responseString) {
                Document doc = Jsoup.parse(responseString);
                Element div = doc.select("body > div").first();
                String crappyCourseName = div.text().trim();
                String awesomeCourseName = crappyCourseName.replaceFirst(".+?(?=:)", "").split("_")[0].split(":")[1];
                textResponse.onSuccess(awesomeCourseName);
            }

            @Override
            public void onFailure(Throwable throwable) {
                textResponse.onFailure(throwable);
            }
        });
    }


    public void getCourseResources(final String courseId, final CourseResourcesResponse response) {

        client.get("/access/content/group/" + courseId + "/", new TextResponse() {

            @Override
            public void onSuccess(String responseString) {

                Document doc = Jsoup.parse(responseString);
                Elements tRows = doc.select("body > div > table > tbody > tr");

                final List<CourseResource> courseResourceList = new ArrayList<>();

                for(Element row: tRows) {
                    courseResourceList.add(new CourseResource(courseId, row.select("td:nth-child(1) > a").first().text().trim()));
                }
                response.onSuccess(courseResourceList);
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.onFailure(throwable);
            }
        });
    }


    public void getResourceFileSize(String courseId, String fileName, final TextResponse textResponse) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            fileName = URLEncoder.encode(fileName).replace("+", "%20");
        }
        client.get("/access/content/group/" + courseId + "/" + fileName, new RawResponse() {
            @Override
            public void onSuccess(Response rawResponse) {

                final String contentLength = rawResponse.header("Content-Length", null);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textResponse.onSuccess(contentLength);
                    }
                });

                try {
                    rawResponse.body().close();
                } catch (Exception ignored) { }
            }

            @Override
            public void onFailure(Throwable throwable) {
                textResponse.onFailure(throwable);
            }
        });

    }

    public void downloadResource(String courseId, String fileName, final FileResponse fileResponse) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            fileName = URLEncoder.encode(fileName).replace("+", "%20");
        }
        client.get("/access/content/group/" + courseId + "/" + fileName, null, new FileResponse() {
            @Override
            public void onSuccess(File file) {
                fileResponse.onSuccess(file);
            }

            @Override
            public void onFailure(Throwable throwable) {
                fileResponse.onFailure(throwable);
            }
        });
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private void loadSemesterMapping() {
        semesterMapping.clear();
        semesterMapping.put("1", "7");
        semesterMapping.put("2", "8");
        semesterMapping.put("Vacation 1", "231");
        semesterMapping.put("3", "9");
        semesterMapping.put("4", "10");
        semesterMapping.put("Vacation 2", "232");
        semesterMapping.put("5", "11");
        semesterMapping.put("6", "12");
        semesterMapping.put("Vacation 3", "233");
        semesterMapping.put("7", "13");
        semesterMapping.put("8", "14");
        semesterMapping.put("Vacation 4", "234");
        semesterMapping.put("9", "72");
        semesterMapping.put("10", "73");
        semesterMapping.put("Vacation 5", "243");
        semesterMapping.put("11", "138");
        semesterMapping.put("12", "139");
        semesterMapping.put("Vacation 6", "244");
        semesterMapping.put("13", "177");
        semesterMapping.put("14", "190");
        semesterMapping.put("15", "219");
    }
}
