package com.njlabs.amrita.aid.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.njlabs.amrita.aid.R;


public class App extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
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
