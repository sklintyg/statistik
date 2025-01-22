/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.hsa;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.GeoCoordDto;
import se.inera.statistics.integration.hsa.model.GeoCoordType;
import se.inera.statistics.integration.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.integration.hsa.model.GetStatisticsPersonResponseDto;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.StatisticsHsaUnitDto;
import se.inera.statistics.integration.hsa.services.HsaStatisticsService;

@Component
public class HSAServiceImpl implements HSAService {

    private static final Logger LOG = LoggerFactory.getLogger(HSAServiceImpl.class);
    private static final String SKYDDAD_IDENTITET_FORNAMN = "Läkare med skyddad";
    private static final String SKYDDAD_IDENTITET_EFTERNAMN = "personuppgift";

    @Autowired
    private HsaStatisticsService service;

    @Override
    public HsaInfo getHSAInfo(HSAKey key) {
        return getHSAInfo(key, null);
    }

    @Override
    public HsaInfo getHSAInfo(HSAKey key, HsaInfo baseHsaInfo) {
        try {
            return getHsaInfoWithoutExceptionHandling(key, baseHsaInfo);
        } catch (RuntimeException e) {
            LOG.error("Unexpected error fetching HSA data", e);
            return null;
        }
    }

    private HsaInfo getHsaInfoWithoutExceptionHandling(HSAKey key, HsaInfo baseHsaInfo) {
        HsaInfo nonNullBase = baseHsaInfo != null ? baseHsaInfo : new HsaInfo(null, null, null, null);

        HsaInfoEnhet enhet = nonNullBase.getEnhet();
        HsaInfoEnhet huvudenhet = nonNullBase.getHuvudenhet();

        if (noEnhetIsDefined(enhet)
            || enhetHasNoVgAndNoHuvudenhetIsDefined(enhet, huvudenhet)
            || neitherEnhetOrHuvudenhetHasVg(enhet, huvudenhet)) {
            GetStatisticsHsaUnitResponseDto unit = getStatisticsHsaUnit(key.getEnhetId().getId());
            if (unit != null) {
                // huvudenhet=vårdenhet och enhet kan vara vårdenhet, om det inte finns huvudenhet, annars motsvarar det
                // kopplad/underliggande enhet
                enhet = createUnit(unit.getStatisticsUnit());
                huvudenhet = createUnit(unit.getStatisticsCareUnit());
            }
        }

        HsaInfoVg vardgivare = getVardgivare(key, nonNullBase, enhet, huvudenhet);
        HsaInfoPersonal personal = getPersonal(key, nonNullBase);

        return new HsaInfo(enhet, huvudenhet, vardgivare, personal);
    }

    private boolean neitherEnhetOrHuvudenhetHasVg(HsaInfoEnhet enhet, HsaInfoEnhet huvudenhet) {
        return enhet.getVgid() == null && huvudenhet != null && huvudenhet.getVgid() == null;
    }

    private boolean enhetHasNoVgAndNoHuvudenhetIsDefined(HsaInfoEnhet enhet, HsaInfoEnhet huvudenhet) {
        return enhet.getVgid() == null && huvudenhet == null;
    }

    private boolean noEnhetIsDefined(HsaInfoEnhet enhet) {
        return enhet == null;
    }

    private HsaInfoPersonal getPersonal(HSAKey key, HsaInfo nonNullBase) {
        HsaInfoPersonal personal = nonNullBase.getPersonal();

        if (personal == null) {
            GetStatisticsPersonResponseDto personalType = getStatisticsPerson(key.getLakareId().getId());
            GetStatisticsNamesResponseDto names = getStatisticsNames(key.getLakareId().getId());
            personal = createPersonal(personalType, names);
        }
        return personal;
    }

    private HsaInfoVg getVardgivare(HSAKey key, HsaInfo nonNullBase, HsaInfoEnhet enhet, HsaInfoEnhet huvudenhet) {
        HsaInfoVg vardgivare = nonNullBase.getVardgivare();

        if (vardgivare == null) {
            final HsaIdVardgivare vgIdFromHsa = getVardgivareHsaId(enhet, huvudenhet);
            if (vgIdFromHsa == null || !vgIdFromHsa.equals(key.getVardgivareId())) {
                LOG.info("VardgivarId mismatch found for enhet: {}. Expected {} from intyg but was {} in HSA", key.getEnhetId(),
                    key.getVardgivareId(), vgIdFromHsa);
            }
            if (vgIdFromHsa != null && !vgIdFromHsa.isEmpty()) {
                GetStatisticsCareGiverResponseDto caregiver = getStatisticsCareGiver(vgIdFromHsa.getId());
                vardgivare = createCareGiver(caregiver);
            }
        }
        return vardgivare;
    }

    private HsaIdVardgivare getVardgivareHsaId(HsaInfoEnhet enhet, HsaInfoEnhet huvudenhet) {
        if (enhet != null && enhet.getVgid() != null) {
            return new HsaIdVardgivare(enhet.getVgid());
        }
        if (huvudenhet != null && huvudenhet.getVgid() != null) {
            return new HsaIdVardgivare(huvudenhet.getVgid());
        }
        return null;
    }

    private GetStatisticsPersonResponseDto getStatisticsPerson(String key) {
        GetStatisticsPersonResponseDto person = service.getStatisticsPerson(key);
        if (person == null) {
            LOG.warn("No statistics person '{}' found", key);
        }
        return person;
    }

