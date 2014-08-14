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

package se.inera.statistics.hsa.stub;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.AttributeValueListType;
import se.inera.ifv.hsawsresponder.v3.AttributeValuePairType;
import se.inera.ifv.hsawsresponder.v3.CareUnitType;
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
import se.inera.ifv.hsawsresponder.v3.VpwGetPublicUnitsResponseType;
import se.inera.ifv.hsawsresponder.v3.VpwGetPublicUnitsType;
import se.inera.statistics.hsa.model.Vardenhet;

/**
 * @author johannesc
 */
public class HsaWebServiceStub implements HsaWsResponderInterface {

    @Autowired
    private HsaServiceStub hsaService;

    @Override
    public GetHsaUnitResponseType getHsaUnit(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        GetHsaUnitResponseType response = new GetHsaUnitResponseType();

        Vardenhet enhet = hsaService.getVardenhet(parameters.getHsaIdentity());
        if (enhet != null) {
            response.setHsaIdentity(enhet.getId());
            response.setName(enhet.getNamn());
            return response;
        }

        return response;

    }

    /**
     * Method used to get miuRights for a HoS Person.
     */
    @Override
    public GetMiuForPersonResponseType getMiuForPerson(AttributedURIType logicalAddress, AttributedURIType id, GetMiuForPersonType parameters) throws HsaWsFault {
        GetMiuForPersonResponseType response = new GetMiuForPersonResponseType();

        for (Medarbetaruppdrag medarbetaruppdrag : hsaService.getMedarbetaruppdrag()) {
            if (medarbetaruppdrag.getHsaId().equals(parameters.getHsaIdentity())) {
                response.getMiuInformation().addAll(
                        miuInformationTypesForEnhetsIds(medarbetaruppdrag));
            }
        }
        return response;
    }

    @Override
    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType logicalAddress, @WebParam(partName = "Id", name = "MessageID", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType id, @WebParam(partName = "parameters", name = "GetStatisticsCareGiver", targetNamespace = "urn:riv:hsa:HsaWsResponder:3") GetStatisticsCareGiverType parameters) throws HsaWsFault {
        return null;
    }

    private List<MiuInformationType> miuInformationTypesForEnhetsIds(Medarbetaruppdrag medarbetaruppdrag) {
        List<MiuInformationType> informationTypes = new ArrayList<>();

        for (Vardenhet enhet : hsaService.getVardenhets()) {
            if (medarbetaruppdrag.getEnhetIds().contains(enhet.getId())) {
                MiuInformationType miuInfo = new MiuInformationType();
                miuInfo.setHsaIdentity(medarbetaruppdrag.getHsaId());
                miuInfo.setMiuPurpose(medarbetaruppdrag.getAndamal());
                miuInfo.setCareUnitHsaIdentity(enhet.getId());
                miuInfo.setCareUnitName(enhet.getNamn());
                miuInfo.setCareGiver(enhet.getVardgivarId());
                miuInfo.setCareGiverName(enhet.getVardgivarNamn());
                informationTypes.add(miuInfo);
            }
        }

        return informationTypes;
    }

    /**
     * Returns work place code.
     */
    @Override
    public HsawsSimpleLookupResponseType hsawsSimpleLookup(AttributedURIType logicalAddress, AttributedURIType id,
            HsawsSimpleLookupType parameters) throws HsaWsFault {

        HsawsSimpleLookupResponseType response = new HsawsSimpleLookupResponseType();

        if (parameters.getLookup().getSearchAttribute().equals("hsaIdentity")) {
            response.getResponseValues().add(createAttributeValueListForEnhet(parameters.getLookup().getValue()));
        }

        return response;
    }

    @Override
    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType logicalAddress, @WebParam(partName = "Id", name = "MessageID", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType id, @WebParam(partName = "parameters", name = "GetStatisticsHsaUnit", targetNamespace = "urn:riv:hsa:HsaWsResponder:3") GetStatisticsHsaUnitType parameters) throws HsaWsFault {
        return null;
    }

