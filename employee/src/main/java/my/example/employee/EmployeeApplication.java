package my.example.employee;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmployeeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(EmployeeApplication.class, args);
	}	
	
	 @Bean
	 public Exchange eventExchange() {
	   return new TopicExchange("eventExchange");
	 }

}
