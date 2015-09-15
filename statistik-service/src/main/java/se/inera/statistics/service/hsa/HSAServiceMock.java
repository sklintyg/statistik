/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.GeoCoord;
import se.inera.ifv.hsawsresponder.v3.GeoCoordEnum;
import se.inera.ifv.hsawsresponder.v3.GetCareUnitListResponseType;
import se.inera.ifv.hsawsresponder.v3.GetCareUnitMembersResponseType;
import se.inera.ifv.hsawsresponder.v3.GetCareUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetHospLastUpdateResponseType;
import se.inera.ifv.hsawsresponder.v3.GetHospLastUpdateType;
import se.inera.ifv.hsawsresponder.v3.GetHospPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetHospPersonType;
import se.inera.ifv.hsawsresponder.v3.GetHsaPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetHsaPersonType;
import se.inera.ifv.hsawsresponder.v3.GetHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetInformationListResponseType;
import se.inera.ifv.hsawsresponder.v3.GetInformationListType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonType;
import se.inera.ifv.hsawsresponder.v3.GetPriceUnitsForAuthResponseType;
import se.inera.ifv.hsawsresponder.v3.GetPriceUnitsForAuthType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonType;
import se.inera.ifv.hsawsresponder.v3.HandleCertifierResponseType;
import se.inera.ifv.hsawsresponder.v3.HandleCertifierType;
import se.inera.ifv.hsawsresponder.v3.HsawsSimpleLookupResponseType;
import se.inera.ifv.hsawsresponder.v3.HsawsSimpleLookupType;
import se.inera.ifv.hsawsresponder.v3.IsAuthorizedToSystemResponseType;
import se.inera.ifv.hsawsresponder.v3.IsAuthorizedToSystemType;
import se.inera.ifv.hsawsresponder.v3.LookupHsaObjectType;
import se.inera.ifv.hsawsresponder.v3.MiuInformationType;
import se.inera.ifv.hsawsresponder.v3.PingResponseType;
import se.inera.ifv.hsawsresponder.v3.PingType;
import se.inera.ifv.hsawsresponder.v3.StatisticsHsaUnit;
import se.inera.ifv.hsawsresponder.v3.StatisticsNameInfo;
import se.inera.ifv.hsawsresponder.v3.VpwGetPublicUnitsResponseType;
import se.inera.ifv.hsawsresponder.v3.VpwGetPublicUnitsType;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.stub.HsaServiceStub;
import se.inera.statistics.hsa.stub.Medarbetaruppdrag;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Component
@Profile({ "dev", "hsa-stub" })
@Primary
public class HSAServiceMock implements HsaWsResponderInterface, HsaDataInjectable {
    private static final int POSITIVE_MASK = 0x7fffffff;
    public static final int VERKSAMHET_MODULO = 7;

    private JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final Lan LAN = new Lan();
    private static final List<String> LAN_CODES;
    private static final Kommun KOMMUN = new Kommun();
    private static final String[] TILLTALS_NAMN = new String[] {"Abdullah", "Beata", "Cecilia", "David", "Egil", "Fredrika", "Gustave", "Henning", "Ibrahim", "José", "Kone", "Lazlo", "My", "Natasha", "Orhan", "Pawel", "Rebecca", "Sirkka", "Tuula", "Urban", "Vieux", "Åsa"};
    private static final String[] EFTER_NAMN = new String[] {"Andersson", "Bardot", "Cohen", "Derrida", "En", "Flod", "Gran", "Holmberg", "Isaac", "Juhanen", "Karlsson", "Lazar", "Manard", "Nadal", "Olrik", "Pettersson", "Rawls", "Sadat", "Tot", "Uddhammar", "Wedén", "Åsgren", "Örn"};
    private static final List<String> KOMMUN_CODES;
    private static final VerksamhetsTyp VERKSAMHET = new VerksamhetsTyp();
    private static final List<String> VERKSAMHET_CODES;
    private final Map<String, JsonNode> personals = new HashMap<>();
    private String nextLanCode = null;
    private String nextEnhetName = null;
    private String nextHuvudenhetId = null;
    private HSAKey hsaKey = null;

