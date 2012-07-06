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
import java.util.Date;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;

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

		
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2012);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 15);
		
		final Date start = c.getTime();
		
		c.roll(Calendar.MONTH, true);
		
		final Date end = c.getTime();
		
		//TODO: Are we just generating sample data ?
		final MedicalCertificateEntity entity = MedicalCertificateEntity.newEntity( start, end);
		final PersonEntity person = PersonEntity.newEntity(18, "Male");
		personRepository.save(person);
		entity.setPersonId(person.getId());
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
