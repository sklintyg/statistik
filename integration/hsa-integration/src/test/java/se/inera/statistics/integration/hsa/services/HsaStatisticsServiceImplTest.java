/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.statistics.integration.hsa.services;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.services.HsatkEmployeeService;
import se.inera.intyg.infra.integration.hsatk.services.HsatkOrganizationService;

@ExtendWith(MockitoExtension.class)
public class HsaStatisticsServiceImplTest {

  @Mock private HsatkOrganizationService hsatkOrganizationService;

  @Mock private HsatkEmployeeService hsatkEmployeeService;

  @InjectMocks private HsaStatisticsServiceImpl hsaStatisticsService;

  @Test
  public void shallGetHealthCareUnitIfCallingUnitHasUnitIsHealtCareUnitFalse() {
    final var unitId = "UNIT_ID";
    final var healthCareUnitHsaId = "HSA_ID";
    final var healthCareUnit = new HealthCareUnit();
    healthCareUnit.setHealthCareUnitHsaId(healthCareUnitHsaId);
    healthCareUnit.setUnitIsHealthCareUnit(false);
    final var mockedUnit = mock(Unit.class);

    doReturn(healthCareUnit).when(hsatkOrganizationService).getHealthCareUnit(unitId);
    doReturn(mockedUnit).when(hsatkOrganizationService).getUnit(unitId, "");

    hsaStatisticsService.getStatisticsHsaUnit(unitId);

    verify(hsatkOrganizationService).getHealthCareUnit(healthCareUnitHsaId);
  }

  @Test
  public void shallGetHealthCareUnitIfCallingUnitHasUnitIsHealtCareUnitNull() {
    final var unitId = "UNIT_ID";
    final var healthCareUnitHsaId = "HSA_ID";
    final var healthCareUnit = new HealthCareUnit();
    healthCareUnit.setHealthCareUnitHsaId(healthCareUnitHsaId);
    healthCareUnit.setUnitIsHealthCareUnit(null);
    final var mockedUnit = mock(Unit.class);

    doReturn(healthCareUnit).when(hsatkOrganizationService).getHealthCareUnit(unitId);
    doReturn(mockedUnit).when(hsatkOrganizationService).getUnit(unitId, "");

    hsaStatisticsService.getStatisticsHsaUnit(unitId);

    verify(hsatkOrganizationService).getHealthCareUnit(healthCareUnitHsaId);
  }
}
