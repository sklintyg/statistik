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

import se.inera.statistics.service.report.api.RollingLength;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.db.SjukfallslangdKey;
import se.inera.statistics.service.report.model.db.SjukfallslangdRow;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppPersistenceHandler implements SjukfallslangdGrupp {
    private static final int LONG_SICKLEAVE_CUTOFF = 91;
    private static final int LONG_SICKLEAVE_DISPLAY_LENGTH = 18;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public SjukfallslangdResponse getHistoricalStatistics(String hsaId, LocalDate when, RollingLength length) {
        TypedQuery<SjukfallslangdRow> query = manager.createQuery("SELECT a FROM SjukfallslangdRow a WHERE a.key.hsaId = :hsaId AND a.key.period = :when AND a.key.periods = :periods ", SjukfallslangdRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("when", ReportUtil.toPeriod(when));
        query.setParameter("periods", length.getPeriods());

        return translateForOutput(query.getResultList(), length.getPeriods());
    }

    @Override
    public SjukfallslangdResponse getCurrentStatistics(String hsaId) {
        return getHistoricalStatistics(hsaId, new LocalDate(), RollingLength.SINGLE_MONTH);
    }

    @Override
    public SimpleKonResponse<SimpleKonDataRow> getLongSickLeaves(String hsaId, Range range) {
        TypedQuery<SimpleKonDataRow> query = manager.createQuery("SELECT new se.inera.statistics.service.report.model.SimpleKonDataRow(r.key.period, SUM(r.female), SUM(r.male)) FROM SjukfallslangdRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period BETWEEN :from AND :to AND r.key.grupp IN :grupper group by r.key.period", SimpleKonDataRow.class);

        List<Ranges.Range> ranges = SjukfallslangdUtil.RANGES.lookupRangesLongerThan(LONG_SICKLEAVE_CUTOFF);
        List<String> names = new ArrayList<>(ranges.size());
        for (se.inera.statistics.service.report.util.Ranges.Range r: ranges) {
            names.add(r.getName());
        }
        query.setParameter("periods", 1);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        query.setParameter("grupper", names);

        List<SimpleKonDataRow> rows = query.getResultList();
        List<SimpleKonDataRow> sexRows = new ArrayList<>();
        LocalDate date = range.getFrom();
        while (!date.isAfter(range.getTo())) {
            String key = ReportUtil.toPeriod(date);
            SimpleKonDataRow row = lookupSimpleDualSexDataRow(key, rows);
            sexRows.add(row);
            date = date.plusMonths(1);
        }

        return new SimpleKonResponse<>(sexRows, LONG_SICKLEAVE_DISPLAY_LENGTH);
    }

    private SimpleKonDataRow lookupSimpleDualSexDataRow(String key, List<SimpleKonDataRow> rows) {
        for (SimpleKonDataRow row : rows) {
            if (key.equals(row.getName())) {
                return row;
            }
        }
        return new SimpleKonDataRow(key, 0, 0);
    }

    private SjukfallslangdResponse translateForOutput(List<SjukfallslangdRow> list, int periods) {
        List<SjukfallslangdRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (se.inera.statistics.service.report.util.Ranges.Range s: SjukfallslangdUtil.RANGES) {
            String group = s.getName();
            for (SjukfallslangdRow r: list) {
                if (group.equals(r.getGroup())) {
                    translatedCasesPerMonthRows.add(r);
                }
            }
        }

        return new SjukfallslangdResponse(translatedCasesPerMonthRows, periods);
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Kon sex) {
        SjukfallslangdRow existingRow = manager.find(SjukfallslangdRow.class, new SjukfallslangdKey(period, hsaId, group, length.getPeriods()));
        int female = Kon.Female.equals(sex) ? 1 : 0;
        int male = Kon.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            SjukfallslangdRow row = new SjukfallslangdRow(period, hsaId, group, length.getPeriods(), typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public void recount(String period, String hsaId, String group, String newGroup, RollingLength length, Verksamhet typ, Kon sex) {
        count(period, hsaId, newGroup, length, typ, sex);
        SjukfallslangdRow existingRow = manager.find(SjukfallslangdRow.class, new SjukfallslangdKey(period, hsaId, group, length.getPeriods()));
        int female = Kon.Female.equals(sex) ? 1 : 0;
        int male = Kon.Male.equals(sex) ? 1 : 0;

        if (existingRow != null) {
            existingRow.setFemale(existingRow.getFemale() - female);
            existingRow.setMale(existingRow.getMale() - male);
            if (existingRow.getFemale() == 0 && existingRow.getMale() == 0) {
                manager.remove(existingRow);
            } else {
                manager.merge(existingRow);
            }
        }
    }

}
