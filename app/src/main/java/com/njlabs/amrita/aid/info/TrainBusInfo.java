package com.njlabs.amrita.aid.info;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;

import java.util.ArrayList;


public class TrainBusInfo extends BaseActivity {

    private ListView listView = null;
    private ArrayList<Transporter> transporters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("type");

        setupLayout(R.layout.activity_train_bus_info,Color.parseColor("#9c27b0"));
        getSupportActionBar().setSubtitle(type);

        listView = (ListView) findViewById(R.id.list);
        transporters = new ArrayList<Transporter>();

        //
        // TODO TRAINS FROM CBE TO ETMD
        //
        if (type.equals("Trains from Coimbatore")) {
            transporters.add(new Transporter(
                    "56323 Coimbatore Mangalore Fast Passenger",
                    "All Days",
                    "07:20 hours",
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
            transporters.add(new Transporter(
                    "66607 Coimbatore Palakkad Town Passenger",
                    "Except Sundays",
                    "18:10 hours",
                    "18:38 hours",
                    "cbe",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56847 Trichy - Palakkad Town Fast Passenger ",
                    "All Days",
                    "18:25 hours",
                    "19:26 hours",
                    "cbe",
                    "etmd",
                    "train"
            ));
        }
        ///
        /// TODO TRAINS FROM PGT TO ETMD
        ///
        if (type.equals("Trains from Palghat")) {
            transporters.add(new Transporter(
                    "56848 Palghat Town Tiruchchirapalli Fast Passenger",
                    "All Days",
                    "06:45 hours",
                    "07:29 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66606 Palghat Town Coimbatore Passenger",
                    "Except Sundays",
                    "07:33 hours",
                    "08:18 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56604 Shoranur Coimbatore Passenger",
                    "All Days",
                    "09:10 hours",
                    "09:55 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56650 Kannur Coimbatore Fast Passenger",
                    "All Days",
                    "11:35 hours",
                    "12:24 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66608 Palakkad Erode MEMU",
                    "Except Thursdays",
                    "14:45 hours",
                    "15:31 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66604 Shoranur Coimbatore Passenger",
                    "Except Sundays",
                    "15:55 hours",
                    "16:40 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56324 Mangalore - Coimbatore Fast Passenger",
                    "All Days",
                    "18:00 hours",
                    "18:51 hours",
                    "pgt",
                    "etmd",
                    "train"
            ));
        }
        //
        // TODO TRAINS FROM ETMD TO CBE
        //
        if (type.equals("Trains to Coimbatore")) {
            transporters.add(new Transporter(
                    "56848 Palghat Town Tiruchchirapalli Fast Passenger",
                    "All Days",
                    "07:30 hours",
                    "08:05 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66606 Palghat Town Coimbatore Passenger",
                    "Except Sundays",
                    "08:19 hours",
                    "09:00 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56604 Shoranur Coimbatore Passenger",
                    "All Days",
                    "09:56 hours",
                    "10:45 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56650 Kannur Coimbatore Fast Passenger",
                    "All Days",
                    "12:25 hours",
                    "13:25 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66608 Palakkad Erode MEMU",
                    "Except Thursdays",
                    "15:32 hours",
                    "16:05 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66604 Shoranur Coimbatore Passenger",
                    "Except Sundays",
                    "16:41 hours",
                    "17:30 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56324 Mangalore - Coimbatore Fast Passenger",
                    "All Days",
                    "18:52 hours",
                    "19:35 hours",
                    "etmd",
                    "cbe",
                    "train"
            ));
        }
        //
        // TODO TRAINS FROM ETMD TO PGT
        //
        if (type.equals("Trains to Palghat")) {
            transporters.add(new Transporter(
                    "56323 Coimbatore Mangalore Fast Passenger",
                    "All Days",
                    "08:10 hours",
                    "09:00  hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66605 Coimbatore Shoranur Passenger",
                    "Except Sundays",
                    "10:15 hours",
                    "11:00 hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66609 Erode Palakkad MEMU",
                    "Except Thursdays",
                    "11:15 hours",
                    "12:05  hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56651 Coimbatore Kannur Fast Passenger",
                    "All Days",
                    "14:37 hours",
                    "15:20 hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56605 Coimbatore Thrissur Passenger",
                    "All Days",
                    "17:09 hours",
                    "17:55 hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
            transporters.add(new Transporter(
                    "66607 Coimbatore Palakkad Town Passenger",
                    "Except Sundays",
                    "18:39 hours",
                    "19:22 hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
            transporters.add(new Transporter(
                    "56847 Trichy - Palakkad Town Fast Passenger",
                    "All Days",
                    "19:27 hours",
                    "20:22 hours",
                    "etmd",
                    "pgt",
                    "train"
            ));
        }
        //
        // TODO BUS FROM CBE TO ETMD
        //
        if (type.equals("Buses from Coimbatore")) {
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "06:15 hours",
                    "07:15 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "09:30 hours",
                    "10:30 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "11:30 hours",
                    "12:30 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "13:40 hours",
                    "14:40 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "15:55 hours",
                    "16:55 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "18:15 hours",
                    "19:15 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "20:30 hours",
                    "21:30 hours",
                    "cbe",
                    "etmd",
                    "bus"
            ));
        }
        //
        // TODO BUS FROM ETMD TO CBE
        //
        if (type.equals("Buses to Coimbatore")) {
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "08:30 hours",
                    "09:30 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "10:30 hours",
                    "11:30 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "12:25 hours",
                    "13:25 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "14:45 hours",
                    "15:45 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "17:00 hours",
                    "18:00 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "19:20 hours",
                    "20:20 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
            transporters.add(new Transporter(
                    "Local Govt. Transport Bus 'A3'",
                    "All Days",
                    "21:40 hours",
                    "22:40 hours",
                    "etmd",
                    "cbe",
                    "bus"
            ));
        }
        setupList();
    }

    public void setupList() {
        ArrayAdapter<Transporter> aa = new ArrayAdapter<Transporter>(getBaseContext(), R.layout.list_transport_item, transporters) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_transport_card, null);
                }
                Transporter transporter = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(transporter.name);

                if (transporter.from.equals("cbe"))
                    ((TextView) convertView.findViewById(R.id.departure)).setText(Html.fromHtml("Departs from Coimbatore @ <b>" + transporter.departure + "</b>"));
                if (transporter.from.equals("etmd"))
                    ((TextView) convertView.findViewById(R.id.departure)).setText(Html.fromHtml("Departs from Ettimadai @ <b>" + transporter.departure + "</b>"));
                if (transporter.from.equals("pgt"))
                    ((TextView) convertView.findViewById(R.id.departure)).setText(Html.fromHtml("Departs from Palghat @ <b>" + transporter.departure + "</b>"));

                if (transporter.to.equals("cbe"))
                    ((TextView) convertView.findViewById(R.id.arrival)).setText(Html.fromHtml("Reaches Coimbatore @ <b>" + transporter.arrival + "</b>"));
                if (transporter.to.equals("etmd"))
                    ((TextView) convertView.findViewById(R.id.arrival)).setText(Html.fromHtml("Reaches Ettimadai @ <b>" + transporter.arrival + "</b>"));
                if (transporter.to.equals("pgt"))
                    ((TextView) convertView.findViewById(R.id.arrival)).setText(Html.fromHtml("Reaches Palghat @ <b>" + transporter.arrival + "</b>"));

                if (transporter.days.equals("All Days"))
                    ((TextView) convertView.findViewById(R.id.days)).setText(Html.fromHtml("Runs on <b>All Days</b>"));
                else
                    ((TextView) convertView.findViewById(R.id.days)).setText(Html.fromHtml("Runs on All Days <b>" + transporter.days + "</b>"));

                return convertView;
            }
        };
        listView.setAdapter(aa);
        listView.setTextFilterEnabled(true);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
