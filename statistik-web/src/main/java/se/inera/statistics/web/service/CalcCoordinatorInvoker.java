/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import javax.ws.rs.core.Response;

import org.apache.cxf.common.util.ClassHelper;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.service.warehouse.query.CalcException;

public class CalcCoordinatorInvoker extends JAXRSInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(CalcCoordinatorInvoker.class);

    @Override
    public Object invoke(Exchange exchange, Object requestParams, Object resourceObject) {
        Class<?> realClass = ClassHelper.getRealClass(resourceObject);
        if (realClass != ProtectedChartDataService.class && realClass != ProtectedLandstingService.class) {
            return super.invoke(exchange, requestParams, resourceObject);
        }
        CalcCoordinator.Ticket ticket = null;
        try {
            ticket = CalcCoordinator.getTicket();
            return super.invoke(exchange, requestParams, resourceObject);
        } catch (CalcException c) {
            LOG.debug("calc exception", c);
            return new MessageContentsList(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
        } catch (Fault f) {
            if (f.getCause() instanceof AccessDeniedException) {
                throw (AccessDeniedException) f.getCause();
            }
            throw f;
        } finally {
            CalcCoordinator.returnTicket(ticket);
        }
    }

}
