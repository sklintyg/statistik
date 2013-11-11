package se.inera.statistics.service.hsa;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.ifv.hsawsresponder.v3.GeoCoord;
import se.inera.ifv.hsawsresponder.v3.GeoCoordEnum;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.BusinessClassificationCodes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.BusinessTypes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.CareTypes;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit.Managements;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

//@Component
public class HSAServiceImpl implements HSAService {
    private JsonNodeFactory factory = JsonNodeFactory.instance;

    @Autowired
    private HSAWebServiceCalls service;

    @Override
    public JsonNode getHSAInfo(HSAKey key) {
        GetStatisticsHsaUnitResponseType unit = service.getStatisticsHsaUnit(key.getEnhetId());
        GetStatisticsCareGiverResponseType caregiver = service.getStatisticsCareGiver(key.getVardgivareId());
        GetStatisticsPersonResponseType personal = service.getStatisticsPerson(key.getLakareId());

        Builder root = new Builder();
        root.put("enhet", createUnit(unit.getStatisticsUnit()));
        root.put("huvudenhet", createUnit(unit.getStatisticsCareUnit()));
        root.put("vardgivare", createCareGiver(caregiver));
        root.put("personal", createPersonal(personal));
        return root.root;
    }

    private Builder createPersonal(GetStatisticsPersonResponseType personal) {
        if (personal == null) {
            return null;
        }
        Builder root = new Builder();
        root.put("id", personal.getHsaIdentity());
        root.put("efternamn", "Not yet");
        root.put("tilltalsnamn", "Not yet");
        root.put("initial", "Not yet");
        root.put("kon", personal.getGender());
        root.put("alder", personal.getAge());
        root.put("befattning", personal.getPaTitleCodes() != null ? personal.getPaTitleCodes().getPaTitleCode() : null);
        root.put("specialitet", personal.getSpecialityCodes() != null ? personal.getSpecialityCodes().getSpecialityCode() : null);
        root.put("yrkesgrupp", personal.getHsaTitles() != null ? personal.getHsaTitles().getHsaTitle() : null);
        root.put("skyddad", personal.isIsProtectedPerson());
        return root;
    }

    private Builder createCareGiver(GetStatisticsCareGiverResponseType caregiver) {
        if (caregiver == null) {
            return null;
        }
        Builder root = new Builder();
        root.put("id", caregiver.getHsaIdentity());
        root.put("orgnr", caregiver.getCareGiverOrgNo());
        root.put("namn", "Not yet");
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
        root.put("namn", "Not yet");
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

    private JsonNode createHosPersonal() {
        ObjectNode root = factory.objectNode();
        root.put("placeholder", "value");
        return root;
    }

    private JsonNode createGeografiskIndelning(StatisticsHsaUnit unit) {
        ObjectNode root = factory.objectNode();
        root.put("koordinat", createCoordinate(unit));
        root.put("plats", unit.getLocation());
        root.put("kommundelskod", unit.getMunicipalitySectionCode());
        root.put("kommundelsnamn", unit.getMunicipalitySectionName());
        root.put("kommun", unit.getMunicipalityCode());
        root.put("lan", unit.getCountyCode());
        return root;
    }


    private JsonNode createCoordinate(StatisticsHsaUnit unit) {
        return createCoordinates(unit.getGeographicalCoordinatesRt90());
    }

    private JsonNode createCoordinates(GeoCoord coordinate) {
        if (coordinate == null || coordinate.getType() != GeoCoordEnum.RT_90) {
            return null;
        }
        ObjectNode root = factory.objectNode();
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
        public Builder put(String name, JsonNode node) {
            if (node != null) {
                root.put(name,  node);
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
