/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.client.Aums;
import com.njlabs.amrita.aid.aums.models.CourseGradeData;
import com.njlabs.amrita.aid.aums.responses.GradesResponse;
import com.njlabs.amrita.aid.util.ExtendedSwipeRefreshLayout;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class GradesActivity extends BaseActivity {

    private ExtendedSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Aums aums;
    private String semester;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_aums_list, Color.parseColor("#e91e63"));
        String server = getIntent().getStringExtra("server");
        semester = getIntent().getStringExtra("semester");

        swipeRefreshLayout = (ExtendedSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#e91e63"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGrades();
            }
        });

        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutParams);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        aums = new Aums(baseContext);
        aums.setServer(server);
        getGrades();
    }

    private void getGrades() {
        aums.getGrades(semester, new GradesResponse() {
            @Override
            public void onSuccess(String sgpa, List<CourseGradeData> gradeDataList) {
                swipeRefreshLayout.setRefreshing(false);
                setupList(sgpa, gradeDataList);
            }

            @Override
            public void onDataUnavailable() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(baseContext, "Grades data unavailable for the selected semester", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(baseContext, "An error occurred while connecting to the server", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSiteStructureChange() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(baseContext, "Site's structure has changed. Reported to the developer", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void setupList(String sgpa, List<CourseGradeData> gradesData) {

        if (sgpa == null || sgpa.trim().equals("null")) {
            Toast.makeText(baseContext, "Results for the semester have not been published yet.", Toast.LENGTH_LONG).show();
            finish();
        }

        CourseGradeData cgpaData = new CourseGradeData();
        cgpaData.setGrade(sgpa);
        cgpaData.setCourseCode("sgpa");
        cgpaData.setCourseTitle(null);

        gradesData.add(0, cgpaData);

        GradesAdapter adapter = new GradesAdapter(gradesData);
        recyclerView.setAdapter(new SlideInBottomAnimationAdapter(new AlphaInAnimationAdapter(adapter)));
    }

    public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.ViewHolder> {

        private List<CourseGradeData> courseGradeDataList;
        private int HEADER = 1;
        private int ITEM = 2;

        GradesAdapter(List<CourseGradeData> courseGradeDataList) {
            this.courseGradeDataList = courseGradeDataList;
        }

        @Override
        public GradesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            if (viewType == HEADER) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aums_grades_sgpa, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aums_attendance, parent, false);
            }
            return new ViewHolder(v);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER;
            }
            return ITEM;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            CourseGradeData courseGradeData = courseGradeDataList.get(position);

            if (courseGradeData.courseCode.equals("sgpa") || courseGradeData.courseTitle == null) {

                ((TextView) holder.root).setText("This semester's GPA : " + courseGradeData.grade);

            } else {

                holder.courseTitle.setText(courseGradeData.courseTitle);
                String grade = courseGradeData.grade.trim();
                if (grade.toLowerCase().contains("supply")) {
                    grade = grade.replace("(Supply)", "");
                    holder.attendanceStatus.setText(courseGradeData.courseCode + " - " + courseGradeData.type + " - Supply");
                } else {
                    holder.attendanceStatus.setText(courseGradeData.courseCode + " - " + courseGradeData.type);
                }
                switch (grade) {
                    case "A+":
                    case "A":
                    case "B+":
                    case "B":
                    case "C+":
                        holder.indicator.setBackgroundResource(R.drawable.circle_green);
                        break;
                    case "C":
                    case "D+":
                    case "D":
                        holder.indicator.setBackgroundResource(R.drawable.circle_yellow);
                        break;
                    default:
                        holder.indicator.setBackgroundResource(R.drawable.circle_red);
                        break;
                }
                holder.percentage.setText(grade);

            }
        }

        @Override
        public int getItemCount() {
            return courseGradeDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public View indicator;
            TextView courseTitle;
            TextView attendanceStatus;
            TextView percentage;
            View root;

            ViewHolder(View v) {
                super(v);
                courseTitle = ((TextView) v.findViewById(R.id.course_title));
                attendanceStatus = ((TextView) v.findViewById(R.id.attendance_status));
                percentage = ((TextView) v.findViewById(R.id.percentage));
                indicator = v.findViewById(R.id.indicator);
                root = v;
            }

        }
    }
}
