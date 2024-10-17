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

    @Mock
    private HsatkOrganizationService hsatkOrganizationService;

    @Mock
    private HsatkEmployeeService hsatkEmployeeService;

    @InjectMocks
    private HsaStatisticsServiceImpl hsaStatisticsService;

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