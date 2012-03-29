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

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

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
		log.info("==== INERA STATISTICS SERVICE STARTED ====");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		log.info("==== INERA STATISTICS SERVICE STOPPED ====");
	}
}
