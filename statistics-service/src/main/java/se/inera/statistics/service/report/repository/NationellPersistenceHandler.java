package se.inera.statistics.service.report.repository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.service.report.util.Verksamhet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Component
public class NationellPersistenceHandler implements NationellUpdater {
    private static final String NATIONELL = Verksamhet.NATIONELL.name();

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;


    @Override
    @Transactional
    public void updateCasesPerMonth() {
        Query delete = manager.createQuery("DELETE FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s  SELECT PERIOD, 'NATIONELL', 'NATIONELL',  sum(FEMALE), sum(MALE) FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD", CasesPerMonthRow.TABLE));
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateAldersgrupp() {
        Query delete = manager.createQuery("DELETE FROM AgeGroupsRow c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s  SELECT PERIOD, 'NATIONELL', 'NATIONELL', grupp, periods, sum(FEMALE), sum(MALE) FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, grupp, periods", AgeGroupsRow.TABLE));
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateDiagnosgrupp() {
        Query delete = manager.createQuery("DELETE FROM DiagnosisGroupData c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s  SELECT PERIOD, 'NATIONELL', 'NATIONELL', diagnosgrupp, sum(FEMALE), sum(MALE) FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, diagnosgrupp", DiagnosisGroupData.TABLE));
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateDiagnosundergrupp() {
        Query delete = manager.createQuery("DELETE FROM DiagnosisSubGroupData c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s  SELECT PERIOD, 'NATIONELL', 'NATIONELL', diagnosgrupp, undergrupp, sum(FEMALE), sum(MALE) FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, diagnosgrupp, undergrupp", DiagnosisSubGroupData.TABLE));
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateSjukfallslangd() {
        Query delete = manager.createQuery("DELETE FROM SickLeaveLengthRow c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s  SELECT PERIOD, 'NATIONELL', 'NATIONELL', grupp, periods, sum(FEMALE), sum(MALE) FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, grupp, periods", SickLeaveLengthRow.TABLE));
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateSjukskrivningsgrad() {
        Query delete = manager.createQuery("DELETE FROM SjukskrivningsgradData c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s  SELECT PERIOD, 'NATIONELL', 'NATIONELL', grad, sum(FEMALE), sum(MALE) FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, grad", SjukskrivningsgradData.TABLE));
        update.executeUpdate();
    }

}
