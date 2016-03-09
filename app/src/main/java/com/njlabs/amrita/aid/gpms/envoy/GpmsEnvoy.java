/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.envoy;

import android.content.Context;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.njlabs.amrita.aid.MainApplication;
import com.njlabs.amrita.aid.gpms.client.AbstractGpms;
import com.njlabs.amrita.aid.gpms.models.Relay;
import com.njlabs.amrita.aid.gpms.responses.HistoryResponse;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.gpms.ui.GpmsActivity;
import com.njlabs.amrita.aid.util.Identifier;
import com.njlabs.amrita.aid.util.ark.logging.Ln;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;
import com.njlabs.amrita.aid.util.socket.Listener;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GpmsEnvoy implements AbstractGpms {

    private WebSocket webSocket;
    private Context context;
    private String rollNo;
    private String password;
    private String identifier;
    private ArrayList<Relay> relays;
    private Timer timer = new Timer();

    public GpmsEnvoy(Context context) {
        this.context = context;
    }

    public GpmsEnvoy(Context context, String rollNo, String password, String identifier, ArrayList<Relay> relays) {
        this.context = context;
        this.rollNo = rollNo;
        this.password = password;
        this.identifier = identifier;
        this.relays = relays;
    }

    private void openSocket(WebSocketAdapter webSocketAdapter) throws IOException {
        if(webSocket != null) {
            try {
                webSocket.disconnect();
            } catch (Exception ignored) { }
        }

        webSocket = new WebSocketFactory()
                .setConnectionTimeout(15 * 1000)
                .createSocket(MainApplication.socketServer)
                .addListener(webSocketAdapter)
                .connectAsynchronously();
    }

    @Override
    public void login(final String rollNo, final String password, final InfoResponse infoResponse) {
        try {
            openSocket(new Listener(){
                @Override
                public void onTextMessage(WebSocket webSocket, String message) {
                    try {
                        JSONObject response = new JSONObject(message);
                        switch (response.getString("action")) {
                            case "start_communication_with_me": {
                                relays.add(new Relay(response.getString("from"), Integer.parseInt(response.getString("signal_strength"))));
                                if(relays.size() >= 2) {

                                    try { timer.cancel(); } catch (Exception ignored) { }


                                    Collections.sort(relays, new Comparator<Relay>() {
                                        @Override
                                        public int compare(Relay one, Relay two) {
                                            return one.getSignalLevel() > two.getSignalLevel() ? -1 : (one.getSignalLevel() > two.getSignalLevel() ) ? 1 : 0;
                                        }
                                    });

                                    identifier = relays.get(0).getIdentifier();

                                    ((GpmsActivity) context).identifier = identifier;
                                    ((GpmsActivity) context).relays = relays;

                                    JSONObject request = new JSONObject();
                                    request.put("action", "pass_payload");
                                    request.put("pass_to", identifier);

                                    JSONObject requestPayload = new JSONObject();
                                    requestPayload.put("action", "login");
                                    requestPayload.put("roll_no", rollNo);
                                    requestPayload.put("password", password);

                                    request.put("payload", requestPayload);
                                    webSocket.sendText(request.toString());
                                }
                                break;
                            }
                            case "payload_process": {

                                JSONObject responsePayload = response.getJSONObject("payload");
                                switch (responsePayload.getString("response")) {
                                    case "simple_success":
                                        infoResponse.onSuccess();
                                        break;

                                    case "success":
                                        String name = responsePayload.getString("name");
                                        String regNo = responsePayload.getString("regNo");
                                        String hostel = responsePayload.getString("hostel");
                                        String roomNo = responsePayload.getString("roomNo");
                                        String mobile = responsePayload.getString("mobile");
                                        String email = responsePayload.getString("email");
                                        infoResponse.onSuccess(regNo, name, hostel, roomNo, mobile, email, "", "");
                                        break;

                                    case "authentication_failed":
                                        infoResponse.onFailedAuthentication();
                                        break;

                                    case "error":
                                        infoResponse.onFailure(new Throwable());
                                        break;
                                }

                                break;
                            }
                        }
                    } catch (JSONException e) {
                        infoResponse.onFailure(e);
                    }
                }

                @Override
                public void onConnected(final WebSocket webSocket, Map<String, List<String>> headers) throws Exception {
                    webSocket.sendText(Identifier.getJsonIdentifier(context));
                    webSocket.sendText(new JSONObject().put("action", "broadcast").toString());

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(relays.size() == 0) {
                                infoResponse.onFailure(new Throwable("no_relays"));
                                webSocket.disconnect();
                            } else if(relays.size() == 1) {

                                JSONObject fakeResponse = new JSONObject();

                                try {

                                    fakeResponse.put("action", "start_communication_with_me");
                                    fakeResponse.put("signal_strength", "0");
                                    fakeResponse.put("from", "self");
                                    onTextMessage(webSocket, fakeResponse.toString());

                                } catch (JSONException ignored) { }
                            }
                        }
                    }, 10000);
                }

                @Override
                public void onConnectError(WebSocket webSocket, WebSocketException exception) throws Exception {
                    infoResponse.onFailure(exception);
                }

                @Override
                public void onDisconnected(WebSocket webSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Ln.d("Connection closed by " + (closedByServer ? "server" : "client"));
                }
            });

        } catch (IOException e) {
            infoResponse.onFailure(e);
        }
    }

    @Override
    public void applyDayPass(DateTime fromDate, String occasion, String reason, SuccessResponse successResponse) {

    }

    @Override
    public void applyHomePass(DateTime fromDate, DateTime toDate, String occasion, String reason, SuccessResponse successResponse) {

    }

    @Override
    public void getPendingPasses(PendingResponse pendingResponse) {

    }

    @Override
    public void cancelPass(String requestId, SuccessResponse successResponse) {

    }

    @Override
    public void getPassesHistory(HistoryResponse historyResponse) {

    }

    @Override
    public String getStudentName() {
        return "";
    }

    @Override
    public String getStudentRollNo() {
        return "";
    }

    @Override
    public void logout() {
        if(webSocket != null) {
            webSocket.disconnect();
        }
    }


}
