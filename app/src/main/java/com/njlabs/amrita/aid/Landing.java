package com.njlabs.amrita.aid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.njlabs.amrita.aid.about.Amrita;
import com.njlabs.amrita.aid.about.App;
import com.njlabs.amrita.aid.aums.Aums;
import com.njlabs.amrita.aid.explorer.Explorer;
import com.njlabs.amrita.aid.explorer.ExplorerSignup;
import com.njlabs.amrita.aid.settings.SettingsActivity;
import com.onemarker.ark.ConnectionDetector;

import org.acra.ACRA;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class Landing extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        checkForUpdates();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        switch(position)
        {
            case 1:
                startActivity(new Intent(Landing.this, App.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                break;
            case 3:

                break;
            case 2:
                startActivity(new Intent(Landing.this, SettingsActivity.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                break;
            default:
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.app_name);
                break;
            case 2:
                mTitle = getString(R.string.app_name);
                break;
            case 3:
                mTitle = getString(R.string.app_name);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

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
                            // DEPARTMENT INFO
                            final CharSequence[] items_d = {"Aerospace", "Civil", "Chemical", "Computer Science", "Electrical", "Electronics", "Mechanical", "Humanities", "Mathematics", "Sciences"};
                            AlertDialog.Builder builder_d = new AlertDialog.Builder(getActivity());
                            builder_d.setTitle("Select a Department");
                            builder_d.setItems(items_d, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    // Showing Alert Message
                                    //Toast.makeText(getActivity(), "Welcome to the " + items_d[item] +" Department", Toast.LENGTH_LONG).show();
                                    Intent department_open = new Intent(getActivity(), Departments.class);
                                    department_open.putExtra("department", items_d[item]);
                                    startActivity(department_open);
                                    getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                }
                            });
                            AlertDialog alert_d = builder_d.create();
                            alert_d.show();
                            break;
                        case 5:
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
                        case 6:
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
            ((Landing) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
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
