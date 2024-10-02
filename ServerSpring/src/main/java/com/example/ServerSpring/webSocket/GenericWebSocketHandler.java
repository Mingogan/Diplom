package com.example.ServerSpring.webSocket;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GenericWebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = new ArrayList<>();

    @Value("${jwt.secret}")
    private  String SECRET_KEY;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String authToken = getAuthToken(session);

        if (authToken == null || !validateToken(authToken)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
            System.out.println("Unauthorized WebSocket connection attempt: " + session.getId());
            return;
        }

        sessions.add(session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    public void broadcastMessage(String message) {
        try {
            for (WebSocketSession session : sessions) {
                session.sendMessage(new TextMessage(message));
                System.out.println("Sent message: " + message);
            }
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    private String getAuthToken(WebSocketSession session) {
        Map<String, List<String>> headers = session.getHandshakeHeaders();
        List<String> authorization = headers.get("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            return authorization.get(0).replace("Bearer ", "");
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims != null && claims.getSubject() != null;
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return false;
        }
    }
}