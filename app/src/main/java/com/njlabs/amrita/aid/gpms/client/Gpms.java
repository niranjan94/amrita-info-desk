/*
 * MIT License
 *
 * Copyright (c) 2016 Niranjan Rajendran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.njlabs.amrita.aid.gpms.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.njlabs.amrita.aid.gpms.models.HistoryEntry;
import com.njlabs.amrita.aid.gpms.models.PendingEntry;
import com.njlabs.amrita.aid.gpms.responses.HistoryResponse;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.gpms.responses.LoginResponse;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.util.ark.Security;
import com.njlabs.amrita.aid.util.okhttp.extras.PersistentCookieStore;
import com.njlabs.amrita.aid.util.okhttp.extras.RequestParams;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.TextResponse;
import com.onemarker.ln.logger.Ln;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Gpms implements AbstractGpms {

    public static String dateFormat = "dd MMM yyyy HH:mm:ss";
    public static String shortDateFormat = "dd MMM yyyy";
    public GpmsClient client;
    private String studentRollNo = null;
    private String studentName = null;
    private String studentHostelCode = null;
    private SharedPreferences cookiePrefFile;
    private Context context;

    public Gpms(Context context) {
        this.context = context;
        client = new GpmsClient(context);
        setupAuthentication();
        try {
            client.powerUp();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        cookiePrefFile = context.getSharedPreferences(PersistentCookieStore.GPMS_COOKIE_PREFS, Context.MODE_PRIVATE);

        studentRollNo = cookiePrefFile.getString("gpms_roll_no", null);
        studentName = cookiePrefFile.getString("gpms_name", null);
        studentHostelCode = cookiePrefFile.getString("gpms_hostel_code", null);
    }

    public Gpms(Context context, String cookiePrefFilename) {
        this.context = context;
        client = new GpmsClient(context, cookiePrefFilename);
        setupAuthentication();
        try {
            client.powerUp();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        cookiePrefFile = context.getSharedPreferences(cookiePrefFilename, Context.MODE_PRIVATE);

        studentRollNo = cookiePrefFile.getString("gpms_roll_no", null);
        studentName = cookiePrefFile.getString("gpms_name", null);
        studentHostelCode = cookiePrefFile.getString("gpms_hostel_code", null);
    }

    private void setupAuthentication() {
        if(client.isProxyOn()) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo("com.njlabs.amrita.aid", PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String hash = Security.convertToHex(md.digest());
                    client.addCustomHeader("Dilithium", hash);
                }
            } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
                Ln.e(ignored);
            }

        }
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        cookiePrefFile.edit().putString("gpms_name", studentName).apply();
        this.studentName = studentName;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        cookiePrefFile.edit().putString("gpms_roll_no", studentRollNo).apply();
        this.studentRollNo = studentRollNo;
    }

    public void setStudentHostelCode(String studentHostelCode) {
        cookiePrefFile.edit().putString("gpms_hostel_code", studentHostelCode).apply();
        this.studentHostelCode = studentHostelCode;
    }

    @SuppressLint("CommitPrefEdits")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void logout() {
        client.resetClient();
    }

    public void deletePrefs() {
        try {
            @SuppressLint("SdCardPath")
            File deletePrefFile = new File("/data/data/com.njlabs.amrita.aid/shared_prefs/" + client.COOKIE_FILE + ".xml");
            deletePrefFile.delete();
        } catch (Exception ignored) {
        }
    }

    public void basicLogin(String rollNo, String password, final LoginResponse loginResponse) {

        RequestParams params = new RequestParams();
        params.put("userid", rollNo);
        params.put("passwd", password);
        params.put("submit", "");

        client.post("/index.php", params, new TextResponse() {

            @Override
            public void onFailure(Throwable throwable) {
                loginResponse.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {
                loginResponse.onSuccess();
            }
        });
    }

    @Override
    public void login(String rollNo, String password, final InfoResponse infoResponse) {

        basicLogin(rollNo, password, new LoginResponse() {
            @Override
            public void onFailedAuthentication() {
                infoResponse.onFailedAuthentication();
            }

            @Override
            public void onSuccess() {
                client.get("/applyleave.php", null, new TextResponse() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        infoResponse.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(String responseString) {
                        try {
                            Document doc = Jsoup.parse(responseString);

                            Elements forgotPassword = doc.select("p.forgot");
                            if (forgotPassword.size() != 0) {
                                infoResponse.onFailedAuthentication();
                                return;
                            }
                            Element mainContent = doc.select("div.maincontent").first();

                            Element infoTableBody = mainContent.select("form > center > table > tbody > tr > td:nth-child(1) > table > tbody").first();

                            String regNo = infoTableBody.select("tr:nth-child(2) > td.textFont-2").first().text().trim();
                            String name = infoTableBody.select("tr:nth-child(3) > td.textFont-2").first().text().trim();
                            String hostelCode = mainContent.select("input[name=sthostel]").first().val();
                            String hostel = infoTableBody.select("tr:nth-child(4) > td.textFont-2").first().text().trim();
                            String roomNo = infoTableBody.select("tr:nth-child(5) > td.textFont-2").first().text().trim();
                            String mobile = infoTableBody.select("tr:nth-child(6) > td.textFont-2").first().text().trim();
                            String email = infoTableBody.select("tr:nth-child(7) > td.textFont-2").first().text().trim();
                            String numPasses = infoTableBody.select("tr:nth-child(13) > td.textFont-2").first().text().trim();
                            String photoUrl = infoTableBody.select("tr:nth-child(2) > td:nth-child(3) > img").first().attr("src").trim();
                            photoUrl = client.getAbsoluteUrl("/" + photoUrl);
                            Ln.d(photoUrl);
                            setStudentRollNo(regNo);
                            setStudentName(name);
                            setStudentHostelCode(hostelCode);

                            infoResponse.onSuccess(regNo, name, hostel, roomNo, mobile, email, photoUrl, numPasses);
                        } catch (Exception e) {
                            infoResponse.onFailure(e);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                infoResponse.onFailure(throwable);
            }
        });
    }

    @Override
    public void applyDayPass(final DateTime fromDate, final String occasion, final String reason, final SuccessResponse successResponse) {

        RequestParams params = new RequestParams();
        params.put("stregno", studentRollNo);
        params.put("stname", studentName);
        params.put("sthostel", studentHostelCode);
        params.put("passtaken", "");
        params.put("passtype", "Day Pass");
        params.put("fromdate", roundOffDate(fromDate).toString(shortDateFormat));
        params.put("fhour", roundOffDate(fromDate).getHourOfDay());
        params.put("fmin", leftPadInteger(roundOffDate(fromDate).getMinuteOfHour()));
        params.put("applyingto", "Warden");
        params.put("occassion", occasion);
        params.put("groundsforleave", reason);
        params.put("leavesubmit", "Submit Leave");

        client.setReferrer(client.getUnproxiedUrl("/applyleave.php"));
        client.post("/applyleave.php", params, new TextResponse() {

            @Override
            public void onSuccess(String responseString) {

                Document doc = Jsoup.parse(responseString);
                Elements formInputs = doc.select("form[action=applyleave.php]").first().select("input");
                RequestParams params = new RequestParams();

                for (Element formInput : formInputs) {
                    params.put(formInput.attr("name"), formInput.val());
                }

                client.post("/applyleave.php", params, new TextResponse() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        client.removeReferrer();
                        successResponse.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(String responseString) {
                        client.removeReferrer();
                        successResponse.onSuccess();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                client.removeReferrer();
                successResponse.onFailure(throwable);
            }
        });


    }

    @Override
    public void applyHomePass(final DateTime fromDate, final DateTime toDate, final String occasion, final String reason, final SuccessResponse successResponse) {


        RequestParams params = new RequestParams();
        params.put("stregno", studentRollNo);
        params.put("stname", studentName);
        params.put("sthostel", studentHostelCode);
        params.put("passtaken", "");
        params.put("passtype", "Home Pass");
        params.put("fromdate", roundOffDate(fromDate).toString(shortDateFormat));
        params.put("fhour", roundOffDate(fromDate).getHourOfDay());
        params.put("fmin", leftPadInteger(roundOffDate(fromDate).getMinuteOfHour()));
        params.put("todate", roundOffDate(toDate).toString(shortDateFormat));
        params.put("thour", roundOffDate(toDate).getHourOfDay());
        params.put("tmin", leftPadInteger(roundOffDate(toDate).getMinuteOfHour()));
        params.put("applyingto", "Warden");
        params.put("occassion", occasion);
        params.put("groundsforleave", reason);
        params.put("leavesubmit", "Submit Leave");

        client.setReferrer(client.getUnproxiedUrl("/applyleave.php"));
        client.post("/applyleave.php", params, new TextResponse() {
            @Override
            public void onSuccess(String responseString) {

                Document doc = Jsoup.parse(responseString);
                Elements formInputs = doc.select("form[action=applyleave.php]").first().select("input");
                RequestParams params = new RequestParams();

                for (Element formInput : formInputs) {
                    params.put(formInput.attr("name"), formInput.val());
                }

                params.put("confirm", "Confirm");

                client.post("/applyleave.php", params, new TextResponse() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        client.removeReferrer();
                        successResponse.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(String responseString) {
                        client.removeReferrer();
                        successResponse.onSuccess();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                client.removeReferrer();
                successResponse.onFailure(throwable);
            }
        });
    }

    @Override
    public void getPendingPasses(final PendingResponse pendingResponse) {
        client.setReferrer(client.getUnproxiedUrl("/home.php"));
        client.get("/leavestatus.php", null, new TextResponse() {
            @Override
            public void onFailure(Throwable throwable) {
                client.removeReferrer();
                pendingResponse.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {
                client.removeReferrer();
                Document doc = Jsoup.parse(responseString);

                Element tBody = doc.select("body > div:nth-child(7) > table > tbody").first();
                Elements rows = tBody.select("tr");
                Ln.d(rows.size());
                if (rows.size() == 1) {
                    pendingResponse.onSuccess(new ArrayList<PendingEntry>());
                } else {
                    List<PendingEntry> pendingEntries = new ArrayList<>();
                    for (int i = 1; i < rows.size(); i++) {
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

    @Override
    public void cancelPass(String requestId, final SuccessResponse successResponse) {
        RequestParams params = new RequestParams();
        params.put("reqid", requestId);
        params.put("cancel", "Cancel");

        client.post("/leavestatus.php", params, new TextResponse() {
            @Override
            public void onFailure(Throwable throwable) {
                successResponse.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {
                successResponse.onSuccess();
            }
        });
    }

    @Override
    public void getPassesHistory(final HistoryResponse historyResponse) {
        client.setReferrer(client.getUnproxiedUrl("/home.php"));
        client.get("/leavehistory.php", null, new TextResponse() {
            @Override
            public void onFailure(Throwable throwable) {
                client.removeReferrer();
                historyResponse.onFailure(throwable);
            }

            @Override
            public void onSuccess(String responseString) {
                client.removeReferrer();
                Document doc = Jsoup.parse(responseString);
                Element tBody = doc.select("#FilterForm > label > table > tbody").first();
                Elements rows = tBody.select("tr");
                Ln.d(responseString);
                if (rows.size() == 1) {
                    historyResponse.onSuccess(new ArrayList<HistoryEntry>());
                } else {
                    List<HistoryEntry> historyEntries = new ArrayList<>();
                    for (int i = 1; i < rows.size(); i++) {
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

                        if (historyEntry.getCancellation().equals("No") && !historyEntry.getApprovalStatus().equals("Rejected") && !historyEntry.getApprovalStatus().equals("Pending")) {
                            historyEntries.add(historyEntry);
                        }
                    }
                    historyResponse.onSuccess(historyEntries);
                }
            }
        });
    }

    private DateTime roundOffDate(DateTime target) {
        int minutes = target.getMinuteOfHour();
        if (minutes > 0 && minutes < 15) {
            return target.withMinuteOfHour(15);
        } else if (minutes > 15 && minutes < 30) {
            return target.withMinuteOfHour(30);
        } else if (minutes > 30 && minutes < 45) {
            return target.withMinuteOfHour(45);
        } else if (minutes > 45 && minutes <= 59) {
            if (target.getHourOfDay() == 23) {
                return target.plusDays(1).withHourOfDay(0).withMinuteOfHour(0);
            } else {
                return target.plusHours(1).withMinuteOfHour(0);
            }
        } else {
            return target;
        }
    }

    private String leftPadInteger(int val) {
        return String.format("%02d", val);
    }

    public DateTimeFormatter getLongDateTimeFormatter() {
        return DateTimeFormat.forPattern(dateFormat);
    }
}
