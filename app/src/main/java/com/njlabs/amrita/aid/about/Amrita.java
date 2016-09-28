/*
 * MIT License
 *
 * Copyright (c) 2016 Niranjan Rajendran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.njlabs.amrita.aid.about;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.info.TrainBusInfo;

import java.util.ArrayList;
import java.util.List;

public class Amrita extends BaseActivity {

    ViewPager viewPager;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_about_campus, Color.parseColor("#03a9f4"));

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(Color.parseColor("#03a9f4"));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        CampusAbout campusAbout = new CampusAbout();
        CampusContact campusContact = new CampusContact();
        adapter.addFrag(campusAbout, "About");
        adapter.addFrag(campusContact, "Contact");
        viewPager.setAdapter(adapter);
    }

    public void call_cbe(View view) {
        String num = "+914222685000";
        String number = "tel:" + num;
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+914222685000")));
    }

    public void email_cbe(View view) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_EMAIL, "mailto:univhq@amrita.edu");
        it.putExtra(Intent.EXTRA_TEXT, "Hi,");
        it.setType("text/plain");
        startActivity(Intent.createChooser(it, "Choose an Email Client"));
    }

    public void view_train_cbe(View view) {
        final CharSequence[] items_t = {"Trains from Coimbatore", "Trains from Palghat", "Trains to Coimbatore", "Trains to Palghat", "Buses from Coimbatore", "Buses to Coimbatore"};
        AlertDialog.Builder builder_t = new AlertDialog.Builder(Amrita.this);
        builder_t.setTitle("View timings of ?");
        builder_t.setItems(items_t, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent trainBusOpen = new Intent(Amrita.this, TrainBusInfo.class);
                trainBusOpen.putExtra("type", items_t[item]);
                startActivity(trainBusOpen);
            }
        });
        AlertDialog alert_t = builder_t.create();
        alert_t.show();
    }

    public void directions_cbe(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener loc_listener = new LocationListener() {
            public void onLocationChanged(Location l) {
            }

            public void onProviderEnabled(String p) {
            }

            public void onProviderDisabled(String p) {
            }

            public void onStatusChanged(String p, int status, Bundle extras) {
            }
        };
        locationManager.requestLocationUpdates(bestProvider, 0, 0, loc_listener);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        double lat;
        double lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (NullPointerException e) {
            lat = -1.0;
            lon = -1.0;
        }
        Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=" + lat + "," + lon + "&daddr=10.900539,76.902806&hl=en");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
