package com.njlabs.amrita.aid.about;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.TrainInfo;

import static android.app.ActionBar.Tab;

public class Amrita extends FragmentActivity {

    ActionBar mActionBar;
    ViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_campus);
        /** Getting a reference to action bar of this activity */
        mActionBar = getActionBar();

        /** Set tab navigation mode */
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        /** Getting a reference to ViewPager from the layout */
        mPager = (ViewPager) findViewById(R.id.pager);

        /** Getting a reference to FragmentManager */
        FragmentManager fm = getSupportFragmentManager();

        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mActionBar.setSelectedNavigationItem(position);
            }
        };

        /** Setting the pageChange listner to the viewPager */
        mPager.setOnPageChangeListener(pageChangeListener);

        /** Creating an instance of FragmentPagerAdapter */
        CampusListener fragmentPagerAdapter = new CampusListener(fm);

        /** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fragmentPagerAdapter);

        mActionBar.setDisplayShowTitleEnabled(true);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };

        /** Creating Android Tab */
        Tab tab = mActionBar.newTab()
                .setText("About Campus")
                /*.setIcon(R.drawable.android)*/
                .setTabListener(tabListener);

        mActionBar.addTab(tab);

        /** Creating Apple Tab */
        tab = mActionBar.newTab()
                .setText("Contact Details")
                /*.setIcon(R.drawable.apple)*/
                .setTabListener(tabListener);

        mActionBar.addTab(tab);

    }


    public void call_cbe(View view) {
        String num = "+914222685000";
        String number = "tel:" + num;
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
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
        final CharSequence[] items = {"From Coimbatore", "From Palakkad", "To Coimbatore", "To Palakkad"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Amrita.this);
        builder.setTitle("From / To ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(Amrita.this, "Viewing all trains " + items[item], Toast.LENGTH_LONG).show();
                if (items[item] == "From Coimbatore") {
                    Intent it = new Intent(getBaseContext(), TrainInfo.class);
                    it.putExtra("title", items[item]);
                    it.putExtra("train", "from_coimbatore.htm");
                    startActivity(it);
                } else if (items[item] == "From Palakkad") {
                    Intent it = new Intent(getBaseContext(), TrainInfo.class);
                    it.putExtra("title", items[item]);
                    it.putExtra("train", "from_palakkad.htm");
                    startActivity(it);
                } else if (items[item] == "To Coimbatore") {
                    Intent it = new Intent(getBaseContext(), TrainInfo.class);
                    it.putExtra("title", items[item]);
                    it.putExtra("train", "to_coimbatore.htm");
                    startActivity(it);
                } else if (items[item] == "To Palakkad") {
                    Intent it = new Intent(getBaseContext(), TrainInfo.class);
                    it.putExtra("title", items[item]);
                    it.putExtra("train", "to_palakkad.htm");
                    startActivity(it);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void directions_cbe(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);

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
        location = locationManager.getLastKnownLocation(bestProvider);

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
        //Uri uri = Uri.parse("http://maps.google.com/maps?f=d&daddr=10.900539,76.902806");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);

    }
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        finish(); //go back to the previous Activity
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
