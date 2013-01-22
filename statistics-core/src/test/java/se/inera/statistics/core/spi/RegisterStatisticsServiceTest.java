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
package se.inera.statistics.core.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.repository.CareUnitRepository;
import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.core.repository.DateUtil;
import se.inera.statistics.core.repository.DiagnosisRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;
import se.inera.statistics.model.entity.WorkCapability;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public class RegisterStatisticsServiceTest {
	@Autowired 
	private MedicalCertificateRepository certificateRepository;
	@Autowired 
	private PersonRepository personRepository;
	@Autowired 
	private DiagnosisRepository diagnosisRepository;
	@Autowired 
	private DateRepository dateRepository;
	@Autowired 
	private CareUnitRepository careUnitRepository;
	@Autowired 
	private RegisterStatisticsService registerService;
	
	@Before
	public void setUp(){
		certificateRepository.deleteAll();
		diagnosisRepository.deleteAll();
		personRepository.deleteAll();
		careUnitRepository.deleteAll();
		DateUtil.createDates(dateRepository);
	}

	
	@Test
	@Rollback(true)
	public void lookupDate() throws ParseException {
        long dateId = lookupDate("2010-01-01");
        assertTrue(dateId > 0);
	}

	@Test
	@Rollback(true)
	public void testRegisterStatisitcs() {
		final long startDate = lookupDate("2010-01-01");
		final long endDate = lookupDate("2011-01-01");
		
		registerService.registerMedicalCertificateStatistics(generateCertificate());
		
		List<MedicalCertificateEntity> certificates= certificateRepository.findCertificatesInRange(startDate, endDate);
		MedicalCertificateEntity certificate = certificates.get(0);	
		PersonEntity person = personRepository.findOne(certificate.getPersonId());
		DiagnosisEntity diagnosis = diagnosisRepository.findOne(certificate.getDiagnosisId());
		CareUnitEntity careUnit = careUnitRepository.findOne(certificate.getCareUnitId());
	
		assertNotNull(certificate);
		assertNotNull(person);
		assertNotNull(diagnosis);
		assertNotNull(careUnit);
		
		assertEquals(18, person.getAge());
		assertEquals("Female", person.getGender());
		assertEquals("C879", diagnosis.getIcd10());
		assertTrue(diagnosis.isDiagnose());
		assertEquals(WorkCapability.THREE_QUARTER_WORKING_CAPABILITY, diagnosis.getWorkCapability());
		assertEquals("Torslanda", careUnit.getName());
		
		assertTrue(certificate.getBasedOnExamination());
		assertFalse(certificate.getBasedOnTelephoneContact());
	}

	private long lookupDate(String dateTest) {
	    try {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		Date date = sdf.parse(dateTest);
            return dateRepository.findByCalendarDate(date).getId();
	    } catch (ParseException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	private MedicalCertificateDto generateCertificate(){
		MedicalCertificateDto cert = new MedicalCertificateDto();
		
		cert.setStartDate("2010-01-01");
		cert.setEndDate("2011-01-01");

		cert.setAge(18);
		cert.setFemale(true);
		cert.setBasedOnExamination(Boolean.TRUE);
		cert.setBasedOnTelephoneContact(Boolean.FALSE);
		cert.setDiagnose(true);
		cert.setIcd10("C879");
		cert.setWorkDisability(25);
		cert.setCareUnit("Torslanda");
		cert.setCareGiver("careGiver");
		cert.setIssuer("issuer");
		cert.setIssuerAge(50);
		cert.setIssuerGender("F");
		return cert;
	}
}