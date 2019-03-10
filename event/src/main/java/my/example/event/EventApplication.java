package my.example.event;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventApplication.class, args);
	}

	@Bean
	public Exchange eventExchange() {
		return new TopicExchange("eventExchange");
	}

	@Bean
	public Queue queue() {
		return new Queue("employeeEventQueue");
	}

	@Bean
	public Binding binding(Queue queue, Exchange eventExchange) {
		return BindingBuilder.bind(queue).to(eventExchange).with("employee.*").noargs();
	}
}
