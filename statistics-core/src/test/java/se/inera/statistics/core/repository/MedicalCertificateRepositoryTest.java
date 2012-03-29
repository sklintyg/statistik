package se.inera.statistics.core.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public class MedicalCertificateRepositoryTest {

	@Autowired private MedicalCertificateRepository repo;
	
	@Test
	@Rollback(true)
	@Transactional
	public void testInsertFind() throws Exception {
		
		assert(true);
		
	}
}
