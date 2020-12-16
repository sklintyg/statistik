package se.inera.statistics.integration.hsa.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.ifv.statistics.spi.authorization.impl.HsaCommunicationException;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.services.HsatkEmployeeService;
import se.inera.intyg.infra.integration.hsatk.services.HsatkOrganizationService;
import se.inera.statistics.integration.hsa.model.GeoCoordDto;
import se.inera.statistics.integration.hsa.model.GeoCoordType;
import se.inera.statistics.integration.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsPersonResponseDto;
import se.inera.statistics.integration.hsa.model.StatisticsHsaUnitDto;
import se.inera.statistics.integration.hsa.model.StatisticsNameInfoDto;

@Service
public class HsaStatisticsServiceImpl implements HsaStatisticsService {

    @Autowired
    HsatkOrganizationService hsatkOrganizationService;

    @Autowired
    HsatkEmployeeService hsatkEmployeeService;

    @Override
    public GetStatisticsCareGiverResponseDto getStatisticsCareGiver(String careGiverId) {
        try {
            List<HealthCareProvider> healthCareProviders = hsatkOrganizationService.getHealthCareProvider(careGiverId, null);

            if (healthCareProviders.isEmpty()) {
                return new GetStatisticsCareGiverResponseDto();
            }
            return toStatisticsCareGiverDto(healthCareProviders.get(0));
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsCareGiver for " + careGiverId, ex);
        }
    }

    private GetStatisticsCareGiverResponseDto toStatisticsCareGiverDto(HealthCareProvider healthCareProvider) {
        if (healthCareProvider == null) {
            return null;
        }
        final GetStatisticsCareGiverResponseDto getStatisticsCareGiverResponseDto = new GetStatisticsCareGiverResponseDto();
        getStatisticsCareGiverResponseDto.setStartDate(healthCareProvider.getHealthCareProviderStartDate());
        getStatisticsCareGiverResponseDto.setArchived(healthCareProvider.getArchivedHealthCareProvider());
        getStatisticsCareGiverResponseDto.setCareGiverOrgNo(healthCareProvider.getHealthCareProviderOrgNo());
        getStatisticsCareGiverResponseDto.setEndDate(healthCareProvider.getHealthCareProviderEndDate());
        getStatisticsCareGiverResponseDto.setHsaIdentity(healthCareProvider.getHealthCareProviderHsaId());
        return getStatisticsCareGiverResponseDto;
    }

    @Override
    public GetStatisticsHsaUnitResponseDto getStatisticsHsaUnit(String unitId) {
        try {
            String profile = "";

            HealthCareUnit healthCareUnit = hsatkOrganizationService.getHealthCareUnit(unitId);

            Unit unit = hsatkOrganizationService.getUnit(unitId, profile);

            Unit careUnit = null;
            if (!healthCareUnit.getUnitIsHealthCareUnit()) {
                careUnit = hsatkOrganizationService.getUnit(healthCareUnit.getHealthCareUnitHsaId(), profile);
            }

            return toStatisticsHsaUnitResponseDto(healthCareUnit, unit, careUnit);
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsHsaUnit for " + unitId, ex);
        }
    }

    public static GetStatisticsHsaUnitResponseDto toStatisticsHsaUnitResponseDto(HealthCareUnit healthCareUnit, Unit unit,
        Unit careUnit) {
        if (healthCareUnit == null || unit == null) {
            return null;
        }
        final GetStatisticsHsaUnitResponseDto getStatisticsHsaUnitResponseDto = new GetStatisticsHsaUnitResponseDto();
        var statisticsUnit = toStatisticsUnitDto(healthCareUnit, unit);
        getStatisticsHsaUnitResponseDto.setStatisticsUnit(statisticsUnit);
        getStatisticsHsaUnitResponseDto
            .setStatisticsCareUnit(careUnit == null ? statisticsUnit : toStatisticsUnitDto(healthCareUnit, careUnit));
        return getStatisticsHsaUnitResponseDto;
    }

