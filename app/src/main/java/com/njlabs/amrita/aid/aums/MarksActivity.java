/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.client.Aums;
import com.njlabs.amrita.aid.aums.responses.MarksResponse;
import com.njlabs.amrita.aid.classes.CourseMarkData;

import java.util.List;

public class MarksActivity extends BaseActivity {

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
        dialog.setMessage("Loading your marks");
        dialog.show();

        aums.setServer(server);
        aums.getMarks(semester, new MarksResponse() {
            @Override
            public void onSuccess(List<CourseMarkData> markDataList) {
                setupList(markDataList);
            }

            @Override
            public void onDataUnavailable() {
                dialog.dismiss();
                Toast.makeText(baseContext, "Marks data unavailable for the selected semester", Toast.LENGTH_LONG).show();
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

    public void setupList(List<CourseMarkData> markDataList) {

        AumsMarksHeaderListAdapter adapter = new AumsMarksHeaderListAdapter(this);

        for (CourseMarkData markData : markDataList) {
            if(markData.mark == null) {
                adapter.addSectionHeaderItem(markData);
            } else {
                adapter.addItem(markData);
            }
        }

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
}
