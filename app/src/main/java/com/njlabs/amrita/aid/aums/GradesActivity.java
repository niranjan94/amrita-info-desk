/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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
import com.njlabs.amrita.aid.aums.responses.GradesResponse;
import com.njlabs.amrita.aid.classes.CourseGradeData;

import java.util.List;

public class GradesActivity extends BaseActivity {

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
        dialog.setMessage("Loading your grades");
        dialog.show();

        aums.setServer(server);
        aums.getGrades(semester, new GradesResponse() {
            @Override
            public void onSuccess(String sgpa, List<CourseGradeData> gradeDataList) {
                setupList(sgpa, gradeDataList);
            }

            @Override
            public void onDataUnavailable() {
                dialog.dismiss();
                Toast.makeText(baseContext, "Grades data unavailable for the selected semester", Toast.LENGTH_LONG).show();
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

    public void setupList(String sgpa, List<CourseGradeData> gradesData) {

        list = (ListView) findViewById(R.id.list);
        list.setBackgroundColor(getResources().getColor(R.color.white));

        ArrayAdapter<CourseGradeData> dataAdapter = new ArrayAdapter<CourseGradeData>(getBaseContext(), R.layout.item_aums_attendance, gradesData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_aums_attendance, null);
                }
                CourseGradeData data = getItem(position);
                ((TextView)convertView.findViewById(R.id.course_title)).setText(data.courseTitle);

                String grade = data.grade.trim();

                if(grade.toLowerCase().contains("supply")) {
                    grade = grade.replace("(Supply)","");
                    ((TextView)convertView.findViewById(R.id.attendance_status)).setText(data.courseCode+" - " + data.type + " - Supply");
                } else {
                    ((TextView)convertView.findViewById(R.id.attendance_status)).setText(data.courseCode+" - " + data.type);
                }

                switch (grade) {
                    case "A+":
                    case "A":
                    case "B+":
                    case "B":
                    case "C+":
                        convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_green);
                        break;
                    case "C":
                    case "D+":
                    case "D":
                        convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_yellow);
                        break;
                    default:
                        convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_red);
                        break;
                }
                ((TextView)convertView.findViewById(R.id.percentage)).setText(grade);
                return convertView;
            }
        };

        TextView header = new TextView(this);
        header.setPadding(10,10,10,10);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        header.setText("This semester's GPA : " + sgpa);
        if(sgpa == null || sgpa.trim().equals("null") || header.getText().toString().trim().equals("This semester's GPA : null")){
            Toast.makeText(baseContext,"Results for the semester have not been published yet.", Toast.LENGTH_LONG).show();
            finish();
        }

        list.setHeaderDividersEnabled(true);
        list.addHeaderView(header);
        list.setAdapter(dataAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        list.setVisibility(View.VISIBLE);
        dialog.dismiss();
    }
}
