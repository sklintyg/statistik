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
package se.inera.ifv.statistics.spi.authorization.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3.wsaddressing10.AttributedURIType;
import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.GeoCoord;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonType;
import se.inera.ifv.hsawsresponder.v3.HsaWsFaultType;
import se.inera.ifv.hsawsresponder.v3.PingResponseType;
import se.inera.ifv.hsawsresponder.v3.PingType;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit;
import se.inera.ifv.hsawsresponder.v3.StatisticsNameInfo;
import se.inera.statistics.hsa.model.GeoCoordDto;
import se.inera.statistics.hsa.model.GeoCoordType;
import se.inera.statistics.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsPersonResponseDto;
import se.inera.statistics.hsa.model.StatisticsHsaUnitDto;
import se.inera.statistics.hsa.model.StatisticsNameInfoDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class interfaces with the "old" Web Service-based HSA, e.g. NOT over NTjP. It is subject to be replaced by
 * NTjP-based HSA integration (INTYG-2226).
 */
public class HSAWebServiceCalls {

    @Autowired
    private HsaWsResponderInterface serverInterface;

    private static final Logger LOG = LoggerFactory.getLogger(HSAWebServiceCalls.class);

    private AttributedURIType logicalAddressHeader = new AttributedURIType();

    private AttributedURIType messageId = new AttributedURIType();

