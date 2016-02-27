/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.bugs.BugReport;
import com.njlabs.amrita.aid.classes.CourseMarkData;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AumsMarks extends BaseActivity {

    ProgressDialog dialog;
    ListView list;
    AumsMarksHeaderListAdapter adapter;
    ArrayList<String> subjects = new ArrayList<>();

    @Override
    public void init(Bundle savedInstanceState) {
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

        try {
            DataParser(response);
        } catch (Exception e) {
            Ln.e(e);
            Toast.makeText(baseContext, "Marks for the semester have not been published yet.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void DataParser(String html) throws Exception{
        adapter = new AumsMarksHeaderListAdapter(this);
        Document doc = Jsoup.parse(html);

        Element table = doc.select("table[width=75%]").first();

        Elements rows = table.select("tr");

        Elements headerRowCells = rows.get(0).select("td");

        for(int i=3; i < headerRowCells.size(); i++){
            Element cell = headerRowCells.get(i);
            if(cell.text().trim().length()>0){
                subjects.add(cell.text().trim());
            }
        }

        for(int i=1; i < rows.size(); i++) {

            boolean hasMarks = false;

            Elements cells = rows.get(i).select("td");
            String exam = cells.get(0).text();

            int k = 0;
            for(int j=3; j < cells.size(); j++){
                Element cell = cells.get(j);
                String mark = cell.text();
                if(isNumeric(mark)){
                    hasMarks = true;
                }
                k++;
            }

            if(hasMarks){
                adapter.addSectionHeaderItem(new CourseMarkData(exam));

                k = 0;
                for(int j=3; j < cells.size(); j++){
                    Element cell = cells.get(j);
                    String mark = cell.text();
                    if(isNumeric(mark)){
                        adapter.addItem(new CourseMarkData(subjects.get(k), mark, exam));
                    }
                    k++;
                }
            }
        }

        setupList();
    }

    public void setupList() {

        list = (ListView) findViewById(R.id.list);
        list.setBackgroundColor(getResources().getColor(R.color.white));

        list.setAdapter(adapter);
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
    }
    public boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

}
