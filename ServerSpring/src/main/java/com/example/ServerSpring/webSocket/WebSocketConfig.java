package com.example.ServerSpring.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(genericWebSocketHandler(), "/ws").setAllowedOrigins("*");
    }

    @Bean
    public GenericWebSocketHandler genericWebSocketHandler() {
        return new GenericWebSocketHandler();
    }
}
