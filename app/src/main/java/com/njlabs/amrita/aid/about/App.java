package com.njlabs.amrita.aid.about;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.BuildConfig;
import com.njlabs.amrita.aid.R;
import com.parse.ParseInstallation;


public class App extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_about_app, Color.parseColor("#5B96E7"));
        ((TextView) findViewById(R.id.version)).setText("Version "+BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.install_id_view)).setText("Installation ID: "+ ParseInstallation.getCurrentInstallation().getInstallationId());
    }
}
