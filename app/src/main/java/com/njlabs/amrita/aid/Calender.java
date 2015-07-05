package com.njlabs.amrita.aid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressLint("SimpleDateFormat")
public class Calender extends AppCompatActivity {

    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    HashMap<String, String> desc;


    public Date ParseDate(String date_str) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dateStr = null;
        try {
            dateStr = formatter.parse(date_str);
        } catch (ParseException e) {
            // TODO ACRA.getErrorReporter().handleException(e);
        }
        return dateStr;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setCustomResourceForDates() {
        // Create a hash map
        HashMap hm = new HashMap();
        // HOLIDAYS
        hm.put(ParseDate("15-08-2014"), R.color.caldroid_green);
        hm.put(ParseDate("16-08-2014"), R.color.caldroid_green);
        hm.put(ParseDate("17-08-2014"), R.color.caldroid_green);
        hm.put(ParseDate("29-08-2014"), R.color.caldroid_green);
        hm.put(ParseDate("30-08-2014"), R.color.caldroid_green);
        hm.put(ParseDate("31-08-2014"), R.color.caldroid_green);
        hm.put(ParseDate("05-09-2014"), R.color.caldroid_green);
        hm.put(ParseDate("06-09-2014"), R.color.caldroid_green);
        hm.put(ParseDate("07-09-2014"), R.color.caldroid_green);
        hm.put(ParseDate("27-09-2014"), R.color.caldroid_green);
        hm.put(ParseDate("02-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("03-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("04-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("05-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("18-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("19-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("20-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("21-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("22-10-2014"), R.color.caldroid_green);
        hm.put(ParseDate("03-12-2014"), R.color.caldroid_green);
        hm.put(ParseDate("10-12-2014"), R.color.caldroid_green);
        hm.put(ParseDate("25-12-2014"), R.color.caldroid_green);
        hm.put(ParseDate("01-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("14-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("15-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("16-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("17-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("18-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("26-01-2015"), R.color.caldroid_green);
        hm.put(ParseDate("15-02-2015"), R.color.caldroid_green);
        hm.put(ParseDate("21-03-2015"), R.color.caldroid_green);
        hm.put(ParseDate("03-04-2015"), R.color.caldroid_green);
        hm.put(ParseDate("05-04-2015"), R.color.caldroid_green);
        hm.put(ParseDate("13-04-2015"), R.color.caldroid_green);
        hm.put(ParseDate("14-04-2015"), R.color.caldroid_green);
        hm.put(ParseDate("15-04-2015"), R.color.caldroid_green);
        hm.put(ParseDate("01-05-2015"), R.color.caldroid_green);
        hm.put(ParseDate("21-05-2015"), R.color.caldroid_green);


        // EVENTS
        hm.put(ParseDate("12-07-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("23-07-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("30-07-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("06-08-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("21-11-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("29-11-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("29-12-2014"), R.color.caldroid_blue);
        hm.put(ParseDate("06-03-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("07-03-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("30-04-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("11-07-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("20-07-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("21-07-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("22-07-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("23-07-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("24-07-2015"), R.color.caldroid_blue);
        hm.put(ParseDate("25-07-2015"), R.color.caldroid_blue);


        // EXAMS
        hm.put(ParseDate("01-09-2014"), R.color.caldroid_red);
        hm.put(ParseDate("02-09-2014"), R.color.caldroid_red);
        hm.put(ParseDate("03-09-2014"), R.color.caldroid_red);
        hm.put(ParseDate("08-09-2014"), R.color.caldroid_red);
        hm.put(ParseDate("09-09-2014"), R.color.caldroid_red);
        hm.put(ParseDate("10-09-2014"), R.color.caldroid_red);
        hm.put(ParseDate("13-10-2014"), R.color.caldroid_red);
        hm.put(ParseDate("14-10-2014"), R.color.caldroid_red);
        hm.put(ParseDate("15-10-2014"), R.color.caldroid_red);
        hm.put(ParseDate("27-10-2014"), R.color.caldroid_red);
        hm.put(ParseDate("28-10-2014"), R.color.caldroid_red);
        hm.put(ParseDate("29-10-2014"), R.color.caldroid_red);
        hm.put(ParseDate("10-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("11-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("12-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("17-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("18-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("19-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("24-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("25-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("26-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("27-11-2014"), R.color.caldroid_red);
        hm.put(ParseDate("01-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("02-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("03-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("04-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("05-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("06-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("08-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("09-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("15-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("16-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("17-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("18-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("19-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("22-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("23-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("24-12-2014"), R.color.caldroid_red);
        hm.put(ParseDate("09-02-2014"), R.color.caldroid_red);
        hm.put(ParseDate("10-02-2014"), R.color.caldroid_red);
        hm.put(ParseDate("11-02-2014"), R.color.caldroid_red);
        hm.put(ParseDate("12-02-2014"), R.color.caldroid_red);
        hm.put(ParseDate("23-03-2014"), R.color.caldroid_red);
        hm.put(ParseDate("24-03-2014"), R.color.caldroid_red);
        hm.put(ParseDate("25-03-2014"), R.color.caldroid_red);
        hm.put(ParseDate("20-04-2014"), R.color.caldroid_red);
        hm.put(ParseDate("21-04-2014"), R.color.caldroid_red);
        hm.put(ParseDate("22-04-2014"), R.color.caldroid_red);
        hm.put(ParseDate("04-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("05-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("06-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("07-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("08-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("11-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("12-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("13-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("14-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("15-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("16-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("18-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("19-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("20-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("22-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("23-05-2014"), R.color.caldroid_red);
        hm.put(ParseDate("01-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("02-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("03-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("04-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("05-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("08-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("09-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("10-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("11-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("12-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("15-06-2015"), R.color.caldroid_red);
        hm.put(ParseDate("16-06-2015"), R.color.caldroid_red);


        // TEXT HASH MAP
        HashMap thm = new HashMap();
        thm.put(ParseDate("15-08-2014"), R.color.white);
        thm.put(ParseDate("16-08-2014"), R.color.white);
        thm.put(ParseDate("17-08-2014"), R.color.white);
        thm.put(ParseDate("29-08-2014"), R.color.white);
        thm.put(ParseDate("30-08-2014"), R.color.white);
        thm.put(ParseDate("31-08-2014"), R.color.white);
        thm.put(ParseDate("05-09-2014"), R.color.white);
        thm.put(ParseDate("06-09-2014"), R.color.white);
        thm.put(ParseDate("07-09-2014"), R.color.white);
        thm.put(ParseDate("27-09-2014"), R.color.white);
        thm.put(ParseDate("02-10-2014"), R.color.white);
        thm.put(ParseDate("03-10-2014"), R.color.white);
        thm.put(ParseDate("04-10-2014"), R.color.white);
        thm.put(ParseDate("05-10-2014"), R.color.white);
        thm.put(ParseDate("18-10-2014"), R.color.white);
        thm.put(ParseDate("19-10-2014"), R.color.white);
        thm.put(ParseDate("20-10-2014"), R.color.white);
        thm.put(ParseDate("21-10-2014"), R.color.white);
        thm.put(ParseDate("22-10-2014"), R.color.white);
        thm.put(ParseDate("03-12-2014"), R.color.white);
        thm.put(ParseDate("10-12-2014"), R.color.white);
        thm.put(ParseDate("25-12-2014"), R.color.white);
        thm.put(ParseDate("01-01-2015"), R.color.white);
        thm.put(ParseDate("14-01-2015"), R.color.white);
        thm.put(ParseDate("15-01-2015"), R.color.white);
        thm.put(ParseDate("16-01-2015"), R.color.white);
        thm.put(ParseDate("17-01-2015"), R.color.white);
        thm.put(ParseDate("18-01-2015"), R.color.white);
        thm.put(ParseDate("26-01-2015"), R.color.white);
        thm.put(ParseDate("15-02-2015"), R.color.white);
        thm.put(ParseDate("21-03-2015"), R.color.white);
        thm.put(ParseDate("03-04-2015"), R.color.white);
        thm.put(ParseDate("05-04-2015"), R.color.white);
        thm.put(ParseDate("13-04-2015"), R.color.white);
        thm.put(ParseDate("14-04-2015"), R.color.white);
        thm.put(ParseDate("15-04-2015"), R.color.white);
        thm.put(ParseDate("01-05-2015"), R.color.white);
        thm.put(ParseDate("21-05-2015"), R.color.white);
        thm.put(ParseDate("12-07-2014"), R.color.white);
        thm.put(ParseDate("23-07-2014"), R.color.white);
        thm.put(ParseDate("30-07-2014"), R.color.white);
        thm.put(ParseDate("06-08-2014"), R.color.white);
        thm.put(ParseDate("21-11-2014"), R.color.white);
        thm.put(ParseDate("29-11-2014"), R.color.white);
        thm.put(ParseDate("29-12-2014"), R.color.white);
        thm.put(ParseDate("06-03-2015"), R.color.white);
        thm.put(ParseDate("07-03-2015"), R.color.white);
        thm.put(ParseDate("30-04-2015"), R.color.white);
        thm.put(ParseDate("11-07-2015"), R.color.white);
        thm.put(ParseDate("20-07-2015"), R.color.white);
        thm.put(ParseDate("21-07-2015"), R.color.white);
        thm.put(ParseDate("22-07-2015"), R.color.white);
        thm.put(ParseDate("23-07-2015"), R.color.white);
        thm.put(ParseDate("24-07-2015"), R.color.white);
        thm.put(ParseDate("25-07-2015"), R.color.white);
        thm.put(ParseDate("01-09-2014"), R.color.white);
        thm.put(ParseDate("02-09-2014"), R.color.white);
        thm.put(ParseDate("03-09-2014"), R.color.white);
        thm.put(ParseDate("08-09-2014"), R.color.white);
        thm.put(ParseDate("09-09-2014"), R.color.white);
        thm.put(ParseDate("10-09-2014"), R.color.white);
        thm.put(ParseDate("13-10-2014"), R.color.white);
        thm.put(ParseDate("14-10-2014"), R.color.white);
        thm.put(ParseDate("15-10-2014"), R.color.white);
        thm.put(ParseDate("27-10-2014"), R.color.white);
        thm.put(ParseDate("28-10-2014"), R.color.white);
        thm.put(ParseDate("29-10-2014"), R.color.white);
        thm.put(ParseDate("10-11-2014"), R.color.white);
        thm.put(ParseDate("11-11-2014"), R.color.white);
        thm.put(ParseDate("12-11-2014"), R.color.white);
        thm.put(ParseDate("17-11-2014"), R.color.white);
        thm.put(ParseDate("18-11-2014"), R.color.white);
        thm.put(ParseDate("19-11-2014"), R.color.white);
        thm.put(ParseDate("24-11-2014"), R.color.white);
        thm.put(ParseDate("25-11-2014"), R.color.white);
        thm.put(ParseDate("26-11-2014"), R.color.white);
        thm.put(ParseDate("27-11-2014"), R.color.white);
        thm.put(ParseDate("01-12-2014"), R.color.white);
        thm.put(ParseDate("02-12-2014"), R.color.white);
        thm.put(ParseDate("03-12-2014"), R.color.white);
        thm.put(ParseDate("04-12-2014"), R.color.white);
        thm.put(ParseDate("05-12-2014"), R.color.white);
        thm.put(ParseDate("06-12-2014"), R.color.white);
        thm.put(ParseDate("08-12-2014"), R.color.white);
        thm.put(ParseDate("09-12-2014"), R.color.white);
        thm.put(ParseDate("15-12-2014"), R.color.white);
        thm.put(ParseDate("16-12-2014"), R.color.white);
        thm.put(ParseDate("17-12-2014"), R.color.white);
        thm.put(ParseDate("18-12-2014"), R.color.white);
        thm.put(ParseDate("19-12-2014"), R.color.white);
        thm.put(ParseDate("22-12-2014"), R.color.white);
        thm.put(ParseDate("23-12-2014"), R.color.white);
        thm.put(ParseDate("24-12-2014"), R.color.white);
        thm.put(ParseDate("09-02-2014"), R.color.white);
        thm.put(ParseDate("10-02-2014"), R.color.white);
        thm.put(ParseDate("11-02-2014"), R.color.white);
        thm.put(ParseDate("12-02-2014"), R.color.white);
        thm.put(ParseDate("23-03-2014"), R.color.white);
        thm.put(ParseDate("24-03-2014"), R.color.white);
        thm.put(ParseDate("25-03-2014"), R.color.white);
        thm.put(ParseDate("20-04-2014"), R.color.white);
        thm.put(ParseDate("21-04-2014"), R.color.white);
        thm.put(ParseDate("22-04-2014"), R.color.white);
        thm.put(ParseDate("04-05-2014"), R.color.white);
        thm.put(ParseDate("05-05-2014"), R.color.white);
        thm.put(ParseDate("06-05-2014"), R.color.white);
        thm.put(ParseDate("07-05-2014"), R.color.white);
        thm.put(ParseDate("08-05-2014"), R.color.white);
        thm.put(ParseDate("11-05-2014"), R.color.white);
        thm.put(ParseDate("12-05-2014"), R.color.white);
        thm.put(ParseDate("13-05-2014"), R.color.white);
        thm.put(ParseDate("14-05-2014"), R.color.white);
        thm.put(ParseDate("15-05-2014"), R.color.white);
        thm.put(ParseDate("16-05-2014"), R.color.white);
        thm.put(ParseDate("18-05-2014"), R.color.white);
        thm.put(ParseDate("19-05-2014"), R.color.white);
        thm.put(ParseDate("20-05-2014"), R.color.white);
        thm.put(ParseDate("22-05-2014"), R.color.white);
        thm.put(ParseDate("23-05-2014"), R.color.white);
        thm.put(ParseDate("01-06-2015"), R.color.white);
        thm.put(ParseDate("02-06-2015"), R.color.white);
        thm.put(ParseDate("03-06-2015"), R.color.white);
        thm.put(ParseDate("04-06-2015"), R.color.white);
        thm.put(ParseDate("05-06-2015"), R.color.white);
        thm.put(ParseDate("08-06-2015"), R.color.white);
        thm.put(ParseDate("09-06-2015"), R.color.white);
        thm.put(ParseDate("10-06-2015"), R.color.white);
        thm.put(ParseDate("11-06-2015"), R.color.white);
        thm.put(ParseDate("12-06-2015"), R.color.white);
        thm.put(ParseDate("15-06-2015"), R.color.white);
        thm.put(ParseDate("16-06-2015"), R.color.white);


        if (caldroidFragment != null) {
            //caldroidFragment.setBackgroundResourceForDate(R.color.blue, ParseDate("20/07/2013"));
            caldroidFragment.setBackgroundResourceForDates(hm);
            caldroidFragment.setTextColorForDates(thm);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final Calender this_context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e51c23"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Academic Calender");
        SharedPreferences preferences = getSharedPreferences("app_extra", Context.MODE_PRIVATE);
        Boolean AgreeStatus = preferences.getBoolean("calender_agree", false);
        if (AgreeStatus == true) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this_context);
            builder.setTitle("Academic Calender").setIcon(R.drawable.info)
                    .setMessage("Red denotes that it's an Exam day. Blue denotes an event (not a Holiday). Whereas Green denotes a Holiday. I'm not resposibile for the accuracy of the dates")
                    .setCancelable(false)
                    .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences preferences = getSharedPreferences("app_extra", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("calender_agree", true);
                            editor.commit();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        caldroidFragment = new CaldroidFragment();
        // DESCRIPTION FOR EACH DATE
        desc = new HashMap<String, String>();
        desc.put("15-08-2014", "Independence Day");
        desc.put("16-08-2014", "Holiday");
        desc.put("17-08-2014", "Sreekrishna Jayanthi Celebrations");
        desc.put("29-08-2014", "Ganesh Chathurthi");
        desc.put("30-08-2014", "Holiday");
        desc.put("31-08-2014", "Holiday");
        desc.put("05-09-2014", "Onam");
        desc.put("06-09-2014", "First Onam");
        desc.put("07-09-2014", "Thiruvonam");
        desc.put("27-09-2014", "Amma's Birthday");
        desc.put("02-10-2014", "Gandhi Jayanthi / Ayudhapooja");
        desc.put("03-10-2014", "Vijayadasami");
        desc.put("04-10-2014", "Pooja Holidays");
        desc.put("05-10-2014", "Bakrid");
        desc.put("18-10-2014", "Deepavali Holidays");
        desc.put("19-10-2014", "Deepavali Holidays");
        desc.put("20-10-2014", "Deepavali Holidays");
        desc.put("21-10-2014", "Deepavali Eve");
        desc.put("22-10-2014", "Deepavali");
        desc.put("03-12-2014", "Winter Vacation for III, V & VII Sem. Of all UG & PG Programmes Starts");
        desc.put("10-12-2014", "Winter Vacation for I Sem. Of all UG & PG Programmes Starts");
        desc.put("25-12-2014", "Christmas");
        desc.put("01-01-2015", "New Year 2015");
        desc.put("14-01-2015", "Pongal");
        desc.put("15-01-2015", "Mattu Pongal / Thiruvalluvar Day");
        desc.put("16-01-2015", "Uzhavar Thirunal");
        desc.put("17-01-2015", "Pongal Holidays");
        desc.put("18-01-2015", "Pongal Holidays");
        desc.put("26-01-2015", "Republic Day");
        desc.put("15-02-2015", "Alumni Day");
        desc.put("21-03-2015", "Ugadi");
        desc.put("03-04-2015", "Good Friday");
        desc.put("05-04-2015", "Easter");
        desc.put("13-04-2015", "Tamil New Year Holiday");
        desc.put("14-04-2015", "Dr. B.R. Ambedkar Jayanthi / Tamil New Year Day");
        desc.put("15-04-2015", "Vishu");
        desc.put("01-05-2015", "May Day");
        desc.put("21-05-2015", "Summer Vacation Starts");
        desc.put("12-07-2014", "Gurupoornima / Vyasa Poornima");
        desc.put("23-07-2014", "Enrolment & Commencement of Classes for all III, V, VII Sem. Of all UG Programmes and III & V Sem of all PG programmes");
        desc.put("30-07-2014", "Registration, Enrolment & Commencement of classes for I sem. Of all UG & Integrated PG programmes");
        desc.put("06-08-2014", "Registration, Enrolment & Commencement of classes for I sem. Of all PG programmes");
        desc.put("21-11-2014", "Last Instruction day for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("29-11-2014", "Last Instruction day for I Sem. Of all UG & PG Programmes");
        desc.put("29-12-2014", "Enrolment & Commencement of Classes for all UG & PG Programmes");
        desc.put("06-03-2015", "Amritotsavam");
        desc.put("07-03-2015", "Institution Day");
        desc.put("30-04-2015", "Last instruction day for all UG & PG Programmes");
        desc.put("11-07-2015", "M.Tech Submission of Thesis");
        desc.put("20-07-2015", "M.Tech Viva-Voce");
        desc.put("21-07-2015", "M.Tech Viva-Voce");
        desc.put("22-07-2015", "M.Tech Viva-Voce");
        desc.put("23-07-2015", "M.Tech Viva-Voce");
        desc.put("24-07-2015", "M.Tech Viva-Voce");
        desc.put("25-07-2015", "M.Tech Viva-Voce");
        desc.put("01-09-2014", "First Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("02-09-2014", "First Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("03-09-2014", "First Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("08-09-2014", "First Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("09-09-2014", "First Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("10-09-2014", "First Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("13-10-2014", "Second Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("14-10-2014", "Second Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("15-10-2014", "Second Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("27-10-2014", "Second Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("28-10-2014", "Second Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("29-10-2014", "Second Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("10-11-2014", "Third Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("11-11-2014", "Third Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("12-11-2014", "Third Assesment for III, V & VII Sem. Of all UG & PG Programmes");
        desc.put("17-11-2014", "Third Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("18-11-2014", "Third Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("19-11-2014", "Third Assesment for I Sem. Of all UG & PG Programmes");
        desc.put("24-11-2014", "End Semester Examinations for  for III, V & VII Sem. Of all UG & PG Programmes (24.11.2014 to 02.12.2014)");
        desc.put("25-11-2014", "End Semester Examinations for  for III, V & VII Sem. Of all UG & PG Programmes (24.11.2014 to 02.12.2014)");
        desc.put("26-11-2014", "End Semester Examinations for  for III, V & VII Sem. Of all UG & PG Programmes (24.11.2014 to 02.12.2014)");
        desc.put("27-11-2014", "End Semester Examinations for  for III, V & VII Sem. Of all UG & PG Programmes (24.11.2014 to 02.12.2014)");
        desc.put("01-12-2014", "End Semester Examinations for  for III, V & VII Sem. Of all UG & PG Programmes (24.11.2014 to 02.12.2014) and End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("02-12-2014", "End Semester Examinations for  for III, V & VII Sem. Of all UG & PG Programmes (24.11.2014 to 02.12.2014) and End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("03-12-2014", "End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("04-12-2014", "End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("05-12-2014", "End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("06-12-2014", "End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("08-12-2014", "End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("09-12-2014", "End Semester Examinations for I Sem. Of all UG & PG Programmes (01.12.2014 to 09.12.2014)");
        desc.put("15-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("16-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("17-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("18-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("19-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("22-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("23-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("24-12-2014", "Second Chance Examination for all UG & PG Programmes (15.12.2014 to 24.12.2014)");
        desc.put("09-02-2014", "First Assessment for all UG & PG Programmes");
        desc.put("10-02-2014", "First Assessment for all UG & PG Programmes");
        desc.put("11-02-2014", "First Assessment for all UG & PG Programmes");
        desc.put("12-02-2014", "First Assessment for all UG & PG Programmes");
        desc.put("23-03-2014", "Second Assessment for all UG & PG Programmes");
        desc.put("24-03-2014", "Second Assessment for all UG & PG Programmes");
        desc.put("25-03-2014", "Second Assessment for all UG & PG Programmes");
        desc.put("20-04-2014", "Third Assessment for all UG & PG Programmes");
        desc.put("21-04-2014", "Third Assessment for all UG & PG Programmes");
        desc.put("22-04-2014", "Third Assessment for all UG & PG Programmes");
        desc.put("04-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("05-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("06-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("07-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("08-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("11-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("12-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("13-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("14-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("15-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("16-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("18-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("19-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("20-05-2014", "End Semester Examination for all UG & PG Programmes (04.05.2015 to 20.05.2015)");
        desc.put("22-05-2014", "Supplementary Examinations (20.05.2015 to 23.05.2015)");
        desc.put("23-05-2014", "Supplementary Examinations (20.05.2015 to 23.05.2015)");
        desc.put("01-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("02-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("03-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("04-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("05-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("08-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("09-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("10-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("11-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("12-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("15-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");
        desc.put("16-06-2015", "Second Chance Examination for all UG & PG Programmes (01.06.2015 to 16.06.2015)");


        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);

            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //Toast.makeText(getApplicationContext(), formatter.format(date),Toast.LENGTH_SHORT).show();
                if (formatter.format(date) == null || formatter.format(date) == "" || formatter.format(date).equals("")) {
                    // DO NOTHING
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this_context);
                    builder.setTitle(formatter.format(date)).setIcon(R.drawable.info)
                            .setMessage(desc.get(formatter.format(date)))
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

            @Override
            public void onChangeMonth(int month, int year) {
                /*String text = "month: " + month + " year: " + year;
				Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT).show();*/
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish(); //go back to the previous Activity
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
