package se.inera.statistics.core.spi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.commons.support.ServiceResult;
import se.inera.statistics.core.api.MedicalCertificate;
import se.inera.statistics.core.api.StatisticsResult;
import se.inera.statistics.core.api.StatisticsViewRange;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.model.entity.MedicalCertificateEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public class StatisticsServiceTest {

	@Autowired private MedicalCertificateRepository repo;
	@Autowired private StatisticsService service;
	
	@Test
	@Rollback(true)
	public void testStatistics() throws Exception {
		this.setupTestData(10, 10, Calendar.MONTH);
		
		final MedicalCertificate cert = new MedicalCertificate();
		cert.setAge(18);
		cert.setFemale(true);
		cert.setStartDate("1990-01-01");
		cert.setEndDate("1991-01-01");
		cert.setViewRange(StatisticsViewRange.MONTHLY.getCode());
		
		final ServiceResult<StatisticsResult> result = this.service.loadBySearch(cert);
		assertNotNull(result);
		assertFalse(result.getData().getTotals().isEmpty());
		assertFalse(result.getData().getMatches().isEmpty());
	}
	
	private void setupTestData(final int numberOfPeriods, final int certificatesPerPeriod, final int period) {
		
		final List<MedicalCertificateEntity> certs = new ArrayList<MedicalCertificateEntity>();
		
		final Calendar cal = Calendar.getInstance();
		cal.set(1990, 0, 1, 0, 0, 0);
		
		
		for (int i = 0; i < numberOfPeriods; i++) {
			
			final Random r = new Random();
			for (int j = 0; j < certificatesPerPeriod; j++) {
				
				//int age = r.nextInt(60) + 10;
				//boolean female = r.nextBoolean();
				int day = r.nextInt(27) + 1;
				
				final Calendar start = Calendar.getInstance();
				start.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
				
				final Date d1 = start.getTime();
				start.roll(Calendar.DAY_OF_YEAR, true);
				
				final Date d2 = start.getTime();
				
				final MedicalCertificateEntity e = MedicalCertificateEntity.newEntity(18, true, d1, d2);
				e.setBasedOnExamination(r.nextBoolean());
				e.setBasedOnTelephoneContact(r.nextBoolean());
				
				certs.add(e);
			}
			
			cal.roll(period, true);
		}
		
		this.repo.save(certs);
	}
}
