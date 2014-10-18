package com.njlabs.amrita.aid.explorer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.njlabs.amrita.aid.R;
import com.onemarker.ark.ConnectionDetector;

import org.acra.ACRA;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is not
 * installed/enabled/updated on a user's device.
 */

/**
 * Note that this may be null if the Google Play services APK is not available.
 */
@SuppressLint("UseSparseArrays")
public class Explorer extends FragmentActivity implements LocationListener {

    private LocationManager locationManager;

    public GoogleMap mMap;
    public UiSettings mUiSettings;

    public String deviceid = null;
    public String mobile_num = null;

    ConnectionDetector cd;

    String latitude = null;
    String longitude = null;
    String provider;

    AQuery aq;
    Timer t;

    HashMap<String, Marker> visibleMarkers = new HashMap<String, Marker>();

    Boolean InitialUpdate = true;
    Boolean isInternetPresent = false;
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

        // DECLARE AQUERY INSTANCE
        aq = new AQuery(this);

        // ACTIONBAR STUFF
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
            String url = "http://api.onemarker.com/aid/explorer.php?type=get_data_spl&mobile=" + mobile_num + "&lat=" + latitude + "&lon=" + longitude + "&datetime=" + timeStamp;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("authkey", "bdc0fabcbfa45a3506d1e66a6ff77596");
            aq.progress(this).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json != null) {
                        JSONObject data = null;
                        try {
                            data = json.getJSONObject("data");
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
                    } else {
                        // TODO handle error here
                    }
                }
            });
        }
    }

    public void ExitingCampus() {
        // ORIGINAL  String url = "http://njlabs.kovaideals.com/api/aid/explorer.php?type=campus_exit&mobile="+mobile_num;
        String url = "http://api.onemarker.com/aid/explorer.php?type=campus_exit&mobile=" + mobile_num;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("authkey", "bdc0fabcbfa45a3506d1e66a6ff77596");
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    //successful ajax call, show status code and json content
                } else {
                    //ajax error, show error code
                }
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById((R.id.map))).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mUiSettings = mMap.getUiSettings();
        mMap.addMarker(new MarkerOptions().position(new LatLng(10.900539, 76.902806)).title("Amrita School Of Engineering"));
    }
}
