package my.example.event.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.example.event.model.Event;

@RestController
@RequestMapping(path = "events", produces = "application/json")
public class EventService {
	@Autowired
	MongoTemplate mongoTemplate;
	
	@GetMapping
	public List<Event> findAllByEmployeeId(@RequestParam(required = false) String employeeId) {
		Query query = new Query();
		if(employeeId != null) {
			query.addCriteria(Criteria.where("employeeId").is(employeeId));
		}
		return mongoTemplate
				.find(query, Event.class)
				.stream()
				.sorted(Comparator.comparing(Event::getTimestamp))
				.collect(Collectors.toList());
	}
	
}
