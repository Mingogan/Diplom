package com.example.springclient.websocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class GenericWebSocketClient extends WebSocketClient {

    private WebSocketListener listener;

    public GenericWebSocketClient(URI serverUri, Map<String, String> headers) {
        super(serverUri, headers);
    }

    public void setWebSocketListener(WebSocketListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected");
    }

    @Override
    public void onMessage(String message) {
        Log.d("WebSocket", "Message received: " + message);
        if (listener != null) {
            listener.onMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d("WebSocket", "An error occurred: " + ex.getMessage());
    }
}