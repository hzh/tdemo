package my.example.employee.service;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

	@Autowired 
	RabbitTemplate rabbitTmpl;

	@Autowired 
	Exchange exchange;

	public void employeeCreated(String employeeId) {
		sendMessage("employee.created", employeeId);
	}
	
	public void employeeUpdated(String employeeId) {
		sendMessage("employee.updated", employeeId);
	}
	
	public void employeeDeleted(String employeeId) {
		sendMessage("employee.deleted", employeeId);
	}
	
	private void sendMessage(String routingKey, String employeeId) {
		String message = String.format("%s;%d", employeeId, System.currentTimeMillis());
		rabbitTmpl.convertAndSend(exchange.getName(), routingKey, message);
	}
}
