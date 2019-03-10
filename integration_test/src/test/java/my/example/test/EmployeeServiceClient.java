package my.example.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class EmployeeServiceClient {
	private final String baseUrl;
	private final String username;
	private final String password;

	public EmployeeServiceClient(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	public String getDepartments() throws IOException {
		return getContentAsString(baseUrl + "/departments");
	}

	public String getEmployee(String employeeId) throws IOException {
		return getContentAsString(String.format("%s/%s/%s", baseUrl, "employees", employeeId));
	}
	
	public int getEmployeeStatus(String employeeId) throws IOException {
		return Request.Get(String.format("%s/%s/%s", baseUrl, "employees", employeeId))
				.addHeader("Authorization", basicAuth())
				.execute()
				.returnResponse()
				.getStatusLine()
				.getStatusCode();
	}
	
	public String createDepartment(String payload) throws IOException {
		HttpResponse resp = postJsonPayload(baseUrl + "/departments", payload);
		
		Header locationHeader = resp.getFirstHeader("Location");
		assertNotNull("Location header should not be null", locationHeader);
		
		return locationHeader.getValue().replaceFirst("/departments/", "");
	}

	public String createEmployee(final String employeeJson) throws IOException {
		HttpResponse resp = postJsonPayload(baseUrl + "/employees", employeeJson);
		
		Header locationHeader = resp.getFirstHeader("Location");
		assertNotNull("Location header should not be null", locationHeader);
		
		return locationHeader.getValue().replaceFirst("/employees/", "");
	}

	public void updateEmployee(String employeeId, String employeeJson) throws IOException {
		HttpResponse resp = Request.Put(String.format("%s/%s/%s", baseUrl, "employees", employeeId))
				.addHeader("Authorization", basicAuth())
				.bodyString(employeeJson, ContentType.APPLICATION_JSON)
				.execute()
				.returnResponse();
		assertEquals("Expecting OK 200", HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
	}

	public void deleteEmployee(String employeeId) throws IOException {
		HttpResponse resp = Request.Delete(String.format("%s/%s/%s", baseUrl, "employees", employeeId))
				.addHeader("Authorization", basicAuth())
				.execute()
				.returnResponse();
		assertEquals("Expecting 204 NO_CONTENT", HttpStatus.SC_NO_CONTENT, resp.getStatusLine().getStatusCode());
	}
	
	private String getContentAsString(final String url) throws IOException, ClientProtocolException {
		HttpResponse resp = Request.Get(url)
				.addHeader("Authorization", basicAuth())
				.execute()
				.returnResponse();
		
		assertEquals("Expecting OK 200", HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
		return EntityUtils.toString(resp.getEntity());
	}

	private HttpResponse postJsonPayload(final String url, final String payload) throws IOException {
		HttpResponse resp = Request.Post(url)
				.addHeader("Authorization", basicAuth())
				.bodyString(payload, ContentType.APPLICATION_JSON)
				.execute()
				.returnResponse();
		return resp;
	}
	
	private String basicAuth() {
		String auth = String.format("%s:%s", username, password);
		return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
	}



}
