package com.njlabs.amrita.aid.aums;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.njlabs.amrita.aid.R;
import com.onemarker.ark.Security;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AumsGrades extends Activity implements OnItemSelectedListener {

    private AQuery aq;
    ProgressDialog dialog;

    String FinalUserName = null;
    String FinalPassword = null;

    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aums_grades);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FinalUserName = extras.getString("FinalUserName");
            FinalPassword = extras.getString("FinalPassword");
        }

        aq = new AQuery(this);

        SetSpinnerContent(0);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);

    }

    public void SetSpinnerContent(int position) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> itemss = new ArrayList<String>();
        itemss.add("1");
        itemss.add("2");
        itemss.add("Vacation 1");
        itemss.add("3");
        itemss.add("4");
        itemss.add("Vacation 2");
        itemss.add("5");
        itemss.add("6");
        itemss.add("Vacation 3");
        itemss.add("7");
        itemss.add("8");
        itemss.add("Vacation 4");
        itemss.add("9");
        itemss.add("10");
        itemss.add("Vacation 5");
        itemss.add("11");
        itemss.add("12");
        itemss.add("Vacation 6");
        itemss.add("13");
        itemss.add("14");
        itemss.add("15");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemss);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        if (position != 0) {
            spinner.setSelection(position);
        }
        check = 0;
        spinner.setOnItemSelectedListener(this);

    }

    public void GetGrades(String Semester, final int position) {
        dialog.setMessage("Receiving Data (Might take a while)");
        dialog.show();
        String key = "6f0d380df08a284d";
        String url = "http://njlabs.kovaideals.com/api/aid/aums/main.php?what=grades";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", Security.encrypt(FinalUserName, key));
        params.put("pwd", Security.encrypt(FinalPassword, key));
        params.put("semester", Semester);

        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String html, AjaxStatus status) {
                dialog.setMessage("Parsing the received data ");
                DataParser(html, position);
            }

        });
    }

    public void DataParser(String html, int position) {
        setContentView(R.layout.activity_aums_grades);
        SetSpinnerContent(position);

        boolean Published = true;

        Document doc = Jsoup.parse(html);

        Element PublishedState = doc.select("input[name=htmlPageTopContainer_status]").first();
        if (PublishedState.attr("value").equals("Result Not Published.")) {
            Published = false;
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.grade_layout);

        if (Published) {
            Elements Columns = doc.select("span.rowBG1");
            int count = 0;
            int TrueCount = 0;
            TextView tv = null;
            for (Element column : Columns) {
                String Data = column.text();
                if (TrueCount > 4) {
                    TextView dummy;
                    switch (count) {
                        case 0:
                            count++;
                            break;

                        case 1:
                            tv = new TextView(getApplicationContext());
                            tv.setText(Data);
                            tv.setTextAppearance(this, android.R.style.TextAppearance_Small);
                            layout.addView(tv);
                            count++;
                            break;

                        case 2:
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

                        case 3:
                            tv = new TextView(getApplicationContext());
                            tv.setText("Type   : " + Data);
                            tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                            layout.addView(tv);
                            count++;
                            break;

                        case 4:
                            tv = new TextView(getApplicationContext());
                            tv.setText("Grade : " + Data);
                            tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                            layout.addView(tv);

                            dummy = new TextView(getApplicationContext());
                            dummy.setText(" ");
                            dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
                            layout.addView(dummy);

                            tv = new TextView(getApplicationContext());
                            tv.setTypeface(Typeface.DEFAULT_BOLD);
                            tv.setTextAppearance(this, android.R.style.TextAppearance_Small);

                            if (Data.trim().equals("F") || Data.trim().equals("F(Supply)")) {
                                tv.setText("SORRY ! YOU HAVE FAILED !");
                                tv.setTextColor(Color.parseColor("#cc0000"));
                            } else if (Data.trim().equals("A+") || Data.trim().equals("A") || Data.trim().equals("B") || Data.trim().equals("B+") || Data.trim().equals("C") || Data.trim().equals("C+") || Data.trim().equals("D") || Data.trim().equals("D+")) {
                                tv.setText("CONGRATULATIONS ! YOU PASSED !");
                                tv.setTextColor(Color.parseColor("#669900"));
                            } else if (Data.trim().equals("A+(Supply)") || Data.trim().equals("A(Supply)") || Data.trim().equals("B(Supply)") || Data.trim().equals("B+(Supply)") || Data.trim().equals("C(Supply)") || Data.trim().equals("C+(Supply)") || Data.trim().equals("D(Supply)") || Data.trim().equals("D+(Supply)")) {
                                tv.setText("CONGRATULATIONS ! YOU PASSED !");
                                tv.setTextColor(Color.parseColor("#669900"));
                            } else {
                                tv.setText("");
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
            tv.setVisibility(View.GONE);

            Element SGPA = doc.select("span.rowBG1").last();
            String Data = SGPA.text();
            tv = new TextView(getApplicationContext());
            tv.setText("Final SGPA   : " + Data);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            layout.addView(tv);
        } else {
            TextView dummy = new TextView(getApplicationContext());
            dummy.setText(" ");
            dummy.setTextAppearance(this, android.R.style.TextAppearance_Small);
            layout.addView(dummy);

            TextView tv = new TextView(getApplicationContext());
            tv.setText("The Results have not been published yet !");
            tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
            layout.addView(tv);
        }

        dialog.dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        check = check + 1;
        if (check > 1) {
            String item = parent.getItemAtPosition(position).toString();
            GetGrades(item, position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}
