package com.njlabs.amrita.aid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Niranjan on 06-09-2014.
 */
@SuppressWarnings("deprecation")
public class LandingAdapter extends BaseAdapter {

    public static List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater;
    Context context;

    public LandingAdapter(Context context, int Group) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        items.clear();
        items.add(new Item("#03a9f4", "About Amrita", Iconify.IconValue.fa_info_circle));
        items.add(new Item("#3f51b5", "Amrita Explorer", Iconify.IconValue.fa_globe));
        items.add(new Item("#e51c23", "Academic Calender", Iconify.IconValue.fa_calendar));
        items.add(new Item("#e91e63", "Amrita UMS Login", Iconify.IconValue.fa_lock));
        items.add(new Item("#9c27b0", "Train & Bus Timings", Iconify.IconValue.fa_clock_o));
        items.add(new Item("#009688", "Attendance Manager", Iconify.IconValue.fa_smile_o));
        items.add(new Item("#259b24", "Curriculum Info", Iconify.IconValue.fa_book));
        items.add(new Item("#ffc107", "Photo Gallery", Iconify.IconValue.fa_photo));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;
        FrameLayout holder;

        if (v == null) {
            v = inflater.inflate(R.layout.item_landing_grid, viewGroup, false);
            v.setTag(R.id.landing_item_holder, v.findViewById(R.id.landing_item_holder));
            v.setTag(R.id.landing_picture, v.findViewById(R.id.landing_picture));
            v.setTag(R.id.landing_text, v.findViewById(R.id.landing_text));
        }
        holder = (FrameLayout) v.getTag(R.id.landing_item_holder);
        picture = (ImageView) v.getTag(R.id.landing_picture);
        name = (TextView) v.getTag(R.id.landing_text);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


        // GOOGLE MATERIAL DESIGN COLOR PALETTE
        int colors[] = {
                Color.parseColor("#e51c23"),
                Color.parseColor("#e91e63"),
                Color.parseColor("#9c27b0"),
                Color.parseColor("#673ab7"),
                Color.parseColor("#3f51b5"),
                Color.parseColor("#5677fc"),
                Color.parseColor("#03a9f4"),
                Color.parseColor("#00bcd4"),
                Color.parseColor("#009688"),
                Color.parseColor("#259b24"),
                Color.parseColor("#8bc34a"),
                Color.parseColor("#cddc39"),
                Color.parseColor("#ffeb3b"),
                Color.parseColor("#ffc107"),
                Color.parseColor("#ff9800"),
                Color.parseColor("#ff5722"),
                Color.parseColor("#795548"),
                Color.parseColor("#9e9e9e"),
                Color.parseColor("#607d8b")
        };

        color = colors[new Random().nextInt(colors.length)];

        holder.setBackgroundColor(color);

        Item item = (Item) getItem(i);
        picture.setImageDrawable(new IconDrawable(context, item.icon)
                .colorRes(R.color.white));
        name.setText(item.name);
        holder.setBackgroundColor(Color.parseColor(item.color));
        return v;
    }

    public static class Item {

        final String color;
        final String name;
        final Iconify.IconValue icon;

        Item(String color, String name, Iconify.IconValue icon) {
            this.color = color;
            this.name = name;
            this.icon = icon;
        }
    }
}
