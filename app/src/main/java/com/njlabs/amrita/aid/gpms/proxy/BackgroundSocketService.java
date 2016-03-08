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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.gpms.client.Gpms;
import com.njlabs.amrita.aid.gpms.client.GpmsSlave;
import com.njlabs.amrita.aid.gpms.models.HistoryEntry;
import com.njlabs.amrita.aid.gpms.models.PendingEntry;
import com.njlabs.amrita.aid.gpms.responses.HistoryResponse;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.util.Identifier;
import com.njlabs.amrita.aid.util.ark.logging.Ln;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class BackgroundSocketService extends Service {

    Context context;
    String identifier;
    private WebSocket webSocket;
    private ProxyWebsocketAdapter proxyWebsocketAdapter;
    private Gson gson;

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
            try {
                WebSocketThread.lock.unlock();
            } catch (Exception ignored) {  }

            return START_NOT_STICKY;
        } else {
            return START_STICKY;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && Identifier.isConnectedToAmrita(context)) {
            identifier = Identifier.identify(context);
            startWebsocketComm();
        } else {
            if(webSocket != null) {
                webSocket.disconnect();
            }
            stopSelf();

            try {
                WebSocketThread.lock.unlock();
            } catch (Exception ignored) {  }
        }
    }


    private WebSocket buildWebSocket() throws IOException {
        proxyWebsocketAdapter = new ProxyWebsocketAdapter();
        return new WebSocketFactory()
                .setConnectionTimeout(15 * 1000)
                .createSocket(MainApplication.socketServer)
                .addListener(proxyWebsocketAdapter);
    }

    public class ProxyWebsocketAdapter extends WebSocketAdapter {

        @Override
        public void onTextMessage(WebSocket webSocket, String message) {
            Ln.d("Raw string: " + message);
            if (processJson(message, webSocket)) {
                Ln.d("JSON Processed.");
            }
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
            try {
                WebSocketThread.lock.unlock();
            } catch (Exception ignored) {  }
            stopSelf();
        }
    }


    public void startWebsocketComm() {
        try {
            webSocket = buildWebSocket();
            new WebSocketThread(webSocket, proxyWebsocketAdapter).start();
        } catch (Exception e) {
            Ln.e(e);
            stopSelf();
            try {
                WebSocketThread.lock.unlock();
            } catch (Exception ignored) {  }
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

        try {
            WebSocketThread.lock.unlock();
        } catch (Exception ignored) {  }
    }

    private boolean processJson(String s, final WebSocket webSocket) {
        try {
            JSONObject request = new JSONObject(s);

            switch (request.getString("action")) {
                case "respond_if_available":
                    String[] wifiDetails = Identifier.getWifiInfo(context);
                    if (Identifier.isConnectedToAmrita(context)) {
                        JSONObject response = new JSONObject();
                        response.put("action", "respond_back");
                        response.put("from", identifier);
                        response.put("respond_to", request.getString("caller"));
                        response.put("signal_strength", wifiDetails[1]);
                        Ln.d(response.toString());
                        webSocket.sendText(response.toString());
                    }
                    break;

                case "payload_process":
                    Gpms gpms = new Gpms(context, request.getString("from"));
                    final JSONObject requestPayload = request.getJSONObject("payload");
                    String rollNo = requestPayload.getString("roll_no");
                    String password = requestPayload.getString("password");

                    String from = request.getString("from");

                    String activity = requestPayload.getString("activity");

                    final JSONObject response = new JSONObject();
                    response.put("action", "pass_payload");
                    response.put("pass_to", from);

                    final JSONObject responsePayload = new JSONObject();
                    responsePayload.put("activity", activity + "_response");


                    switch (activity) {
                        case "login": {
                            gpms.login(rollNo, password, new InfoResponse() {
                                @Override
                                public void onSuccess() {
                                    try {
                                        responsePayload.put("response", "simple_success");
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }

                                @Override
                                public void onSuccess(String regNo, String name, String hostel, String hostelCode, String roomNo, String mobile, String email, String photoUrl, String numPasses) {
                                    try {
                                        responsePayload.put("response", "success");
                                        responsePayload.put("name", name);
                                        responsePayload.put("regNo", regNo);
                                        responsePayload.put("hostel", hostel);
                                        responsePayload.put("roomNo", roomNo);
                                        responsePayload.put("mobile", mobile);
                                        responsePayload.put("email", email);
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }

                                @Override
                                public void onFailedAuthentication() {
                                    try {
                                        responsePayload.put("response", "authentication_failed");
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    try {
                                        responsePayload.put("response", "error");
                                        responsePayload.put("message", throwable.getMessage());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }
                            });
                        }

                        case "apply_day_pass": {
                            DateTime fromDate = DateTime.parse(requestPayload.getString("from_date"));
                            String occasion = requestPayload.getString("occasion");
                            String leaveReason = requestPayload.getString("leave_reason");
                            GpmsSlave.applyDayPass(gpms, rollNo, password, fromDate, occasion, leaveReason, new SuccessResponse() {
                                @Override
                                public void onSuccess() {
                                    try {
                                        responsePayload.put("response", "success");
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    try {
                                        responsePayload.put("response", "error");
                                        responsePayload.put("message", throwable.getMessage());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }
                            });
                            break;
                        }

                        case "apply_home_pass": {
                            DateTime fromDate = DateTime.parse(requestPayload.getString("from_date"));
                            DateTime toDate = DateTime.parse(requestPayload.getString("to_date"));
                            String occasion = requestPayload.getString("occasion");
                            String leaveReason = requestPayload.getString("leave_reason");
                            GpmsSlave.applyHomePass(gpms, rollNo, password, fromDate, toDate, occasion, leaveReason, new SuccessResponse() {
                                @Override
                                public void onSuccess() {
                                    try {
                                        responsePayload.put("response", "success");
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    try {
                                        responsePayload.put("response", "error");
                                        responsePayload.put("message", throwable.getMessage());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                    webSocket.sendText(response.toString());
                                }
                            });
                            break;
                        }

                        case "get_pending_passes": {
                            GpmsSlave.getPendingPasses(gpms, rollNo, password, new PendingResponse() {
                                @Override
                                public void onSuccess(List<PendingEntry> pendingEntries) {
                                    try {
                                        responsePayload.put("response", "success");
                                        String pendingEntriesJson = gson.toJson(pendingEntries);
                                        requestPayload.put("data", new JsonParser().parse(pendingEntriesJson).getAsJsonArray());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    try {
                                        responsePayload.put("response", "error");
                                        responsePayload.put("message", throwable.getMessage());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                }
                            });

                            break;
                        }

                        case "get_passes_history": {
                            GpmsSlave.getPassesHistory(gpms, rollNo, password, new HistoryResponse() {
                                @Override
                                public void onSuccess(List<HistoryEntry> historyEntries) {
                                    try {
                                        responsePayload.put("response", "success");
                                        String historyEntriesJson = gson.toJson(historyEntries);
                                        requestPayload.put("data", new JsonParser().parse(historyEntriesJson).getAsJsonArray());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    try {
                                        responsePayload.put("response", "error");
                                        responsePayload.put("message", throwable.getMessage());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                }
                            });

                            break;
                        }

                        case "cancel_pass": {
                            String passId = requestPayload.getString("pass_id");

                            GpmsSlave.cancelPass(gpms, rollNo, password, passId, new SuccessResponse() {

                                @Override
                                public void onSuccess() {
                                    try {
                                        responsePayload.put("response", "success");
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    try {
                                        responsePayload.put("response", "error");
                                        responsePayload.put("message", throwable.getMessage());
                                        response.put("payload", responsePayload);
                                    } catch (JSONException e) {
                                        Ln.e(e);
                                    }
                                }
                            });

                            break;
                        }
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
