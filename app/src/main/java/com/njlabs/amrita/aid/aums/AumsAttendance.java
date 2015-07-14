package com.njlabs.amrita.aid.aums;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.classes.CourseAttendanceData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AumsAttendance extends BaseActivity {

    ProgressDialog dialog;
    ListView list;
    String responseString = null;
    ArrayList<CourseAttendanceData> attendanceData = new ArrayList<CourseAttendanceData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            responseString = extras.getString("response");
        }

        setupLayout(R.layout.activity_aums_data, Color.parseColor("#e91e63"));

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Parsing the received data ");

        try {
            DataParser(responseString);
        } catch (Exception e) {
            Toast.makeText(baseContext, "There has been a change in the AUMS Site. This has been reported to the developer", Toast.LENGTH_LONG).show();
            Crashlytics.logException(e);
        }

    }

    public void DataParser(String html) {
        Document doc = Jsoup.parse(html);

            Element table = doc.select("table[width=75%] > tbody").first();
            Elements rows = table.select("tr");

            int index = 0;
            for (Element row : rows) {
                index++;
                if ((index & 1) == 0) {
                    Elements dataHolders = row.select("td > span");
                    CourseAttendanceData adata = new CourseAttendanceData();
                    adata.setCourseCode(dataHolders.get(0).text());
                    adata.setCourseTitle(dataHolders.get(1).text());
                    adata.setTotal(dataHolders.get(5).text());
                    adata.setAttended(dataHolders.get(6).text());
                    adata.setPercentage(dataHolders.get(7).text());
                    attendanceData.add(adata);
                }
            }
            setupList();

    }
    public void setupList() {
        list = (ListView) findViewById(R.id.list);
        list.setBackgroundColor(getResources().getColor(R.color.white));

        ArrayAdapter<CourseAttendanceData> dataAdapter = new ArrayAdapter<CourseAttendanceData>(getBaseContext(), R.layout.item_aums_attendance, attendanceData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_aums_attendance, null);
                }
                CourseAttendanceData data = getItem(position);
                ((TextView)convertView.findViewById(R.id.course_title)).setText(data.courseTitle);
                ((TextView)convertView.findViewById(R.id.attendance_status)).setText(Html.fromHtml("You attended <b>"+data.attended+"</b> of <b>"+data.total+"</b> classes"));
                ((TextView)convertView.findViewById(R.id.percentage)).setText(Math.round(data.percentage)+"%");

                if(Math.round(data.percentage)>=85) {
                    convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_green);
                }
                else if(Math.round(data.percentage)>=80) {
                    convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_yellow);
                }
                else {
                    convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_red);
                }

                return convertView;
            }
        };
        list.setAdapter(dataAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        list.setVisibility(View.VISIBLE);
        dialog.dismiss();
    }


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
                finish();
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                return true;
            case R.id.action_bug_report:
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                Exception e = new Exception("AUMS Grades Error Reported - "+currentDateandTime);
                Crashlytics.logException(e);
                Toast.makeText(getBaseContext(),"The error has been reported.",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }
}
