/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.helper.certificate.JsonDocumentHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.warehouse.WidelineConverter;

@Component
public class VardgivareManager {

    private static final Logger LOG = LoggerFactory.getLogger(VardgivareManager.class);
    public static final HsaIdEnhet UTAN_ENHETSID = new HsaIdEnhet(JsonDocumentHelper.UTANENHETSID);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void saveEnhet(HsaInfo hsaInfo, String enhetIdFromIntyg) {
        boolean hsaEnhet = true;
        String huvudenhetIdString = HSAServiceHelper.getHuvudEnhetId(hsaInfo);
        String underenhetIdString = (hsaInfo == null || hsaInfo.getEnhet() == null) ? null : hsaInfo.getEnhet().getId();
        String enhetIdString = underenhetIdString != null ? underenhetIdString : huvudenhetIdString;
        if (enhetIdString == null) {
            hsaEnhet = false;
            enhetIdString = enhetIdFromIntyg != null ? enhetIdFromIntyg.toUpperCase(Locale.ENGLISH) : null;
        }
        final HsaIdEnhet enhet = new HsaIdEnhet(enhetIdString);
        final HsaIdVardgivare vardgivare = HSAServiceHelper.getVardgivarId(hsaInfo);
        String enhetNamn = getEnhetNamn(enhet);
        String lansId = HSAServiceHelper.getLan(hsaInfo);
        String kommunId = HSAServiceHelper.getKommun(hsaInfo);
        String verksamhetsTyper = HSAServiceHelper.getVerksamhetsTyper(hsaInfo, false);

        if (validate(vardgivare, enhetNamn, lansId, kommunId, verksamhetsTyper, hsaInfo)) {
            if (huvudenhetIdString != null) {
                persistVardenhet(hsaInfo, huvudenhetIdString, vardgivare, lansId, kommunId);
            }

            // Must use 'LIKE' instead of '=' due to STATISTIK-1231
            TypedQuery<Enhet> vardgivareQuery = manager
                .createQuery("SELECT v FROM Enhet v WHERE v.enhetId LIKE :enhetId AND v.vardgivareId = :vardgivareId", Enhet.class);
            List<Enhet> resultList = vardgivareQuery.setParameter("enhetId", enhet.getId()).setParameter("vardgivareId", vardgivare.getId())
                .getResultList();

            final String huvudenhetId = huvudenhetIdString != null ? huvudenhetIdString.toUpperCase(Locale.ENGLISH) : null;
            if (resultList.isEmpty()) {
                manager.persist(new Enhet(vardgivare, enhet, enhetNamn, lansId, kommunId, verksamhetsTyper, huvudenhetId));
            } else if (hsaEnhet) {
                Enhet updatedEnhet = resultList.get(0);
                updatedEnhet.setLansId(lansId);
                updatedEnhet.setKommunId(kommunId);
                updatedEnhet.setVerksamhetsTyper(verksamhetsTyper);
                updatedEnhet.setVardenhetId(huvudenhetId);
                manager.merge(updatedEnhet);
            }
        }
    }

    private void persistVardenhet(HsaInfo hsaInfo, String huvudenhetIdString, HsaIdVardgivare vardgivare, String lansId, String kommunId) {
        // Must use 'LIKE' instead of '=' due to STATISTIK-1231
        TypedQuery<Enhet> vardenhetQuery = manager
                .createQuery("SELECT v FROM Enhet v WHERE v.enhetId LIKE :enhetId AND v.vardgivareId = :vardgivareId", Enhet.class);
        final HsaIdEnhet hsaIdVardenhet = new HsaIdEnhet(huvudenhetIdString);
        List<Enhet> resultListVe = vardenhetQuery
                .setParameter("enhetId", hsaIdVardenhet.getId())
                .setParameter("vardgivareId", vardgivare.getId())
                .getResultList();

        if (resultListVe.isEmpty()) {
            final String veVerksamheter = HSAServiceHelper.getVerksamhetsTyper(hsaInfo, true);
            final String veName = getEnhetNamn(hsaIdVardenhet);
            manager.persist(new Enhet(vardgivare, hsaIdVardenhet, veName, lansId, kommunId, veVerksamheter, null));
        }
    }

    private String getEnhetNamn(HsaIdEnhet enhet) {
        return UTAN_ENHETSID.equals(enhet) ? "Utan enhets-id" : enhet.getId();
    }

    @Transactional
    public List<Enhet> getEnhets(String vardgivare) {
        TypedQuery<Enhet> query = manager.createQuery("SELECT v FROM Enhet v WHERE v.vardgivareId = :vardgivareId", Enhet.class)
            .setParameter("vardgivareId", vardgivare);
        return query.getResultList();
    }

    @Transactional
    public List<Enhet> getAllEnhets() {
        TypedQuery<Enhet> query = manager.createQuery("SELECT v FROM Enhet v", Enhet.class);
        return query.getResultList();
    }

    private boolean validate(HsaIdVardgivare vardgivare, String enhetNamn, String lansId, String kommunId, String verksamhetsTyper,
        HsaInfo hsaInfo) {
        // Utan vardgivare har vi inget uppdrag att behandla intyg, avbryt direkt
        if (vardgivare == null || vardgivare.isEmpty()) {
            LOG.error("Vardgivare saknas: " + hsaInfo);
            return false;
        }
        if (vardgivare.getId().length() > WidelineConverter.MAX_LENGTH_VGID) {
            LOG.error("Vardgivare id ogiltigt: " + hsaInfo);
            return false;
        }
        boolean result = checkLength(enhetNamn, "Enhetsnamn", WidelineConverter.MAX_LENGTH_ENHETNAME, hsaInfo);
        result &= lansId != null && checkLength(lansId, "Lansid", WidelineConverter.MAX_LENGTH_LAN_ID, hsaInfo);
        result &= kommunId != null && checkLength(kommunId, "Kommunid", WidelineConverter.MAX_LENGTH_KOMMUN_ID, hsaInfo);
        result &= verksamhetsTyper != null
            && checkLength(verksamhetsTyper, "Verksamhetstyper", WidelineConverter.MAX_LENGTH_VERKSAMHET_TYP, hsaInfo);
        return result;
    }

    private boolean checkLength(String field, String name, int max, HsaInfo hsaInfo) {
        if (field != null && field.length() > max) {
            LOG.error(name + " saknas: " + hsaInfo);
            return false;
        }
        return true;
    }

}
