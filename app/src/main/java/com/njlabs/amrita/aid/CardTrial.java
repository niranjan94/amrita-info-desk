/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class CardTrial extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static Typeface font;

    ArrayList<Transporter> transporters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_trial);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        transporters = new ArrayList<Transporter>();


        font = Typeface.createFromAsset(getAssets(), "icons.ttf");


        transporters.add(new Transporter(
                "56323 Coimbatore Mangalore Fast Passenger",
                "All Days",
                "07:40 hours",
                "08:09 hours",
                "cbe",
                "etmd",
                "train"
        ));
        transporters.add(new Transporter(
                "66605 Coimbatore Shoranur Passenger",
                "Except Sundays",
                "09:45 hours",
                "10:14 hours",
                "cbe",
                "etmd",
                "train"
        ));
        transporters.add(new Transporter(
                "66609 Erode Palakkad MEMU",
                "Except Thursdays",
                "10:30 hours",
                "11:14 hours",
                "cbe",
                "etmd",
                "train"
        ));
        transporters.add(new Transporter(
                "56651 Coimbatore Kannur Fast Passenger",
                "All Days",
                "14:10 hours",
                "14:36 hours",
                "cbe",
                "etmd",
                "train"
        ));
        transporters.add(new Transporter(
                "56605 Coimbatore Thrissur Passenger ",
                "All Days",
                "16:40 hours",
                "17:08 hours",
                "cbe",
                "etmd",
                "train"
        ));



        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(transporters);
        mRecyclerView.setAdapter(mAdapter);
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Transporter> transporters;


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public CardView cardView;
            public ViewHolder(View cardView) {
                super(cardView);
                this.cardView = (CardView) cardView;
            }
        }

        public MyAdapter(ArrayList<Transporter> transporters) {
            this.transporters = transporters;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

            View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_timings, parent, false);
            ViewHolder vh = new ViewHolder(cardView);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ((TextView)holder.cardView.findViewById(R.id.info_text)).setText(transporters.get(position).name);
            ((TextView)holder.cardView.findViewById(R.id.icon)).setTypeface(font);
            ((TextView)holder.cardView.findViewById(R.id.icon)).setText("\\e000");
        }

        @Override
        public int getItemCount() {
            return transporters.size();
        }
    }

    class Transporter {
        private String name;
        private String days;
        private String departure;
        private String arrival;
        private String from;
        private String to;
        private String type;

        Transporter(String name, String days, String departure, String arrival, String from, String to, String type) {
            this.name = name;
            this.days = days;
            this.departure = departure;
            this.arrival = arrival;
            this.from = from;
            this.to = to;
            this.type = type;
        }
    }
}
