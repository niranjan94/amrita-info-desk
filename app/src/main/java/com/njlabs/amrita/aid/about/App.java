package com.njlabs.amrita.aid.about;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.BuildConfig;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.bugs.BugReport;
import com.parse.ParseInstallation;


public class App extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_about_app, Color.parseColor("#5B96E7"));
        ((TextView) findViewById(R.id.version)).setText("Version "+BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.install_id_view)).setText("Installation ID: "+ ParseInstallation.getCurrentInstallation().getInstallationId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.aums, menu);
        return true;//return true so that the menu pop up is opened

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                return true;
            case R.id.action_bug_report:
                SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
                String RollNo = preferences.getString("RollNo", "0");

                Intent intent = new Intent(getApplicationContext(), BugReport.class);
                intent.putExtra("studentName",("Anonymous"));
                intent.putExtra("studentRollNo",RollNo);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
