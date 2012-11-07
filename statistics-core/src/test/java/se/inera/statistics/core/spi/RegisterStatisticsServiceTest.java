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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	public void testRegisterStatisitcs() throws ParseException {
		final long startDate = lookupDate("2010-01-01");
		final long endDate = lookupDate("2011-01-01");
		
		assertNotNull(startDate);
		assertNotNull(endDate);
		
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
		assertEquals("C879422", diagnosis.getIcd10());
		assertEquals(true, diagnosis.isDiagnose());
		assertEquals(WorkCapability.THREE_QUARTER_WORKING_CAPABILITY, diagnosis.getWorkCapability());
		assertEquals("Torslanda", careUnit.getName());
		
		assertEquals(Boolean.TRUE, certificate.getBasedOnExamination());
		assertEquals(Boolean.FALSE, certificate.getBasedOnTelephoneContact());
	}

	private long lookupDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return this.dateRepository.findByCalendarDate(sdf.parse(date)).getId();
	}
	
	private MedicalCertificateDto generateCertificate(){
		final String endDateString = new String("2011-01-01");

		final MedicalCertificateDto cert = new MedicalCertificateDto();
		cert.setStartDate(new String("2010-01-01"));
		cert.setEndDate(endDateString);

		cert.setAge(18);
		cert.setFemale(true);
		cert.setBasedOnExamination(Boolean.TRUE);
		cert.setBasedOnTelephoneContact(Boolean.FALSE);
		cert.setDiagnose(true);
		cert.setIcd10("C879422");
		cert.setWorkDisability(25);
		cert.setCareUnit("Torslanda");
		cert.setCareGiver("careGiver");
		cert.setIssuer("issuer");
		cert.setIssuerAge(50);
		cert.setIssuerGender("F");
		return cert;
	}
}