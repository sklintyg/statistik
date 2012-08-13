/**
 * Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
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
import java.util.Date;
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
import se.inera.statistics.model.entity.CareUnitEntity;
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
	
	private static final Logger log = LoggerFactory.getLogger(ExampleData.class);

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
	
	@PostConstruct
	private void generate() {
		log.info("==== INERA STATISTICS GENERATING SAMPLE DATA ====");
		setupInitialData(18, 100, Calendar.MONTH);
	}
	
	private void setupInitialData(final int numberOfPeriods, final int certificatesPerPeriod, final int period) {

		certificateRepository.deleteAll();
		personRepository.deleteAll();
		diagnosisRepository.deleteAll();
		careUnitRepository.deleteAll();
		
		final List<MedicalCertificateEntity> certs = new ArrayList<MedicalCertificateEntity>();
		
		final Calendar cal = Calendar.getInstance();
		cal.set(2012, 0, 1, 0, 0, 0);
		
		
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("544334bg", false, WorkCapability.NO_WORKING_CAPABILITY);
		diagnosisRepository.save(diagnosis);
		CareUnitEntity careUnit1 = CareUnitEntity.newEntity("Gårda");
		careUnitRepository.save(careUnit1);
		CareUnitEntity careUnit2 = CareUnitEntity.newEntity("Askim");
		careUnitRepository.save(careUnit2);
		
		for (int i = 0; i < numberOfPeriods; i++) {
			final Random r = new Random();
			for (int j = 0; j < certificatesPerPeriod; j++) {
				int day = r.nextInt(27) + 1;
				
				final Calendar start = Calendar.getInstance();
				start.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
				
				final Date d1 = start.getTime();
				start.add(Calendar.DAY_OF_YEAR, r.nextInt(130));
				
				final Date d2 = start.getTime();
				
				final long d1Id = dateRepository.findByCalendarDate(d1).getId();
				final long d2Id = dateRepository.findByCalendarDate(d2).getId();

				final int age = r.nextInt(80);
				final String gender = r.nextBoolean() ? "Male" : "Female";
				PersonEntity person = personRepository.findByAgeAndGender(age, gender);
				if (null == person){
					person = PersonEntity.newEntity(age, gender);
					personRepository.save(person);
				}
				
				final MedicalCertificateEntity e = MedicalCertificateEntity.newEntity(d1Id, d2Id);
				e.setPersonId(person.getId());
				e.setDiagnosisId(diagnosis.getId());
				e.setCareUnitId(r.nextBoolean() ? careUnit1.getId() : careUnit2.getId());
				e.setBasedOnExamination(false);
				e.setBasedOnTelephoneContact(false);

				certs.add(e);
			}		
			cal.add(period, 1);
		}
		certificateRepository.save(certs);
	}	
}
