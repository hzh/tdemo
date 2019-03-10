package my.example.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import my.example.employee.model.Employee;

@RestController
@RequestMapping(path = "employees", produces = "application/json")
public class EmployeeService {
	
	@Autowired
	EmployeeRepository employeeRepo;
	
	@Autowired
	EventProducer eventProducer;
	
	@GetMapping(path = "/{id}")
	public Employee findById(@PathVariable String id) {
		return employeeRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Cannot find employee with ID: " + id));
	}
	
	@PostMapping
	public ResponseEntity<String> createEmployee(@RequestBody Employee employee) {
		try {
			Employee saved = employeeRepo.insert(employee);
			eventProducer.employeeCreated(saved.getId());
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.header(HttpHeaders.LOCATION, "/employees/"+saved.getId())
					.build();
		} catch (DuplicateKeyException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Email address has already be used by another user " + employee.getEmail());
		}
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<String> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
		employee.setId(id);
		employeeRepo.save(employee);
		eventProducer.employeeUpdated(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<String> deleteById(@PathVariable String id) {
		employeeRepo.deleteById(id);
		eventProducer.employeeDeleted(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
