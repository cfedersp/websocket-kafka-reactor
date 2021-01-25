package org.cjf.demo.kafkareactor;

import java.util.HashMap;
import java.util.Map;

import org.cjf.demo.kafkareactor.websockets.KafkaEventsWebsocketHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@SpringBootApplication
public class KafkaReactorDemo {

	public static void main(String[] args) {
		SpringApplication.run(KafkaReactorDemo.class, args);
	}
	
	@Bean
    public HandlerMapping handlerMapping(KafkaEventsWebsocketHandler wsHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/events", wsHandler);
 
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }
 
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter(webSocketService());
    }
 
    @Bean
    public WebSocketService webSocketService() {
        return new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
    }
    
    @Bean
    public DefaultDataBufferFactory bufferFactory() {
    	return new DefaultDataBufferFactory();
    }
    
    /*
    @Bean
    public Flux<ReceiverRecord<String,String>> createEventsFlux(KafkaReceiver<String,String> kafkaReceiver) {
    	return kafkaReceiver.receive();
    }
    */
    
    
}
