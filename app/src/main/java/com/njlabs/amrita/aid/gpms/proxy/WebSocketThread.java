/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.proxy;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.njlabs.amrita.aid.util.ark.logging.Ln;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebSocketThread extends Thread {

    private final WebSocket webSocket;
    public static Lock lock = new ReentrantLock();
    private BackgroundSocketService.ProxyWebsocketAdapter proxyWebsocketAdapter;

    public WebSocketThread(WebSocket webSocket, BackgroundSocketService.ProxyWebsocketAdapter proxyWebsocketAdapter) {
        this.webSocket = webSocket;
        this.proxyWebsocketAdapter = proxyWebsocketAdapter;

    }

    public void run() {
        Ln.d("run");
        if (WebSocketThread.lock.tryLock()) {
            Ln.d("connect");
            try {
                webSocket.connect();
            } catch (WebSocketException e) {
                handleError(e);
            }
        }
    }

    private void handleError(WebSocketException cause) {
        WebSocketThread.lock.unlock();
        try {
            proxyWebsocketAdapter.onError(webSocket, cause);
            proxyWebsocketAdapter.onConnectError(webSocket, cause);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

}