    /**
     * @param hsaLogicalAddress
     *            the hsaLogicalAddress to set
     */
    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        logicalAddressHeader.setValue(hsaLogicalAddress);
    }

    /**
     * Help method to test access to HSA.
     */
    public void callPing() {
        try {
            PingType pingtype = new PingType();
            PingResponseType response = serverInterface.ping(logicalAddressHeader, messageId, pingtype);
            LOG.debug("Response:" + response.getMessage());

        } catch (Exception ex) {
            LOG.warn("Exception={}", ex.getMessage(), ex);
            throw new HsaCommunicationException("Could not call ping", ex);
        }
    }

    public GetStatisticsCareGiverResponseDto getStatisticsCareGiver(String careGiverId) {
        try {
            GetStatisticsCareGiverType parameters = new GetStatisticsCareGiverType();
            parameters.setHsaIdentity(careGiverId);
            parameters.setSearchBase("c=SE");
            return toDto(serverInterface.getStatisticsCareGiver(logicalAddressHeader, messageId, parameters));
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsCareGiver for {} hsaWsFault ({}, {}). {}", careGiverId, faultInfo.getCode(),
                    faultInfo.getMessage(), hsaWsFault.getMessage());
            LOG.debug("getStatisticsCareGiver fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsCareGiver for " + careGiverId, ex);
        }
    }

    private GetStatisticsCareGiverResponseDto toDto(GetStatisticsCareGiverResponseType statisticsCareGiver) {
        if (statisticsCareGiver == null) {
            return null;
        }
        final GetStatisticsCareGiverResponseDto getStatisticsCareGiverResponseDto = new GetStatisticsCareGiverResponseDto();
        getStatisticsCareGiverResponseDto.setStartDate(statisticsCareGiver.getStartDate());
        getStatisticsCareGiverResponseDto.setArchived(statisticsCareGiver.isIsArchived());
        getStatisticsCareGiverResponseDto.setCareGiverOrgNo(statisticsCareGiver.getCareGiverOrgNo());
        getStatisticsCareGiverResponseDto.setEndDate(statisticsCareGiver.getEndDate());
        getStatisticsCareGiverResponseDto.setHsaIdentity(statisticsCareGiver.getHsaIdentity());
        return getStatisticsCareGiverResponseDto;
    }

    public GetStatisticsHsaUnitResponseDto getStatisticsHsaUnit(String unitId) {
        try {
            GetStatisticsHsaUnitType parameters = new GetStatisticsHsaUnitType();
            parameters.setHsaIdentity(unitId);
            parameters.setSearchBase("c=SE");
            parameters.setIncludeOrgNo(Boolean.TRUE);
            return toDto(serverInterface.getStatisticsHsaUnit(logicalAddressHeader, messageId, parameters));
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsHsaUnit for {} hsaWsFault ({}, {}). {}", unitId, faultInfo.getCode(),
                    faultInfo.getMessage(), hsaWsFault.getMessage());
            LOG.debug("getStatisticsHsaUnit fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsHsaUnit for " + unitId, ex);
        }
    }

    public static GetStatisticsHsaUnitResponseDto toDto(GetStatisticsHsaUnitResponseType statisticsHsaUnit) {
        if (statisticsHsaUnit == null) {
            return null;
        }
        final GetStatisticsHsaUnitResponseDto getStatisticsHsaUnitResponseDto = new GetStatisticsHsaUnitResponseDto();
        getStatisticsHsaUnitResponseDto.setStatisticsCareUnit(toDto(statisticsHsaUnit.getStatisticsCareUnit()));
        getStatisticsHsaUnitResponseDto.setStatisticsUnit(toDto(statisticsHsaUnit.getStatisticsUnit()));
        return getStatisticsHsaUnitResponseDto;
    }

    private static StatisticsHsaUnitDto toDto(StatisticsHsaUnit statisticsCareUnit) {
        if (statisticsCareUnit == null) {
            return null;
        }
        final StatisticsHsaUnitDto statisticsHsaUnitDto = new StatisticsHsaUnitDto();
        statisticsHsaUnitDto.setArchived(statisticsCareUnit.isIsArchived());
        if (statisticsCareUnit.getBusinessClassificationCodes() != null) {
            statisticsHsaUnitDto
                    .setBusinessClassificationCodes(statisticsCareUnit.getBusinessClassificationCodes().getBusinessClassificationCode());
        }
        if (statisticsCareUnit.getBusinessTypes() != null) {
            statisticsHsaUnitDto.setBusinessTypes(statisticsCareUnit.getBusinessTypes().getBusinessType());
        }
        statisticsHsaUnitDto.setCareGiverHsaIdentity(statisticsCareUnit.getCareGiverHsaIdentity());
        if (statisticsCareUnit.getCareTypes() != null) {
            statisticsHsaUnitDto.setCareTypes(statisticsCareUnit.getCareTypes().getCareType());
        }
        statisticsHsaUnitDto.setCountyCode(statisticsCareUnit.getCountyCode());
        statisticsHsaUnitDto.setEndDate(statisticsCareUnit.getEndDate());
        statisticsHsaUnitDto.setGeographicalCoordinatesRt90(toDto(statisticsCareUnit.getGeographicalCoordinatesRt90()));
        statisticsHsaUnitDto.setHsaIdentity(statisticsCareUnit.getHsaIdentity());
        statisticsHsaUnitDto.setLocation(statisticsCareUnit.getLocation());
        if (statisticsCareUnit.getManagements() != null) {
            statisticsHsaUnitDto.setManagements(statisticsCareUnit.getManagements().getManagement());
        }
        statisticsHsaUnitDto.setMunicipalityCode(statisticsCareUnit.getMunicipalityCode());
        statisticsHsaUnitDto.setMunicipalitySectionName(statisticsCareUnit.getMunicipalitySectionName());
        statisticsHsaUnitDto.setStartDate(statisticsCareUnit.getStartDate());
        statisticsHsaUnitDto.setMunicipalitySectionCode(statisticsCareUnit.getMunicipalitySectionCode());
        return statisticsHsaUnitDto;
    }

    private static GeoCoordDto toDto(GeoCoord geographicalCoordinatesRt90) {
        if (geographicalCoordinatesRt90 == null) {
            return null;
        }
        final GeoCoordDto geoCoordDto = new GeoCoordDto();
        if (geographicalCoordinatesRt90.getType() != null && geographicalCoordinatesRt90.getType().value() != null) {
            geoCoordDto.setType(GeoCoordType.fromValue(geographicalCoordinatesRt90.getType().value()));
        }
        geoCoordDto.setX(geographicalCoordinatesRt90.getX());
        geoCoordDto.setY(geographicalCoordinatesRt90.getY());
        return geoCoordDto;
    }

    public GetStatisticsPersonResponseDto getStatisticsPerson(String personId) {
        try {
            GetStatisticsPersonType parameters = new GetStatisticsPersonType();
            parameters.setHsaIdentity(personId);
            parameters.setSearchBase("c=SE");
            return toDto(serverInterface.getStatisticsPerson(logicalAddressHeader, messageId, parameters));
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsPerson for {} hsaWsFault ({}, {}). {}", personId, faultInfo.getCode(),
                    faultInfo.getMessage(), hsaWsFault.getMessage());
            LOG.debug("getStatisticsPerson fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }

    private GetStatisticsPersonResponseDto toDto(GetStatisticsPersonResponseType statisticsPerson) {
        if (statisticsPerson == null) {
            return null;
        }
        final GetStatisticsPersonResponseDto getStatisticsPersonResponseDto = new GetStatisticsPersonResponseDto();
        getStatisticsPersonResponseDto.setHsaIdentity(statisticsPerson.getHsaIdentity());
        getStatisticsPersonResponseDto.setAge(statisticsPerson.getAge());
        getStatisticsPersonResponseDto.setGender(statisticsPerson.getGender());
        if (statisticsPerson.getHsaTitles() != null) {
            getStatisticsPersonResponseDto.setHsaTitles(statisticsPerson.getHsaTitles().getHsaTitle());
        }
        if (statisticsPerson.getPaTitleCodes() != null) {
            getStatisticsPersonResponseDto.setPaTitleCodes(statisticsPerson.getPaTitleCodes().getPaTitleCode());
        }
        getStatisticsPersonResponseDto.setProtectedPerson(statisticsPerson.isIsProtectedPerson());
        if (statisticsPerson.getSpecialityCodes() != null) {
            getStatisticsPersonResponseDto.setSpecialityCodes(statisticsPerson.getSpecialityCodes().getSpecialityCode());
        }
        return getStatisticsPersonResponseDto;
    }

    public GetStatisticsNamesResponseDto getStatisticsNames(String personId) {
        try {
            GetStatisticsNamesType parameters = new GetStatisticsNamesType();
            GetStatisticsNamesType.HsaIdentities hsaIds = new GetStatisticsNamesType.HsaIdentities();
            hsaIds.getHsaIdentity().add(personId);
            parameters.setHsaIdentities(hsaIds);
            parameters.setSearchBase("c=SE");
            return toDto(serverInterface.getStatisticsNames(logicalAddressHeader, messageId, parameters));
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsNames for {} hsaWsFault ({}, {}). {}", personId, faultInfo.getCode(),
                    faultInfo.getMessage(), hsaWsFault.getMessage());
            LOG.debug("getStatisticsNames fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }

    private GetStatisticsNamesResponseDto toDto(GetStatisticsNamesResponseType statisticsNames) {
        if (statisticsNames == null || statisticsNames.getStatisticsNameInfos() == null) {
            return null;
        }
        final GetStatisticsNamesResponseDto getStatisticsNamesResponseDto = new GetStatisticsNamesResponseDto();
        final List<StatisticsNameInfoDto> statisticsNameInfo = statisticsNames.getStatisticsNameInfos().getStatisticsNameInfo().stream()
                .map(this::toDto).collect(Collectors.toList());
        getStatisticsNamesResponseDto.setStatisticsNameInfos(statisticsNameInfo);
        return getStatisticsNamesResponseDto;
    }

    private StatisticsNameInfoDto toDto(StatisticsNameInfo sni) {
        if (sni == null) {
            return null;
        }
        final StatisticsNameInfoDto statisticsNameInfoDto = new StatisticsNameInfoDto();
        statisticsNameInfoDto.setHsaIdentity(sni.getHsaIdentity());
        statisticsNameInfoDto.setPersonGivenName(sni.getPersonGivenName());
        statisticsNameInfoDto.setPersonMiddleAndSurName(sni.getPersonMiddleAndSurName());
        return statisticsNameInfoDto;
    }

}