    @Autowired
    private HsaServiceStub hsaServiceStub;

    static {
        LAN_CODES = new ArrayList<>();
        for (String kod : LAN) {
            LAN_CODES.add(kod);
        }
        KOMMUN_CODES = new ArrayList<>();
        for (String kod : KOMMUN) {
            KOMMUN_CODES.add(kod);
        }
        VERKSAMHET_CODES = new ArrayList<>();
        for (String kod : VERKSAMHET) {
            VERKSAMHET_CODES.add(kod);
        }
    }

    private JsonNode getOrCreatePersonal(HSAKey key) {
        if (personals.containsKey(key.getLakareId())) {
            return personals.get(key.getLakareId());
        }
        return createPersonal(key);
    }

    public JsonNode createEnhet(HSAKey key, boolean isHuvudenhet) {
        ObjectNode root = factory.objectNode();
        final String enhetId = getEnhetId(key.getEnhetId(), isHuvudenhet);
        root.put("id", enhetId);
        root.put("namn", getEnhetsNamn(enhetId));
        root.put(HSAService.ENHETS_TYP, asList(VerksamhetsTyp.VARDCENTRAL_ID));
        root.put("agarform", asList("Landsting/Region"));
        root.put("startdatum", "");
        root.put("slutdatum", "");
        root.put("arkiverad", (JsonNode) null);
        root.put("organisationsnamn", "Organisationsnamn");
        root.put("vardform", (JsonNode) null);
        root.put("geografi", createGeografiskIndelning(key));
        root.put("verksamhet", asList(createVerksamhet(key)));
        root.put("vgid", key.getVardgivareId());
        return root;
    }

    private String getEnhetId(String enhetId, boolean isHuvudenhet) {
        if (isHuvudenhet && nextHuvudenhetId != null) {
            return nextHuvudenhetId;
        }
        return enhetId;
    }

    private String getEnhetsNamn(String enhetId) {
        if (nextEnhetName != null) {
            return nextEnhetName;
        }
        if (enhetId.startsWith("vg1-")) {
            String suffix = enhetId.substring(enhetId.lastIndexOf('-') + 1);
            return "Verksamhet " + suffix;
        }
        return "Enhet " + enhetId;
    }

    private JsonNode createPersonal(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("id", key.getLakareId());
        root.put("initial", (JsonNode) null);
        root.put("kon", (JsonNode) null);
        root.put("alder", (JsonNode) null);
        root.put("befattning", (JsonNode) null);
        root.put("specialitet", (JsonNode) null);
        root.put("yrkesgrupp", (JsonNode) null);
        root.put("skyddad", (JsonNode) null);
        root.put("tilltalsnamn", getTilltalsnamn(key));
        root.put("efternamn", getEfternamn(key));
        return root;
    }

    public ObjectNode createPersonal(String id, String firstName, String lastName, HsaKon kon, int age, List<String> befattnings) {
        ObjectNode root = factory.objectNode();
        root.put("id", id);
        root.put("initial", (JsonNode) null);
        root.put("kon", String.valueOf(kon.getHsaRepresantation()));
        root.put("alder", String.valueOf(age));
        root.put("befattning", toArrayNode(befattnings));
        root.put("specialitet", (JsonNode) null);
        root.put("yrkesgrupp", (JsonNode) null);
        root.put("skyddad", (JsonNode) null);
        root.put("tilltalsnamn", firstName);
        root.put("efternamn", lastName);
        return root;
    }

    private ArrayNode toArrayNode(List<String> numbers) {
        final ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (String number : numbers) {
            arrayNode.add(String.valueOf(number));
        }
        return arrayNode;
    }

    private String getTilltalsnamn(Object key) {
        if (key == null) {
            return null;
        }
        int index = key.toString().hashCode() & POSITIVE_MASK;
        return TILLTALS_NAMN[index % TILLTALS_NAMN.length ];
    }

