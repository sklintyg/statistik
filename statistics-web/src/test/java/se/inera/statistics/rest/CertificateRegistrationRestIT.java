package se.inera.statistics.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import se.inera.statistics.core.api.MedicalCertificate;

public class CertificateRegistrationRestIT {

	@Test
	public void testMedicalCertificateRegistration() throws Exception {
		
		final String url = "http://localhost:8080/statistics-web/api/certificate/medical/register";
		
		final DefaultHttpClient client = new DefaultHttpClient();
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				final AuthState state = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
				state.setAuthScheme(new BasicScheme());
				state.setCredentials(new UsernamePasswordCredentials("api-user", "password"));
			}
		}, 0);
		
		final HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory(client);
		final RestTemplate rt = new RestTemplate(rf);
		
		final MedicalCertificate mc = new MedicalCertificate();
		mc.setAge(18);
		mc.setFemale(false);
		mc.setStartDate("2012-02-25 10:00");
		mc.setEndDate("2012-03-25 10:00");
		
		final ResponseEntity<Boolean> responseEntity = rt.postForEntity(new URI(url), mc, Boolean.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
}
