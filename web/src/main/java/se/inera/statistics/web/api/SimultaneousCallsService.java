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
package se.inera.statistics.web.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;

@Service
@Path("/simultaneous")
public class SimultaneousCallsService {

    @Value("${taskCoordinatorService.simultaneous.calls.allowed}")
    private int simultaneousCallsAllowed;

    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8")
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till mängden simultaneous calls en användare kan göra."
            + "Det används för att begränsa antalet samtidiga anrop från en enskild användare till "
            + "verksamhets- och regionsstatistik för att minimera risk för överbelastning av tjänsten.")
    public int getSimultaneousCallsAllowed() {
        return simultaneousCallsAllowed;
    }

}
