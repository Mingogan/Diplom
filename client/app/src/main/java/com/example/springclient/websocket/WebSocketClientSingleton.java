package com.example.springclient.websocket;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketClientSingleton {
    private static GenericWebSocketClient instance;

    public static synchronized GenericWebSocketClient getInstance(URI uri, String token) {
        if (instance == null) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            instance = new GenericWebSocketClient(uri, headers);
            instance.connect();
        }
        return instance;
    }

    public static GenericWebSocketClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WebSocketClient not initialized. Call getInstance(URI uri, String token) first.");
        }
        return instance;
    }
}