    private String getEfternamn(Object key) {
        if (key == null) {
            return null;
        }
        int index = key.toString().hashCode() & POSITIVE_MASK;
        return EFTER_NAMN[index % EFTER_NAMN.length ];
    }

    private JsonNode createGeografiskIndelning(HSAKey key) {
        ObjectNode root = factory.objectNode();
        root.put("koordinat", "nagonsortskoordinat");
        root.put("plats", "Plats");
        root.put("kommundelskod", "0");
        root.put("kommundelsnamn", "Centrum");
        String lan = createLan(key);
        root.put("lan", lan);
        root.put("kommun", createKommun(key, lan));
        return root;
    }

    private String createLan(HSAKey key) {
        if (nextLanCode != null) {
            return nextLanCode;
        }
        int keyIndex = key != null && key.getVardgivareId() != null ? key.getVardgivareId().hashCode() & POSITIVE_MASK : 0;
        return LAN_CODES.get(keyIndex % LAN_CODES.size());
    }

    private String createKommun(HSAKey key, final String lan) {
        int keyIndex = key != null && key.getEnhetId() != null ? key.getEnhetId().hashCode() & POSITIVE_MASK : 0;
        List<String> relevantKommuns = FluentIterable.from(KOMMUN_CODES).filter(new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s.startsWith(lan) || s.equals(Kommun.OVRIGT_ID);
            }
        }).toList();
        return relevantKommuns.get(keyIndex % relevantKommuns.size()).substring(2);
    }

    private String[] createVerksamhet(HSAKey key) {
        if (key == null || key.getVardgivareId() == null) {
            return new String[] {VerksamhetsTyp.OVRIGT_ID};
        }
        Set<String> returnSet = new HashSet<>();
        int numberOfVerksamhet = (key.getEnhetId().hashCode() & POSITIVE_MASK) % VERKSAMHET_MODULO;
        int i = 0;
        while (returnSet.size() < numberOfVerksamhet) {
            int index = ((key.getVardgivareId() + key.getEnhetId() + i).hashCode()) & POSITIVE_MASK;
            returnSet.add(VERKSAMHET_CODES.get(index % VERKSAMHET_CODES.size()));
            i++;
        }
        String[] returnArray = returnSet.toArray(new String[returnSet.size()]);
        Arrays.sort(returnArray);
        return returnArray;
    }

    private JsonNode asList(String... items) {
        ArrayNode container = factory.arrayNode();
        for (String item : items) {
            container.add(item);
        }
        return container;
    }

    @Override
    public void addPersonal(String id, String firstName, String lastName, HsaKon kon, int age, List<String> befattning) {
        final ObjectNode personal = createPersonal(id, firstName, lastName, kon, age, befattning);
        personals.put(id, personal);
    }

    @Override
    public void setCountyForNextIntyg(String countyCode) {
        nextLanCode = countyCode;
    }

    @Override
    public void setHuvudenhetIdForNextIntyg(String huvudenhetId) {
        nextHuvudenhetId = huvudenhetId;
    }

    @Override
    public void setEnhetNameForNextIntyg(String name) {
        nextEnhetName = name;
    }

    @Override
    public void setHsaKey(HSAKey hsaKey) {
        this.hsaKey = hsaKey;
    }

    @Override
    public VpwGetPublicUnitsResponseType vpwGetPublicUnits(AttributedURIType logicalAddress, AttributedURIType id, VpwGetPublicUnitsType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetCareUnitResponseType getCareUnit(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetStatisticsPersonResponseType getStatisticsPerson(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsPersonType parameters) throws HsaWsFault {
        GetStatisticsPersonResponseType resp = new GetStatisticsPersonResponseType();
        String hsaId = parameters.getHsaIdentity();
        resp.setHsaIdentity(hsaId);
        JsonNode personal = getOrCreatePersonal(getHsaKey(hsaId));
        resp.setGender(personal.get("kon").textValue());
        resp.setAge(personal.get("alder").textValue());
        GetStatisticsPersonResponseType.PaTitleCodes titleCodes = new GetStatisticsPersonResponseType.PaTitleCodes();
        Iterator<JsonNode> befattnings = personal.get("befattning").iterator();
        while (befattnings.hasNext()) {
            JsonNode befattning = befattnings.next();
            titleCodes.getPaTitleCode().add(befattning.textValue());
        }
        resp.setPaTitleCodes(titleCodes);
        return resp;
    }

    @Override
    public IsAuthorizedToSystemResponseType isAuthorizedToSystem(AttributedURIType logicalAddress, AttributedURIType id, IsAuthorizedToSystemType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetCareUnitListResponseType getCareUnitList(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetHospLastUpdateResponseType getHospLastUpdate(AttributedURIType logicalAddress, AttributedURIType id, GetHospLastUpdateType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetHsaUnitResponseType getHsaUnit(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetPriceUnitsForAuthResponseType getPriceUnitsForAuth(AttributedURIType logicalAddress, AttributedURIType id, GetPriceUnitsForAuthType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetHsaPersonResponseType getHsaPerson(AttributedURIType logicalAddress, AttributedURIType id, GetHsaPersonType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetStatisticsNamesResponseType getStatisticsNames(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsNamesType parameters) throws HsaWsFault {
        String hsaid = parameters.getHsaIdentities().getHsaIdentity().get(0);
        GetStatisticsNamesResponseType resp = new GetStatisticsNamesResponseType();
        GetStatisticsNamesResponseType.StatisticsNameInfos nameInfos = new GetStatisticsNamesResponseType.StatisticsNameInfos();
        StatisticsNameInfo nameInfo = new StatisticsNameInfo();
        JsonNode personal = getOrCreatePersonal(getHsaKey(hsaid));
        nameInfo.setPersonGivenName(personal.get("tilltalsnamn").textValue());
        nameInfo.setPersonMiddleAndSurName(personal.get("efternamn").textValue());
        nameInfo.setHsaIdentity(hsaid);
        nameInfos.getStatisticsNameInfo().add(nameInfo);
        resp.setStatisticsNameInfos(nameInfos);
        return resp;
    }

    private HSAKey getHsaKey(String hsaid) {
        if (this.hsaKey != null) {
            return this.hsaKey;
        }
        return new HSAKey(hsaid, hsaid, hsaid);
    }

    @Override
    public PingResponseType ping(AttributedURIType logicalAddress, AttributedURIType id, PingType parameters) throws HsaWsFault {
        // Is part of HSAWebServiceCalls but is never used
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetMiuForPersonResponseType getMiuForPerson(AttributedURIType logicalAddress, AttributedURIType id, GetMiuForPersonType parameters) throws HsaWsFault {
        GetMiuForPersonResponseType response = new GetMiuForPersonResponseType();

        for (Medarbetaruppdrag medarbetaruppdrag : hsaServiceStub.getMedarbetaruppdrag()) {
            if (medarbetaruppdrag.getHsaId().getId().equals(parameters.getHsaIdentity())) {
                response.getMiuInformation().addAll(
                        miuInformationTypesForEnhetsIds(medarbetaruppdrag));
            }
        }
        return response;
    }

    private List<MiuInformationType> miuInformationTypesForEnhetsIds(Medarbetaruppdrag medarbetaruppdrag) {
        List<MiuInformationType> informationTypes = new ArrayList<>();

        for (Vardenhet enhet : hsaServiceStub.getVardenhets()) {
            if (medarbetaruppdrag.getEnhetIds().contains(enhet.getId())) {
                MiuInformationType miuInfo = new MiuInformationType();
                miuInfo.setHsaIdentity(medarbetaruppdrag.getHsaId().getId());
                miuInfo.setMiuPurpose(medarbetaruppdrag.getAndamal());
                miuInfo.setCareUnitHsaIdentity(enhet.getId().getId());
                miuInfo.setCareUnitName(enhet.getNamn());
                miuInfo.setCareGiver(enhet.getVardgivarId().getId());
                miuInfo.setCareGiverName(enhet.getVardgivarNamn());
                informationTypes.add(miuInfo);
            }
        }

        return informationTypes;
    }

    @Override
    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsCareGiverType parameters) throws HsaWsFault {
        String hsaid = parameters.getHsaIdentity();
        GetStatisticsCareGiverResponseType resp = new GetStatisticsCareGiverResponseType();
        resp.setHsaIdentity(hsaid);
        return resp;
    }

    @Override
    public HsawsSimpleLookupResponseType hsawsSimpleLookup(AttributedURIType logicalAddress, AttributedURIType id, HsawsSimpleLookupType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsHsaUnitType parameters) throws HsaWsFault {
        String hsaid = parameters.getHsaIdentity();
        HSAKey key = getHsaKey(hsaid);
        if (shouldEnhetExistInHsa(hsaid)) {
            GetStatisticsHsaUnitResponseType resp = new GetStatisticsHsaUnitResponseType();
            resp.setStatisticsUnit(createHsaUnit(key, false));
            resp.setStatisticsCareUnit(createHsaUnit(key, true));
            nextLanCode = null;
            nextEnhetName = null;
            nextHuvudenhetId = null;
            return resp;
        }

        nextLanCode = null;
        nextEnhetName = null;
        nextHuvudenhetId = null;
        return null;
    }

    public static boolean shouldEnhetExistInHsa(String enhetId) {
        return enhetId != null && !enhetId.startsWith("EJHSA") && !"UTANENHETSID".equals(enhetId);
    }

    private StatisticsHsaUnit createHsaUnit(HSAKey key, boolean isHuvudenhet) {
        JsonNode enhet = createEnhet(key, isHuvudenhet);
        StatisticsHsaUnit unit = new StatisticsHsaUnit();
        unit.setHsaIdentity(enhet.get("id").textValue());
        StatisticsHsaUnit.BusinessTypes businessTypes = new StatisticsHsaUnit.BusinessTypes();
        businessTypes.getBusinessType().add(VerksamhetsTyp.VARDCENTRAL_ID);
        unit.setBusinessTypes(businessTypes);
        StatisticsHsaUnit.Managements value = new StatisticsHsaUnit.Managements();
        value.getManagement().add("Landsting/Region");
        unit.setManagements(value);
        GeoCoord geoCoord = new GeoCoord();
        geoCoord.setType(GeoCoordEnum.RT_90);
        geoCoord.setE("1");
        geoCoord.setN("1");
        geoCoord.setX("1");
        geoCoord.setY("1");
        unit.setGeographicalCoordinatesRt90(geoCoord);
        String lan = createLan(key);
        unit.setCountyCode(lan);
        unit.setCounty(LAN.getNamn(lan));
        String kommun = createKommun(key, lan);
        unit.setMunicipalityCode(kommun);
        unit.setMunicipality(KOMMUN.getNamn(kommun));
        unit.setCareGiverHsaIdentity(enhet.get("vgid").textValue());
        StatisticsHsaUnit.CareTypes careTypes = new StatisticsHsaUnit.CareTypes();
        String[] verksamhet = createVerksamhet(key);
        StatisticsHsaUnit.BusinessClassificationCodes classificationCodes = new StatisticsHsaUnit.BusinessClassificationCodes();
        classificationCodes.getBusinessClassificationCode().addAll(Arrays.asList(verksamhet));
        unit.setBusinessClassificationCodes(classificationCodes);
        return unit;
    }

    @Override
    public GetCareUnitMembersResponseType getCareUnitMembers(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetHospPersonResponseType getHospPerson(AttributedURIType logicalAddress, AttributedURIType id, GetHospPersonType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public GetInformationListResponseType getInformationList(AttributedURIType logicalAddress, AttributedURIType id, GetInformationListType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

    @Override
    public HandleCertifierResponseType handleCertifier(AttributedURIType logicalAddress, AttributedURIType id, HandleCertifierType parameters) throws HsaWsFault {
        throw new RuntimeException("This method is not used by Statistiktjansten and has therefore not been implemented");
    }

}
