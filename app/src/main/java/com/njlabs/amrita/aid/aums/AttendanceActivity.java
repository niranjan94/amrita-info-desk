/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.aums.client.Aums;
import com.njlabs.amrita.aid.aums.models.CourseAttendanceData;
import com.njlabs.amrita.aid.aums.responses.AttendanceResponse;
import com.njlabs.amrita.aid.util.ExtendedSwipeRefreshLayout;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class AttendanceActivity extends BaseActivity {

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
                getAttendance();
            }
        });
        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutParams);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        getAttendance();
    }

    private void getAttendance() {
        aums.getAttendance(semester, new AttendanceResponse() {
            @Override
            public void onSuccess(List<CourseAttendanceData> attendanceDataList) {
                swipeRefreshLayout.setRefreshing(false);
                setupList(attendanceDataList);
            }

            @Override
            public void onDataUnavailable() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(baseContext, "Attendance data unavailable for the selected semester", Toast.LENGTH_LONG).show();
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

    public void setupList(List<CourseAttendanceData> attendanceData) {
        AttendanceAdapter adapter = new AttendanceAdapter(attendanceData);
        recyclerView.setAdapter(new SlideInBottomAnimationAdapter(new AlphaInAnimationAdapter(adapter)));
    }

    public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

        private List<CourseAttendanceData> courseAttendanceDataList;

        public AttendanceAdapter(List<CourseAttendanceData> courseAttendanceDataList) {
            this.courseAttendanceDataList = courseAttendanceDataList;
        }

        @Override
        public AttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aums_attendance, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CourseAttendanceData courseAttendanceData = courseAttendanceDataList.get(position);
            holder.courseTitle.setText(courseAttendanceData.courseTitle);
            holder.attendanceStatus.setText(Html.fromHtml("You attended <b>" + courseAttendanceData.attended + "</b> of <b>" + courseAttendanceData.total + "</b> classes"));
            holder.percentage.setText(Math.round(courseAttendanceData.percentage) + "%");

            if (Math.round(courseAttendanceData.percentage) >= 85) {
                holder.indicator.setBackgroundResource(R.drawable.circle_green);
            } else if (Math.round(courseAttendanceData.percentage) >= 80) {
                holder.indicator.setBackgroundResource(R.drawable.circle_yellow);
            } else {
                holder.indicator.setBackgroundResource(R.drawable.circle_red);
            }
        }

        @Override
        public int getItemCount() {
            return courseAttendanceDataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView courseTitle;
            public TextView attendanceStatus;
            public TextView percentage;
            public View indicator;

            public ViewHolder(View v) {
                super(v);
                courseTitle = ((TextView) v.findViewById(R.id.course_title));
                attendanceStatus = ((TextView) v.findViewById(R.id.attendance_status));
                percentage = ((TextView) v.findViewById(R.id.percentage));
                indicator = v.findViewById(R.id.indicator);
            }
        }
    }

}