    private GetStatisticsNamesResponseDto getStatisticsNames(String key) {
        GetStatisticsNamesResponseDto person = service.getStatisticsNames(key);
        if (person == null) {
            LOG.warn("No statistics names '{}' found", key);
        }
        return person;
    }

    private GetStatisticsCareGiverResponseDto getStatisticsCareGiver(String key) {
        GetStatisticsCareGiverResponseDto caregiver = service.getStatisticsCareGiver(key);
        if (caregiver == null) {
            LOG.warn("No care giver '{}' found", key);
        }
        return caregiver;
    }

    private GetStatisticsHsaUnitResponseDto getStatisticsHsaUnit(String key) {
        GetStatisticsHsaUnitResponseDto unit = service.getStatisticsHsaUnit(key);
        if (unit == null) {
            LOG.warn("No unit '{}' found", key);
        }
        return unit;
    }

    private HsaInfoPersonal createPersonal(GetStatisticsPersonResponseDto personal, GetStatisticsNamesResponseDto names) {
        if (personal == null) {
            return null;
        }
        final String id = personal.getHsaIdentity();
        final String gender = personal.getGender();
        final String age = personal.getAge();
        final List<String> befattning = personal.getPaTitleCodes();
        final List<String> specialitet = personal.getSpecialityCodes();
        final List<String> yrkesgrupp = personal.getHsaTitles();

        final Boolean isProtectedPerson = personal.isProtectedPerson();
        final String fornamn = getFornamn(names, nullSafe(isProtectedPerson));
        final String efternamn = getMellanOchEfternamn(names, nullSafe(isProtectedPerson));
        return new HsaInfoPersonal(id, gender, age, befattning, specialitet, yrkesgrupp, isProtectedPerson, fornamn, efternamn);
    }

    private boolean nullSafe(Boolean boolObj) {
        return boolObj == null ? false : boolObj;
    }

    private String getFornamn(GetStatisticsNamesResponseDto names, boolean isProtected) {
        if (isProtected) {
            return SKYDDAD_IDENTITET_FORNAMN;
        }
        if (names == null) {
            return null;
        }
        return names.getStatisticsNameInfos().get(0).getPersonGivenName();
    }

    private String getMellanOchEfternamn(GetStatisticsNamesResponseDto names, boolean isProtected) {
        if (isProtected) {
            return SKYDDAD_IDENTITET_EFTERNAMN;
        }
        if (names == null) {
            return null;
        }
        return names.getStatisticsNameInfos().get(0).getPersonMiddleAndSurName();
    }

    private HsaInfoVg createCareGiver(GetStatisticsCareGiverResponseDto caregiver) {
        if (caregiver == null) {
            return null;
        }
        final String hsaIdentity = caregiver.getHsaIdentity();
        final String careGiverOrgNo = caregiver.getCareGiverOrgNo();
        final LocalDateTime startDate = caregiver.getStartDate();
        final LocalDateTime endDate = caregiver.getEndDate();
        final Boolean isArchived = caregiver.isArchived();
        return new HsaInfoVg(hsaIdentity, careGiverOrgNo, startDate, endDate, isArchived);
    }

    private HsaInfoEnhet createUnit(StatisticsHsaUnitDto unit) {
        if (unit == null) {
            return null;
        }

        final String hsaIdentity = unit.getHsaIdentity();
        final List<String> enhetsTyp = unit.getBusinessTypes();
        final List<String> agarTyp = unit.getManagements();
        final LocalDateTime startDate = unit.getStartDate();
        final LocalDateTime endDate = unit.getEndDate();
        final Boolean isArchived = unit.isArchived();
        final List<String> verksamhet = unit.getBusinessClassificationCodes();
        final List<String> vardform = unit.getCareTypes();
        final HsaInfoEnhetGeo geografiskIndelning = createGeografiskIndelning(unit);
        final String careGiverHsaIdentity = unit.getCareGiverHsaIdentity();
        return new HsaInfoEnhet(hsaIdentity, enhetsTyp, agarTyp, startDate, endDate, isArchived, verksamhet, vardform, geografiskIndelning,
            careGiverHsaIdentity);
    }

    private HsaInfoEnhetGeo createGeografiskIndelning(StatisticsHsaUnitDto unit) {
        final HsaInfoCoordinate coordinate = createCoordinate(unit);
        final String location = unit.getLocation();
        final String municipalitySectionCode = unit.getMunicipalitySectionCode();
        final String municipalitySectionName = unit.getMunicipalitySectionName();
        final String municipalityCode = unit.getMunicipalityCode();
        final String countyCode = unit.getCountyCode();
        final HsaInfoEnhetGeo hsaInfoEnhetGeo = new HsaInfoEnhetGeo(coordinate, location, municipalitySectionCode, municipalitySectionName,
            municipalityCode, countyCode);
        return hsaInfoEnhetGeo.isEmpty() ? null : hsaInfoEnhetGeo;
    }

    private HsaInfoCoordinate createCoordinate(StatisticsHsaUnitDto unit) {
        return createCoordinates(unit.getGeographicalCoordinatesRt90());
    }

    private HsaInfoCoordinate createCoordinates(GeoCoordDto coordinate) {
        if (coordinate == null || coordinate.getType() != GeoCoordType.RT_90) {
            return null;
        }
        final String typ = coordinate.getType().toString();
        final String x = coordinate.getX();
        final String y = coordinate.getY();
        return new HsaInfoCoordinate(typ, x, y);
    }

}
