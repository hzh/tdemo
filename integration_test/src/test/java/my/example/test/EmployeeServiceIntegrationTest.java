package my.example.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.junit.Test;

/**
 * Integration tests in docker compose environment
 */
public class EmployeeServiceIntegrationTest {
	// URLs are accessible within docker compose network
	// user name, passwords are configured in applications.properties or docker-compse.yml
	final String employeeServiceUrl="http://employee:8080";
	final String employeeServiceUser = "user";
	final String employeeServicePassword = "pass";

	final String eventServiceUrl="http://event:8080";
	final String eventServiceUser = "audit";
	final String eventServicePassword = "pass2";
	
	@Test
	public void testCreateDepartment() throws AuthenticationException, IOException {
		EmployeeServiceClient employeeClient = new EmployeeServiceClient(employeeServiceUrl, employeeServiceUser, employeeServicePassword);
		EventServiceClient eventClient = new EventServiceClient(eventServiceUrl, eventServiceUser, eventServicePassword);
		
		final String departmentName = UUID.randomUUID().toString();
		
		final String departmentId = employeeClient.createDepartment(departmentJson(departmentName));
		assertNotNull("New department id should not be null", departmentId);
		
		String departments = employeeClient.getDepartments();
		assertEquals("Department id should exist exactly once", 1, StringUtils.countMatches(departments, departmentId));
		
		final String email = UUID.randomUUID().toString();
		final String employeeId = employeeClient.createEmployee(employeeJson(email, departmentName, departmentId));
		
		String createdEmployee = employeeClient.getEmployee(employeeId);
		assertTrue(createdEmployee.contains(employeeId));
		assertTrue(createdEmployee.contains(email));
		assertTrue(createdEmployee.contains(departmentId));
		assertTrue(createdEmployee.contains(departmentName));
		
		String email2 = UUID.randomUUID().toString();
		employeeClient.updateEmployee(employeeId, employeeJson(email2 , departmentName, departmentId));
		
		String updatedEmployee = employeeClient.getEmployee(employeeId);
		assertTrue(updatedEmployee.contains(employeeId));
		assertTrue(updatedEmployee.contains(email2));
		assertTrue(updatedEmployee.contains(departmentId));
		assertTrue(updatedEmployee.contains(departmentName));
		
		employeeClient.deleteEmployee(employeeId);
		
		int statusCode = employeeClient.getEmployeeStatus(employeeId);
		assertEquals(404, statusCode);
		
		// due to the async nature, it is reliable to test it this way, just for demonstration
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// doesnt matter here
		}
		
		String events = eventClient.getEventsOfEmployee(employeeId);
		
		List<String> eventTypes = new ArrayList<>();
		Matcher matcher = Pattern.compile("employee\\.[a-z]+").matcher(events);
		while (matcher.find()) {
		   eventTypes.add(events.substring(matcher.start(),matcher.end()));
		}
		
		assertEquals(Arrays.asList("employee.created", "employee.updated", "employee.deleted"), eventTypes);
	}
	
	private String departmentJson(String name) {
		return String.format("{\"name\":\"%s\"}", name);
	}

	private String departmentJson(String name, String id) {
		return String.format("{\"name\":\"%s\", \"id\":\"%s\"}", name, id );
	}

	private String employeeJson(String email, String departmentName, String departmentId) {
		return String.format("{" + 
				"    \"email\": \"%s\"," + 
				"    \"name\": \"John smith\"," + 
				"    \"birthday\": \"1999-12-02\"," + 
				"    \"department\": %s " + 
				"}", email, departmentJson(departmentName, departmentId));
	}
}
