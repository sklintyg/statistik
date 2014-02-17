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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.Aldersgrupp;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.db.AldersgruppKey;
import se.inera.statistics.service.report.model.db.AldersgruppRow;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges.Range;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class AldersgruppPersistenceHandler implements Aldersgrupp {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public SimpleKonResponse<SimpleKonDataRow> getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rolling) {
        TypedQuery<AldersgruppRow> query = manager.createQuery("SELECT a FROM AldersgruppRow a WHERE a.key.hsaId = :hsaId AND a.key.period = :when AND a.key.periods = :periods ", AldersgruppRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("when", ReportUtil.toPeriod(when));
        query.setParameter("periods", rolling.getPeriods());

        return translateForOutput(query.getResultList(), rolling.getPeriods());
    }

    @Override
    public SimpleKonResponse<SimpleKonDataRow> getCurrentAgeGroups(String hsaId) {
        return getHistoricalAgeGroups(hsaId, new LocalDate(), RollingLength.SINGLE_MONTH);
    }

    private SimpleKonResponse<SimpleKonDataRow> translateForOutput(List<AldersgruppRow> list, int periods) {
        List<SimpleKonDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (Range s: AldersgroupUtil.RANGES) {
            String group = s.getName();
            for (AldersgruppRow r: list) {
                if (group.equals(r.getGroup())) {
                    translatedCasesPerMonthRows.add(new SimpleKonDataRow(r.getGroup(), r.getFemale(), r.getMale()));
                }
            }
        }

        return new SimpleKonResponse<SimpleKonDataRow>(translatedCasesPerMonthRows, periods);
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Kon sex) {
        AldersgruppRow existingRow = manager.find(AldersgruppRow.class, new AldersgruppKey(period, hsaId, group, length.getPeriods()));
        int female = Kon.Female.equals(sex) ? 1 : 0;
        int male = Kon.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            AldersgruppRow row = new AldersgruppRow(period, hsaId, group, length.getPeriods(), typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }
}
