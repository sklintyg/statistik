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
package se.inera.statistics.web.handler;

import java.util.HashMap;
import java.util.Map;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.springframework.security.access.AccessDeniedException;

@Provider
public final class AccessDeniedExceptionHandler implements
    ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(final AccessDeniedException exception) {

        Map<String, Object> mappedResult = new HashMap<>();
        mappedResult.put("error", exception.getMessage());

        return Response.status(Response.Status.FORBIDDEN)
            .entity(mappedResult).build();
    }

}