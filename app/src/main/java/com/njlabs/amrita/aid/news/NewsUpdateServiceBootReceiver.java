/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NewsUpdateServiceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NewsUpdateService.class));
    }
}
