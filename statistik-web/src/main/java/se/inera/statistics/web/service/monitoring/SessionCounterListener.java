/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service.monitoring;

import org.springframework.context.ApplicationContext;
import org.springframework.security.web.context.support.SecurityWebApplicationContextUtils;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionCounterListener implements HttpSessionListener {

    private static AtomicInteger totalActiveSessions = new AtomicInteger();

    ApplicationContext getContext(ServletContext servletContext) {
        return SecurityWebApplicationContextUtils.findRequiredWebApplicationContext(servletContext);
    }


    public static int getTotalActiveSession() {
        return totalActiveSessions.get();
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        totalActiveSessions.getAndIncrement();
    }

    /**
     * Decrements the AtomicInteger of this class that keeps track of the number of total active users.
     *
     * Please note that we re-propagate the HttpSessionEvent as a {@link org.springframework.security.web.session.HttpSessionDestroyedEvent}
     * so the {@link org.springframework.security.core.session.SessionRegistryImpl} can update its internal state.
     *
     * For more details about this, see javadoc of {@link org.springframework.security.web.session.HttpSessionEventPublisher}
     *
     * @param arg0
     *      The HttpSessionEvent notifying that a session has been destroyed.
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        totalActiveSessions.getAndDecrement();

        HttpSessionDestroyedEvent e = new HttpSessionDestroyedEvent(arg0.getSession());
        getContext(arg0.getSession().getServletContext()).publishEvent(e);
    }
}
