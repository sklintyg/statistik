/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).
 *
 * Inera Statistics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Inera Statistics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import se.inera.statistics.core.api.MedicalCertificateDto;

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
		
		final MedicalCertificateDto mc = new MedicalCertificateDto();
		mc.setAge(32);
		mc.setFemale(true);
		
		mc.setStartDate("2012-02-25 10:00");
		mc.setEndDate("2012-03-25 10:00");
		
		mc.setBasedOnExamination(Boolean.FALSE);
		mc.setBasedOnTelephoneContact(Boolean.TRUE);
		
		mc.setDiagnose(Boolean.FALSE);
		mc.setIcd10("B1044333");
		mc.setWorkDisability(25);
		
		mc.setCareUnit("ateegoog");
		
		final ResponseEntity<Boolean> responseEntity = rt.postForEntity(new URI(url), mc, Boolean.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
}
