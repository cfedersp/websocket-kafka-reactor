package org.cjf.demo.kafkareactor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.cjf.demo.kafkareactor.websockets.KafkaEventsWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import lombok.AllArgsConstructor;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class SpringSSEConfigurations {
	
    private static final String TOPIC = "REACTOR-WEBSOCKETS-DEMO";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    // private static final String CLIENT_ID_CONFIG = "my-app-id";
    private static final String GROUP_ID_CONFIG = "websocket-kafka-reactor";

    @Bean
    public KafkaReceiver kafkaReceiver(){

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // We want all instances to receive all messages.
        //props.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG); 
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE, ReceiverOptions.create(props).subscription(Collections.singleton(TOPIC)));
    }
   
    /** 
     * For maximum confusion, Consumer Group refers to Client-ID.
     * from stack overflow:
     * If all consumer instances have the same consumer group, then this works just like a traditional queue balancing load over the consumers. 
     * If all consumer instances have different consumer groups, then this works like a publish-subscribe and all messages are broadcast to all the consumers
     */
	
	
}
