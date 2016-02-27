/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.njlabs.amrita.aid.gpms.models.HistoryEntry;
import com.njlabs.amrita.aid.gpms.models.PendingEntry;
import com.njlabs.amrita.aid.gpms.responses.HistoryResponse;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.gpms.responses.SuccessResponse;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Gpms {

    public GpmsClient client;
    private String studentRollNo = null;
    private String studentName = null;
    private Context context;
    private SharedPreferences cookiePrefFile;
    public static String dateFormat = "dd MMM yyyy HH:mm:ss";

    public Gpms(Context context) {
        this.context = context;
        client = new GpmsClient(context);
        cookiePrefFile = context.getSharedPreferences("CookiePrefsFile", Context.MODE_PRIVATE);

        studentRollNo = cookiePrefFile.getString("gpms_roll_no", null);
        studentName = cookiePrefFile.getString("gpms_name", null);
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        cookiePrefFile.edit().putString("gpms_roll_no", studentRollNo).apply();
        this.studentRollNo = studentRollNo;
    }

    public void setStudentName(String studentName) {
        cookiePrefFile.edit().putString("gpms_name", studentName).apply();
        this.studentName = studentName;
    }

    @SuppressLint("CommitPrefEdits")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void logout() {
        client.closeClient();
        cookiePrefFile.edit().clear().commit();
        String filePath = context.getFilesDir().getParent()+"/shared_prefs/CookiePrefsFile.xml";
        File deletePrefFile = new File(filePath );
        deletePrefFile.delete();

    }

    public void basicLogin(String rollNo, String password, final SuccessResponse successResponse) {
        RequestParams params = new RequestParams();
        params.put("userid", rollNo);
        params.put("passwd", password);
        params.put("submit", "");

        client.post("/index.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                successResponse.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                successResponse.onSuccess();
            }
        });
    }

    public void login(String rollNo, String password, final InfoResponse infoResponse) {

        basicLogin(rollNo, password, new SuccessResponse() {
            @Override
            public void onSuccess() {
                client.get("/applyleave.php", null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        infoResponse.onFailure(responseString, throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Document doc = Jsoup.parse(responseString);
                            Element mainContent = doc.select("div.maincontent").first();

                            Element infoTableBody = mainContent.select("form > center > table > tbody > tr > td:nth-child(1) > table > tbody").first();

                            String regNo = infoTableBody.select("tr:nth-child(2) > td.textFont-2").first().text().trim();
                            String name = infoTableBody.select("tr:nth-child(3) > td.textFont-2").first().text().trim();
                            String hostel = infoTableBody.select("tr:nth-child(4) > td.textFont-2").first().text().trim();
                            String roomNo = infoTableBody.select("tr:nth-child(5) > td.textFont-2").first().text().trim();
                            String mobile = infoTableBody.select("tr:nth-child(6) > td.textFont-2").first().text().trim();
                            String email = infoTableBody.select("tr:nth-child(7) > td.textFont-2").first().text().trim();
                            String numPasses = infoTableBody.select("tr:nth-child(13) > td.textFont-2").first().text().trim();
                            String photoUrl = infoTableBody.select("tr:nth-child(2) > td:nth-child(3) > img").first().attr("src").trim();
                            photoUrl = "https://anokha.amrita.edu" + photoUrl;
                            Ln.d(photoUrl);
                            setStudentRollNo(regNo);
                            setStudentName(name);

                            infoResponse.onSuccess(regNo, name, hostel, roomNo, mobile, email, photoUrl, numPasses);
                        } catch (Exception e) {
                            infoResponse.onFailure(responseString, e);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String response, Throwable throwable) {
                infoResponse.onFailure(response, throwable);
            }
        });
    }

    public void applyDayPass(String dateTime, String occasion, String reason, final SuccessResponse successResponse) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
        DateTime toDate = formatter.parseDateTime(dateTime).withHourOfDay(18).withMinuteOfHour(59);

        RequestParams params = new RequestParams();
        params.put("regno", studentRollNo);
        params.put("stname", studentName);
        params.put("applyingto", "Warden");
        params.put("passtype", "Day Pass");
        params.put("occassion", occasion);
        params.put("groundsforleave", reason);
        params.put("fromdate", dateTime);
        params.put("todate", toDate.toString(dateFormat));
        params.put("noofdays", 1);
        params.put("confirm", "confirm");

        client.post("/applyleave.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                successResponse.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                successResponse.onSuccess();
            }
        });
    }

    public void applyHomePass(String fromDateTime, String toDateTime, long days, String occasion, String reason, final SuccessResponse successResponse) {
        RequestParams params = new RequestParams();
        params.put("regno", studentRollNo);
        params.put("stname", studentName);
        params.put("applyingto", "Warden");
        params.put("passtype", "Home Pass");
        params.put("occassion", occasion);
        params.put("groundsforleave", reason);
        params.put("fromdate", fromDateTime);
        params.put("todate", toDateTime);
        params.put("noofdays", days);
        params.put("confirm", "confirm");

        client.post("/applyleave.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                successResponse.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                successResponse.onSuccess();
            }
        });
    }

    public void getPendingPasses(final PendingResponse pendingResponse) {
        client.get("/leavestatus.php", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pendingResponse.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Document doc = Jsoup.parse(responseString);
                Element tBody = doc.select("body > div:nth-child(9) > table > tbody").first();
                Elements rows = tBody.select("tr");
                Ln.d(rows.size());
                if(rows.size() == 1) {
                    pendingResponse.onSuccess(new ArrayList<PendingEntry>());
                } else {
                    List<PendingEntry> pendingEntries = new ArrayList<>();
                    for(int i = 1; i < rows.size(); i++) {
                        try {
                            Element row = rows.get(i);
                            PendingEntry pendingEntry = new PendingEntry();
                            pendingEntry.setAppliedFrom(row.select("td:nth-child(2)").first().text().trim());
                            pendingEntry.setAppliedTill(row.select("td:nth-child(3)").first().text().trim());
                            pendingEntry.setRequestedWith(row.select("td:nth-child(4)").first().text().trim());
                            pendingEntry.setPassType(row.select("td:nth-child(5)").first().text().trim());
                            pendingEntry.setApprovalStatus(row.select("td:nth-child(6)").first().text().trim());
                            pendingEntry.setCfwApproval(row.select("td:nth-child(7)").first().text().trim());
                            pendingEntry.setStatus(row.select("td:nth-child(8)").first().text().trim());
                            pendingEntry.setId(row.select("td:nth-child(9) > form > input[name=reqid]").first().attr("value").trim());
                            pendingEntries.add(pendingEntry);
                        } catch (NullPointerException e) {
                            Ln.e(e);
                        }
                    }
                    pendingResponse.onSuccess(pendingEntries);
                }
            }
        });
    }

    public void cancelPass(String requestId, final SuccessResponse successResponse) {
        RequestParams params = new RequestParams();
        params.put("reqid", requestId);
        params.put("cancel", "Cancel");

        client.post("/leavestatus.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                successResponse.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                successResponse.onSuccess();
            }
        });
    }

    public void getPassesHistory(final HistoryResponse historyResponse) {
        client.get("/leavehistory.php", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                historyResponse.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Document doc = Jsoup.parse(responseString);
                Element tBody = doc.select("#FilterForm > label > table > tbody").first();
                Elements rows = tBody.select("tr");
                Ln.d(responseString);
                if(rows.size() == 1) {
                    historyResponse.onSuccess(new ArrayList<HistoryEntry>());
                } else {
                    List<HistoryEntry> historyEntries = new ArrayList<>();
                    for(int i = 1; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        HistoryEntry historyEntry = new HistoryEntry();
                        historyEntry.setDepartureTime(row.select("td:nth-child(2)").first().text().trim());
                        historyEntry.setArrivalTime(row.select("td:nth-child(3)").first().text().trim());
                        historyEntry.setNumDays(row.select("td:nth-child(4)").first().text().trim());
                        historyEntry.setActualNumDays(row.select("td:nth-child(5)").first().text().trim());
                        historyEntry.setPassType(row.select("td:nth-child(6)").first().text().trim());
                        historyEntry.setOccasion(row.select("td:nth-child(7)").first().text().trim());
                        historyEntry.setApprovalStatus(row.select("td:nth-child(8)").first().text().trim());
                        historyEntry.setCfwApproval(row.select("td:nth-child(9)").first().text().trim());
                        historyEntry.setGateApproval(row.select("td:nth-child(10)").first().text().trim());
                        historyEntry.setCancellation(row.select("td:nth-child(11)").first().text().trim());

                        if(historyEntry.getCancellation().equals("No") && !historyEntry.getApprovalStatus().equals("Rejected") && !historyEntry.getApprovalStatus().equals("Pending")) {
                            historyEntries.add(historyEntry);
                        }
                    }
                    historyResponse.onSuccess(historyEntries);
                }
            }
        });
    }
}
