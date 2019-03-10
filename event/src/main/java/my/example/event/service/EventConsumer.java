package my.example.event.service;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import my.example.event.model.Event;

@Component
public class EventConsumer {
	@Autowired
	MongoTemplate mongoTemplate;
	
	@RabbitListener(queues="employeeEventQueue")
	public void receiveMessage(Message message) {
		mongoTemplate.insert(parseMessage(message));
	}

	private Event parseMessage(Message message) {
		try {
			String routingKey = message.getMessageProperties().getReceivedRoutingKey();
			String body = new String(message.getBody(), "UTF-8");
			String[] parts = body.split(";");
			
			Event event = new Event();
			event.setType(routingKey);
			event.setEmployeeId(parts[0]);
			event.setTimestamp(Long.parseLong(parts[1]));
			
			return event;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("invalid message body");
		}
	}
}
