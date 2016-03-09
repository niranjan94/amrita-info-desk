/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.util.socket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

public abstract class Listener extends WebSocketAdapter{

    @Override
    public abstract void onTextMessage(WebSocket webSocket, String message);

    @Override
    public abstract void onConnected(WebSocket webSocket, Map<String, List<String>> headers) throws Exception;

    @Override
    public abstract void onConnectError(WebSocket webSocket, WebSocketException exception) throws Exception;

    @Override
    public abstract void onDisconnected(WebSocket webSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception;
}
