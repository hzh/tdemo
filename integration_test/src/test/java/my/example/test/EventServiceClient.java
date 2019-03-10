package my.example.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

public class EventServiceClient {
	private final String baseUrl;
	private final String username;
	private final String password;

	public EventServiceClient(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	public String getEventsOfEmployee(String employeeId) throws IOException {
		return getContentAsString(String.format("%s/%s?employeeId=%s", baseUrl, "events", employeeId));
	}

	private String getContentAsString(final String url) throws IOException, ClientProtocolException {
		HttpResponse resp = Request.Get(url)
				.addHeader("Authorization", basicAuth())
				.execute()
				.returnResponse();
		
		assertEquals("Expecting OK 200", HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
		return EntityUtils.toString(resp.getEntity());
	}
	
	private String basicAuth() {
		String auth = String.format("%s:%s", username, password);
		return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
	}

}
