/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.gpms.client.AbstractGpms;
import com.njlabs.amrita.aid.gpms.client.Gpms;
import com.njlabs.amrita.aid.gpms.envoy.GpmsEnvoy;
import com.njlabs.amrita.aid.gpms.models.PendingEntry;
import com.njlabs.amrita.aid.gpms.models.Relay;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.util.ExtendedSwipeRefreshLayout;
import com.njlabs.amrita.aid.util.Identifier;
import com.njlabs.amrita.aid.util.ark.Security;
import com.njlabs.amrita.aid.util.ark.logging.Ln;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;

import java.util.ArrayList;
import java.util.List;

public class PendingPassActivity extends BaseActivity {

    private ExtendedSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AbstractGpms gpms;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_gpms_list, Color.parseColor("#009688"));

        swipeRefreshLayout = (ExtendedSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#009688"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutParams);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        swipeRefreshLayout.setRefreshing(true);

        SharedPreferences preferences = getSharedPreferences("gpms_prefs", Context.MODE_PRIVATE);
        String rollNo = preferences.getString("roll_no", "");
        String password = Security.decrypt(preferences.getString("password", ""), MainApplication.key);

        if(Identifier.isConnectedToAmrita(baseContext)) {
            gpms = new Gpms(baseContext);
        } else {
            if(!getIntent().hasExtra("relays") || !getIntent().hasExtra("identifier")) {
                Toast.makeText(baseContext, "An unexpected error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                finish();
            }

            ArrayList<Relay> relays = getIntent().getParcelableArrayListExtra("relays");
            String identifier = getIntent().getStringExtra("identifier");

            gpms = new GpmsEnvoy(baseContext, rollNo, password, identifier, relays);
        }

        loadData();
    }

    public void loadData() {
        gpms.getPendingPasses(new PendingResponse() {
            @Override
            public void onSuccess(List<PendingEntry> pendingEntries) {
                recyclerView.setAdapter(new PassAdapter(pendingEntries));
                swipeRefreshLayout.setRefreshing(false);
                if(pendingEntries.size() == 0) {
                    findViewById(R.id.no_data_view).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.no_data_view).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Snackbar.make(parentView, "Cannot establish reliable connection to the server. Try again.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void cancelPass(final View v) {
        String requestId = (String) v.getTag();
        ProgressBar progressBar = null;
        ViewGroup row = (ViewGroup) v.getParent();
        for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
            View view = row.getChildAt(itemPos);
            if (view instanceof ProgressBar) {
                progressBar = (ProgressBar) view;
                break;
            }
        }

        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            v.setVisibility(View.GONE);
        }

        final ProgressBar finalProgressBar = progressBar;

        gpms.cancelPass(requestId, new SuccessResponse() {
            @Override
            public void onSuccess() {
                swipeRefreshLayout.setRefreshing(true);
                loadData();
                Snackbar.make(parentView, "Pass cancelled successfully.", Snackbar.LENGTH_LONG).show();

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("GPMS")
                        .setAction("Cancel Pass")
                        .setLabel(gpms.getStudentName() + " - " + gpms.getStudentRollNo())
                        .build());

            }

            @Override
            public void onFailure(Throwable throwable) {
                Ln.e(throwable);
                if(finalProgressBar != null) {
                    finalProgressBar.setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                }
                Snackbar.make(parentView, "An error occurred while cancelling your pass", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public class PassAdapter extends RecyclerView.Adapter<PassAdapter.ViewHolder> {

        private List<PendingEntry> pendingEntries;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView to;
            public TextView from;
            public TextView applied_to;
            public TextView status;
            public TextView id;
            public Button cancel_button;
            public ViewHolder(View v) {
                super(v);
                to = (TextView) v.findViewById(R.id.to);
                from = (TextView) v.findViewById(R.id.from);
                applied_to = (TextView) v.findViewById(R.id.applied_to);
                status = (TextView) v.findViewById(R.id.status);
                id = (TextView) v.findViewById(R.id.id);
                cancel_button = (Button) v.findViewById(R.id.cancel_button);
            }

        }

        public PassAdapter(List<PendingEntry> pendingEntries) {
            this.pendingEntries = pendingEntries;
        }

        @Override
        public PassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pass_pending_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PendingEntry pendingEntry = pendingEntries.get(position);
            holder.to.setText(pendingEntry.getAppliedTill());
            holder.from.setText(pendingEntry.getAppliedFrom());
            holder.applied_to.setText(pendingEntry.getRequestedWith());
            holder.status.setText(pendingEntry.getApprovalStatus());

            if(pendingEntry.getApprovalStatus().toLowerCase().equals("pending")) {
                holder.status.setTextColor(getResources().getColor(R.color.md_amber_800));
            } else if(pendingEntry.getApprovalStatus().toLowerCase().equals("issued")) {
                holder.status.setTextColor(getResources().getColor(R.color.md_green_500));
            } else {
                holder.status.setTextColor(getResources().getColor(R.color.md_red_500));
            }

            holder.id.setText(pendingEntry.getId());
            holder.cancel_button.setTag(pendingEntry.getId());
        }

        @Override
        public int getItemCount() {
            return pendingEntries.size();
        }
    }
}
