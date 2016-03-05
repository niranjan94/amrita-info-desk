/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.proxy;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.gpms.client.Gpms;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.util.Identifier;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BackgroundSocketService extends Service {

    Context context;
    String identifier;
    private WebSocket webSocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getBooleanExtra("stop", false)) {
            if(webSocket != null) {
                webSocket.disconnect();
            }
            stopSelf();
            return START_NOT_STICKY;
        } else {
            return START_STICKY;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && Identifier.isConnectedToAmrita(context)) {
            identifier = Identifier.identify(context);
            startWebsocketComm();
        } else {
            if(webSocket != null) {
                webSocket.disconnect();
            }
            stopSelf();
        }
    }


    private WebSocket connectToSocket() throws IOException {
        return new WebSocketFactory()
                .setConnectionTimeout(15 * 1000)
                .createSocket(MainApplication.socketServer)
                .addListener(new ProxyWebsocketAdapter())
                .connectAsynchronously();
    }



    private class ProxyWebsocketAdapter extends WebSocketAdapter {

        @Override
        public void onTextMessage(WebSocket webSocket, String message) {
            Ln.d("Raw string: " + message);
            if (processJson(message, webSocket)) {
                Ln.d("JSON Processed.");
            };
        }

        @Override
        public void onConnected(WebSocket webSocket, Map<String, List<String>> headers) throws Exception {
            Ln.d("Connected");
            webSocket.sendText("device:"+identifier);
            webSocket.sendText(Identifier.getJsonIdentifier(context));
        }

        @Override
        public void onConnectError(WebSocket webSocket, WebSocketException exception) throws Exception {
            Ln.d("Connection error");
            stopSelf();
        }

        @Override
        public void onDisconnected(WebSocket webSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            Ln.d("Connection closed by " + (closedByServer ? "server" : "client"));
            stopSelf();
        }
    }


    public void startWebsocketComm() {
        try {
            webSocket = connectToSocket();
        } catch (Exception e) {
            Ln.e(e);
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(webSocket != null) {
            webSocket.disconnect();
        }
        try {
            sendBroadcast(new Intent("com.njlabs.amrita.aid.boot"));
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    private boolean processJson(String s, WebSocket webSocket) {
        try {
            JSONObject json = new JSONObject(s);

            switch (json.getString("action")) {
                case "respond_if_available":
                    String[] wifiDetails = Identifier.getWifiInfo(context);
                    if (Identifier.isConnectedToAmrita(context)) {
                        JSONObject response = new JSONObject();
                        response.put("action", "respond_back");
                        response.put("from", identifier);
                        response.put("respond_to", json.getString("caller"));
                        response.put("signal_strength", wifiDetails[1]);
                        Ln.d(response.toString());
                        webSocket.sendText(response.toString());
                    }
                    break;

                case "payload_process":
                    Gpms gpms = new Gpms(context, json.getString("from"));
                    JSONObject innerPayload = json.getJSONObject("payload");
                    String rollNo = innerPayload.getString("roll_no");
                    String password = innerPayload.getString("password");

                    String method = innerPayload.getString("method");
                    switch (method) {
                        case "login":
                            gpms.login(rollNo, password, new InfoResponse() {
                                @Override
                                public void onSuccess(String regNo, String name, String hostel, String roomNo, String mobile, String email, String photoUrl, String numPasses) {

                                }

                                @Override
                                public void onFailedAuthentication() {

                                }

                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure(Throwable throwable) {

                                }
                            });
                    }

                    gpms.logout();
                    gpms.deletePrefs();
            }

        } catch (JSONException e) {
            return false;
        }
        return true;
    }

}
