package se.inera.statistics.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
@ActiveProfiles(profiles={"db-psql","test"}, inheritProfiles=true)
public class MedicalCertificateRepositoryTest {

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
	public void setUp(){
		this.repo.deleteAll();
		this.personRepository.deleteAll();
		this.diagnosisRepository.deleteAll();
		this.careUnitRepository.deleteAll();
		
		PersonEntity person = PersonEntity.newEntity(18, "Male");
		this.personRepository.save(person);
		
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("544334bg", false, WorkCapability.HALF_WORKING_CAPABILITY);
		this.diagnosisRepository.save(diagnosis);
		
		CareUnitEntity careUnit = CareUnitEntity.newEntity("Gaminia");
		this.careUnitRepository.save(careUnit);
		
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 60 * 24 * 10));
		this.startId = this.dateRepository.findByCalendarDate(start).getId();
		this.endId = this.dateRepository.findByCalendarDate(end).getId();
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testInsertFind() throws Exception {
		MedicalCertificateEntity ent = createEmptyCertificate();
		this.repo.save(ent);
		
		assertEquals(1, this.repo.count());
	}
	
	@Test
	public void testGetCorrectCountByDuration() throws Exception{
		for (int i = 0; i < 10; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			this.repo.save(ent);
		}
		
		for (int i = 0; i < 12; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setEndDate(this.startId + 55);
			this.repo.save(ent);
		}
		long result = this.repo.findCountByDuration(0, 30, "Male", this.startId, this.endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(10, result);
		assertEquals(12, this.repo.findCountByDuration(31, 60, "Male", this.startId, this.endId, Boolean.FALSE, Boolean.TRUE));
	}
	
	@Test
	public void testGetCorrectCountByAge() throws Exception{
		this.personRepository.save(PersonEntity.newEntity(35, "Female"));
		PersonEntity person1 = this.personRepository.findByAgeAndGender(35, "Female");
		for (int i = 0; i < 10; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setPersonId(person1.getId());
			this.repo.save(ent);
		}
		
		this.personRepository.save(PersonEntity.newEntity(26, "Male"));
		PersonEntity person2 = this.personRepository.findByAgeAndGender(26, "Male");
		for (int i = 0; i < 12; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setPersonId(person2.getId());
			this.repo.save(ent);
		}
		long result = this.repo.findCountBySearchAndAge(35, 39, "Female", this.startId, this.endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(10, result);
		assertEquals(12, this.repo.findCountBySearchAndAge(25, 29, "Male", this.startId, this.endId, Boolean.FALSE, Boolean.TRUE));
	}
		
	@Test
	public void testGetCorrectCountByMonth() throws Exception{
		for (int i = 0; i < 10; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			this.repo.save(ent);
		}
		final long result1 = this.repo.findCountByMonth("Male", this.dateRepository.findOne(this.startId).getMonthStart(), Boolean.FALSE, Boolean.TRUE);
		assertEquals(10, result1);

		final long dateId = this.startId + 180;
		for (int i = 0; i < 12; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setStartDate(dateId);
			this.repo.save(ent);
		}
		final long result2 = this.repo.findCountByMonth("Male", this.dateRepository.findOne(dateId).getMonthStart(), Boolean.FALSE, Boolean.TRUE);
		assertEquals(12, result2);
	}
		
	@Test
	public void testGetCorrectCountByCareUnit() throws Exception{
		for (int i = 0; i < 10; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			this.repo.save(ent);
		}
		final long result1 = this.repo.findCountByCareUnit(this.careUnitRepository.findByName("Gaminia").getId(), this.startId, this.endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(10, result1);
	
		CareUnitEntity careUnit = CareUnitEntity.newEntity("care unit 2");
		this.careUnitRepository.save(careUnit);
		for (int i = 0; i < 12; i++){
			MedicalCertificateEntity ent = createEmptyCertificate();
			ent.setCareUnitId(careUnit.getId());
			this.repo.save(ent);
		}
		final long result2 = this.repo.findCountByCareUnit(this.careUnitRepository.findByName("care unit 2").getId(), this.startId, this.endId, Boolean.FALSE, Boolean.TRUE);
		assertEquals(12, result2);
	}
	
	private MedicalCertificateEntity createEmptyCertificate() {
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(this.startId, this.endId);
		PersonEntity person = this.personRepository.findByAgeAndGender(18, "Male");
		if (null == person){
			fail("Encountered null person where we should not!");
		}
		ent.setPersonId(person.getId());
		
		DiagnosisEntity diagnosis = this.diagnosisRepository.findByIcd10AndWorkCapability("544334bg", WorkCapability.HALF_WORKING_CAPABILITY);
		if (null == diagnosis){
			fail("Encountered null diagnosis where we should not!");
		}
		
		CareUnitEntity careUnit = this.careUnitRepository.findByName("Gaminia");
		if (null == careUnit){
			fail("Encountered null careUnit where we should not!");
		}
		
		assertNotNull(diagnosis.getId());
		assertNotNull(careUnit.getId());
		
		ent.setDiagnosisId(diagnosis.getId());
		ent.setCareUnitId(careUnit.getId());
		ent.setBasedOnExamination(Boolean.FALSE);
		ent.setBasedOnTelephoneContact(Boolean.TRUE);
		return ent;
	}
}
