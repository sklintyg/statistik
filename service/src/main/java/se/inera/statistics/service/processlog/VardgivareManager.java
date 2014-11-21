package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.warehouse.WidelineConverter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class VardgivareManager {
    private static final Logger LOG = LoggerFactory.getLogger(VardgivareManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void saveEnhet(JsonNode hsaInfo) {
        String enhet = HSAServiceHelper.getEnhetId(hsaInfo);
        String vardgivare = HSAServiceHelper.getVardgivarId(hsaInfo);
        String enhetNamn = HSAServiceHelper.getEnhetNamn(hsaInfo);
        String vardgivareNamn = HSAServiceHelper.getVardgivarNamn(hsaInfo);
        String lansId = HSAServiceHelper.getLan(hsaInfo);
        String kommunId = HSAServiceHelper.getKommun(hsaInfo);
        String verksamhetsTyper = HSAServiceHelper.getVerksamhetsTyper(hsaInfo);

        if (enhet == null) {
            enhet = vardgivare;
        }
        if (enhetNamn == null) {
            enhetNamn = enhet;
        }
        if (vardgivareNamn == null) {
            vardgivareNamn = vardgivare;
        }

        if (validate(vardgivare, enhetNamn, vardgivareNamn, lansId, kommunId, verksamhetsTyper, hsaInfo)) {
            TypedQuery<Enhet> vardgivareQuery = manager.createQuery("SELECT v FROM Enhet v WHERE v.enhetId = :enhetId AND v.vardgivareId = :vardgivareId", Enhet.class);
            List<Enhet> resultList = vardgivareQuery.setParameter("enhetId", enhet).setParameter("vardgivareId", vardgivare).getResultList();

            if (resultList.isEmpty()) {
                manager.persist(new Enhet(vardgivare, vardgivareNamn, enhet, enhetNamn, lansId, kommunId, verksamhetsTyper));
            } else {
                Enhet updatedEnhet = resultList.get(0);
                updatedEnhet.setVardgivareNamn(vardgivareNamn);
                updatedEnhet.setNamn(enhetNamn);
                updatedEnhet.setLansId(lansId);
                updatedEnhet.setKommunId(kommunId);
                updatedEnhet.setVerksamhetsTyper(verksamhetsTyper);
                manager.merge(updatedEnhet);
            }
        }
    }

    @Transactional
    public List<Enhet> getEnhets(String vardgivare) {
        TypedQuery<Enhet> query = manager.createQuery("SELECT v FROM Enhet v WHERE v.vardgivareId = :vardgivareId", Enhet.class).setParameter("vardgivareId", vardgivare);
        return query.getResultList();
    }

    @Transactional
    public List<Enhet> getAllEnhets() {
        TypedQuery<Enhet> query = manager.createQuery("SELECT v FROM Enhet v", Enhet.class);
        return query.getResultList();
    }

    @Transactional
    public List<Vardgivare> getAllVardgivares() {
        TypedQuery<Vardgivare> query = manager.createQuery("SELECT DISTINCT new se.inera.statistics.service.processlog.Vardgivare(v.vardgivareId, v.vardgivareNamn) FROM Enhet v", Vardgivare.class);
        return query.getResultList();
    }

    private boolean validate(String vardgivare, String enhetNamn, String vardgivareNamn, String lansId, String kommunId, String verksamhetsTyper, JsonNode hsaInfo) {
        // Utan vardgivare har vi inget uppdrag att behandla intyg, avbryt direkt
        if (vardgivare == null) {
            LOG.error("Vardgivare saknas: " + hsaInfo.asText());
            return false;
        }
        if (vardgivare.length() > WidelineConverter.MAX_LENGTH_VGID) {
            LOG.error("Vardgivare saknas: " + hsaInfo.asText());
            return false;
        }
        boolean result = checkLength(enhetNamn, "Enhetsnamn", WidelineConverter.MAX_LENGTH_ENHETNAME, hsaInfo);
        result |= checkLength(vardgivareNamn, "Vardgivarnamn", WidelineConverter.MAX_LENGTH_VARDGIVARE_NAMN, hsaInfo);
        result |= lansId != null && checkLength(lansId, "Lansid", WidelineConverter.MAX_LENGTH_LAN_ID, hsaInfo);
        result |= kommunId != null && checkLength(kommunId, "Kommunid", WidelineConverter.MAX_LENGTH_KOMMUN_ID, hsaInfo);
        result |= verksamhetsTyper != null && checkLength(verksamhetsTyper, "Verksamhetstyper", WidelineConverter.MAX_LENGTH_VERKSAMHET_TYP, hsaInfo);
        return result;
    }

    private boolean checkLength(String field, String name, int max, JsonNode hsaInfo) {
        if (field != null && field.length() > max) {
            LOG.error(name + " saknas: " + hsaInfo.asText());
            return false;
        }
        return true;
    }

}