    private static StatisticsHsaUnitDto toStatisticsUnitDto(HealthCareUnit healthCareUnit, Unit unit) {
        if (healthCareUnit == null || unit == null) {
            return null;
        }
        final StatisticsHsaUnitDto statisticsHsaUnitDto = new StatisticsHsaUnitDto();
        statisticsHsaUnitDto.setArchived(healthCareUnit.getArchivedHealthCareUnit());
        if (unit.getBusinessClassification() != null) {
            statisticsHsaUnitDto
                .setBusinessClassificationCodes(
                    unit.getBusinessClassification().stream().map(Unit.BusinessClassification::getBusinessClassificationCode)
                        .collect(Collectors.toList()));
        }
        if (unit.getBusinessType() != null) {
            statisticsHsaUnitDto.setBusinessTypes(unit.getBusinessType());
        }
        statisticsHsaUnitDto.setCareGiverHsaIdentity(healthCareUnit.getHealthCareProviderHsaId());
        if (unit.getCareType() != null) {
            statisticsHsaUnitDto.setCareTypes(unit.getCareType());
        }
        statisticsHsaUnitDto.setCountyCode(unit.getCountyCode());
        statisticsHsaUnitDto.setEndDate(unit.getUnitEndDate());
        statisticsHsaUnitDto.setGeographicalCoordinatesRt90(toRt90Dto(unit.getGeographicalCoordinatesRt90()));
        statisticsHsaUnitDto.setHsaIdentity(unit.getUnitHsaId());
        statisticsHsaUnitDto.setLocation(unit.getLocation());
        if (unit.getManagement() != null) {
            statisticsHsaUnitDto.setManagements(unit.getManagement());
        }
        statisticsHsaUnitDto.setMunicipalityCode(unit.getMunicipalityCode());
        statisticsHsaUnitDto.setStartDate(unit.getUnitStartDate());
        //statisticsHsaUnitDto.setMunicipalitySectionName(statisticsCareUnit.getMunicipalitySectionName());
        //statisticsHsaUnitDto.setMunicipalitySectionCode(statisticsCareUnit.getMunicipalitySectionCode());
        return statisticsHsaUnitDto;
    }

    private static GeoCoordDto toRt90Dto(Unit.GeoCoordRt90 geographicalCoordinatesRt90) {
        if (geographicalCoordinatesRt90 == null) {
            return null;
        }
        final GeoCoordDto geoCoordDto = new GeoCoordDto();
        geoCoordDto.setType(GeoCoordType.fromValue("RT90"));
        geoCoordDto.setX(geographicalCoordinatesRt90.getXCoordinate());
        geoCoordDto.setY(geographicalCoordinatesRt90.getYCoordinate());
        return geoCoordDto;
    }

    public GetStatisticsPersonResponseDto getStatisticsPerson(String personId) {
        try {
            var employeeOptional = hsatkEmployeeService.getEmployee(personId, null, null).stream().findFirst();
            return employeeOptional.map(this::toStatisticsPersonDto).orElse(null);
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }

    private GetStatisticsPersonResponseDto toStatisticsPersonDto(PersonInformation statisticsPerson) {
        if (statisticsPerson == null) {
            return null;
        }
        final GetStatisticsPersonResponseDto getStatisticsPersonResponseDto = new GetStatisticsPersonResponseDto();
        getStatisticsPersonResponseDto.setHsaIdentity(statisticsPerson.getPersonHsaId());
        getStatisticsPersonResponseDto.setAge(statisticsPerson.getAge());
        getStatisticsPersonResponseDto.setGender(statisticsPerson.getGender());
        if (statisticsPerson.getHealthCareProfessionalLicence() != null) {
            getStatisticsPersonResponseDto.setHsaTitles(statisticsPerson.getHealthCareProfessionalLicence());
        }
        if (statisticsPerson.getPaTitle() != null) {
            getStatisticsPersonResponseDto.setPaTitleCodes(
                statisticsPerson.getPaTitle().stream().map(PersonInformation.PaTitle::getPaTitleCode).collect(Collectors.toList()));
        }
        getStatisticsPersonResponseDto.setProtectedPerson(statisticsPerson.getProtectedPerson());
        if (statisticsPerson.getSpecialityCode() != null) {
            getStatisticsPersonResponseDto.setSpecialityCodes(statisticsPerson.getSpecialityCode());
        }
        return getStatisticsPersonResponseDto;
    }

    @Override
    public GetStatisticsNamesResponseDto getStatisticsNames(String personId) {
        try {
            return toStatisticsNamesDto(hsatkEmployeeService.getEmployee(personId, null, null));
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }

    private GetStatisticsNamesResponseDto toStatisticsNamesDto(List<PersonInformation> statisticsNames) {
        if (statisticsNames == null) {
            return null;
        }
        final GetStatisticsNamesResponseDto getStatisticsNamesResponseDto = new GetStatisticsNamesResponseDto();
        final List<StatisticsNameInfoDto> statisticsNameInfo = statisticsNames.stream()
                .map(this::toStatisticsNamesInfoDto).collect(Collectors.toList());
        getStatisticsNamesResponseDto.setStatisticsNameInfos(statisticsNameInfo);
        return getStatisticsNamesResponseDto;
    }

    private StatisticsNameInfoDto toStatisticsNamesInfoDto(PersonInformation sni) {
        if (sni == null) {
            return null;
        }
        final StatisticsNameInfoDto statisticsNameInfoDto = new StatisticsNameInfoDto();
        statisticsNameInfoDto.setHsaIdentity(sni.getPersonHsaId());
        statisticsNameInfoDto.setPersonGivenName(sni.getGivenName());
        statisticsNameInfoDto.setPersonMiddleAndSurName(sni.getMiddleAndSurName());
        return statisticsNameInfoDto;
    }
}
