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
import com.njlabs.amrita.aid.aums.models.CourseMarkData;
import com.njlabs.amrita.aid.aums.responses.MarksResponse;
import com.njlabs.amrita.aid.util.ExtendedSwipeRefreshLayout;
import com.onemarker.ln.logger.Ln;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class MarksActivity extends BaseActivity {

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
                getMarks();
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
        getMarks();
    }


    public void getMarks() {
        aums.getMarks(semester, new MarksResponse() {
            @Override
            public void onSuccess(List<CourseMarkData> markDataList) {
                swipeRefreshLayout.setRefreshing(false);
                setupList(markDataList);
            }

            @Override
            public void onDataUnavailable() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(baseContext, "Marks data unavailable for the selected semester", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Ln.e(throwable);
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

    public void setupList(List<CourseMarkData> markDataList) {
        MarksAdapter adapter = new MarksAdapter(markDataList);
        recyclerView.setAdapter(new SlideInBottomAnimationAdapter(new AlphaInAnimationAdapter(adapter)));
    }


    public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.ViewHolder> {

        private List<CourseMarkData> courseMarkDataList;
        private int HEADER = 1;
        private int ITEM = 2;

        MarksAdapter(List<CourseMarkData> courseMarkDataList) {
            this.courseMarkDataList = courseMarkDataList;
        }

        public void setCourseMarkDataList(List<CourseMarkData> courseMarkDataList) {
            this.courseMarkDataList = courseMarkDataList;
        }

        @Override
        public int getItemViewType(int position) {
            if (courseMarkDataList.get(position).mark == null) {
                return HEADER;
            }
            return ITEM;
        }

        @Override
        public MarksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == HEADER) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aums_marks_section, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aums_generic, parent, false);
            }

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CourseMarkData courseMarkData = courseMarkDataList.get(position);

            if (courseMarkData.mark == null) {
                holder.sectionHeader.setText(courseMarkData.exam);
            } else {
                holder.name.setText(courseMarkData.courseCode);
                holder.value.setText(courseMarkData.mark);

                double marks = Double.parseDouble(courseMarkData.mark);
                if (marks >= 40) {
                    holder.indicator.setBackgroundResource(R.drawable.circle_green);
                } else if (marks >= 25) {
                    holder.indicator.setBackgroundResource(R.drawable.circle_yellow);
                } else {
                    holder.indicator.setBackgroundResource(R.drawable.circle_red);
                }
            }

        }

        @Override
        public int getItemCount() {
            return courseMarkDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public TextView value;
            public View indicator;

            TextView sectionHeader;

            ViewHolder(View v) {
                super(v);
                try {
                    name = (TextView) v.findViewById(R.id.name);
                    value = (TextView) v.findViewById(R.id.value);
                    indicator = (View) v.findViewById(R.id.indicator);
                } catch (Exception ignored) {
                }

                try {
                    sectionHeader = (TextView) v.findViewById(R.id.section_header);
                } catch (Exception ignored) {
                }
            }

        }
    }
}
