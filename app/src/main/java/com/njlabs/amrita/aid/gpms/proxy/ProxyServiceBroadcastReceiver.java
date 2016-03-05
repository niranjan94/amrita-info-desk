/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.proxy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.njlabs.amrita.aid.util.Identifier;

public class ProxyServiceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && Identifier.isConnectedToAmrita(context)) {
            context.startService(new Intent(context, ProxyRequestReceivedService.class));
        } else {
            Intent stopIntent = new Intent(context, ProxyRequestReceivedService.class);
            stopIntent.putExtra("stop", true);
            context.startService(stopIntent);
        }
    }
}
