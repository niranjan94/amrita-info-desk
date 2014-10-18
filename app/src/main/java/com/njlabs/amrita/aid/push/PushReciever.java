package com.njlabs.amrita.aid.push;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class PushReciever extends BroadcastReceiver{
	
	@SuppressLint("Wakelock")
	@Override
    public void onReceive(Context context, Intent intent) 
    {   
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        Parse.initialize(context, "rh6SYwa5Gfxk9rBzIEZvXSloGRl50pMnockRYK5E", "DwA9WHCbzgXBfLMosxl32LPEhZGtEqe2jYVuXhCj");
        PushService.setDefaultPushCallback(context, PushNotifications.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpened(intent);
        wl.release();
    }

	public void SetPushReciever(Context context)
	{
	    AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    Intent i = new Intent(context, PushReciever.class);
	    PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
	    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pi); // Millisec * Second * Minute
	}
	
	public void CancelPushReciever(Context context)
	{
	    Intent intent = new Intent(context, PushReciever.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.cancel(sender);
	}

}