    private AttributeValueListType createAttributeValueListForEnhet(String enhetsId) {

        AttributeValueListType attributeList = new AttributeValueListType();
        attributeList.setDN(enhetsId);

        AttributeValuePairType identityValue = new AttributeValuePairType();
        identityValue.setAttribute("hsaIdentity");
        identityValue.getValue().add(enhetsId);
        attributeList.getResponse().add(identityValue);


        return attributeList;
    }

    /**
     * Method to retrieve data for a hsa unit.
     */
    @Override
    public GetCareUnitResponseType getCareUnit(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetStatisticsPersonResponseType getStatisticsPerson(@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType logicalAddress, @WebParam(partName = "Id", name = "MessageID", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType id, @WebParam(partName = "parameters", name = "GetStatisticsPerson", targetNamespace = "urn:riv:hsa:HsaWsResponder:3") GetStatisticsPersonType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public IsAuthorizedToSystemResponseType isAuthorizedToSystem(AttributedURIType logicalAddress,
            AttributedURIType id, IsAuthorizedToSystemType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public VpwGetPublicUnitsResponseType vpwGetPublicUnits(AttributedURIType logicalAddress, AttributedURIType id,
            VpwGetPublicUnitsType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetCareUnitListResponseType getCareUnitList(AttributedURIType logicalAddress, AttributedURIType id,
            LookupHsaObjectType parameters) throws HsaWsFault {

        GetCareUnitListResponseType response = new GetCareUnitListResponseType();

        for (Vardenhet enhet : hsaService.getVardenhets()) {
            if (enhet.getId().equals(parameters.getSearchBase())) {

                CareUnitType careUnit = new CareUnitType();
                careUnit.setHsaIdentity(enhet.getId());
                careUnit.setCareUnitName(enhet.getNamn());
                response.setCareUnits(new GetCareUnitListResponseType.CareUnits());
                response.getCareUnits().getCareUnit().add(careUnit);

                return response;
            }
        }
        return response;
    }

    @Override
    public GetHospLastUpdateResponseType getHospLastUpdate(@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType logicalAddress, @WebParam(partName = "Id", name = "MessageID", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType id, @WebParam(partName = "parameters", name = "GetHospLastUpdate", targetNamespace = "urn:riv:hsa:HsaWsResponder:3") GetHospLastUpdateType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetPriceUnitsForAuthResponseType getPriceUnitsForAuth(AttributedURIType logicalAddress,
            AttributedURIType id, GetPriceUnitsForAuthType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetHsaPersonResponseType getHsaPerson(AttributedURIType logicalAddress, AttributedURIType id,
            GetHsaPersonType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetStatisticsNamesResponseType getStatisticsNames(@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType logicalAddress, @WebParam(partName = "Id", name = "MessageID", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType id, @WebParam(partName = "parameters", name = "GetStatisticsNames", targetNamespace = "urn:riv:hsa:HsaWsResponder:3") GetStatisticsNamesType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public PingResponseType ping(AttributedURIType logicalAddress, AttributedURIType id, PingType parameters)
            throws HsaWsFault {
        return null;
    }

    @Override
    public GetCareUnitMembersResponseType getCareUnitMembers(AttributedURIType logicalAddress, AttributedURIType id,
            LookupHsaObjectType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetHospPersonResponseType getHospPerson(AttributedURIType logicalAddress, AttributedURIType id,
            GetHospPersonType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetInformationListResponseType getInformationList(AttributedURIType logicalAddress, AttributedURIType id,
            GetInformationListType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public HandleCertifierResponseType handleCertifier(@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType logicalAddress, @WebParam(partName = "Id", name = "MessageID", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType id, @WebParam(partName = "parameters", name = "HandleCertifier", targetNamespace = "urn:riv:hsa:HsaWsResponder:3") HandleCertifierType parameters) throws HsaWsFault {
        return null;
    }
}
