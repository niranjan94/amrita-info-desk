package com.njlabs.amrita.aid.aums;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.classes.CourseGradeData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AumsGrades extends Activity {

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

        setContentView(new LinearLayout(this));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                    sgpa=dataHolders.get(1).text();
                }
            }
        }
        setupList();
    }

    public void setupList() {

        list = new ListView(this);
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
                if(grade.equals("A+") || grade.equals("A") || grade.equals("B+") || grade.equals("B") || grade.equals("C+")) {
                    convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_green);
                }
                else if(grade.equals("C") || grade.equals("D+") || grade.equals("D")) {
                    convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_yellow);
                }
                else {
                    convertView.findViewById(R.id.indicator).setBackgroundResource(R.drawable.circle_red);
                }
                ((TextView)convertView.findViewById(R.id.percentage)).setText(grade);
                return convertView;
            }
        };

        TextView header = new TextView(this);
        header.setPadding(10,10,10,10);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        header.setText("This semester's GPA : "+sgpa);
        list.setHeaderDividersEnabled(true);
        list.addHeaderView(header);
        list.setAdapter(dataAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        setContentView(list);
        dialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
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
