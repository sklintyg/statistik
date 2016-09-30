/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.ifv.hsawsresponder.v3.GeoCoord;
import se.inera.ifv.hsawsresponder.v3.GeoCoordEnum;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.BusinessClassificationCodes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.BusinessTypes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.CareTypes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.Managements;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

@Component
public class HSAServiceImpl implements HSAService {
    private static final Logger LOG = LoggerFactory.getLogger(HSAServiceImpl.class);
    private static final String SKYDDAD = "Skyddad";
    private static final String IDENTITET = "Identitet";

    @Autowired
    private HsaWebService service;

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
                || neitherEnhetOrHuvudenhetHasVg(enhet, huvudenhet)
                ) {
            GetStatisticsHsaUnitResponseType unit = getStatisticsHsaUnit(key.getEnhetId().getId());
            if (unit != null) {
                //huvudenhet=vårdenhet och enhet kan vara vårdenhet, om det inte finns huvudenhet, annars motsvarar det kopplad/underliggande enhet
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
            GetStatisticsPersonResponseType personalType = getStatisticsPerson(key.getLakareId().getId());
            GetStatisticsNamesResponseType names = getStatisticsNames(key.getLakareId().getId());
            personal = createPersonal(personalType, names);
        }
        return personal;
    }

    private HsaInfoVg getVardgivare(HSAKey key, HsaInfo nonNullBase, HsaInfoEnhet enhet, HsaInfoEnhet huvudenhet) {
        HsaInfoVg vardgivare = nonNullBase.getVardgivare();

        if (vardgivare == null) {
            final HsaIdVardgivare vgId = getVardgivareHsaId(enhet, huvudenhet);
            if (vgId == null || !vgId.equals(key.getVardgivareId())) {
                LOG.info("VardgivarId mismatch found for enhet: {}. Was {} in HSA but expected {} from intyg", key.getEnhetId(), key.getVardgivareId(), vgId);
            }
            if (vgId != null && !vgId.isEmpty()) {
                GetStatisticsCareGiverResponseType caregiver = getStatisticsCareGiver(vgId.getId());
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

    private GetStatisticsPersonResponseType getStatisticsPerson(String key) {
        GetStatisticsPersonResponseType person = service.getStatisticsPerson(key);
        if (person == null) {
            LOG.warn("No statistics person '{}' found", key);
        }
        return person;
    }

    private GetStatisticsNamesResponseType getStatisticsNames(String key) {
        GetStatisticsNamesResponseType person = service.getStatisticsNames(key);
        if (person == null) {
            LOG.warn("No statistics names '{}' found", key);
        }
        return person;
    }

    private GetStatisticsCareGiverResponseType getStatisticsCareGiver(String key) {
        GetStatisticsCareGiverResponseType caregiver = service.getStatisticsCareGiver(key);
        if (caregiver == null) {
            LOG.warn("No care giver '{}' found", key);
        }
        return caregiver;
    }

    private GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(String key) {
        GetStatisticsHsaUnitResponseType unit = service.getStatisticsHsaUnit(key);
        if (unit == null) {
            LOG.warn("No unit '{}' found", key);
        }
        return unit;
    }

    private HsaInfoPersonal createPersonal(GetStatisticsPersonResponseType personal, GetStatisticsNamesResponseType names) {
        if (personal == null) {
            return null;
        }
        final String id = personal.getHsaIdentity();
        final String gender = personal.getGender();
        final String age = personal.getAge();
        final List<String> befattning = personal.getPaTitleCodes() != null ? personal.getPaTitleCodes().getPaTitleCode() : null;
        final List<String> specialitet = personal.getSpecialityCodes() != null ? personal.getSpecialityCodes().getSpecialityCode() : null;
        final List<String> yrkesgrupp = personal.getHsaTitles() != null ? personal.getHsaTitles().getHsaTitle() : null;

        final Boolean isProtectedPerson = personal.isIsProtectedPerson();
        final String fornamn = getFornamn(names, nullSafe(isProtectedPerson));
        final String efternamn = getMellanOchEfternamn(names, nullSafe(isProtectedPerson));
        return new HsaInfoPersonal(id, gender, age, befattning, specialitet, yrkesgrupp, isProtectedPerson, fornamn, efternamn);
    }

    private boolean nullSafe(Boolean boolObj) {
        return boolObj == null ? false : boolObj;
    }

    private String getFornamn(GetStatisticsNamesResponseType names, boolean isProtected) {
        if (isProtected) {
            return SKYDDAD;
        }
        if (names == null) {
            return null;
        }
        return names.getStatisticsNameInfos().getStatisticsNameInfo().get(0).getPersonGivenName();
    }

    private String getMellanOchEfternamn(GetStatisticsNamesResponseType names, boolean isProtected) {
        if (isProtected) {
            return IDENTITET;
        }
        if (names == null) {
            return null;
        }
        return names.getStatisticsNameInfos().getStatisticsNameInfo().get(0).getPersonMiddleAndSurName();
    }

    private HsaInfoVg createCareGiver(GetStatisticsCareGiverResponseType caregiver) {
        if (caregiver == null) {
            return null;
        }
        final String hsaIdentity = caregiver.getHsaIdentity();
        final String careGiverOrgNo = caregiver.getCareGiverOrgNo();
        final LocalDateTime startDate = toJavaTime(caregiver.getStartDate());
        final LocalDateTime endDate = toJavaTime(caregiver.getEndDate());
        final Boolean isArchived = caregiver.isIsArchived();
        return new HsaInfoVg(hsaIdentity, careGiverOrgNo, startDate, endDate, isArchived);
    }

    private HsaInfoEnhet createUnit(StatisticsHsaUnit unit) {
        if (unit == null) {
            return null;
        }

        final String hsaIdentity = unit.getHsaIdentity();
        final List<String> enhetsTyp = createEnhetsTyp(unit.getBusinessTypes());
        final List<String> agarTyp = createAgarTyp(unit.getManagements());
        final LocalDateTime startDate = toJavaTime(unit.getStartDate());
        final LocalDateTime endDate = toJavaTime(unit.getEndDate());
        final Boolean isArchived = unit.isIsArchived();
        final List<String> verksamhet = createVerksamhet(unit.getBusinessClassificationCodes());
        final List<String> vardform = createVardform(unit.getCareTypes());
        final HsaInfoEnhetGeo geografiskIndelning = createGeografiskIndelning(unit);
        final String careGiverHsaIdentity = unit.getCareGiverHsaIdentity();
        return new HsaInfoEnhet(hsaIdentity, enhetsTyp, agarTyp, startDate, endDate, isArchived, verksamhet, vardform, geografiskIndelning, careGiverHsaIdentity);
    }

    private List<String> createVardform(CareTypes careTypes) {
        return careTypes != null ? careTypes.getCareType() : null;
    }

    private List<String> createVerksamhet(BusinessClassificationCodes codes) {
        return codes != null ? codes.getBusinessClassificationCode() : null;
    }

    private List<String> createAgarTyp(Managements managements) {
        return managements != null ? managements.getManagement() : null;
    }

    private List<String> createEnhetsTyp(BusinessTypes businessTypes) {
        return businessTypes != null ? businessTypes.getBusinessType() : null;
    }

    private HsaInfoEnhetGeo createGeografiskIndelning(StatisticsHsaUnit unit) {
        final HsaInfoCoordinate coordinate = createCoordinate(unit);
        final String location = unit.getLocation();
        final String municipalitySectionCode = unit.getMunicipalitySectionCode();
        final String municipalitySectionName = unit.getMunicipalitySectionName();
        final String municipalityCode = unit.getMunicipalityCode();
        final String countyCode = unit.getCountyCode();
        final HsaInfoEnhetGeo hsaInfoEnhetGeo = new HsaInfoEnhetGeo(coordinate, location, municipalitySectionCode, municipalitySectionName, municipalityCode, countyCode);
        return hsaInfoEnhetGeo.isEmpty() ? null : hsaInfoEnhetGeo;
    }


    private HsaInfoCoordinate createCoordinate(StatisticsHsaUnit unit) {
        return createCoordinates(unit.getGeographicalCoordinatesRt90());
    }

    private HsaInfoCoordinate createCoordinates(GeoCoord coordinate) {
        if (coordinate == null || coordinate.getType() != GeoCoordEnum.RT_90) {
            return null;
        }
        final String typ = coordinate.getType().toString();
        final String x = coordinate.getX();
        final String y = coordinate.getY();
        return new HsaInfoCoordinate(typ, x, y);
    }

    private LocalDateTime toJavaTime(org.joda.time.LocalDateTime joda) {
        if (joda == null) {
            return null;
        }
        return LocalDateTime.of(joda.getYear(), joda.getMonthOfYear(), joda.getDayOfMonth(), joda.getHourOfDay(), joda.getMinuteOfHour(), joda.getSecondOfMinute());
    }

}
