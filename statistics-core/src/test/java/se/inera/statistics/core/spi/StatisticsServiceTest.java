package se.inera.statistics.core.spi;

import static org.junit.Assert.assertEquals;
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
import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.api.StatisticsResult;
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
public class StatisticsServiceTest {

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
	private StatisticsService service;
	
	@Test
	@Rollback(true)
	public void testLoadBySearch() throws Exception {
		this.setupTestData(10, 10, Calendar.MONTH);
		
		final MedicalCertificateDto cert = new MedicalCertificateDto();
		cert.setAge(18);
		cert.setStartDate("Januari 2011");
		cert.setEndDate("December 2011");
		cert.setDiagnose(true);
		cert.setBasedOnExamination(true);
		cert.setBasedOnTelephoneContact(false);
//		cert.setViewRange(StatisticsViewRange.MONTHLY.getCode());
		
		final ServiceResult<StatisticsResult> result = this.service.loadByAge(cert);
		assertNotNull(result);
		//TODO: add more tests
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(7, result.getData().getMatches().size());
		assertEquals("20-29", result.getData().getMatches().get(1).getxValue());
		assertEquals(100, result.getData().getMatches().get(1).getyValue1());
		assertEquals(0, result.getData().getMatches().get(1).getyValue2());
//		assertFalse(result.getData().getMatches().isEmpty());
	}
	
	@Test
	@Rollback(true)
	public void testLoadDurationBySearch() throws Exception {
		this.setupTestData(10, 10, Calendar.MONTH);
		
		final MedicalCertificateDto search_parameters = new MedicalCertificateDto();
		search_parameters.setAge(18);
		search_parameters.setStartDate("Januari 2011");
		search_parameters.setEndDate("December 2011");
		search_parameters.setDiagnose(true);
		search_parameters.setBasedOnExamination(true);
		search_parameters.setBasedOnTelephoneContact(false);
		
		final ServiceResult<StatisticsResult> result = this.service.loadStatisticsByDuration(search_parameters);
		assertNotNull(result);
		//TODO: add more tests
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(4, result.getData().getMatches().size());
		assertEquals("15-30", result.getData().getMatches().get(1).getxValue());
		assertEquals(10, result.getData().getMatches().get(0).getyValue1());
		assertEquals(10, result.getData().getMatches().get(1).getyValue1());
		assertEquals(40, result.getData().getMatches().get(2).getyValue1());
		assertEquals(40, result.getData().getMatches().get(3).getyValue1());
//		assertEquals(0, result.getData().getMatches().get(4).getyValue1());
//		assertEquals(0, result.getData().getMatches().get(1).getyValue2());
//		assertFalse(result.getData().getMatches().isEmpty());
	}
	
	@Test
	@Rollback(true)
	public void testLoadCareUnitBySearch() throws Exception {
		this.setupTestData(10, 10, Calendar.MONTH);
		
		final MedicalCertificateDto search_parameters = new MedicalCertificateDto();
		search_parameters.setStartDate("Januari 2011");
		search_parameters.setEndDate("December 2011");
//		search_parameters.setDiagnose(true);
		search_parameters.setBasedOnExamination(true);
		search_parameters.setBasedOnTelephoneContact(false);
		
		final ServiceResult<StatisticsResult> result = this.service.loadStatisticsByCareUnit(search_parameters);
		assertNotNull(result);
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(1, result.getData().getMatches().size());
		assertEquals("Gårda", result.getData().getMatches().get(0).getxValue());
		assertEquals(100, result.getData().getMatches().get(0).getyValue1());
//		assertFalse(result.getData().getMatches().isEmpty());
	}
	
	private void setupTestData(final int numberOfPeriods, final int certificatesPerPeriod, final int period) {
		
		final List<MedicalCertificateEntity> certs = new ArrayList<MedicalCertificateEntity>();
		
		final Calendar cal = Calendar.getInstance();
		cal.set(2011, 0, 1, 0, 0, 0);
		
		this.personRepository.deleteAll();
		this.diagnosisRepository.deleteAll();
		this.careUnitRepository.deleteAll();
		
		PersonEntity person = PersonEntity.newEntity(28, "Male");
		this.personRepository.save(person);
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("544334bg", false, WorkCapability.NO_WORKING_CAPABILITY);
		this.diagnosisRepository.save(diagnosis);
		CareUnitEntity careUnit = CareUnitEntity.newEntity("Gårda");
		this.careUnitRepository.save(careUnit);
		
		for (int i = 0; i < numberOfPeriods; i++) {
			
			final Random r = new Random();
			for (int j = 0; j < certificatesPerPeriod; j++) {
				
				//int age = r.nextInt(60) + 10;
				//boolean female = r.nextBoolean();
				int day = r.nextInt(27) + 1;
				
				final Calendar start = Calendar.getInstance();
				start.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
				
				final Date d1 = start.getTime();
//				start.roll(Calendar.DAY_OF_YEAR, true);
				start.add(Calendar.DAY_OF_MONTH, 10 + (15 * i));
				
				final Date d2 = start.getTime();
				
				final long d1Id = this.dateRepository.findByCalendarDate(d1).getId();
				final long d2Id = this.dateRepository.findByCalendarDate(d2).getId();
				
				final MedicalCertificateEntity e = MedicalCertificateEntity.newEntity(d1Id, d2Id);
				e.setPersonId(person.getId());
				e.setDiagnosisId(diagnosis.getId());
				e.setCareUnitId(careUnit.getId());
				e.setBasedOnExamination(true);
				e.setBasedOnTelephoneContact(false);

				certs.add(e);
			}
			
			cal.roll(period, true);
		}
		
		this.certificateRepository.save(certs);
	}
}
