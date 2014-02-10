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

package se.inera.statistics.service.report.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.model.db.AgeGroupsRow;
import se.inera.statistics.service.report.model.db.CasesPerMonthRow;
import se.inera.statistics.service.report.model.db.SickLeaveLengthRow;
import se.inera.statistics.service.report.util.Verksamhet;

public class NationellPersistenceHandler implements NationellUpdater {
    private static final String INSERT_PREFIX = "INSERT INTO %1$s SELECT PERIOD, 'NATIONELL', 'NATIONELL',";

    private static final String NATIONELL = Verksamhet.NATIONELL.name();

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Value("${national.cutoff.limit:5}")
    private int cutoff;

    @Override
    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

    @Override
    @Transactional
    public void updateCasesPerMonth() {
        Query delete = manager.createQuery("DELETE FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();

        Query update = manager.createNativeQuery(String.format(INSERT_PREFIX + " CASE WHEN SUM(FEMALE) >= :cutoff THEN SUM(FEMALE) ELSE 0 END, CASE WHEN SUM(MALE) >= :cutoff THEN SUM(MALE) ELSE 0 END FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD", CasesPerMonthRow.TABLE));
        update.setParameter("cutoff", cutoff);
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateAldersgrupp() {
        delete("AgeGroupsRow");

        Query update = manager.createNativeQuery(String.format(INSERT_PREFIX + " grupp, periods, CASE WHEN SUM(FEMALE) >= :cutoff THEN SUM(FEMALE) ELSE 0 END, CASE WHEN SUM(MALE) >= :cutoff THEN SUM(MALE) ELSE 0 END FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, grupp, periods", AgeGroupsRow.TABLE));
        update.setParameter("cutoff", cutoff);
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateDiagnosgrupp() {
        delete("DiagnosisGroupData");

        Query update = manager.createNativeQuery(String.format(INSERT_PREFIX + " diagnosgrupp, CASE WHEN SUM(FEMALE) >= :cutoff THEN SUM(FEMALE) ELSE 0 END, CASE WHEN SUM(MALE) >= :cutoff THEN SUM(MALE) ELSE 0 END FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, diagnosgrupp", DiagnosisGroupData.TABLE));
        update.setParameter("cutoff", cutoff);
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateDiagnosundergrupp() {
        delete("DiagnosisSubGroupData");

        Query update = manager.createNativeQuery(String.format(INSERT_PREFIX + " diagnosgrupp, undergrupp, CASE WHEN SUM(FEMALE) >= :cutoff THEN SUM(FEMALE) ELSE 0 END, CASE WHEN SUM(MALE) >= :cutoff THEN SUM(MALE) ELSE 0 END FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, diagnosgrupp, undergrupp", DiagnosisSubGroupData.TABLE));
        update.setParameter("cutoff", cutoff);
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateSjukfallslangd() {
        delete("SickLeaveLengthRow");

        Query update = manager.createNativeQuery(String.format(INSERT_PREFIX + " grupp, periods, CASE WHEN SUM(female) >= :cutoff THEN SUM(FEMALE) ELSE 0 END, CASE WHEN SUM(MALE) >= :cutoff THEN SUM(MALE) ELSE 0 END FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, grupp, periods", SickLeaveLengthRow.TABLE));
        update.setParameter("cutoff", cutoff);
        update.executeUpdate();
    }

    @Override
    @Transactional
    public void updateSjukskrivningsgrad() {
        delete("SjukskrivningsgradData");

        Query update = manager.createNativeQuery(String.format("INSERT INTO %1$s SELECT PERIOD, 'NATIONELL', 'NATIONELL', grad, CASE WHEN SUM(FEMALE) >= :cutoff THEN SUM(FEMALE) ELSE 0 END, CASE WHEN SUM(MALE) >= :cutoff THEN SUM(MALE) ELSE 0 END FROM %1$s WHERE typ = 'VARDGIVARE' GROUP BY PERIOD, grad", SjukskrivningsgradData.TABLE));
        update.setParameter("cutoff", cutoff);
        update.executeUpdate();
    }

    private void delete(String type) {
        Query delete = manager.createQuery("DELETE FROM " + type + " c WHERE c.key.hsaId = :hsaId");
        delete.setParameter("hsaId", NATIONELL);
        delete.executeUpdate();
    }

}
