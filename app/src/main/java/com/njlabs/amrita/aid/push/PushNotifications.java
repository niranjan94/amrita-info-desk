package com.njlabs.amrita.aid.push;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Niranjan on 5/20/13.
 */
public class PushNotifications extends Activity {
	
	boolean ACE=false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String Verify = extras.getString("verify");
            if(Verify!=null&&Verify.equals("true"))
            {
            	ACE=true;
            }
        }
        else
        {
        	ACE=false;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        ScrollView scrolllayout = new ScrollView(this);        
        setContentView(scrolllayout);
        LinearLayout layout = new LinearLayout(this);
        scrolllayout.addView(layout);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);      
        DatabaseHandler db = new DatabaseHandler(this);
        List<Announcement> announcements;
        if(ACE==true)
        {
        	announcements = db.getAllAceAnnouncements();
        	actionBar.setTitle("Club Announcements");
        	actionBar.setSubtitle("Amrita Center for Entrepreneurship");
        }
        else
        {
        	announcements = db.getAllAnnouncements();
        }        
        for (Announcement cn : announcements) {
           
        	TextView aalert=new TextView(getApplicationContext());
            aalert.setText(cn.getAlert());
            aalert.setTextAppearance(this, android.R.style.TextAppearance_Small);
            layout.addView(aalert);
            
            TextView atitle=new TextView(getApplicationContext());
            atitle.setText(cn.getTitle());
            atitle.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            layout.addView(atitle);           
            
            TextView adatetime=new TextView(getApplicationContext());
            adatetime.setText(cn.getDatetime());
            adatetime.setTextAppearance(this, android.R.style.TextAppearance_Small);
            layout.addView(adatetime);
            
            TextView dummy=new TextView(getApplicationContext());
            dummy.setText(" ");
            dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
            layout.addView(dummy);
            
            View divider=new View(getApplicationContext());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1, getResources().getDisplayMetrics());
            divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
            divider.setBackgroundColor(Color.parseColor("#969696"));
            layout.addView(divider);
            
            dummy=new TextView(getApplicationContext());
            dummy.setText(" ");
            dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
            layout.addView(dummy);
        }
    }
}