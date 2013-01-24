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
package se.inera.statistics.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;
import se.inera.statistics.model.entity.WorkCapability;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public class MedicalCertificateRepositoryTest {

	private static final String FEMALE = "Female";
	private static final String MALE = "Male";
	@Autowired 
	private MedicalCertificateRepository repo;
	@Autowired 
	private PersonRepository personRepository;
	@Autowired 
	private DiagnosisRepository diagnosisRepository;
	@Autowired 
	private DateRepository dateRepository;
	@Autowired 
	private CareUnitRepository careUnitRepository;
	
	private long startId;
	private long endId;
	
	@Before
	public void setUp() throws Exception {
		repo.deleteAll();
		personRepository.deleteAll();
		diagnosisRepository.deleteAll();
		careUnitRepository.deleteAll();
		DateUtil.createDates(dateRepository, "2010-01-01", "2013-01-01");
		
		PersonEntity person = PersonEntity.newEntity(18, MALE);
		personRepository.save(person);
		
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("C54", false, WorkCapability.HALF_WORKING_CAPABILITY, "II", "Tumörer");
		diagnosisRepository.save(diagnosis);
		
		CareUnitEntity careUnit = CareUnitEntity.newEntity("Gaminia");
		careUnitRepository.save(careUnit);
		
		final Date start = DateUtil.parse("2011-01-01");
		startId = lookupDate(start);
		endId = startId + 10;
	}

	private long lookupDate(Date date) {
		return dateRepository.findByCalendarDate(date).getId();
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testInsertFind() throws Exception {
		MedicalCertificateEntity ent = createEmptyCertificate();
		repo.save(ent);
		
		assertEquals(1, repo.count());
	}
	
	@Test
	public void testGetCorrectCountByDuration() throws Exception{
		createEmptyCertificates(10, startId, 10);		
		createEmptyCertificates(12, startId, 55);

		long between0And30 = repo.findCountByDuration(0, 30, MALE, startId, endId + 100);
		long between31And60 = repo.findCountByDuration(31, 60, MALE, startId, endId);

		assertEquals(10, between0And30);
		assertEquals(12, between31And60);
	}
	
	@Test
	public void testGetCorrectCountByAge() throws Exception{
		personRepository.save(PersonEntity.newEntity(35, FEMALE));
		PersonEntity person1 = personRepository.findByAgeAndGender(35, FEMALE);
		for (int i = 0; i < 10; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setPersonId(person1.getId());
			repo.save(ent);
		}
		
		personRepository.save(PersonEntity.newEntity(26, MALE));
		PersonEntity person2 = personRepository.findByAgeAndGender(26, MALE);
		for (int i = 0; i < 12; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setPersonId(person2.getId());
			repo.save(ent);
		}
		long result = repo.findCountBySearchAndAge(35, 39, FEMALE, startId, endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(10, result);
		assertEquals(12, repo.findCountBySearchAndAge(25, 29, MALE, startId, endId, Boolean.FALSE, Boolean.TRUE));
	}
		
	@Test
	public void testGetCorrectCountByMonth() throws Exception{
		createEmptyCertificates(10, startId, 10);
		
		final long result1 = repo.findCountByMonth(MALE, getMonthStart(startId));
		assertEquals(10, result1);

		long dateId = startId + 180;
		createEmptyCertificates(12, dateId, dateId + 10);

		final long result2 = repo.findCountByMonth(MALE, getMonthStart(dateId));
		assertEquals(12, result2);
	}

	@Test
	public void testGetCorrectCountByCareUnit() throws Exception{
		createEmptyCertificates(10, startId, 10);

		final long result1 = repo.findCountByCareUnit(MALE, careUnitRepository.findByName("Gaminia").getId(), startId, endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(10, result1);
	
		CareUnitEntity careUnit = CareUnitEntity.newEntity("care unit 2");
		careUnitRepository.save(careUnit);
		for (int i = 0; i < 12; i++){
			MedicalCertificateEntity ent = createEmptyCertificate(startId, 10);
			ent.setCareUnitId(careUnit.getId());
			repo.save(ent);
		}
		final long result2 = repo.findCountByCareUnit(MALE, careUnitRepository.findByName("care unit 2").getId(), startId, endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(12, result2);
	}

	private void createEmptyCertificates(int count, long start, long duration) {
		for (int i = 0; i < count; i++){
			MedicalCertificateEntity ent = createEmptyCertificate(start, duration);
			repo.save(ent);
		}
	}
	
	private Date getMonthStart(long id) {
		return dateRepository.findOne(id).getMonthStart();
	}
	
	
	private MedicalCertificateEntity createEmptyCertificate() {
		return createEmptyCertificate(startId, 10);
	}
	
	private MedicalCertificateEntity createEmptyCertificate(long from, long duration) {
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(from, from + duration);

		PersonEntity person = personRepository.findByAgeAndGender(18, MALE);
		assertNotNull("Encountered null person where we should not!", person);
		ent.setPersonId(person.getId());
		
		DiagnosisEntity diagnosis = diagnosisRepository.findByIcd10AndWorkCapability("C54", WorkCapability.HALF_WORKING_CAPABILITY);
		assertNotNull("Encountered null diagnosis where we should not!", diagnosis);
		
		CareUnitEntity careUnit = careUnitRepository.findByName("Gaminia");
		assertNotNull("Encountered null careUnit where we should not!", careUnit);
		
		assertNotNull(diagnosis.getId());
		assertNotNull(careUnit.getId());
		
		ent.setDiagnosisId(diagnosis.getId());
		ent.setCareUnitId(careUnit.getId());
		ent.setCareGiverId("careGiverId");
		ent.setIssuerId("issuerId");
		ent.setIssuerAge(50);
		ent.setIssuerGender(FEMALE);
		ent.setBasedOnExamination(Boolean.FALSE);
		ent.setBasedOnTelephoneContact(Boolean.TRUE);
		ent.setWorkDisability(100);
		return ent;
	}
}
