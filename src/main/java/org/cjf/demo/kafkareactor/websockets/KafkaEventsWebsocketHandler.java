package org.cjf.demo.kafkareactor.websockets;

import org.apache.kafka.common.record.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaEventsWebsocketHandler implements WebSocketHandler {
	
    private final KafkaReceiver<String,String> kafkaReceiver;
	
	private final DefaultDataBufferFactory bufferFactory;
	
	private Flux<ReceiverRecord<String,String>> generalKafkaFlux = null;
	private Flux<String> unfilteredTextFlux = null;
	
	@PostConstruct
	public void createKafkaReceiver() {
		generalKafkaFlux = kafkaReceiver.receive().publish().autoConnect(1);
		unfilteredTextFlux = generalKafkaFlux.doOnNext(r -> r.receiverOffset().acknowledge())
				.map(ReceiverRecord::value);
	}
	
	@Override
    public Mono<Void> handle(WebSocketSession session) 
    {
		try {
			
			Map<String, String> queryParams = parseQuery(session.getHandshakeInfo().getUri().getQuery());
			String agentId = queryParams.get("agentId");
			
			// create a Reactor Publisher for this client:
			Flux<String> filteredTextFlux = unfilteredTextFlux;
			if(agentId != null) {
				filteredTextFlux = unfilteredTextFlux.filter((record) -> record.contains(agentId));
			}
			// We cant pass work-in-progress to inner classes, so mark the final flow as final:
			final Flux<String> composedTextFlux = unfilteredTextFlux;
			
			// traffic is handled with session.receive() and session.send()
			Mono<Void> wsSessionPublisher = session.send(
					filteredTextFlux
					.map(i -> new WebSocketMessage(WebSocketMessage.Type.TEXT, bufferFactory.wrap(ByteBuffer.wrap(i.getBytes(StandardCharsets.UTF_8)))))
	        ).doOnSubscribe((subscription) -> composedTextFlux.subscribe());
			
			
			return wsSessionPublisher;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

	private Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
		Map<String, String> queryParams = new HashMap<String, String>();
		if(query==null || query.isEmpty()) {
			return queryParams;
		}
		String[] params = query.split("&");
		for(String currParam : params) {
			int eqOffset = currParam.indexOf("=");
			if(eqOffset > 0 && eqOffset+1 < currParam.length()) {
				String key = URLDecoder.decode(currParam.substring(0, eqOffset), "UTF-8");
				String value = URLDecoder.decode(currParam.substring(eqOffset+1), "UTF-8");
				queryParams.put(key,  value);
			}
		}
		return queryParams;
	}
}
