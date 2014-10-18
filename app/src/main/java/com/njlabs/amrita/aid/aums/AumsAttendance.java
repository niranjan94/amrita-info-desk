package com.njlabs.amrita.aid.aums;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.onemarker.ark.Security;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class AumsAttendance extends Activity {

    private AQuery aq;
    ProgressDialog dialog;

    String StudentCurrentSem = null;
    String FinalUserName = null;
    String FinalPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            StudentCurrentSem = extras.getString("StudentCurrentSem");
            FinalUserName = extras.getString("FinalUserName");
            FinalPassword = extras.getString("FinalPassword");
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        aq = new AQuery(this);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Receiving Data (Might take a while)");
        GetData();
    }

    public void GetData() {
        dialog.show();
        String key = "6f0d380df08a284d";
        String url = "http://njlabs.kovaideals.com/api/aid/aums/main.php?what=attendance";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", Security.encrypt(FinalUserName, key));
        params.put("pwd", Security.encrypt(FinalPassword, key));
        params.put("semester", StudentCurrentSem);

        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String html, AjaxStatus status) {
                dialog.setMessage("Parsing the received data ");
                DataParser(html);
            }

        });
    }

    public void DataParser(String html) {
        Document doc = Jsoup.parse(html);

        ScrollView scrolllayout = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        scrolllayout.addView(layout);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);


        Elements Columns = doc.select("span.rowBG1");
        int count = 0;
        int TrueCount = 0;
        int TotalTemp = 0;
        for (Element column : Columns) {
            String Data = column.text();
            if (TrueCount > 5) {
                TextView tv;
                TextView dummy;
                switch (count) {
                    case 0:
                        tv = new TextView(getApplicationContext());
                        tv.setText(Data);
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Small);
                        layout.addView(tv);
                        count++;
                        break;

                    case 1:
                        tv = new TextView(getApplicationContext());
                        tv.setText(Data);
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                        layout.addView(tv);

                        dummy = new TextView(getApplicationContext());
                        dummy.setText(" ");
                        dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
                        layout.addView(dummy);

                        count++;
                        break;

                    case 2:

                        count++;
                        break;

                    case 3:
                        tv = new TextView(getApplicationContext());
                        tv.setText("Total Classes : " + Data);
                        TotalTemp = Integer.parseInt(Data.trim());
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                        layout.addView(tv);
                        count++;
                        break;

                    case 4:
                        tv = new TextView(getApplicationContext());
                        tv.setText("You Attended : " + Data);
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                        layout.addView(tv);

                        int BunkedClasses = TotalTemp - Integer.parseInt(Data.trim());

                        tv = new TextView(getApplicationContext());
                        tv.setText("You Bunked    : " + BunkedClasses);
                        TotalTemp = Integer.parseInt(Data.trim());
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                        layout.addView(tv);

                        count++;
                        break;

                    case 5:
                        tv = new TextView(getApplicationContext());
                        tv.setText("Percentage     : " + Data);
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                        layout.addView(tv);

                        dummy = new TextView(getApplicationContext());
                        dummy.setText(" ");
                        dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
                        layout.addView(dummy);

                        float percentage = Float.parseFloat(Data);

                        tv = new TextView(getApplicationContext());
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                        tv.setTextAppearance(this, android.R.style.TextAppearance_Small);

                        if (percentage > 85) {
                            tv.setText("YOU ARE SAFE !");
                            tv.setTextColor(Color.parseColor("#669900"));
                        } else if (percentage < 85 && percentage > 80) {
                            tv.setText("YOU MIGHT BE IN DANGER !");
                            tv.setTextColor(Color.parseColor("#ff8800"));
                        } else {
                            tv.setText("YOU ARE IN DANGER !");
                            tv.setTextColor(Color.parseColor("#cc0000"));
                        }

                        layout.addView(tv);

                        dummy = new TextView(getApplicationContext());
                        dummy.setText(" ");
                        dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
                        layout.addView(dummy);

                        View divider = new View(getApplicationContext());
                        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                        divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
                        divider.setBackgroundColor(Color.parseColor("#969696"));
                        layout.addView(divider);

                        dummy = new TextView(getApplicationContext());
                        dummy.setText(" ");
                        dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
                        layout.addView(dummy);

                        count = 0;
                        break;
                }
            }
            TrueCount++;
        }
        setContentView(scrolllayout);
        dialog.dismiss();
    }
}
