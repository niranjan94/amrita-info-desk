package com.njlabs.amrita.aid.landing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.BuildConfig;
import com.njlabs.amrita.aid.info.Calender;
import com.njlabs.amrita.aid.info.Curriculum;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.info.TrainBusInfo;
import com.njlabs.amrita.aid.about.Amrita;
import com.njlabs.amrita.aid.about.App;
import com.njlabs.amrita.aid.aums.Aums;
import com.njlabs.amrita.aid.bunker.AttendanceManager;
import com.njlabs.amrita.aid.explorer.Explorer;
import com.njlabs.amrita.aid.explorer.ExplorerSignup;
import com.njlabs.amrita.aid.news.NewsActivity;
import com.njlabs.amrita.aid.news.NewsUpdateService;
import com.njlabs.amrita.aid.settings.SettingsActivity;
import com.onemarker.ark.ConnectionDetector;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class Landing extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_landing, "Amrita Info Desk");
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        setRecentHeaderColor(getResources().getColor(R.color.white));
        checkForUpdates();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Amrita Info Desk").withEmail("Version " + BuildConfig.VERSION_NAME).setSelectable(false)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        headerResult.getHeaderBackgroundView().setColorFilter(Color.rgb(170, 170, 170), android.graphics.PorterDuff.Mode.MULTIPLY);
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_action_home).withCheckable(false),
                        new PrimaryDrawerItem().withName("News").withIcon(R.drawable.ic_action_speaker_notes).withCheckable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("About the app").withIcon(R.drawable.ic_action_info).withCheckable(false),
                        new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_action_settings).withCheckable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                startActivity(new Intent(baseContext, NewsActivity.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                break;
                            case 3:
                                startActivity(new Intent(baseContext, App.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                break;
                            case 4:
                                startActivity(new Intent(baseContext, SettingsActivity.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                break;
                        }
                        return false;
                    }
                })
                .withCloseOnClick(true)
                .build();

        setupGrid();

        long periodSecs = 21600L;
        long flexSecs = 30L;
        String tag = "periodic  | NewsUpdateService: " + periodSecs + "s, f:" + flexSecs;
        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(NewsUpdateService.class)
                .setPeriod(periodSecs)
                .setFlex(flexSecs)
                .setTag(tag)
                .setPersisted(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();

        GcmNetworkManager.getInstance(this).schedule(periodic);

    }

    private void setupGrid() {

        GridView gridView = (GridView) findViewById(R.id.landing_grid);
        gridView.setAdapter(new LandingAdapter(baseContext, 1));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = ((TextView) view.getTag(R.id.landing_text)).getText().toString();
                switch (name) {
                    case "About Amrita":
                        // ABOUT AMRITA
                        startActivity(new Intent(baseContext, Amrita.class));
                        break;
                    case "Amrita Explorer":
                        // AMRITA EXPLORER
                        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                        String explorer_is_registered = preferences.getString("explorer_is_registered", "");
                        if (explorer_is_registered.equals("yes")) {
                            ConnectionDetector cd = new ConnectionDetector(baseContext);
                            boolean isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent) {
                                Intent intent = new Intent(baseContext, Explorer.class);
                                startActivity(intent);
                            } else {
                                Snackbar.make(parentView,"Internet connection is required for Amrita Explorer", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent intent = new Intent(baseContext, ExplorerSignup.class);
                            startActivity(intent);
                        }
                        break;
                    case "Academic Calender":
                        // ACADEMIC CALENDER
                        startActivity(new Intent(baseContext, Calender.class));
                        break;
                    case "Amrita UMS Login":
                        // AUMS
                        startActivity(new Intent(baseContext, Aums.class));
                        break;
                    case "Train & Bus Timings":
                        // TRAIN & BUS INFO
                        final CharSequence[] transportationOptions = {"Trains from Coimbatore", "Trains from Palghat", "Trains to Coimbatore", "Trains to Palghat", "Buses from Coimbatore", "Buses to Coimbatore"};
                        AlertDialog.Builder transportationDialogBuilder = new AlertDialog.Builder(baseContext);
                        transportationDialogBuilder.setTitle("View timings of ?");
                        transportationDialogBuilder.setItems(transportationOptions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Showing Alert Message
                                Intent trainBusOpen = new Intent(baseContext, TrainBusInfo.class);
                                trainBusOpen.putExtra("type", transportationOptions[item]);
                                startActivity(trainBusOpen);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            }
                        });
                        AlertDialog transportationDialog = transportationDialogBuilder.create();
                        transportationDialog.show();
                        break;

                    case "Attendance Manager":
                        // ATTENDANCE MANAGER
                        startActivity(new Intent(baseContext, AttendanceManager.class));
                        break;
                    case "Curriculum Info":
                        // CURRICULUM INFO
                        final CharSequence[] items_c = {"Aerospace Engineering", "Civil Engineering", "Chemical Engineering", "Computer Science Engineering", "Electrical & Electronics Engineering", "Electronics & Communication Engineering", "Electronics & Instrumentation Engineering", "Mechanical Engineering"};
                        AlertDialog.Builder departmentDialogBuilder = new AlertDialog.Builder(baseContext);
                        departmentDialogBuilder.setTitle("Select your Department");
                        departmentDialogBuilder.setItems(items_c, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Showing Alert Message
                                Intent curriculum_open = new Intent(baseContext, Curriculum.class);
                                curriculum_open.putExtra("department", items_c[item]);
                                startActivity(curriculum_open);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            }
                        });
                        AlertDialog departmentDialog = departmentDialogBuilder.create();
                        departmentDialog.show();
                        break;

                    case "News":
                        // NEWS
                        startActivity(new Intent(baseContext, NewsActivity.class));
                        break;
                    default:
                        Toast.makeText(baseContext, String.valueOf(i), Toast.LENGTH_SHORT).show();
                }
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    public void checkForUpdates() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://api.onemarker.com/update_status.php?app=com.njlabs.amrita.aid", new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String Status = "";
                try {
                    Status = response.getString("status");
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
                if (Status.equals("ok")) {
                    Double Latest = 0.0;
                    String Description = null;
                    try {
                        Latest = response.getDouble("latest");
                        Description = response.getString("description");
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                    }
                    if (Latest > BuildConfig.VERSION_CODE) {

                        AlertDialog.Builder updateDialogBuilder = new AlertDialog.Builder(Landing.this);

                        LayoutInflater factory = LayoutInflater.from(Landing.this);
                        final View changelogView = factory.inflate(R.layout.webview_dialog, null);
                        LinearLayout WebViewDialogLayout = (LinearLayout) changelogView.findViewById(R.id.WebViewDialogLayout);
                        WebViewDialogLayout.setPadding(5, 5, 5, 5);
                        WebView changelogWebView = (WebView) changelogView.findViewById(R.id.LicensesView);
                        changelogWebView.loadData(String.format("%s", Description), "text/html", "utf-8");
                        changelogWebView.setPadding(5,5,5,5);
                        changelogWebView.setBackgroundColor(0);
                        changelogWebView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return true;
                            }
                        });
                        changelogWebView.setLongClickable(false);
                        updateDialogBuilder.setView(changelogView).setCancelable(true)
                                .setCancelable(false)
                                .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Uri uri = Uri.parse("market://details?id=com.njlabs.amrita.aid");
                                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }
                                });
                        AlertDialog alert = updateDialogBuilder.create();
                        alert.setTitle("Update Available");
                        alert.setIcon(R.mipmap.ic_launcher);
                        alert.show();
                    }
                }
            }
        });
    }

}
