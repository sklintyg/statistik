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
package se.inera.ifv.statistics.spi.authorization.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3.wsaddressing10.AttributedURIType;
import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
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

    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(String careGiverId) {
        try {
            GetStatisticsCareGiverType parameters = new GetStatisticsCareGiverType();
            parameters.setHsaIdentity(careGiverId);
            parameters.setSearchBase("c=SE");
            return serverInterface.getStatisticsCareGiver(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsCareGiver for {} hsaWsFault ({}, {}). {}", careGiverId, faultInfo.getCode(), faultInfo.getMessage(), hsaWsFault.getMessage());
            LOG.debug("getStatisticsCareGiver fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsCareGiver for " + careGiverId, ex);
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
            LOG.debug("getStatisticsHsaUnit fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsHsaUnit for " + unitId, ex);
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
            LOG.debug("getStatisticsPerson fault", hsaWsFault);
            return null;
        } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }

    public GetStatisticsNamesResponseType getStatisticsNames(String personId) {
        try {
            GetStatisticsNamesType parameters = new GetStatisticsNamesType();
            GetStatisticsNamesType.HsaIdentities hsaIds = new GetStatisticsNamesType.HsaIdentities();
            hsaIds.getHsaIdentity().add(personId);
            parameters.setHsaIdentities(hsaIds);
            parameters.setSearchBase("c=SE");
            return serverInterface.getStatisticsNames(logicalAddressHeader, messageId, parameters);
        } catch (HsaWsFault hsaWsFault) {
            HsaWsFaultType faultInfo = hsaWsFault.getFaultInfo();
            LOG.error("Could not call getStatisticsNames for {} hsaWsFault ({}, {}). {}", personId, faultInfo.getCode(), faultInfo.getMessage(), hsaWsFault.getMessage());
            LOG.debug("getStatisticsNames fault", hsaWsFault);
            return null;
            } catch (Exception ex) {
            throw new HsaCommunicationException("Could not call getStatisticsPerson for " + personId, ex);
        }
    }
}
