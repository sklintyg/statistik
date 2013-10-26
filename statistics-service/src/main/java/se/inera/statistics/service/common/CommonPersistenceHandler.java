package se.inera.statistics.service.common;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CommonPersistenceHandler implements CommonPersistence {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    @Override
    public void cleanDb() {

        delete("IntygEvent");
        delete("HSAStore");
        delete("EventPointer");
        delete("CasesPerMonthRow");
        delete("DiagnosisGroupData");
        delete("DiagnosisSubGroupData");
        delete("AgeGroupsRow");
        delete("SickLeaveLengthRow");
        delete("SjukfallPerLanRow");
        delete("SjukskrivningsgradData");
        delete("Sjukfall");
    }

    private void delete(String entity) {
        Query query = manager.createQuery("delete from " + entity);
        query.executeUpdate();
    }

}
