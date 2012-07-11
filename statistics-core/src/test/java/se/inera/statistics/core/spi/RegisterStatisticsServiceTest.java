package se.inera.statistics.core.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.repository.CareUnitRepository;
import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.core.repository.DiagnosisRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;
import se.inera.statistics.model.entity.WorkCapability;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-psql","test"}, inheritProfiles=true)
public class RegisterStatisticsServiceTest {
	@Autowired 
	private MedicalCertificateRepository certificateRepository;
	@Autowired 
	private PersonRepository personRepository;
	@Autowired 
	private DiagnosisRepository diagnosisRepository;
	@Autowired 
	private DateRepository dateRepository;
	@Autowired 
	private CareUnitRepository careUnitRepository;
	@Autowired 
	private RegisterStatisticsService registerService;
	
	@Before
	public void setUp(){
		certificateRepository.deleteAll();
		diagnosisRepository.deleteAll();
		personRepository.deleteAll();
	}
	
	@Test
	@Rollback(true)
	public void testRegisterStatisitcs() throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final long startDate = this.dateRepository.findByCalendarDate(sdf.parse("2010-01-01")).getId();
		final long endDate = this.dateRepository.findByCalendarDate(sdf.parse("2011-01-01")).getId();
		
		assertNotNull(startDate);
		assertNotNull(endDate);
		
		registerService.registerMedicalCertificateStatistics(generateCertificate());
		
		List<MedicalCertificateEntity> certificates= certificateRepository.findCertificatesInRange(startDate, endDate);
		MedicalCertificateEntity certificate = certificates.get(0);	
		PersonEntity person = personRepository.findOne(certificate.getPersonId());
		DiagnosisEntity diagnosis = diagnosisRepository.findOne(certificate.getDiagnosisId());
		CareUnitEntity careUnit = careUnitRepository.findOne(certificate.getCareUnitId());
	
		assertNotNull(certificate);
		assertNotNull(person);
		assertNotNull(diagnosis);
		
		assertEquals(18, person.getAge());
		assertEquals("Female", person.getGender());
		assertEquals("879444-22", diagnosis.getIcd10());
		assertEquals(true, diagnosis.isDiagnose());
		assertEquals(WorkCapability.THREE_QUARTER_WORKING_CAPABILITY, diagnosis.getWorkCapability());
		assertEquals("Torslanda", careUnit.getName());
		
		assertEquals(Boolean.TRUE, certificate.getBasedOnExamination());
		assertEquals(Boolean.FALSE, certificate.getBasedOnTelephoneContact());
	}
	
	private MedicalCertificateDto generateCertificate(){
		final String startDateString = new String("2010-01-01");
		final String endDateString = new String("2011-01-01");

		final MedicalCertificateDto cert = new MedicalCertificateDto();
		cert.setAge(18);
		cert.setFemale(true);
		cert.setStartDate(startDateString);
		cert.setEndDate(endDateString);
		cert.setDiagnose(true);
		cert.setIcd10("879444-22");
		cert.setworkDisability(25);
		cert.setCareUnit("Torslanda");
		
		cert.setBasedOnExamination(Boolean.TRUE);
		cert.setBasedOnTelephoneContact(Boolean.FALSE);
//			cert.setViewRange(StatisticsViewRange.MONTHLY.getCode());
		return cert;
	}
}