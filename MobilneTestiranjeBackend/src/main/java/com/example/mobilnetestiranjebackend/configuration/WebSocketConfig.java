package com.example.mobilnetestiranjebackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/socket-subscriber") // ovo su rute za endpointe valjda
                .enableSimpleBroker("/jwt-refresh"); //ovde dodajem vise ruta za pretplacivanje
    }


}
