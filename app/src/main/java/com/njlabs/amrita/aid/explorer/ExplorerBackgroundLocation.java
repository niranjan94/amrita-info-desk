package com.njlabs.amrita.aid.explorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ExplorerBackgroundLocation extends BroadcastReceiver {
    public static String ACTION_ALARM = "com.njlabs.amrita.aid.alaram";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.i("Alarm Receiver", "Entered");
        //Toast.makeText(context, "Entered", Toast.LENGTH_SHORT).show();

        Bundle bundle = intent.getExtras();
        String action = bundle.getString(ACTION_ALARM);
        if (action != null && action.equals(ACTION_ALARM)) {
            //Log.i("Alarm Receiver", "If loop");
            SharedPreferences preferences = context.getSharedPreferences("pref", 0);
            String mobile_num = preferences.getString("mobile_number", "");
            Intent inService = new Intent(context, ExplorerBackgroundLocationService.class);
            inService.putExtra("mobile", mobile_num);
            context.startService(inService);
        } else {
            //Log.i("Alarm Receiver", "Else loop");
            //Toast.makeText(context, "Else loop", Toast.LENGTH_SHORT).show();
        }

    }
}