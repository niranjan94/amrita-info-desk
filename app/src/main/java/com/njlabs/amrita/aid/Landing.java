package com.njlabs.amrita.aid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.njlabs.amrita.aid.about.Amrita;
import com.njlabs.amrita.aid.about.App;
import com.njlabs.amrita.aid.aums.Aums;
import com.njlabs.amrita.aid.aums.alerts.AumsReceiver;
import com.njlabs.amrita.aid.bunker.AttendanceManager;
import com.njlabs.amrita.aid.explorer.Explorer;
import com.njlabs.amrita.aid.explorer.ExplorerSignup;
import com.njlabs.amrita.aid.settings.SettingsActivity;
import com.onemarker.ark.ConnectionDetector;
import com.orm.SugarRecord;

import org.acra.ACRA;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class Landing extends ActionBarActivity{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mTitle = getTitle();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DRAWER SETUP
        setUpDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Amrita Info Desk");

        Intent downloader = new Intent(this, AumsReceiver.class);
        downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 200 , 1000 * 30, pendingIntent);
        Log.d("MyActivity", "Set alarmManager.setRepeating to: ");
        checkForUpdates();
        // SUGAR ORM DATABASE INITIALIZATION
        AppMetadata amd = new AppMetadata("version",String.valueOf(MainApplication.currentVersion));

    }
    // TODO MOVE TO SPERATE FILE FOR IT TO WORK
    public class AppMetadata extends SugarRecord<AppMetadata> {
        public String key;
        public String value;

        public AppMetadata() {
        }

        public AppMetadata(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
    private void setUpDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        String[] drawerItems = new String[]{"Home","About this app","Settings"};
        mDrawerList.setAdapter(new ArrayAdapter<String>(getSupportActionBar().getThemedContext(), R.layout.item_drawer, drawerItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(getSupportActionBar().getThemedContext(), R.layout.item_drawer, null);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.text);
                String text = getItem(position);
                if (text.equals("Home")) {
                    textView.setCompoundDrawables(new IconDrawable(getSupportActionBar().getThemedContext(), Iconify.IconValue.fa_home).colorRes(R.color.njlabs_grey).actionBarSize(), null, null, null);
                    textView.setText(" " + text);
                }
                if (text.equals("About this app")) {
                    textView.setCompoundDrawables(new IconDrawable(getSupportActionBar().getThemedContext(), Iconify.IconValue.fa_info_circle).colorRes(R.color.njlabs_grey).actionBarSize(), null, null, null);
                    textView.setText(" " + text);
                }
                if (text.equals("Help & Support")) {
                    textView.setCompoundDrawables(new IconDrawable(getSupportActionBar().getThemedContext(), Iconify.IconValue.fa_question_circle).colorRes(R.color.njlabs_grey).actionBarSize(), null, null, null);
                    textView.setText(" " + text);
                }
                if (text.equals("Settings")) {
                    textView.setCompoundDrawables(new IconDrawable(getSupportActionBar().getThemedContext(), Iconify.IconValue.fa_cog).colorRes(R.color.njlabs_grey).actionBarSize(), null, null, null);
                    textView.setText(" " + text);
                }
                return convertView;
            }
        });

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                //getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance(1)).commit();
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            switch (position) {
                case 1:
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(new Intent(Landing.this, App.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    break;
                case 2:
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(new Intent(Landing.this, SettingsActivity.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    break;
                default:
                    mDrawerLayout.closeDrawer(mDrawerList);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance(position + 1)).commit();
            }
        }
    }

    public void restoreActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public static class MainFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static MainFragment newInstance(int sectionNumber) {
            MainFragment fragment = new MainFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public MainFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_landing, container, false);
            GridView gridView = (GridView) rootView.findViewById(R.id.landing_grid);
            gridView.setAdapter(new LandingAdapter(getActivity(), 1));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    switch (i) {
                        case 0:
                            // ABOUT AMRITA
                            startActivity(new Intent(getActivity(), Amrita.class));
                            break;
                        case 1:
                            // AMRITA EXPLORER
                            SharedPreferences preferences = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                            String explorer_is_registered = preferences.getString("explorer_is_registered", "");
                            if (explorer_is_registered.equals("yes")) {
                                ConnectionDetector cd = new ConnectionDetector(getActivity());
                                boolean isInternetPresent = cd.isConnectingToInternet();

                                // check for Internet status
                                if (isInternetPresent) {
                                    // Internet Connection is Present
                                    // proceed normally
                                    Intent intent = new Intent(getActivity(), Explorer.class);
                                    startActivity(intent);
                                } else {

                                    // Internet connection is not present
                                    // Ask user to connect to Internet
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());    // ALERT DIALOG
                                    builder.setTitle("No Internet Connection")
                                            .setMessage("A working internet connection is required for using Amrita Explorer !")
                                            .setCancelable(false)
                                            .setIcon(R.drawable.warning)
                                            .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }

                            } else {
                                Intent intent = new Intent(getActivity(), ExplorerSignup.class);
                                startActivity(intent);
                            }
                            break;
                        case 2:
                            // ACADEMIC CALENDER
                            startActivity(new Intent(getActivity(), Calender.class));
                            break;
                        case 3:
                            // AUMS
                            startActivity(new Intent(getActivity(), Aums.class));
                            break;
                        case 4:
                            // TRAIN & BUS INFO
                            final CharSequence[] items_t = {"Trains from Coimbatore", "Trains from Palghat", "Trains to Coimbatore", "Trains to Palghat", "Buses from Coimbatore", "Buses to Coimbatore"};
                            AlertDialog.Builder builder_t = new AlertDialog.Builder(getActivity());
                            builder_t.setTitle("View timings of ?");
                            builder_t.setItems(items_t, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    // Showing Alert Message
                                    Intent trainBusOpen = new Intent(getActivity(), TrainBusInfo.class);
                                    trainBusOpen.putExtra("type", items_t[item]);
                                    startActivity(trainBusOpen);
                                    getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                }
                            });
                            AlertDialog alert_t = builder_t.create();
                            alert_t.show();
                            break;

                        case 5:
                            // ATTENDANCE MANAGER
                            startActivity(new Intent(getActivity(), AttendanceManager.class));
                            break;
                        case 6:
                            // CURRICULUM INFO
                            final CharSequence[] items_c = {"Aerospace Engineering", "Civil Engineering", "Chemical Engineering", "Computer Science Engineering", "Electrical & Electronics Engineering", "Electronics & Communication Engineering", "Electronics & Instrumentation Engineering", "Mechanical Engineering"};
                            AlertDialog.Builder builder_c = new AlertDialog.Builder(getActivity());
                            builder_c.setTitle("Select your Department");
                            builder_c.setItems(items_c, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    // Showing Alert Message
                                    Intent curriculum_open = new Intent(getActivity(), Curriculum.class);
                                    curriculum_open.putExtra("department", items_c[item]);
                                    startActivity(curriculum_open);
                                    getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                }
                            });
                            AlertDialog alert_c = builder_c.create();
                            alert_c.show();
                            break;

                        case 7:
                            // GALLERY
                            startActivity(new Intent(getActivity(), Gallery.class));
                            break;

                        default:
                            Toast.makeText(getActivity(), String.valueOf(i), Toast.LENGTH_SHORT).show();

                    }
                    getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);

                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }
    public void checkForUpdates()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://api.onemarker.com/update_status.php?app=com.njlabs.amrita.aid", new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String Status=null;
                try
                {
                    Status= response.getString("status");
                }

                catch (JSONException e1)
                {
                    ACRA.getErrorReporter().handleSilentException(e1);
                }
                if(Status.equals("ok"))
                {
                    Double Latest = null;
                    String Description = null;
                    try
                    {
                        Latest = response.getDouble("latest");
                        Description = response.getString("description");
                    }
                    catch (JSONException e)
                    {
                        ACRA.getErrorReporter().handleSilentException(e);
                    }
                    if(Latest > MainApplication.currentVersion)
                    {
                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(Landing.this);
                        LayoutInflater factory = LayoutInflater.from(Landing.this);
                        final View WebViewDialog = factory.inflate(R.layout.webview_dialog, null);
                        LinearLayout WebViewDialogLayout = (LinearLayout) WebViewDialog.findViewById(R.id.WebViewDialogLayout);
                        WebViewDialogLayout.setPadding(5, 5, 5, 5);
                        WebView LicensesView = (WebView) WebViewDialog.findViewById(R.id.LicensesView);
                        LicensesView.loadData(String.format("%s", Description), "text/html", "utf-8");
                        LicensesView.setBackgroundColor(0);
                        LicensesView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return true;
                            }
                        });
                        LicensesView.setLongClickable(false);
                        alt_bld.setView(WebViewDialog).setCancelable(true)
                                .setNegativeButton("Will Update Later !",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("I'll Update Now !", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Uri uri = Uri.parse("market://details?id=com.njlabs.amrita.aid");
                                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }
                                });

                        AlertDialog alert = alt_bld.create();
                        alert.setTitle("New Update Available (Version "+Latest+") !");
                        alert.setIcon(R.drawable.ic_launcher);
                        alert.show();
                    }
                }
            }
        });
    }

}
