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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.BuildConfig;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.bugs.BugReport;


public class App extends BaseActivity {

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_about_app, Color.parseColor("#5B96E7"));
        ((TextView) findViewById(R.id.version)).setText("Version " + BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.install_id_view)).setText("Installation ID: " + FirebaseInstanceId.getInstance().getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aums, menu);
        return true;//return true so that the menu pop up is opened

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_bug_report:
                SharedPreferences preferences = getSharedPreferences("aums_prefs", Context.MODE_PRIVATE);
                String RollNo = preferences.getString("RollNo", "0");

                Intent intent = new Intent(getApplicationContext(), BugReport.class);
                intent.putExtra("studentName", ("Anonymous"));
                intent.putExtra("studentRollNo", RollNo);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
