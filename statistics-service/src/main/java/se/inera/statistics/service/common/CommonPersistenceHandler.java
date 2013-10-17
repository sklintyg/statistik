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

        Query deleteIntygEvent = manager.createQuery("delete from IntygEvent ");
        deleteIntygEvent.executeUpdate();

        Query deleteCasesPerMonthRow = manager.createQuery("delete from CasesPerMonthRow ");
        deleteCasesPerMonthRow.executeUpdate();

        Query deleteDiagnosisGroupData = manager.createQuery("delete from DiagnosisGroupData ");
        deleteDiagnosisGroupData.executeUpdate();

        Query deleteDiagnosisSubGroupData = manager.createQuery("delete from DiagnosisSubGroupData ");
        deleteDiagnosisSubGroupData.executeUpdate();

        Query deleteEventPointer = manager.createQuery("delete from EventPointer ");
        deleteEventPointer.executeUpdate();

        Query deleteSjukfall = manager.createQuery("delete from Sjukfall ");
        deleteSjukfall.executeUpdate();

    }

}
