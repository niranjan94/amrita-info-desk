package com.njlabs.amrita.aid.aums;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.bugs.BugReport;
import com.njlabs.amrita.aid.classes.CourseGradeData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AumsGrades extends BaseActivity {

    ProgressDialog dialog;
    ListView list;
    ArrayList<CourseGradeData> attendanceData = new ArrayList<CourseGradeData>();
    String sgpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String response = null;
        if (extras != null) {
            response = extras.getString("response");
        }

        setupLayout(R.layout.activity_aums_data, Color.parseColor("#e91e63"));

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Parsing data...");
        dialog.show();

        DataParser(response);
    }

    public void DataParser(String html) {

        Document doc = Jsoup.parse(html);
        Element PublishedState = doc.select("input[name=htmlPageTopContainer_status]").first();
        if (PublishedState.attr("value").equals("Result Not Published.")) {
            dialog.dismiss();
        }
        else {
            Element table = doc.select("table[width=75%] > tbody").first();
            Elements rows = table.select("tr:gt(0)");

            for(Element row : rows) {
                Elements dataHolders = row.select("td > span");

                CourseGradeData adata = new CourseGradeData();

                if(dataHolders.size()>2) {
                    adata.setCourseCode(dataHolders.get(1).text());
                    adata.setCourseTitle(dataHolders.get(2).text());
                    adata.setType(dataHolders.get(4).text());
                    adata.setGrade(dataHolders.get(5).text());
                    attendanceData.add(adata);
                }
                else {
                    try {
                        sgpa = dataHolders.get(1).text();
                        if(sgpa == null || sgpa.trim().equals("null")){
                            Toast.makeText(baseContext,"Results for the semester have not been published yet.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch(Exception e) {
                        sgpa = "N/A";
                    }
                }
            }
        }
        setupList();
    }

    public void setupList() {

        list = (ListView) findViewById(R.id.list);
        list.setBackgroundColor(getResources().getColor(R.color.white));

        ArrayAdapter<CourseGradeData> dataAdapter = new ArrayAdapter<CourseGradeData>(getBaseContext(), R.layout.item_aums_attendance, attendanceData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_aums_attendance, null);
                }
                CourseGradeData data = getItem(position);
                ((TextView)convertView.findViewById(R.id.course_title)).setText(data.courseTitle);

                String grade = data.grade.trim();

                if(grade.toLowerCase().contains("supply"))
                {
                    grade = grade.replace("(Supply)","");
                    ((TextView)convertView.findViewById(R.id.attendance_status)).setText(data.courseCode+" - " + data.type + " - Supply");
                }
                else
                {
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
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.aums, menu);
        return true;

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
                SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
                String studentRollNo = preferences.getString("RollNo", "");
                Intent intent = new Intent(getApplicationContext(), BugReport.class);
                intent.putExtra("studentName","Anonymous");
                intent.putExtra("studentRollNo", studentRollNo);
                startActivity(intent);
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
