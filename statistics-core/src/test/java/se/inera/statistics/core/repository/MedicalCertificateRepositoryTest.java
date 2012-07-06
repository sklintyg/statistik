package se.inera.statistics.core.repository;

import static org.junit.Assert.assertEquals;

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
	
	@Before
	public void setUp(){
		this.repo.deleteAll();
		this.personRepository.deleteAll();
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testInsertFind() throws Exception {
		MedicalCertificateEntity ent = createEmptyCertificate();
		this.repo.save(ent);
		
		assertEquals(1, this.repo.count());
	}
	
//	@Test
//	public void test
//	
	
//	@Test
//	@Rollback(true)
//	@Transactional
//	public void testMeasureThree() throws Exception {
//		final List<MedicalCertificateEntity> ents = new ArrayList<MedicalCertificateEntity>();
//		ents.add(this.createMeasureThreeDiagnose());
//		ents.add(this.createMeasureThreeDiagnose());
//		ents.add(this.createMeasureThreeDiagnose());
//		ents.add(this.createMeasureThreeDiagnose());
//		ents.add(this.createMeasureThreeDiagnose());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		
//		this.repo.save(ents);
//		
//		final List<MedicalCertificateEntity> m3 = this.repo.loadMeasureThreeStatistics();
//		assertEquals(5, m3.size());
//	}
//	
//	@Test
//	@Rollback(true)
//	@Transactional
//	public void testMeasureNine() throws Exception {
//		final List<MedicalCertificateEntity> ents = new ArrayList<MedicalCertificateEntity>();
//		ents.add(this.createMeasureNineCertificate());
//		ents.add(this.createMeasureNineCertificate());
//		ents.add(this.createMeasureNineCertificate());
//		ents.add(this.createMeasureNineCertificate());
//		ents.add(this.createMeasureNineCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		
//		this.repo.save(ents);
//		
//		final List<MedicalCertificateEntity> results = this.repo.loadMeasureNineStatistics();
//		assertEquals(5, results.size());
//	}
//	
//	@Test
//	@Rollback(true)
//	@Transactional
//	public void testMeasureTen() throws Exception {
//		
//		final List<MedicalCertificateEntity> ents = new ArrayList<MedicalCertificateEntity>();
//		ents.add(this.createMeasureTenCertificate());
//		ents.add(this.createMeasureTenCertificate());
//		ents.add(this.createMeasureTenCertificate());
//		ents.add(this.createMeasureTenCertificate());
//		ents.add(this.createMeasureTenCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		ents.add(this.createEmptyCertificate());
//		
//		this.repo.save(ents);
//		
//		final List<MedicalCertificateEntity> results = this.repo.loadMeasureTenStatistics();
//		assertEquals(5, results.size());
//	}
//	
//	private MedicalCertificateEntity createMeasureThreeDiagnose() {
//		final MedicalCertificateEntity ent = createEmptyCertificate();
//		ent.setDiagnose(true);
//		ent.setActualSicknessProcess(true);
//		ent.setExaminationResults(true);
//		
//		return ent;
//	}
//	
//	private MedicalCertificateEntity createMeasureNineCertificate() {
//		final MedicalCertificateEntity ent = createEmptyCertificate();
//		ent.setIcd10("icd-code-10");
//		
//		return ent;
//	}
//	
//	private MedicalCertificateEntity createMeasureTenCertificate() {
//		final MedicalCertificateEntity ent = createEmptyCertificate();
//		ent.setBasedOnTelephoneContact(true);
//		
//		return ent;
//	}
//	
	private MedicalCertificateEntity createEmptyCertificate() {
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30));

		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(start, end);
		PersonEntity person = this.personRepository.findByAgeAndGender(18, "Male");
		if (null == person){
			person = PersonEntity.newEntity(18, "Male");
			this.personRepository.save(person);
		}
		ent.setPersonId(person.getId());
		return ent;
	}
}
