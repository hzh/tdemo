package my.example.employee.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import my.example.employee.model.Employee;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EmployeeServiceTest {

	@Autowired
	EmployeeService underTest;
	
	@MockBean
	RabbitTemplate mockRabbitTmpl;
	
	@Test
	public void events_should_be_published_after_successful_create_update_delete() {

		final Employee employee = new Employee();
		employee.setEmail(UUID.randomUUID().toString());
		
		// create new employee
		ResponseEntity<String> creationResponse = underTest.createEmployee(employee);
		assertTrue(creationResponse.getStatusCode().is2xxSuccessful());
		
		// location header: Location /employees/5c837c2619049400016d61ac
		final String employeeId = creationResponse.getHeaders().get("Location").get(0)
				.substring(11);
		
		// get employee by id - this will not produce event
		Employee createdEmployee = underTest.findById(employeeId);
		assertEquals(employee.getEmail(), createdEmployee.getEmail());
		
		// update employee by id
		Employee modifiedEmployee = new Employee();
		modifiedEmployee.setName("whatever");
		ResponseEntity<String> updateResponse = underTest.updateEmployee(employeeId, modifiedEmployee);
		assertTrue(updateResponse.getStatusCode().is2xxSuccessful());
		
		// update employee again
		Employee modifiedEmployee2 = new Employee();
		modifiedEmployee2.setName("doesnt matter");
		ResponseEntity<String> updateResponse2 = underTest.updateEmployee(employeeId, modifiedEmployee);
		assertTrue(updateResponse2.getStatusCode().is2xxSuccessful());
		
		// delete employee
		ResponseEntity<String> deleteResponse = underTest.deleteById(employeeId);
		assertTrue(deleteResponse.getStatusCode().is2xxSuccessful());
		
		// request history: create, get, update, update, delete;
		ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
		verify(mockRabbitTmpl, times(4)).convertAndSend(anyString(), routingKeyCaptor.capture(), anyString());
		
		List<String> events = routingKeyCaptor.getAllValues();
		// should have produced 4 events
		// and with the correct order
		assertEquals(Arrays.asList(
				"employee.created",
				"employee.updated",
				"employee.updated",
				"employee.deleted"),
				events);
	}
}
