/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Identifier {

    @SuppressLint("HardwareIds")
    public static String identify(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public static String[] getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int RSSI = wifiManager.getConnectionInfo().getRssi();
        String SSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        int level = WifiManager.calculateSignalLevel(RSSI, 5);
        return new String[]{SSID, String.valueOf(level)};
    }

    public static boolean isConnectedToAmrita(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null) {
            String SSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "").trim();
            return SSID.contentEquals("Amrita");
        }
        return false;
    }

    public static String getJsonIdentifier(Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("identifier", Identifier.identify(context));
            jsonObject.put("action", "set_identifier");
            return jsonObject.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }

}
