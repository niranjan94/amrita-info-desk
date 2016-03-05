/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.gpms.client.Gpms;
import com.njlabs.amrita.aid.util.ark.logging.Ln;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;

import org.angmarch.views.NiceSpinner;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class PassApplyActivity extends BaseActivity {

    DateTime fromDate = null;
    DateTime toDate = null;
    Button fromDateBtn;
    Button toDateBtn;
    EditText reasonEditText;
    NiceSpinner spinner;
    String passType;

    Gpms gpms;
    private ProgressDialog dialog;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_gpms_pass_apply, "Apply for a pass", Color.parseColor("#009688"));

        passType = getIntent().getStringExtra("pass_type");

        spinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("Regular Academic Semester", "Project during vacation", "Training", "Cultural/Club", "Vacation Course"));
        spinner.attachDataSource(dataset);

        fromDateBtn = (Button) findViewById(R.id.from_date_btn);
        toDateBtn = (Button) findViewById(R.id.to_date_btn);
        reasonEditText = (EditText) findViewById(R.id.reason);

        if(passType.equals("Day Pass")) {
            toDateBtn.setVisibility(View.GONE);
            findViewById(R.id.required_till_text).setVisibility(View.GONE);
        }

        gpms = new Gpms(baseContext);

        ((Button) findViewById(R.id.apply_pass)).setText("Apply for " + passType);

        dialog = new ProgressDialog(baseContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Applying ...");
    }

    public void pickFromDate(View v) {
        final DateTime dateTime = DateTime.now();
        loadDateTimePicker(dateTime, v, "fromDate");
    }

    public void pickToDate(View v) {
        loadDateTimePicker(fromDate.plusHours(1), v, "toDate");
    }

    public void applyPass(View v) {
        if(passType.equals("Day Pass")) {
            if(fromDate == null) {
                Snackbar.make(parentView, "Select the from date", Snackbar.LENGTH_LONG).show();
            } else if(reasonEditText.getText().toString().split("\\s+").length < 2) {
                reasonEditText.setError("A minimum of two words is required for the reason");
            } else if(fromDate.getHourOfDay()>=19) {
                Snackbar.make(parentView, "You cannot apply for a day pass after 7pm. Change the time.", Snackbar.LENGTH_LONG).show();
            } else if(fromDate.getMillis() < DateTime.now().getMillis() + 30000) {
                Snackbar.make(parentView, "Please change to a later time.", Snackbar.LENGTH_LONG).show();
            } else {
                dialog.show();
                gpms.applyDayPass(fromDate, spinner.getText().toString(), reasonEditText.getText().toString(), new SuccessResponse() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        Toast.makeText(baseContext, "Your pass has been applied.", Toast.LENGTH_LONG).show();
                        finish();

                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("GPMS")
                                .setAction("Day Pass")
                                .setLabel(gpms.getStudentName() + " - " + gpms.getStudentRollNo())
                                .build());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Ln.e(throwable);
                        dialog.dismiss();
                        Snackbar.make(parentView, "An error occurred. Try again.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        } else if(passType.equals("Home Pass")) {
            if(fromDate == null || toDate == null) {
                Snackbar.make(parentView, "Select the dates", Snackbar.LENGTH_LONG).show();
            } else if(reasonEditText.getText().toString().split("\\s+").length < 2) {
                reasonEditText.setError("A minimum of two words is required for the reason");
            } else if(fromDate.getMillis() < DateTime.now().getMillis() + 30000) {
                Snackbar.make(parentView, "Please change the from date-time to a later time.", Snackbar.LENGTH_LONG).show();
            } else {
                double days = (toDate.getMillis() - fromDate.getMillis())/(1000*60*60*24);
                long roundedDays = Math.round(Math.ceil(days));
                dialog.show();
                gpms.applyHomePass(fromDate, toDate, roundedDays, spinner.getText().toString(), reasonEditText.getText().toString(), new SuccessResponse() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        Toast.makeText(baseContext, "Your pass has been applied.", Toast.LENGTH_LONG).show();
                        finish();

                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("GPMS")
                                .setAction("Home Pass")
                                .setLabel(gpms.getStudentName() + " - " + gpms.getStudentRollNo())
                                .build());

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Ln.e(throwable);
                        dialog.dismiss();
                        Snackbar.make(parentView, "An error occurred. Try again.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void loadDateTimePicker(final DateTime startDate, final View v, final String toModify) {
        MonthAdapter.CalendarDay calendarDay = new MonthAdapter.CalendarDay();
        calendarDay.setDay(startDate.getYear(), startDate.getMonthOfYear()-1, startDate.getDayOfMonth());

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, final int year,final int monthOfYear, final int dayOfMonth) {

                        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                        Ln.d(minute);
                                        boolean error = false;
                                        DateTime selectedDateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
                                        if(toModify.equals("fromDate")) {
                                            fromDate = selectedDateTime;
                                            findViewById(R.id.to_date_btn).setEnabled(true);
                                        } else if(toModify.equals("toDate")) {
                                            toDate = selectedDateTime;
                                            if(toDate.getMillis() <= fromDate.getMillis()) {
                                                error = true;
                                                Snackbar.make(parentView, "The end date should be after the from date.", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                        if(!error) {
                                            ((Button) v).setText(selectedDateTime.toString(Gpms.dateFormat));
                                        }

                                    }
                                })
                                .setStartTime(startDate.getHourOfDay(), (startDate.getMinuteOfHour() - 1 == -1 ? 0 : startDate.getMinuteOfHour() - 1))
                                .setThemeDark(false);
                        rtpd.show(getSupportFragmentManager(), "FRAG_TAG_TIME_PICKER");

                    }
                })
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(startDate.getYear(), startDate.getMonthOfYear()-1, startDate.getDayOfMonth())
                .setDateRange(calendarDay, null)
                .setThemeDark(false);
        cdp.show(getSupportFragmentManager(), "FRAG_TAG_DATE_PICKER");

    }

    /*private DateTime roundOffDate(DateTime target) {
        if(target.getMinuteOfHour() > 0 && target.getMinuteOfHour() < 15) {

        }
    }*/
}
