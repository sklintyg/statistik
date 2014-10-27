/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.hsa;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.ifv.hsawsresponder.v3.*;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.BusinessClassificationCodes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.BusinessTypes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.CareTypes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.Managements;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class HSAServiceImpl implements HSAService {
    private static final Logger LOG = LoggerFactory.getLogger(HSAServiceImpl.class);
    private JsonNodeFactory factory = JsonNodeFactory.instance;

    @Autowired
    private HSAWebServiceCalls service;

    @Override
    public JsonNode getHSAInfo(HSAKey key) {
        try {
            GetStatisticsHsaUnitResponseType unit = getStatisticsHsaUnit(key.getEnhetId());
            GetStatisticsCareGiverResponseType caregiver = getStatisticsCareGiver(key.getVardgivareId());
            GetStatisticsPersonResponseType personal = getStatisticsPerson(key.getLakareId());
            GetStatisticsNamesResponseType names = getStatisticsNames(key.getLakareId());

            Builder root = new Builder();
            if (unit != null) {
                root.put("enhet", createUnit(unit.getStatisticsUnit()));
                root.put("huvudenhet", createUnit(unit.getStatisticsCareUnit()));
            }
            root.put("vardgivare", createCareGiver(caregiver));
            root.put("personal", createPersonal(personal, names));
            return root.root;
        } catch (RuntimeException e) {
            LOG.error("Unexpected error fetching HSA data", e);
            return null;
        }
    }

    private GetStatisticsPersonResponseType getStatisticsPerson(String key) {
        GetStatisticsPersonResponseType person = service.getStatisticsPerson(key);
        if (person == null) {
            LOG.warn("No person '{}' found", key);
        }
        return person;
    }

    private GetStatisticsNamesResponseType getStatisticsNames(String key) {
        GetStatisticsNamesResponseType person = service.getStatisticsNames(key);
        if (person == null) {
            LOG.warn("No person '{}' found", key);
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

    private Builder createPersonal(GetStatisticsPersonResponseType personal, GetStatisticsNamesResponseType names) {
        if (personal == null) {
            return null;
        }
        Builder root = new Builder();
        root.put("id", personal.getHsaIdentity());
        root.put("kon", personal.getGender());
        root.put("alder", personal.getAge());
        root.put("befattning", personal.getPaTitleCodes() != null ? personal.getPaTitleCodes().getPaTitleCode() : null);
        root.put("specialitet", personal.getSpecialityCodes() != null ? personal.getSpecialityCodes().getSpecialityCode() : null);
        root.put("yrkesgrupp", personal.getHsaTitles() != null ? personal.getHsaTitles().getHsaTitle() : null);
        root.put("skyddad", personal.isIsProtectedPerson());
        root.put("tilltalsnamn", getFornamn(names));
        root.put("efternamn", getMellanOchEfternamn(names));
        return root;
    }

    private String getFornamn(GetStatisticsNamesResponseType names) {
        if (names == null) {
            return null;
        }
        return names.getStatisticsNameInfos().getStatisticsNameInfo().get(0).getPersonGivenName();
    }

    private String getMellanOchEfternamn(GetStatisticsNamesResponseType names) {
        if (names == null) {
            return null;
        }
        return names.getStatisticsNameInfos().getStatisticsNameInfo().get(0).getPersonMiddleAndSurName();
    }

    private Builder createCareGiver(GetStatisticsCareGiverResponseType caregiver) {
        if (caregiver == null) {
            return null;
        }
        Builder root = new Builder();
        root.put("id", caregiver.getHsaIdentity());
        root.put("orgnr", caregiver.getCareGiverOrgNo());
        root.put("startdatum", caregiver.getStartDate());
        root.put("slutdatum", caregiver.getEndDate());
        root.put("arkiverad", caregiver.isIsArchived());
        return root;
    }

    private Builder createUnit(StatisticsHsaUnit unit) {
        if (unit == null) {
            return null;
        }
        Builder root = new Builder();

        root.put("id", unit.getHsaIdentity());
        root.put("enhetsTyp", createEnhetsTyp(unit.getBusinessTypes()));
        root.put("agarform", createAgarTyp(unit.getManagements()));
        root.put("startdatum", unit.getStartDate());
        root.put("slutdatum", unit.getEndDate());
        root.put("arkiverad", unit.isIsArchived());
        root.put("verksamhet", createVerksamhet(unit.getBusinessClassificationCodes()));
        root.put("vardform", createVardform(unit.getCareTypes()));
        root.put("geografi", createGeografiskIndelning(unit));
        return root;
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

    private Builder createGeografiskIndelning(StatisticsHsaUnit unit) {
        Builder root = new Builder();
        root.put("koordinat", createCoordinate(unit));
        root.put("plats", unit.getLocation());
        root.put("kommundelskod", unit.getMunicipalitySectionCode());
        root.put("kommundelsnamn", unit.getMunicipalitySectionName());
        root.put("kommun", unit.getMunicipalityCode());
        root.put("lan", unit.getCountyCode());
        return root;
    }


    private Builder createCoordinate(StatisticsHsaUnit unit) {
        return createCoordinates(unit.getGeographicalCoordinatesRt90());
    }

    private Builder createCoordinates(GeoCoord coordinate) {
        if (coordinate == null || coordinate.getType() != GeoCoordEnum.RT_90) {
            return null;
        }
        Builder root = new Builder();
        root.put("typ", coordinate.getType().toString());
        root.put("x", coordinate.getX());
        root.put("y", coordinate.getY());
        return root;
    }

    private class Builder {
        private ObjectNode root = factory.objectNode();
        public Builder put(String name, String value) {
            if (value != null) {
                root.put(name, value);
            }
            return this;
        }
        public Builder put(String name, List<String> items) {
            if (items != null && !items.isEmpty()) {
                ArrayNode container = factory.arrayNode();
                for (String item: items) {
                    container.add(item);
                }
                root.put(name, container);
            }
            return this;
        }
        public Builder put(String name, Builder b) {
            if (b != null && b.root.size() > 0) {
                root.put(name,  b.root);
            }
            return this;
        }
        public Builder put(String name, Boolean bool) {
            if (bool != null) {
                root.put(name, bool);
            }
            return this;
        }
        public Builder put(String name, LocalDateTime date) {
            if (date != null) {
                put(name, date.toString("yyyy-MM-dd"));
            }
            return this;
        }
    }
}
