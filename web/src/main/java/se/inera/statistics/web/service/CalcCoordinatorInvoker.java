/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.common.util.ClassHelper;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.service.warehouse.query.CalcException;
import se.inera.statistics.web.api.ProtectedChartDataService;
import se.inera.statistics.web.api.ProtectedRegionService;
import se.inera.statistics.web.task.TaskCoordinatorResponse;
import se.inera.statistics.web.task.TaskCoordinatorService;

public class CalcCoordinatorInvoker extends JAXRSInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(CalcCoordinatorInvoker.class);


    private final CalcCoordinator calcCoordinator;

    private final TaskCoordinatorService taskCoordinatorService;

    public CalcCoordinatorInvoker(CalcCoordinator calcCoordinator,
        @Qualifier(value = "taskCoordinatorServiceImpl") TaskCoordinatorService taskCoordinatorService) {
        this.calcCoordinator = calcCoordinator;
        this.taskCoordinatorService = taskCoordinatorService;
    }

    @Override
    public Object invoke(Exchange exchange, Object requestParams, Object resourceObject) {
        Class<?> realClass = ClassHelper.getRealClass(resourceObject);
        if (realClass != ProtectedChartDataService.class && realClass != ProtectedRegionService.class) {
            return super.invoke(exchange, requestParams, resourceObject);
        }
        if (taskCoordinatorService.request(requestParams).equals(TaskCoordinatorResponse.DECLINED)) {
            return new MessageContentsList(Response.status(Status.TOO_MANY_REQUESTS).build());
        }
        try {
            return calcCoordinator.submit(() -> super.invoke(exchange, requestParams, resourceObject));
        } catch (Fault f) {
            if (f.getCause() instanceof AccessDeniedException) {
                throw (AccessDeniedException) f.getCause();
            }
            throw f;
        } catch (Exception e) {
            if (e instanceof CalcException) {
                LOG.debug("Unable to submit", e);
            } else {
                LOG.error("Unexpected error", e);
            }
            return new MessageContentsList(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
        } finally {
            taskCoordinatorService.clearRequest(requestParams);
        }
    }
}
