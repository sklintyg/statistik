package se.inera.statistics.core.repository;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.model.entity.MedicalCertificateEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public class MedicalCertificateRepositoryTest {

	@Autowired private MedicalCertificateRepository repo;
	
	@Test
	@Rollback(true)
	@Transactional
	public void testInsertFind() throws Exception {
		
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30)); 
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(18, false, start, end);
		this.repo.save(ent);
		
		assertEquals(1, this.repo.count());
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testMeasureThree() throws Exception {
		final List<MedicalCertificateEntity> ents = new ArrayList<MedicalCertificateEntity>();
		ents.add(this.createMeasureThreeDiagnose());
		ents.add(this.createMeasureThreeDiagnose());
		ents.add(this.createMeasureThreeDiagnose());
		ents.add(this.createMeasureThreeDiagnose());
		ents.add(this.createMeasureThreeDiagnose());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		
		this.repo.save(ents);
		
		final List<MedicalCertificateEntity> m3 = this.repo.loadMeasureThreeStatistics();
		assertEquals(5, m3.size());
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testMeasureNine() throws Exception {
		final List<MedicalCertificateEntity> ents = new ArrayList<MedicalCertificateEntity>();
		ents.add(this.createMeasureNineCertificate());
		ents.add(this.createMeasureNineCertificate());
		ents.add(this.createMeasureNineCertificate());
		ents.add(this.createMeasureNineCertificate());
		ents.add(this.createMeasureNineCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		
		this.repo.save(ents);
		
		final List<MedicalCertificateEntity> results = this.repo.loadMeasureNineStatistics();
		assertEquals(5, results.size());
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testMeasureTen() throws Exception {
		
		final List<MedicalCertificateEntity> ents = new ArrayList<MedicalCertificateEntity>();
		ents.add(this.createMeasureTenCertificate());
		ents.add(this.createMeasureTenCertificate());
		ents.add(this.createMeasureTenCertificate());
		ents.add(this.createMeasureTenCertificate());
		ents.add(this.createMeasureTenCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		ents.add(this.createEmptyCertificate());
		
		this.repo.save(ents);
		
		final List<MedicalCertificateEntity> results = this.repo.loadMeasureTenStatistics();
		assertEquals(5, results.size());
	}
	
	private MedicalCertificateEntity createMeasureThreeDiagnose() {
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30));
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(18, false, start, end);
		ent.setDiagnose(true);
		ent.setActualSicknessProcess(true);
		ent.setExaminationResults(true);
		
		return ent;
	}
	
	private MedicalCertificateEntity createMeasureNineCertificate() {
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30));
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(18, false, start, end);
		ent.setIcd10("icd-code-10");
		
		return ent;
	}
	
	private MedicalCertificateEntity createMeasureTenCertificate() {
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30));
		
		final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(18, false, start, end);
		ent.setBasedOnTelephoneContact(true);
		
		return ent;
	}
	
	private MedicalCertificateEntity createEmptyCertificate() {
		final Date start = new Date();
		final Date end = new Date(start.getTime() + (60000 * 24 * 30));
		
		return MedicalCertificateEntity.newEntity(18, false, start, end);
	}
}
