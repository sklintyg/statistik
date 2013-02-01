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
package se.inera.statistics.web.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.core.repository.CareUnitRepository;
import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.core.repository.DiagnosisRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.core.repository.util.DateUtil;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DateEntity;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;
import se.inera.statistics.model.entity.WorkCapability;

/**
 * Generates example data (for testing and qa)
 *
 * @author Roger Lindsjo [roger.lindsjo@callistaenterprise.se]
 */
public class ExampleData {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleData.class);

    private static final String FEMALE = "Female";
    private static final String MALE = "Male";

    private static final int DISABILITY_100_PERCENT = 100;

    private static final String CARE_GIVER_ID = "careGiverId";
    private static final int ISSUER_AGE = 39;

    private static final int MAX_AGE = 80;
	private static final int MIN_AGE = 16;

    private static final int MAX_CERTIFICATE_LENGTH = 60;

    private static final int START_YEAR = 2012;
    private static final int NUMBER_OF_PERIODS = 24;
    private static final int CERTIFICATES_PER_PERIOD = 1100;

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

	private final Random r = new Random(1234);
	
	@PostConstruct
	public void generate() {
		LOG.info("==== INERA STATISTICS GENERATING SAMPLE DATA ====");
		DateUtil.createDates(dateRepository, "2010-01-01", "2014-12-31");
		setupInitialData(NUMBER_OF_PERIODS, CERTIFICATES_PER_PERIOD, Calendar.MONTH);
	}
	
	private void setupInitialData(final int numberOfPeriods, final int certificatesPerPeriod, final int period) {

		resetRepositories();
		
		final List<MedicalCertificateEntity> certs = new ArrayList<MedicalCertificateEntity>();
		
		final Calendar cal = Calendar.getInstance();
		cal.set(START_YEAR, 0, 1, 0, 0, 0);
		
		List<DiagnosisEntity> diagnosisList = createDiagnoses();		
		List<CareUnitEntity> careUnitList = createCareUnits();
		
		for (int i = 0; i < numberOfPeriods; i++) {
			for (int j = 0; j < certificatesPerPeriod; j++) {
				Calendar start = randomDate(cal, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				Calendar end = randomDate(start,  MAX_CERTIFICATE_LENGTH);

				PersonEntity person = createRandomPerson();
				
				final MedicalCertificateEntity e = MedicalCertificateEntity.newEntity(getDate(start), getDate(end));
				e.setPersonId(person.getId());
				e.setDiagnosisId(getRandom(diagnosisList).getId());
				e.setCareUnitId(getRandom(careUnitList).getId());
				e.setCareGiverId(CARE_GIVER_ID);
				e.setBasedOnExamination(false);
				e.setBasedOnTelephoneContact(false);
				e.setIssuerAge(ISSUER_AGE);
				e.setIssuerGender(FEMALE);
				e.setIssuerId("issuerId");
				e.setWorkDisability(DISABILITY_100_PERCENT);
				certs.add(e);
			}		
			cal.add(period, 1);
		}
		certificateRepository.save(certs);
	}

    private Calendar randomDate(final Calendar cal, int randomOffset) {
        Calendar newDate = (Calendar) cal.clone();
        newDate.add(Calendar.DATE, r.nextInt(randomOffset));
        return newDate;
    }

    private DateEntity getDate(Calendar start) {
        return dateRepository.findByCalendarDate(start.getTime());
    }

    private PersonEntity createRandomPerson() {
        int age = MIN_AGE + r.nextInt(MAX_AGE-MIN_AGE);
        String gender = r.nextBoolean() ? MALE : FEMALE;
        PersonEntity person = personRepository.findByAgeAndGender(age, gender);
        if (null == person){
        	person = PersonEntity.newEntity(age, gender);
        	personRepository.save(person);
        }
        return person;
    }

    private List<DiagnosisEntity> createDiagnoses() {
        List<DiagnosisEntity> diagnosisList = new ArrayList<DiagnosisEntity>();
		DiagnosisEntity diagnosis1 = DiagnosisEntity.newEntity("P00.5", false, WorkCapability.NO_WORKING_CAPABILITY, "XVI", "Vissa perinatala tillstånd");
		diagnosisRepository.save(diagnosis1);
		diagnosisList.add(diagnosis1);
		DiagnosisEntity diagnosis2 = DiagnosisEntity.newEntity("C54.4", false, WorkCapability.HALF_WORKING_CAPABILITY, "II", "Tumörer");
		this.diagnosisRepository.save(diagnosis2);
		diagnosisList.add(diagnosis2);
		DiagnosisEntity diagnosis3 = DiagnosisEntity.newEntity("L45.0", false, WorkCapability.FULL_WORKING_CAPABILITY, "XII", "Hudens och underhudens sjukdomar");
		this.diagnosisRepository.save(diagnosis3);
		diagnosisList.add(diagnosis3);
        return diagnosisList;
    }

    private List<CareUnitEntity> createCareUnits() {
        List<CareUnitEntity> careUnitList = new ArrayList<CareUnitEntity>();
		careUnitList.add(createAndSaveCareUnit("gårda"));
		careUnitList.add(createAndSaveCareUnit("Askim"));
        return careUnitList;
    }
	
	private <T> T getRandom(List<T> items) {
	    return items.get(r.nextInt(items.size()));
	}

    private void resetRepositories() {
        certificateRepository.deleteAll();
		personRepository.deleteAll();
		diagnosisRepository.deleteAll();
		careUnitRepository.deleteAll();
    }

    private CareUnitEntity createAndSaveCareUnit(String name) {
        CareUnitEntity careUnit = CareUnitEntity.newEntity(name);
		careUnitRepository.save(careUnit);
        return careUnit;
    }
}
