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

package com.njlabs.amrita.aid.info;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

import com.google.firebase.crash.FirebaseCrash;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressLint("SimpleDateFormat")
public class Calender extends BaseActivity {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private CaldroidFragment caldroidFragment;
    private HashMap<String, String> descriptions;
    private HashMap<Date, Integer> backgroundColors;
    private HashMap<Date, Integer> textColors;

    private Date parseDate(String date_str) {
        Date dateStr = null;
        try {
            dateStr = formatter.parse(date_str);
        } catch (ParseException e) {
            FirebaseCrash.report(e);
        }
        return dateStr;
    }

    private boolean containsAny(String string, String[] testStrings) {
        for (String testString : testStrings) {
            if (StringUtils.containsIgnoreCase(string, testString)) {
                return true;
            }
        }
        return false;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setCustomResourceForDates() {

        final Handler dataHandler = new Handler();
        (new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    InputStream icsInput = getAssets().open("ASECalendar.ics");
                    CalendarBuilder builder = new CalendarBuilder();
                    net.fortuna.ical4j.model.Calendar calendar = builder.build(icsInput);

                    for (Object calendarComponentObject : calendar.getComponents()) {
                        CalendarComponent calendarComponent = (CalendarComponent) calendarComponentObject;
                        String title = calendarComponent.getProperty(Property.SUMMARY).getValue();
                        if (title.length() > 4) {
                            Date startDate = parseDate(calendarComponent.getProperty(Property.DTSTART).getValue());
                            Date endDate = parseDate(calendarComponent.getProperty(Property.DTEND).getValue());
                            title = title.replaceAll("^CD\\d\\d:\\s", "").replaceAll("^CD\\d:\\s", "");

                            int color = R.color.calendar_green;

                            if (containsAny(title, new String[]{"assessment", "exam", "test", "assesment", "end semester"})) {
                                color = R.color.calendar_red;
                            } else if (containsAny(title, new String[]{"institution day", "amritotsavam", "amritotasavam", "classes", "working", "instruction", "enrolment", "Birthday", "Talent", "TABLE", "orientation", "counselling"})) {
                                color = R.color.calendar_blue;
                            } else if (containsAny(title, new String[]{"anokha", "tech fest"})) {
                                color = R.color.calendar_anokha_orange;
                            }

                            Calendar start = Calendar.getInstance();
                            start.setTime(startDate);
                            Calendar end = Calendar.getInstance();
                            end.setTime(endDate);

                            //noinspection WrongConstant
                            if (start.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH) || end.get(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH) + 1) {
                                backgroundColors.put(startDate, color);
                                textColors.put(startDate, R.color.white);
                                descriptions.put(formatter.format(startDate), title);
                            } else {
                                for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                                    backgroundColors.put(date, color);
                                    textColors.put(date, R.color.white);
                                    descriptions.put(formatter.format(date), title);
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    FirebaseCrash.report(e);
                }

                dataHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (caldroidFragment != null) {
                            caldroidFragment.setBackgroundResourceForDates(backgroundColors);
                            caldroidFragment.setTextColorForDates(textColors);
                            caldroidFragment.refreshView();
                            findViewById(R.id.calendar_holder).setVisibility(View.VISIBLE);
                            findViewById(R.id.progress).setVisibility(View.GONE);
                        }
                    }
                });
            }
        })).start();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_calender, Color.parseColor("#fe5352"));

        final Calender thisContext = this;

        descriptions = new HashMap<>();
        backgroundColors = new HashMap<>();
        textColors = new HashMap<>();


        SharedPreferences preferences = getSharedPreferences("app_extra", Context.MODE_PRIVATE);
        Boolean AgreeStatus = preferences.getBoolean("calender_agree", false);

        if (!AgreeStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
            builder.setTitle("Academic Calender").setIcon(R.drawable.ic_action_info_small)
                    .setMessage("Red denotes that it's an Exam day. Blue denotes an event (not a Holiday). Whereas Green denotes a Holiday. I'm not resposibile for the accuracy of the dates")
                    .setCancelable(false)
                    .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences preferences = getSharedPreferences("app_extra", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("calender_agree", true);
                            editor.apply();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        caldroidFragment = new CaldroidFragment();

        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
        } else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                if (!formatter.format(date).equals("")) {
                    String description = descriptions.get(formatter.format(date));
                    if (description != null && !description.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                        builder.setMessage(description)
                                .setCancelable(true)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
