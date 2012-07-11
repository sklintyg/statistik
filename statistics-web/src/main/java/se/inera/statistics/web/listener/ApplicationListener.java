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

import java.util.Calendar;
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
import se.inera.statistics.model.entity.DateEntity;
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
		
		final WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		final MedicalCertificateRepository repo = wc.getBean(MedicalCertificateRepository.class);
		final PersonRepository personRepository = wc.getBean(PersonRepository.class);
		final DiagnosisRepository diagnosisRepository = wc.getBean(DiagnosisRepository.class);
		final DateRepository dateRepository = wc.getBean(DateRepository.class);
		final CareUnitRepository careUnitRepository = wc.getBean(CareUnitRepository.class);
		repo.deleteAll();
		personRepository.deleteAll();
		diagnosisRepository.deleteAll();
		careUnitRepository.deleteAll();
		
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2012);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 15);
		
		DateEntity dateEntity = dateRepository.findByCalendarDate(c.getTime());
		if (null == dateEntity){
			throw new NullPointerException("Date entity is found to be null when it should not. Calendar value is " + c.getTime());
		}
		final long start = dateEntity.getId();
		
		c.roll(Calendar.MONTH, true);
		
		final long end = dateRepository.findByCalendarDate(c.getTime()).getId();
		
		//TODO: Are we just generating sample data ?
		final MedicalCertificateEntity entity = MedicalCertificateEntity.newEntity(start, end);
		final PersonEntity person = PersonEntity.newEntity(18, "Male");
		personRepository.save(person);
		final DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("544334bg", false, WorkCapability.fromWorkDisabilityPercentage(50));
		diagnosisRepository.save(diagnosis);
		final CareUnitEntity careUnit = CareUnitEntity.newEntity("Nyköping");
		careUnitRepository.save(careUnit);
		
		entity.setPersonId(person.getId());
		entity.setDiagnosisId(diagnosis.getId());
		entity.setCareUnitId(careUnit.getId());
		
		entity.setBasedOnExamination(Boolean.FALSE);
		entity.setBasedOnTelephoneContact(Boolean.TRUE);
		for (int i = 0; i < 100; i++) {
			log.debug("Creating certificate: " + (i+1));
			repo.save(entity);
		}
		
		log.info("==== INERA STATISTICS SERVICE STARTED ====");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		log.info("==== INERA STATISTICS SERVICE STOPPED ====");
	}
}
