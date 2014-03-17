/*
 * Inera Medcert - Sjukintygsapplikation
 *
 * Copyright (C) 2010-2011 Inera AB (http://www.inera.se)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package se.inera.ifv.statistics.spi.authorization.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonType;
import se.inera.ifv.hsawsresponder.v3.HsaWsFaultType;
import se.inera.ifv.hsawsresponder.v3.PingResponseType;
import se.inera.ifv.hsawsresponder.v3.PingType;

import com.google.common.base.Throwables;

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
     *
     * @throws Exception
     */
    public void callPing() throws Exception {

        try {
            PingType pingtype = new PingType();
            PingResponseType response = serverInterface.ping(logicalAddressHeader, messageId, pingtype);
            LOG.debug("Response:" + response.getMessage());

        } catch (Throwable ex) {
            LOG.warn("Exception={}", ex.getMessage(), ex);
            throw new Exception(ex);
        }
    }

    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(String careGiverId) {
        try {
            GetStatisticsCareGiverType parameters = new GetStatisticsCareGiverType();
            parameters.setHsaIdentity(careGiverId);
            parameters.setSearchBase("c=SE");
            return serverInterface.getStatisticsCareGiver(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsCareGiver for {} hsaWsFault ({}, {}). {}", careGiverId, faultInfo.getCode(), faultInfo.getMessage(), hsaWsFault.getMessage());
            return null;
        } catch (Throwable ex) {
            throw new RuntimeException("Could not call getStatisticsCareGiver for " + careGiverId, ex);
        }
    }

    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(String unitId) {
        try {
            GetStatisticsHsaUnitType parameters = new GetStatisticsHsaUnitType();
            parameters.setHsaIdentity(unitId);
            parameters.setSearchBase("c=SE");
            parameters.setIncludeOrgNo(Boolean.TRUE);
            return serverInterface.getStatisticsHsaUnit(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsHsaUnit for {} hsaWsFault ({}, {}). {}", unitId, faultInfo.getCode(), faultInfo.getMessage(), hsaWsFault.getMessage());
            return null;
        } catch (Throwable ex) {
            throw new RuntimeException("Could not call getStatisticsHsaUnit for " + unitId, ex);
        }
    }

    public GetStatisticsPersonResponseType getStatisticsPerson(String personId) {
        try {
            GetStatisticsPersonType parameters = new GetStatisticsPersonType();
            parameters.setHsaIdentity(personId);
            parameters.setSearchBase("c=SE");
            return serverInterface.getStatisticsPerson(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsPerson for {} hsaWsFault ({}, {}). {}", personId, faultInfo.getCode(), faultInfo.getMessage(), hsaWsFault.getMessage());
            return null;
        } catch (Throwable ex) {
            throw new RuntimeException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }

    /**
     * Method used to get miuRights for a HoS Person.
     *
     * @param parameters
     * @return
     * @throws Exception
     */
    public GetMiuForPersonResponseType callMiuRights(GetMiuForPersonType parameters) {
        try {
            return serverInterface.getMiuForPerson(logicalAddressHeader, messageId, parameters);
        } catch (Throwable ex) {
            LOG.error("Failed to call getMiuForPerson", ex);
            Throwables.propagate(ex);
            return null;
        }
    }

}
