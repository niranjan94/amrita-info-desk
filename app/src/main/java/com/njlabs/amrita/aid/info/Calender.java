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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressLint("SimpleDateFormat")
public class Calender extends BaseActivity {

    private CaldroidFragment caldroidFragment;
    HashMap<String, String> desc;


    public Date ParseDate(String date_str) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dateStr = null;
        try {
            dateStr = formatter.parse(date_str);
        } catch (ParseException e) {
            FirebaseCrash.report(e);
        }
        return dateStr;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setCustomResourceForDates() {

        final Handler dataHandler = new Handler();
        (new Thread(new Runnable() {
            @Override
            public void run() {

                // Create a hash map
                final HashMap hm = new HashMap();
                // HOLIDAYS
                hm.put(ParseDate("18-07-2015"), R.color.calendar_green);
                hm.put(ParseDate("15-08-2015"), R.color.calendar_green);
                hm.put(ParseDate("27-08-2015"), R.color.calendar_green);
                hm.put(ParseDate("28-08-2015"), R.color.calendar_green);
                hm.put(ParseDate("29-08-2015"), R.color.calendar_green);
                hm.put(ParseDate("30-08-2015"), R.color.calendar_green);
                hm.put(ParseDate("17-09-2015"), R.color.calendar_green);
                hm.put(ParseDate("24-09-2015"), R.color.calendar_green);
                hm.put(ParseDate("27-09-2015"), R.color.calendar_green);
                hm.put(ParseDate("02-10-2015"), R.color.calendar_green);
                hm.put(ParseDate("03-10-2015"), R.color.calendar_green);
                hm.put(ParseDate("04-10-2015"), R.color.calendar_green);
                hm.put(ParseDate("21-10-2015"), R.color.calendar_green);
                hm.put(ParseDate("22-10-2015"), R.color.calendar_green);
                hm.put(ParseDate("23-10-2015"), R.color.calendar_green);
                hm.put(ParseDate("09-11-2015"), R.color.calendar_green);
                hm.put(ParseDate("10-11-2015"), R.color.calendar_green);
                hm.put(ParseDate("23-12-2015"), R.color.calendar_green);
                hm.put(ParseDate("25-12-2015"), R.color.calendar_green);
                hm.put(ParseDate("01-01-2016"), R.color.calendar_green);
                hm.put(ParseDate("14-01-2016"), R.color.calendar_green);
                hm.put(ParseDate("15-01-2016"), R.color.calendar_green);
                hm.put(ParseDate("16-01-2016"), R.color.calendar_green);
                hm.put(ParseDate("17-01-2016"), R.color.calendar_green);
                hm.put(ParseDate("26-01-2016"), R.color.calendar_green);
                hm.put(ParseDate("07-03-2016"), R.color.calendar_green);
                hm.put(ParseDate("25-03-2016"), R.color.calendar_green);
                hm.put(ParseDate("08-04-2016"), R.color.calendar_green);
                hm.put(ParseDate("13-04-2016"), R.color.calendar_green);
                hm.put(ParseDate("14-04-2016"), R.color.calendar_green);
                hm.put(ParseDate("01-05-2016"), R.color.calendar_green);


                // EVENTS
                hm.put(ParseDate("15-07-2015"), R.color.calendar_blue);
                hm.put(ParseDate("23-07-2015"), R.color.calendar_blue);
                hm.put(ParseDate("25-07-2015"), R.color.calendar_blue);
                hm.put(ParseDate("29-07-2015"), R.color.calendar_blue);
                hm.put(ParseDate("31-07-2015"), R.color.calendar_blue);
                hm.put(ParseDate("07-08-2015"), R.color.calendar_blue);
                hm.put(ParseDate("08-08-2015"), R.color.calendar_blue);
                hm.put(ParseDate("05-09-2015"), R.color.calendar_blue);
                hm.put(ParseDate("19-09-2015"), R.color.calendar_blue);
                hm.put(ParseDate("10-10-2015"), R.color.calendar_blue);
                hm.put(ParseDate("31-10-2015"), R.color.calendar_blue);
                hm.put(ParseDate("07-11-2015"), R.color.calendar_blue);
                hm.put(ParseDate("20-11-2015"), R.color.calendar_blue);
                hm.put(ParseDate("04-01-2016"), R.color.calendar_blue);
                hm.put(ParseDate("09-01-2016"), R.color.calendar_blue);
                hm.put(ParseDate("23-01-2016"), R.color.calendar_blue);
                hm.put(ParseDate("26-02-2016"), R.color.calendar_blue);
                hm.put(ParseDate("27-02-2016"), R.color.calendar_blue);
                hm.put(ParseDate("05-03-2016"), R.color.calendar_blue);
                hm.put(ParseDate("02-04-2016"), R.color.calendar_blue);
                hm.put(ParseDate("16-04-2016"), R.color.calendar_blue);
                hm.put(ParseDate("30-04-2016"), R.color.calendar_blue);


                // EXAMS
                hm.put(ParseDate("17-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("18-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("19-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("20-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("21-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("22-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("31-08-2015"), R.color.calendar_red);
                hm.put(ParseDate("01-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("02-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("03-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("04-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("07-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("08-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("09-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("10-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("11-09-2015"), R.color.calendar_red);
                hm.put(ParseDate("12-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("13-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("14-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("15-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("16-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("17-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("26-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("27-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("28-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("29-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("30-10-2015"), R.color.calendar_red);
                hm.put(ParseDate("16-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("17-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("18-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("21-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("22-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("23-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("24-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("25-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("26-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("27-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("28-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("29-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("30-11-2015"), R.color.calendar_red);
                hm.put(ParseDate("01-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("02-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("03-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("04-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("05-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("06-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("07-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("08-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("14-12-2015"), R.color.calendar_red);
                hm.put(ParseDate("01-02-2016"), R.color.calendar_red);
                hm.put(ParseDate("02-02-2016"), R.color.calendar_red);
                hm.put(ParseDate("03-02-2016"), R.color.calendar_red);
                hm.put(ParseDate("04-02-2016"), R.color.calendar_red);
                hm.put(ParseDate("05-02-2016"), R.color.calendar_red);
                hm.put(ParseDate("06-02-2016"), R.color.calendar_red);
                hm.put(ParseDate("08-03-2016"), R.color.calendar_red);
                hm.put(ParseDate("09-03-2016"), R.color.calendar_red);
                hm.put(ParseDate("10-03-2016"), R.color.calendar_red);
                hm.put(ParseDate("11-03-2016"), R.color.calendar_red);
                hm.put(ParseDate("12-03-2016"), R.color.calendar_red);
                hm.put(ParseDate("18-04-2016"), R.color.calendar_red);
                hm.put(ParseDate("19-04-2016"), R.color.calendar_red);
                hm.put(ParseDate("20-04-2016"), R.color.calendar_red);
                hm.put(ParseDate("21-04-2016"), R.color.calendar_red);
                hm.put(ParseDate("02-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("03-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("04-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("05-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("06-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("07-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("08-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("09-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("10-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("11-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("12-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("13-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("23-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("24-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("25-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("26-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("27-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("28-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("29-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("30-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("31-05-2016"), R.color.calendar_red);
                hm.put(ParseDate("01-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("02-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("03-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("04-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("05-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("06-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("13-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("14-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("15-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("16-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("17-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("18-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("19-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("20-06-2016"), R.color.calendar_red);
                hm.put(ParseDate("21-06-2016"), R.color.calendar_red);

                // ANOKHA
                hm.put(ParseDate("17-02-2016"), R.color.calendar_anokha_orange);
                hm.put(ParseDate("18-02-2016"), R.color.calendar_anokha_orange);
                hm.put(ParseDate("19-02-2016"), R.color.calendar_anokha_orange);

                // TEXT HASH MAP
                final HashMap thm = new HashMap();
                thm.put(ParseDate("15-07-2015"), R.color.white);
                thm.put(ParseDate("23-07-2015"), R.color.white);
                thm.put(ParseDate("25-07-2015"), R.color.white);
                thm.put(ParseDate("29-07-2015"), R.color.white);
                thm.put(ParseDate("31-07-2015"), R.color.white);
                thm.put(ParseDate("07-08-2015"), R.color.white);
                thm.put(ParseDate("08-08-2015"), R.color.white);
                thm.put(ParseDate("05-09-2015"), R.color.white);
                thm.put(ParseDate("19-09-2015"), R.color.white);
                thm.put(ParseDate("10-10-2015"), R.color.white);
                thm.put(ParseDate("31-10-2015"), R.color.white);
                thm.put(ParseDate("07-11-2015"), R.color.white);
                thm.put(ParseDate("20-11-2015"), R.color.white);
                thm.put(ParseDate("04-01-2016"), R.color.white);
                thm.put(ParseDate("09-01-2016"), R.color.white);
                thm.put(ParseDate("23-01-2016"), R.color.white);
                thm.put(ParseDate("26-02-2016"), R.color.white);
                thm.put(ParseDate("27-02-2016"), R.color.white);
                thm.put(ParseDate("05-03-2016"), R.color.white);
                thm.put(ParseDate("02-04-2016"), R.color.white);
                thm.put(ParseDate("16-04-2016"), R.color.white);
                thm.put(ParseDate("30-04-2016"), R.color.white);
                thm.put(ParseDate("17-08-2015"), R.color.white);
                thm.put(ParseDate("18-08-2015"), R.color.white);
                thm.put(ParseDate("19-08-2015"), R.color.white);
                thm.put(ParseDate("20-08-2015"), R.color.white);
                thm.put(ParseDate("21-08-2015"), R.color.white);
                thm.put(ParseDate("22-08-2015"), R.color.white);
                thm.put(ParseDate("31-08-2015"), R.color.white);
                thm.put(ParseDate("01-09-2015"), R.color.white);
                thm.put(ParseDate("02-09-2015"), R.color.white);
                thm.put(ParseDate("03-09-2015"), R.color.white);
                thm.put(ParseDate("04-09-2015"), R.color.white);
                thm.put(ParseDate("07-09-2015"), R.color.white);
                thm.put(ParseDate("08-09-2015"), R.color.white);
                thm.put(ParseDate("09-09-2015"), R.color.white);
                thm.put(ParseDate("10-09-2015"), R.color.white);
                thm.put(ParseDate("11-09-2015"), R.color.white);
                thm.put(ParseDate("12-10-2015"), R.color.white);
                thm.put(ParseDate("13-10-2015"), R.color.white);
                thm.put(ParseDate("14-10-2015"), R.color.white);
                thm.put(ParseDate("15-10-2015"), R.color.white);
                thm.put(ParseDate("16-10-2015"), R.color.white);
                thm.put(ParseDate("17-10-2015"), R.color.white);
                thm.put(ParseDate("26-10-2015"), R.color.white);
                thm.put(ParseDate("27-10-2015"), R.color.white);
                thm.put(ParseDate("28-10-2015"), R.color.white);
                thm.put(ParseDate("29-10-2015"), R.color.white);
                thm.put(ParseDate("30-10-2015"), R.color.white);
                thm.put(ParseDate("16-11-2015"), R.color.white);
                thm.put(ParseDate("17-11-2015"), R.color.white);
                thm.put(ParseDate("18-11-2015"), R.color.white);
                thm.put(ParseDate("21-11-2015"), R.color.white);
                thm.put(ParseDate("22-11-2015"), R.color.white);
                thm.put(ParseDate("23-11-2015"), R.color.white);
                thm.put(ParseDate("24-11-2015"), R.color.white);
                thm.put(ParseDate("25-11-2015"), R.color.white);
                thm.put(ParseDate("26-11-2015"), R.color.white);
                thm.put(ParseDate("27-11-2015"), R.color.white);
                thm.put(ParseDate("28-11-2015"), R.color.white);
                thm.put(ParseDate("29-11-2015"), R.color.white);
                thm.put(ParseDate("30-11-2015"), R.color.white);
                thm.put(ParseDate("01-12-2015"), R.color.white);
                thm.put(ParseDate("02-12-2015"), R.color.white);
                thm.put(ParseDate("03-12-2015"), R.color.white);
                thm.put(ParseDate("04-12-2015"), R.color.white);
                thm.put(ParseDate("05-12-2015"), R.color.white);
                thm.put(ParseDate("06-12-2015"), R.color.white);
                thm.put(ParseDate("07-12-2015"), R.color.white);
                thm.put(ParseDate("08-12-2015"), R.color.white);
                thm.put(ParseDate("14-12-2015"), R.color.white);
                thm.put(ParseDate("01-02-2016"), R.color.white);
                thm.put(ParseDate("02-02-2016"), R.color.white);
                thm.put(ParseDate("03-02-2016"), R.color.white);
                thm.put(ParseDate("04-02-2016"), R.color.white);
                thm.put(ParseDate("05-02-2016"), R.color.white);
                thm.put(ParseDate("06-02-2016"), R.color.white);
                thm.put(ParseDate("08-03-2016"), R.color.white);
                thm.put(ParseDate("09-03-2016"), R.color.white);
                thm.put(ParseDate("10-03-2016"), R.color.white);
                thm.put(ParseDate("11-03-2016"), R.color.white);
                thm.put(ParseDate("12-03-2016"), R.color.white);
                thm.put(ParseDate("18-04-2016"), R.color.white);
                thm.put(ParseDate("19-04-2016"), R.color.white);
                thm.put(ParseDate("20-04-2016"), R.color.white);
                thm.put(ParseDate("21-04-2016"), R.color.white);
                thm.put(ParseDate("02-05-2016"), R.color.white);
                thm.put(ParseDate("03-05-2016"), R.color.white);
                thm.put(ParseDate("04-05-2016"), R.color.white);
                thm.put(ParseDate("05-05-2016"), R.color.white);
                thm.put(ParseDate("06-05-2016"), R.color.white);
                thm.put(ParseDate("07-05-2016"), R.color.white);
                thm.put(ParseDate("08-05-2016"), R.color.white);
                thm.put(ParseDate("09-05-2016"), R.color.white);
                thm.put(ParseDate("10-05-2016"), R.color.white);
                thm.put(ParseDate("11-05-2016"), R.color.white);
                thm.put(ParseDate("12-05-2016"), R.color.white);
                thm.put(ParseDate("13-05-2016"), R.color.white);
                thm.put(ParseDate("23-05-2016"), R.color.white);
                thm.put(ParseDate("24-05-2016"), R.color.white);
                thm.put(ParseDate("25-05-2016"), R.color.white);
                thm.put(ParseDate("26-05-2016"), R.color.white);
                thm.put(ParseDate("27-05-2016"), R.color.white);
                thm.put(ParseDate("28-05-2016"), R.color.white);
                thm.put(ParseDate("29-05-2016"), R.color.white);
                thm.put(ParseDate("30-05-2016"), R.color.white);
                thm.put(ParseDate("31-05-2016"), R.color.white);
                thm.put(ParseDate("01-06-2016"), R.color.white);
                thm.put(ParseDate("02-06-2016"), R.color.white);
                thm.put(ParseDate("03-06-2016"), R.color.white);
                thm.put(ParseDate("04-06-2016"), R.color.white);
                thm.put(ParseDate("05-06-2016"), R.color.white);
                thm.put(ParseDate("06-06-2016"), R.color.white);
                thm.put(ParseDate("13-06-2016"), R.color.white);
                thm.put(ParseDate("14-06-2016"), R.color.white);
                thm.put(ParseDate("15-06-2016"), R.color.white);
                thm.put(ParseDate("16-06-2016"), R.color.white);
                thm.put(ParseDate("17-06-2016"), R.color.white);
                thm.put(ParseDate("18-06-2016"), R.color.white);
                thm.put(ParseDate("19-06-2016"), R.color.white);
                thm.put(ParseDate("20-06-2016"), R.color.white);
                thm.put(ParseDate("21-06-2016"), R.color.white);
                thm.put(ParseDate("18-07-2015"), R.color.white);
                thm.put(ParseDate("15-08-2015"), R.color.white);
                thm.put(ParseDate("27-08-2015"), R.color.white);
                thm.put(ParseDate("28-08-2015"), R.color.white);
                thm.put(ParseDate("29-08-2015"), R.color.white);
                thm.put(ParseDate("30-08-2015"), R.color.white);
                thm.put(ParseDate("17-09-2015"), R.color.white);
                thm.put(ParseDate("24-09-2015"), R.color.white);
                thm.put(ParseDate("27-09-2015"), R.color.white);
                thm.put(ParseDate("02-10-2015"), R.color.white);
                thm.put(ParseDate("03-10-2015"), R.color.white);
                thm.put(ParseDate("04-10-2015"), R.color.white);
                thm.put(ParseDate("21-10-2015"), R.color.white);
                thm.put(ParseDate("22-10-2015"), R.color.white);
                thm.put(ParseDate("23-10-2015"), R.color.white);
                thm.put(ParseDate("09-11-2015"), R.color.white);
                thm.put(ParseDate("10-11-2015"), R.color.white);
                thm.put(ParseDate("23-12-2015"), R.color.white);
                thm.put(ParseDate("25-12-2015"), R.color.white);
                thm.put(ParseDate("01-01-2016"), R.color.white);
                thm.put(ParseDate("14-01-2016"), R.color.white);
                thm.put(ParseDate("15-01-2016"), R.color.white);
                thm.put(ParseDate("16-01-2016"), R.color.white);
                thm.put(ParseDate("17-01-2016"), R.color.white);
                thm.put(ParseDate("26-01-2016"), R.color.white);
                thm.put(ParseDate("07-03-2016"), R.color.white);
                thm.put(ParseDate("25-03-2016"), R.color.white);
                thm.put(ParseDate("08-04-2016"), R.color.white);
                thm.put(ParseDate("13-04-2016"), R.color.white);
                thm.put(ParseDate("14-04-2016"), R.color.white);
                thm.put(ParseDate("01-05-2016"), R.color.white);
                thm.put(ParseDate("17-02-2016"), R.color.white);
                thm.put(ParseDate("18-02-2016"), R.color.white);
                thm.put(ParseDate("19-02-2016"), R.color.white);

                dataHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (caldroidFragment != null) {
                            caldroidFragment.setBackgroundResourceForDates(hm);
                            caldroidFragment.setTextColorForDates(thm);
                            caldroidFragment.refreshView();
                        }
                    }
                });
            }
        })).start();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_calender, Color.parseColor("#fe5352"));
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final Calender this_context = this;

        SharedPreferences preferences = getSharedPreferences("app_extra", Context.MODE_PRIVATE);
        Boolean AgreeStatus = preferences.getBoolean("calender_agree", false);

        if (!AgreeStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this_context);
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
        // DESCRIPTION FOR EACH DATE
        desc = new HashMap<>();
        desc.put("15-07-2015", "Enrolment Senior classes");
        desc.put("23-07-2015", "Enrolment for I UG/Integrated programmes");
        desc.put("25-07-2015", "THURSDAY’S TIME TABLE");
        desc.put("29-07-2015", "Enrolment I PG programme");
        desc.put("31-07-2015", "Gurupoornima");
        desc.put("07-08-2015", "Talents Day");
        desc.put("08-08-2015", "FRIDAY’S TIME TABLE");
        desc.put("05-09-2015", "Janmashtami");
        desc.put("19-09-2015", "THURSDAY’S TIME TABLE");
        desc.put("10-10-2015", "FRIDAY’s TIME TABLE");
        desc.put("31-10-2015", "WEDNESDAY’s TIME TABLE");
        desc.put("07-11-2015", "THURSDAY’S TIME TABLE");
        desc.put("20-11-2015", "Last instruction day for UG / PG");
        desc.put("04-01-2016", "Enrolment and commencement of all UG and PG Programmes");
        desc.put("09-01-2016", "THURSDAY’S TIME TABLE");
        desc.put("23-01-2016", "FRIDAY’S TIME TABLE");
        desc.put("26-02-2016", "Amritotsavam");
        desc.put("27-02-2016", "Institution Day");
        desc.put("05-03-2016", "THURSDAY’S TIME TABLE");
        desc.put("02-04-2016", "FRIDAY’S TIME TABLE");
        desc.put("16-04-2016", "FRIDAY’S TIME TABLE");
        desc.put("30-04-2016", "Last instruction day for all UG & PG Programmes");
        desc.put("17-08-2015", "First assesment for all higher semesters");
        desc.put("18-08-2015", "First assesment for all higher semesters");
        desc.put("19-08-2015", "First assesment for all higher semesters");
        desc.put("20-08-2015", "First assesment for all higher semesters");
        desc.put("21-08-2015", "First assesment for all higher semesters");
        desc.put("22-08-2015", "First assesment for all higher semesters");
        desc.put("31-08-2015", "First assesment for first year UG");
        desc.put("01-09-2015", "First assesment for first year UG");
        desc.put("02-09-2015", "First assesment for first year UG");
        desc.put("03-09-2015", "First assesment for first year UG");
        desc.put("04-09-2015", "First assesment for first year UG");
        desc.put("07-09-2015", "First assesment for first year PG");
        desc.put("08-09-2015", "First assesment for first year PG");
        desc.put("09-09-2015", "First assesment for first year PG");
        desc.put("10-09-2015", "First assesment for first year PG");
        desc.put("11-09-2015", "First assesment for first year PG");
        desc.put("12-10-2015", "Second assesment for all except first year PG");
        desc.put("13-10-2015", "Second assesment for all except first year PG");
        desc.put("14-10-2015", "Second assesment for all except first year PG");
        desc.put("15-10-2015", "Second assesment for all except first year PG");
        desc.put("16-10-2015", "Second assesment for all except first year PG");
        desc.put("17-10-2015", "Second assesment for all except first year PG");
        desc.put("26-10-2015", "Second assesment for first year PG");
        desc.put("27-10-2015", "Second assesment for first year PG");
        desc.put("28-10-2015", "Second assesment for first year PG");
        desc.put("29-10-2015", "Second assesment for first year PG");
        desc.put("30-10-2015", "Second assesment for first year PG");
        desc.put("16-11-2015", "Third Assesment for all UG and PG");
        desc.put("17-11-2015", "Third Assesment for all UG and PG");
        desc.put("18-11-2015", "Third Assesment for all UG and PG");
        desc.put("21-11-2015", "End semester examinations for all UG and PG");
        desc.put("22-11-2015", "End semester examinations for all UG and PG");
        desc.put("23-11-2015", "End semester examinations for all UG and PG");
        desc.put("24-11-2015", "End semester examinations for all UG and PG");
        desc.put("25-11-2015", "End semester examinations for all UG and PG");
        desc.put("26-11-2015", "End semester examinations for all UG and PG");
        desc.put("27-11-2015", "End semester examinations for all UG and PG");
        desc.put("28-11-2015", "End semester examinations for all UG and PG");
        desc.put("29-11-2015", "End semester examinations for all UG and PG");
        desc.put("30-11-2015", "End semester examinations for all UG and PG");
        desc.put("01-12-2015", "End semester examinations for all UG and PG");
        desc.put("02-12-2015", "End semester examinations for all UG and PG");
        desc.put("03-12-2015", "End semester examinations for all UG and PG");
        desc.put("04-12-2015", "End semester examinations for all UG and PG");
        desc.put("05-12-2015", "End semester examinations for all UG and PG");
        desc.put("06-12-2015", "End semester examinations for all UG and PG");
        desc.put("07-12-2015", "End semester examinations for all UG and PG");
        desc.put("08-12-2015", "End semester examinations for all UG and PG");
        desc.put("14-12-2015", "Commencement of Second Chance Examination");
        desc.put("01-02-2016", "First Assesment for all UG and PG");
        desc.put("02-02-2016", "First Assesment for all UG and PG");
        desc.put("03-02-2016", "First Assesment for all UG and PG");
        desc.put("04-02-2016", "First Assesment for all UG and PG");
        desc.put("05-02-2016", "First Assesment for all UG and PG");
        desc.put("06-02-2016", "First Assesment for all UG and PG");
        desc.put("08-03-2016", "Second assesment for all UG and PG");
        desc.put("09-03-2016", "Second assesment for all UG and PG");
        desc.put("10-03-2016", "Second assesment for all UG and PG");
        desc.put("11-03-2016", "Second assesment for all UG and PG");
        desc.put("12-03-2016", "Second assesment for all UG and PG");
        desc.put("18-04-2016", "Third Assesment for all UG and PG");
        desc.put("19-04-2016", "Third Assesment for all UG and PG");
        desc.put("20-04-2016", "Third Assesment for all UG and PG");
        desc.put("21-04-2016", "Third Assesment for all UG and PG");
        desc.put("02-05-2016", "End Semester for all UG and PG");
        desc.put("03-05-2016", "End Semester for all UG and PG");
        desc.put("04-05-2016", "End Semester for all UG and PG");
        desc.put("05-05-2016", "End Semester for all UG and PG");
        desc.put("06-05-2016", "End Semester for all UG and PG");
        desc.put("07-05-2016", "End Semester for all UG and PG");
        desc.put("08-05-2016", "End Semester for all UG and PG");
        desc.put("09-05-2016", "End Semester for all UG and PG");
        desc.put("10-05-2016", "End Semester for all UG and PG");
        desc.put("11-05-2016", "End Semester for all UG and PG");
        desc.put("12-05-2016", "End Semester for all UG and PG");
        desc.put("13-05-2016", "End Semester for all UG and PG");
        desc.put("23-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("24-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("25-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("26-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("27-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("28-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("29-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("30-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("31-05-2016", "Second Chance examinations for all UG and PG");
        desc.put("01-06-2016", "Second Chance examinations for all UG and PG");
        desc.put("02-06-2016", "Second Chance examinations for all UG and PG");
        desc.put("03-06-2016", "Second Chance examinations for all UG and PG");
        desc.put("04-06-2016", "Second Chance examinations for all UG and PG");
        desc.put("05-06-2016", "Second Chance examinations for all UG and PG");
        desc.put("06-06-2016", "Second Chance examinations for all UG and PG");
        desc.put("13-06-2016", "Supplementary examinations");
        desc.put("14-06-2016", "Supplementary examinations");
        desc.put("15-06-2016", "Supplementary examinations");
        desc.put("16-06-2016", "Supplementary examinations");
        desc.put("17-06-2016", "Supplementary examinations");
        desc.put("18-06-2016", "Supplementary examinations");
        desc.put("19-06-2016", "Supplementary examinations");
        desc.put("20-06-2016", "Supplementary examinations");
        desc.put("21-06-2016", "Supplementary examinations");
        desc.put("18-07-2015", "Ramzan");
        desc.put("15-08-2015", "Independence day");
        desc.put("27-08-2015", "Onam Holidays");
        desc.put("28-08-2015", "Onam Holidays");
        desc.put("29-08-2015", "Onam Holidays");
        desc.put("30-08-2015", "Onam Holidays");
        desc.put("17-09-2015", "Ganesh Chathurthi");
        desc.put("24-09-2015", "Bakrid");
        desc.put("27-09-2015", "Amma's Birthday");
        desc.put("02-10-2015", "Gandhi Jayanthi");
        desc.put("03-10-2015", "Gandhi Jayanthi");
        desc.put("04-10-2015", "Gandhi Jayanthi");
        desc.put("21-10-2015", "Mahanavami");
        desc.put("22-10-2015", "Vijayadasami");
        desc.put("23-10-2015", "Muharram");
        desc.put("09-11-2015", "Deepavali Eve");
        desc.put("10-11-2015", "Deepavali ");
        desc.put("23-12-2015", "Milad-un-Nabi");
        desc.put("25-12-2015", "Christmas");
        desc.put("01-01-2016", "New Year");
        desc.put("14-01-2016", "Pongal Holidays");
        desc.put("15-01-2016", "Pongal Holidays");
        desc.put("16-01-2016", "Pongal Holidays");
        desc.put("17-01-2016", "Pongal Holidays");
        desc.put("26-01-2016", "Republic day");
        desc.put("07-03-2016", "Mahashivaratri");
        desc.put("25-03-2016", "Good Friday");
        desc.put("08-04-2016", "Ugadi");
        desc.put("13-04-2016", "Vishu");
        desc.put("14-04-2016", "Tamil New Year");
        desc.put("01-05-2016", "May day");
        desc.put("17-02-2016", "ANOKHA 2016");
        desc.put("18-02-2016", "ANOKHA 2016");
        desc.put("19-02-2016", "ANOKHA 2016");

        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,"CALDROID_SAVED_STATE");
        }
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                if (!formatter.format(date).equals("")) {
                    String description = desc.get(formatter.format(date));
                    if(description!=null&&!description.equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this_context);
                        builder .setMessage(description)
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
