package com.njlabs.amrita.aid.explorer;

import android.app.IntentService;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExplorerBackgroundLocationService extends IntentService {

    String mobile_num;
    LocationManager locationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Starting..", Toast.LENGTH_SHORT).show();
        mobile_num = intent.getStringExtra("mobile");

        return START_STICKY; // or whatever your flag
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
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
            // TODO ACRA.getErrorReporter().handleException(e);
        }
        String latitude = String.valueOf(lat);
        String longitude = String.valueOf(lon);
        locationManager.removeUpdates(loc_listener);
        if (latitude == "-1.0" || latitude == null || longitude == "-1.0" || longitude == null || Double.parseDouble(latitude) == 0 || Double.parseDouble(longitude) == 0 || Double.parseDouble(latitude) == -1.0 || Double.parseDouble(longitude) == -1.0) {

        } else if (Double.parseDouble(latitude) < 10.896957 || Double.parseDouble(latitude) > 10.908957 || Double.parseDouble(longitude) < 76.891486 || Double.parseDouble(longitude) > 76.906486) {

        } else {
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
            try {
                timeStamp = URLEncoder.encode(timeStamp, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO  ACRA.getErrorReporter().handleException(e1);
            }
            SuperActivityToast.cancelAllSuperActivityToasts();
            String url = "http://njlabs.kovaideals.com/api/aid/explorer.php?type=location_ping&mobile=" + mobile_num + "&lat=" + latitude + "&lon=" + longitude + "&datetime=" + timeStamp;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("authkey", "bdc0fabcbfa45a3506d1e66a6ff77596");
            /*aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json != null) {

                    } else {

                    }
                }
            });*/
        }
    }

    public ExplorerBackgroundLocationService() {
        super("ExplorerBackgroundLocationService");
        // TODO Auto-generated constructor stub


    }

    @Override
    protected void onHandleIntent(Intent arg0) {

        // Do some task
        Log.i("TaskService", "Service running");
    }

}
