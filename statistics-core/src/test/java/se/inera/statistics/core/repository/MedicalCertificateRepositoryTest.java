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
	
	@Before
	public void setUp(){
		this.repo.deleteAll();
		this.personRepository.deleteAll();
		this.diagnosisRepository.deleteAll();
		
		PersonEntity person = PersonEntity.newEntity(18, "Male");
		this.personRepository.save(person);
		
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("544334bg", false, WorkCapability.HALF_WORKING_CAPABILITY);
		this.diagnosisRepository.save(diagnosis);
		
		CareUnitEntity careUnit = CareUnitEntity.newEntity("Gaminia");
		this.careUnitRepository.save(careUnit);
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testInsertFind() throws Exception {
		MedicalCertificateEntity ent = createEmptyCertificate();
		this.repo.save(ent);
		
		assertEquals(1, this.repo.count());
	}
	
	private MedicalCertificateEntity createEmptyCertificate() {
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30));
		final long startId = this.dateRepository.findByCalendarDate(start).getId();
		final long endId = this.dateRepository.findByCalendarDate(end).getId();
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(startId, endId);
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
		return ent;
	}
}
