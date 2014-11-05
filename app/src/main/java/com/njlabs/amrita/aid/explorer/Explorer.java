package com.njlabs.amrita.aid.explorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.njlabs.amrita.aid.Landing;
import com.njlabs.amrita.aid.R;

import org.acra.ACRA;
import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Explorer extends ActionBarActivity implements LocationListener {

    private LocationManager locationManager;

    public GoogleMap mMap;
    public UiSettings mUiSettings;

    public String mobile_num = null;

    String latitude = null;
    String longitude = null;
    String provider;

    Timer t;

    HashMap<String, Marker> visibleMarkers = new HashMap<String, Marker>();

    Boolean InitialUpdate = true;
    Boolean WaitingLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ONE TIME LOCATION UPDATE
        OneTimeLocationUpdate();

        // REQUEST TO SHOW INDETERMINATE PROGRESS ON ACTION BAR
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        // DECLARE LOCATION MANAGER AND DO RELATED STUFF
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(crit, false);
        locationManager.requestLocationUpdates(provider, 0, 0, this);

        // GET MOBILE NUMBER FROM SHARED PREFS
        SharedPreferences preferences = getSharedPreferences("pref", 0);
        mobile_num = preferences.getString("mobile_number", "");

        // ACTIONBAR STUFF
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // FINAL MAP SETUPS AND AUTO UPDATE START
        setUpMapIfNeeded();
        StartAutoUpdate();
    }

    public void OneTimeLocationUpdate() {
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
        latitude = String.valueOf(lat);
        longitude = String.valueOf(lon);
        locationManager.removeUpdates(loc_listener);
    }

    public void StartAutoUpdate() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              refresh_positions();
                                          }

                                      });
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                5000);
    }

    public void StopAutoUpdate() {
        t.cancel();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "Please enable location services for Amrita Explorer to work", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), "Location Provider enabled !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // no-op
    }

    public void onChangeLocationProvidersSettingsClick(View view) {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public void refresh_positions() {
        ////
        //// REQUEST DATA
        ////
        //// GET JSON STRING
        Log.d("DATALOG", "mobile=" + mobile_num + "&lat=" + latitude + "&lon=" + longitude);
        if (latitude == "-1.0" || latitude == null || longitude == "-1.0" || longitude == null || Double.parseDouble(latitude) == 0 || Double.parseDouble(longitude) == 0 || Double.parseDouble(latitude) == -1.0 || Double.parseDouble(longitude) == -1.0) {
            WaitingLocation = true;
            SuperActivityToast superActivityToast = new SuperActivityToast(this, SuperToast.Type.PROGRESS);
            superActivityToast.setDuration(SuperToast.Duration.LONG);
            superActivityToast.setText("Waiting for location update ...");
            superActivityToast.show();
        } else if (Double.parseDouble(latitude) < 10.896957 || Double.parseDouble(latitude) > 10.908957 || Double.parseDouble(longitude) < 76.891486 || Double.parseDouble(longitude) > 76.906486) {
            if (WaitingLocation) {
                SuperActivityToast.cancelAllSuperActivityToasts();
                WaitingLocation = false;
            }
            SuperActivityToast.cancelAllSuperActivityToasts();
            SuperActivityToast.create(this, "You must be near Amrita Campus to View the locations of Other Amritians !", SuperToast.Duration.LONG).show();
            ExitingCampus();
        } else {
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
            try {
                timeStamp = URLEncoder.encode(timeStamp, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                ACRA.getErrorReporter().handleException(e1);
            }
            SuperActivityToast.cancelAllSuperActivityToasts();
            // ORIGINAL: String url = "http://njlabs.kovaideals.com/api/aid/explorer.php?type=get_data_spl&mobile=
            RequestParams params = new RequestParams();
            params.put("authkey", "bdc0fabcbfa45a3506d1e66a6ff77596");

            ExplorerClient.get("/explorer.php?type=get_data_spl&mobile=" + mobile_num + "&lat=" + latitude + "&lon=" + longitude + "&datetime=" + timeStamp, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONObject data = null;
                    try {
                        data = response.getJSONObject("data");
                    } catch (JSONException e) {
                        ACRA.getErrorReporter().handleException(e);
                    }
                    JSONArray recs = null;
                    try {
                        recs = data.getJSONArray("records");
                    } catch (JSONException e) {
                        ACRA.getErrorReporter().handleException(e);
                    }
                    if (InitialUpdate) {
                        mMap.clear();
                    }
                    for (int i = 0; i < recs.length(); ++i) {

                        String id = null;
                        String name = null;
                        String s_lat = null;
                        String s_lon = null;
                        String LastTime = null;
                        String LastSeen = null;

                        try {
                            JSONObject rec = recs.getJSONObject(i);
                            id = rec.getString("id");
                            name = rec.getString("name");
                            s_lat = rec.getString("lat");
                            s_lon = rec.getString("lon");
                            LastTime = rec.getString("datetime");
                        } catch (JSONException e) {
                            ACRA.getErrorReporter().handleException(e);
                        }
                        Log.d("LOC DEBUG", "lat=" + s_lat + ",lon=" + s_lon);
                        if (s_lat == "null" || s_lon == "null" || s_lat == null || s_lon == null || s_lat == "" || s_lon == "") {

                        } else {
                            Log.d("DEBUG", "Entered RefreshPos");
                            double lat = Double.parseDouble(s_lat);
                            double lon = Double.parseDouble(s_lon);
                            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                            Date d1 = null;
                            Date d2 = null;
                            try {
                                d1 = format.parse(timeStamp);
                                d2 = format.parse(LastTime);

                                DateTime dt1 = new DateTime(d1);
                                DateTime dt2 = new DateTime(d2);
                                int days = Days.daysBetween(dt1, dt2).getDays();
                                int hours = Math.abs(Hours.hoursBetween(dt1, dt2).getHours() % 24);
                                int minutes = Math.abs(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60);
                                int seconds = Math.abs(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60);
                                LastSeen = "";
                                if (days != 0) {
                                    LastSeen = LastTime;
                                } else if (hours != 0) {
                                    if (hours == 1) {
                                        LastSeen = LastSeen.concat(hours + " hour");
                                    } else {
                                        LastSeen = LastSeen.concat(hours + " hours");
                                    }
                                    LastSeen = LastSeen.concat(" ago");
                                } else {
                                    if (minutes == 0 && seconds != 0) {
                                        LastSeen = LastSeen.concat("Less than a min");
                                    } else {
                                        if (minutes == 1) {
                                            LastSeen = LastSeen.concat(minutes + " min");
                                        } else {
                                            LastSeen = LastSeen.concat(minutes + " mins");
                                        }
                                    }
                                    LastSeen = LastSeen.concat(" ago");
                                }

                            } catch (Exception e) {
                                ACRA.getErrorReporter().handleException(e);
                            }
                            String FinalLastSeen = "";
                            if (LastSeen != "") {
                                FinalLastSeen = "Last Seen " + LastSeen;
                                Log.d("DEBUG", FinalLastSeen);
                            }

                            if (InitialUpdate) {
                                Log.d("DEBUG", "Initial Update");
                                Marker new_marker_main = mMap.addMarker(new MarkerOptions().position(new LatLng(10.900539, 76.902806)).title("Amrita School Of Engineering").snippet("The Main Academic Block").icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.marker_main)));
                                new_marker_main.isVisible();
                                new_marker_main.showInfoWindow();

                                Marker new_marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .title(name)
                                        .snippet(FinalLastSeen)
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.marker)));
                                visibleMarkers.put(id, new_marker);
                            } else {
                                Log.d("DEBUG", "Old Update");
                                if (visibleMarkers.containsKey(id)) {
                                    Log.d("DEBUG", "COntains key Update");
                                    Marker CurrentMarker = visibleMarkers.get(id);
                                    LatLng OldLatLong = CurrentMarker.getPosition();
                                    if (OldLatLong.equals(new LatLng(lat, lon))) {
                                        // DO NOTHING
                                    } else {
                                        CurrentMarker.setPosition(new LatLng(lat, lon));
                                        CurrentMarker.setSnippet(FinalLastSeen);
                                    }
                                } else {
                                    Log.d("DEBUG", "No key Update");
                                    Marker new_marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lon))
                                            .title(name)
                                            .snippet(FinalLastSeen)
                                            .icon(BitmapDescriptorFactory
                                                    .fromResource(R.drawable.marker)));
                                    visibleMarkers.put(id, new_marker);
                                }
                            }

                        }


                    }
                    if (InitialUpdate) {
                        InitialUpdate = false;
                    }
                }
            });
        }
    }

    public void ExitingCampus() {

        RequestParams params = new RequestParams();
        params.put("authkey", "bdc0fabcbfa45a3506d1e66a6ff77596");

        ExplorerClient.get("/explorer.php?type=campus_exit&mobile=" + mobile_num, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mMap != null) {
            // Keep the UI Settings state in sync with the checkboxes.
            mUiSettings.setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            locationManager.requestLocationUpdates(provider, 0, 0, this);
        }
        StartAutoUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        StopAutoUpdate();
        SuperActivityToast.cancelAllSuperActivityToasts();
        ExitingCampus();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById((R.id.map))).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mUiSettings = mMap.getUiSettings();
        mMap.addMarker(new MarkerOptions().position(new LatLng(10.900539, 76.902806)).title("Amrita School Of Engineering"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            Intent exit = new Intent(Explorer.this, Landing.class);
            exit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(exit);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent exit = new Intent(Explorer.this, Landing.class);
        exit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(exit);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
