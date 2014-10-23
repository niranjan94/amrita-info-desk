/*
 * Copyright (c) 2014. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.alerts;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.njlabs.amrita.aid.Landing;
import com.njlabs.amrita.aid.aums.Aums;

/**
 * Created by Niranjan on 22-10-2014.
 */
public class AumsService extends IntentService {

    public AumsService() {
        super("AumsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Aums aums = new Aums(this,"service","","");
        aums.GetSessionID("https://amritavidya.amrita.edu:8444",false);
    }

    private void sendNotification(Context context) {

        Intent notificationIntent = new Intent(context, Landing.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationManager notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification =  new Notification(android.R.drawable.star_on, "Refresh", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, "Title","Content", contentIntent);
        notificationMgr.notify(0, notification);

    }
}
