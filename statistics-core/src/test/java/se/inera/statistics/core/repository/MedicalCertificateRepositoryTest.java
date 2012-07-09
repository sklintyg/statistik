package se.inera.statistics.core.repository;

import static org.junit.Assert.assertEquals;
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

import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;

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
	
	@Before
	public void setUp(){
		this.repo.deleteAll();
		this.personRepository.deleteAll();
		
		PersonEntity person = PersonEntity.newEntity(18, "Male");
		this.personRepository.save(person);
		
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("544334bg", false);
		this.diagnosisRepository.save(diagnosis);
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

		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(start, end);
		PersonEntity person = this.personRepository.findByAgeAndGender(18, "Male");
		if (null == person){
			fail("Encountered null person where we should not!");
		}
		ent.setPersonId(person.getId());
		
		DiagnosisEntity diagnosis = this.diagnosisRepository.findByIcd10("544334bg");
		if (null == diagnosis){
			fail("Encountered null diagnosis where we should not!");
		}
		ent.setDiagnosisId(diagnosis.getId());
		
		return ent;
	}
}
