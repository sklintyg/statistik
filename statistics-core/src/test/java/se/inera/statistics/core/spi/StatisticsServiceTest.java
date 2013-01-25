/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).
 *
 * Inera Statistics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Inera Statistics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.core.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Before;
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
import se.inera.statistics.core.repository.util.DateUtil;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;
import se.inera.statistics.model.entity.WorkCapability;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
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
	
	@Before
	public void setUp(){
		this.personRepository.deleteAll();
		this.diagnosisRepository.deleteAll();
		this.careUnitRepository.deleteAll();
		DateUtil.createDates(dateRepository, "2010-01-01", "2015-01-01");
		this.setupTestData(10, 10, Calendar.MONTH);
		
	}
	
	@Test
	@Rollback(true)
	public void testLoadAgeBySearch() throws Exception {
		final ServiceResult<StatisticsResult> result = this.service.loadByAge("Januari 2011", "December 2011", "all", "all");
		
		assertNotNull(result);
		//TODO: add more tests
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(7, result.getData().getMatches().size());
		assertEquals("20-29", result.getData().getMatches().get(1).getxValue());
		assertEquals(100, result.getData().getMatches().get(1).getyValue2());
		assertEquals(0, result.getData().getMatches().get(1).getyValue1());
	}
	
	@Test
	@Rollback(true)
	public void testLoadDurationBySearch() throws Exception {
		final ServiceResult<StatisticsResult> result = this.service.loadStatisticsByDuration("Januari 2011", "December 2011", "all", "all");
		
		assertNotNull(result);
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(5, result.getData().getMatches().size());
		assertEquals("15-30", result.getData().getMatches().get(1).getxValue());
		assertEquals(10, result.getData().getMatches().get(0).getyValue1());
		assertEquals(10, result.getData().getMatches().get(1).getyValue1());
		assertEquals(40, result.getData().getMatches().get(2).getyValue1());
		assertEquals(40, result.getData().getMatches().get(3).getyValue1());
	}
	
	@Test
	@Rollback(true)
	public void testLoadMonthsBySearch() throws Exception {
		final ServiceResult<StatisticsResult> result = this.service.loadStatisticsByMonth("Januari 2011", "December 2011", "all", "all");
		
		assertNotNull(result);
		
		assertEquals(12, result.getData().getMatches().size());
		assertEquals("februari", result.getData().getMatches().get(1).getxValue());
		for (int month=0; month<10; month++){
			assertEquals(10, result.getData().getMatches().get(month).getyValue1());
			assertEquals(0, result.getData().getMatches().get(month).getyValue2());
		}
		
		assertEquals(0, result.getData().getMatches().get(10).getyValue1());
		assertEquals(0, result.getData().getMatches().get(10).getyValue2());
		assertEquals(0, result.getData().getMatches().get(11).getyValue1());
		assertEquals(0, result.getData().getMatches().get(11).getyValue2());
	}
	
	@Test
	@Rollback(true)
	public void testLoadDiagnosisGroupsBySearch() throws Exception {
		final ServiceResult<StatisticsResult> result = service.loadStatisticsByDiagnosisGroups("Januari 2011", "December 2011", "all");
		
		assertNotNull(result);
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(3, result.getData().getMatches().size());
		
		
		assertEquals("I", result.getData().getMatches().get(0).getxValue());
		assertEquals("II", result.getData().getMatches().get(1).getxValue());
		assertEquals("XII", result.getData().getMatches().get(2).getxValue());
		
		assertThat(result.getData().getMatches().get(0).getyValue1(), not(equalTo(0L)));
		assertThat(result.getData().getMatches().get(1).getyValue1(), not(equalTo(0L)));
		assertThat(result.getData().getMatches().get(2).getyValue1(), not(equalTo(0L)));
	}
	
	@Test
	@Rollback(true)
	public void testLoadCareUnitBySearch() throws Exception {
		final MedicalCertificateDto search_parameters = getSearchParameters();
		final ServiceResult<StatisticsResult> result = this.service.loadStatisticsByCareUnit(search_parameters);
		
		assertNotNull(result);
		assertFalse(result.getData().getMatches().isEmpty());
		assertEquals(1, result.getData().getMatches().size());
		assertEquals("Gårda", result.getData().getMatches().get(0).getxValue());
		assertEquals(100, result.getData().getMatches().get(0).getyValue1());
	}
	
	private void setupTestData(final int numberOfPeriods, final int certificatesPerPeriod, final int period) {
		final List<MedicalCertificateEntity> certs = new ArrayList<MedicalCertificateEntity>();
		
		final Calendar cal = Calendar.getInstance();
		cal.set(2011, 0, 1, 0, 0, 0);
		
		PersonEntity person = PersonEntity.newEntity(28, "Male");
		this.personRepository.save(person);
		
		List<DiagnosisEntity> diagnosisList = new ArrayList<DiagnosisEntity>();
		DiagnosisEntity diagnosis1 = DiagnosisEntity.newEntity("A05442244", false, WorkCapability.NO_WORKING_CAPABILITY, "I", "Vissa infektionssjukdomar och parasitsjukdomar");
		this.diagnosisRepository.save(diagnosis1);
		diagnosisList.add(diagnosis1);
		DiagnosisEntity diagnosis2 = DiagnosisEntity.newEntity("C5442244", false, WorkCapability.HALF_WORKING_CAPABILITY, "II", "Tumörer");
		this.diagnosisRepository.save(diagnosis2);
		diagnosisList.add(diagnosis2);
		DiagnosisEntity diagnosis3 = DiagnosisEntity.newEntity("L45-9433", false, WorkCapability.FULL_WORKING_CAPABILITY, "XII", "Hudens och underhudens sjukdomar");
		this.diagnosisRepository.save(diagnosis3);
		diagnosisList.add(diagnosis3);
		
		CareUnitEntity careUnit = CareUnitEntity.newEntity("Gårda");
		this.careUnitRepository.save(careUnit);
		
		final Random r = new Random();
		for (int i = 0; i < numberOfPeriods; i++) {
			for (int j = 0; j < certificatesPerPeriod; j++) {
				int day = r.nextInt(27) + 1;
				
				final Calendar start = Calendar.getInstance();
				start.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
				
				final Date d1 = start.getTime();
				start.add(Calendar.DAY_OF_MONTH, 10 + (15 * i));
				final Date d2 = start.getTime();
				
				final long d1Id = this.dateRepository.findByCalendarDate(d1).getId();
				final long d2Id = this.dateRepository.findByCalendarDate(d2).getId();
				
				final MedicalCertificateEntity e = MedicalCertificateEntity.newEntity(d1Id, d2Id);
				e.setPersonId(person.getId());
				e.setDiagnosisId(diagnosisList.get(r.nextInt(3)).getId());
				e.setCareUnitId(careUnit.getId());
				e.setCareGiverId("careGiverId");
				e.setIssuerId("issuerId");
				e.setIssuerAge(50);
				e.setIssuerGender("Female");
				e.setBasedOnExamination(true);
				e.setBasedOnTelephoneContact(false);
				e.setWorkDisability(100);
				certs.add(e);
			}			
			cal.add(period, 1);
		}
		this.certificateRepository.save(certs);
	}

	private MedicalCertificateDto getSearchParameters() {
		final MedicalCertificateDto search_parameters = new MedicalCertificateDto();
		search_parameters.setStartDate("Januari 2011");
		search_parameters.setEndDate("December 2011");
		search_parameters.setBasedOnExamination(true);
		search_parameters.setBasedOnTelephoneContact(false);
		
		return search_parameters;
	}
}
