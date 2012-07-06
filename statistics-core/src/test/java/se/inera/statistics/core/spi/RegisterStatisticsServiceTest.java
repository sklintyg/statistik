package se.inera.statistics.core.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.core.api.MedicalCertificate;
import se.inera.statistics.core.api.StatisticsViewRange;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-psql","test"}, inheritProfiles=true)
public class RegisterStatisticsServiceTest {
	@Autowired 
	private MedicalCertificateRepository certificateRepository;
	@Autowired 
	private PersonRepository personRepository;
	@Autowired 
	private RegisterStatisticsService registerService;
	
	@Test
	@Rollback(true)
	public void testregisterStatisitcs() throws Exception {
		final String startDateString = new String("1990-01-01");
		final String endDateString = new String("1991-01-01");
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Date startDate = sdf.parse(startDateString);
		final Date endDate = sdf.parse(endDateString);

		final MedicalCertificate cert = new MedicalCertificate();
		cert.setAge(18);
		cert.setFemale(true);
		cert.setStartDate(startDateString);
		cert.setEndDate(endDateString);
		cert.setViewRange(StatisticsViewRange.MONTHLY.getCode());
		registerService.registerMedicalCertificateStatistics(cert);
		
		List<MedicalCertificateEntity> certificates= certificateRepository.findCertificatesInRange(startDate, endDate);
		MedicalCertificateEntity certificate = certificates.get(0);	
		PersonEntity person = personRepository.findOne(certificate.getPersonId());
	
		assertNotNull(certificate);
		assertNotNull(person);
		assertEquals(18, person.getAge());
		assertEquals("Female", person.getGender());
	}
}