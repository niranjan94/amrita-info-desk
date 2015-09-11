/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.classes.CourseMarkData;

import java.util.ArrayList;
import java.util.TreeSet;

public class AumsMarksHeaderListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<CourseMarkData> mData = new ArrayList<CourseMarkData>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public AumsMarksHeaderListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final CourseMarkData item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final CourseMarkData item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CourseMarkData getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.item_aums_generic, null);
                    holder.subjectCode = (TextView) convertView.findViewById(R.id.name);
                    holder.marks = (TextView) convertView.findViewById(R.id.value);
                    holder.indicator = (View) convertView.findViewById(R.id.indicator);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.item_aums_marks_section, null);
                    holder.sectionHeader = (TextView) convertView.findViewById(R.id.section_header);
                    break;
            }
            if (convertView != null) {
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CourseMarkData data = mData.get(position);
        switch (rowType) {
            case TYPE_ITEM:
                holder.subjectCode.setText(data.courseCode);
                holder.marks.setText(data.mark);
                double marks = Double.parseDouble(data.mark);
                if(marks >= 40){
                    holder.indicator.setBackgroundResource(R.drawable.circle_green);
                } else if (marks >= 25){
                    holder.indicator.setBackgroundResource(R.drawable.circle_yellow);
                } else {
                    holder.indicator.setBackgroundResource(R.drawable.circle_red);
                }
                break;
            case TYPE_SEPARATOR:
                holder.sectionHeader.setText(data.exam);
                break;
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView sectionHeader;
        public TextView subjectCode;
        public TextView marks;
        public View indicator;
    }

}