/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
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
import se.inera.statistics.web.api.ProtectedChartDataService;
import se.inera.statistics.web.api.ProtectedRegionService;
import se.inera.statistics.web.service.task.TaskCoordinatorResponse;
import se.inera.statistics.web.service.task.TaskCoordinatorService;

public class CalcCoordinatorInvoker extends JAXRSInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(CalcCoordinatorInvoker.class);

    private final LoginServiceUtil loginServiceUtil;
    private final CalcCoordinator calcCoordinator;
    private final TaskCoordinatorService taskCoordinatorService;

    public CalcCoordinatorInvoker(LoginServiceUtil loginServiceUtil, CalcCoordinator calcCoordinator,
        TaskCoordinatorService taskCoordinatorService) {
        this.loginServiceUtil = loginServiceUtil;
        this.calcCoordinator = calcCoordinator;
        this.taskCoordinatorService = taskCoordinatorService;
    }

    @Override
    public Object invoke(Exchange exchange, Object requestParams, Object resourceObject) {
        Class<?> realClass = ClassHelper.getRealClass(resourceObject);
        if (realClass != ProtectedChartDataService.class && realClass != ProtectedRegionService.class) {
            return super.invoke(exchange, requestParams, resourceObject);
        }
        final var userHsaId = loginServiceUtil.getCurrentUser().getHsaId().getId();
        if (taskCoordinatorService.request(userHsaId).equals(TaskCoordinatorResponse.DECLINE)) {
            return new MessageContentsList(Response.status(Status.TOO_MANY_REQUESTS).build());
        }
        final var startTime = System.currentTimeMillis();
        final var requestEndpoint = getRequestEndpoint(requestParams);
        LOG.info("Request started for {}. Request endpoint: {}", userHsaId, requestEndpoint);
        try {
            return calcCoordinator.submit(() -> super.invoke(exchange, requestParams, resourceObject), userHsaId, requestEndpoint);
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
            LOG.info("Request completed for {}. Total time taken for request to finish: {} seconds. Request endpoint: {}",
                userHsaId,
                timeElapsed(startTime),
                requestEndpoint);
            taskCoordinatorService.clearRequest(userHsaId);
        }
    }

    private static String getRequestEndpoint(Object requestParams) {
        if (requestParams instanceof List) {
            final var requestParamsList = (List) requestParams;
            if (requestParamsList.get(0) instanceof HttpServletRequest) {
                final var httpServlet = (HttpServletRequest) requestParamsList.get(0);
                return httpServlet.getHttpServletMapping().getMatchValue();
            }
        }
        return null;
    }

    private long timeElapsed(long startTime) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
    }
}