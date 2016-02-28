/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.client.Aums;
import com.njlabs.amrita.aid.aums.responses.AttendanceResponse;
import com.njlabs.amrita.aid.classes.CourseAttendanceData;

import java.util.List;

public class AttendanceActivity extends BaseActivity {

    ListView list;
    ProgressDialog dialog;
    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_aums_data, Color.parseColor("#e91e63"));
        String server = getIntent().getStringExtra("server");
        String semester = getIntent().getStringExtra("semester");

        list = (ListView) findViewById(R.id.list);

        Aums aums = new Aums(baseContext);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading your attendance");
        dialog.show();

        aums.setServer(server);
        aums.getAttendance(semester, new AttendanceResponse() {
            @Override
            public void onSuccess(List<CourseAttendanceData> attendanceDataList) {
                setupList(attendanceDataList);
            }

            @Override
            public void onDataUnavailable() {
                dialog.dismiss();
                Toast.makeText(baseContext, "Attendance data unavailable for the selected semester", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(baseContext, "An error occurred while connecting to the server", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSiteStructureChange() {
                dialog.dismiss();
                Toast.makeText(baseContext, "Site's structure has changed. Reported to the developer", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void setupList(List<CourseAttendanceData> attendanceData) {

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
}
