/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.proxy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.njlabs.amrita.aid.util.Identifier;

public class SocketStarterService extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && Identifier.isConnectedToAmrita(getBaseContext())) {
            startService(new Intent(this, BackgroundSocketService.class));
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public void onInitializeTasks() {

        long periodSecs = 5L;
        long flexSecs = 1L;
        String tag = "periodic  | ProxyServiceStarterService: " + periodSecs + "s, f:" + flexSecs;
        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(SocketStarterService.class)
                .setPeriod(periodSecs)
                .setFlex(flexSecs)
                .setTag(tag)
                .setPersisted(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();

        GcmNetworkManager.getInstance(this).schedule(periodic);

        super.onInitializeTasks();
    }
}
