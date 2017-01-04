/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.hsa;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsPersonResponseDto;

/**
 * This implementation will simply delegate all calls directly to HSA.
 */
public class HsaWebServiceDirect implements HsaWebService {

    @Autowired
    private HSAWebServiceCalls service;

    @Override
    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        service.setHsaLogicalAddress(hsaLogicalAddress);
    }

    @Override
    public void callPing() {
        service.callPing();
    }

    @Override
    public GetStatisticsHsaUnitResponseDto getStatisticsHsaUnit(String unitId) {
        return service.getStatisticsHsaUnit(unitId);
    }

    @Override
    public GetStatisticsNamesResponseDto getStatisticsNames(String personId) {
        return service.getStatisticsNames(personId);
    }

    @Override
    public GetStatisticsPersonResponseDto getStatisticsPerson(String personId) {
        return service.getStatisticsPerson(personId);
    }

    @Override
    public GetStatisticsCareGiverResponseDto getStatisticsCareGiver(String careGiverId) {
        return service.getStatisticsCareGiver(careGiverId);
    }

}
