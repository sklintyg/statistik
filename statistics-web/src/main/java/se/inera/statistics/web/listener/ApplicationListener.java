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
package se.inera.statistics.web.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
 * Called when Spring initialize, destroys, or refreshes the application
 * context.
 *
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public class ApplicationListener extends ContextLoaderListener {
	
	private static final Logger log = LoggerFactory.getLogger(ApplicationListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		this.setupInitialData(event, 10, 100, Calendar.MONTH);
		
		log.info("==== INERA STATISTICS SERVICE STARTED ====");
	}
	
	private void setupInitialData(ServletContextEvent event, final int numberOfPeriods, final int certificatesPerPeriod, final int period) {
		final WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		final MedicalCertificateRepository certificateRepository = wc.getBean(MedicalCertificateRepository.class);
		final PersonRepository personRepository = wc.getBean(PersonRepository.class);
		final DiagnosisRepository diagnosisRepository = wc.getBean(DiagnosisRepository.class);
		final DateRepository dateRepository = wc.getBean(DateRepository.class);
		final CareUnitRepository careUnitRepository = wc.getBean(CareUnitRepository.class);
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
				
				//int age = r.nextInt(60) + 10;
				//boolean female = r.nextBoolean();
				int day = r.nextInt(27) + 1;
				
				final Calendar start = Calendar.getInstance();
				start.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
				
				final Date d1 = start.getTime();
//				start.roll(Calendar.DAY_OF_YEAR, true);
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

//				e.setBasedOnExamination(r.nextBoolean());
//				e.setBasedOnTelephoneContact(r.nextBoolean());
//				
				certs.add(e);
			}
			
			cal.roll(period, true);
		}
		
		certificateRepository.save(certs);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		log.info("==== INERA STATISTICS SERVICE STOPPED ====");
	}
}
