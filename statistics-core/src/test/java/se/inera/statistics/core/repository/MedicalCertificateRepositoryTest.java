package se.inera.statistics.core.repository;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

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
}
