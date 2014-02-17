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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.db.SjukfallPerManadKey;
import se.inera.statistics.service.report.model.db.SjukfallPerManadRow;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallPerManadPersistenceHandler implements SjukfallPerManad {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void count(String hsaId, String period, Verksamhet typ, Kon sex) {
        SjukfallPerManadRow existingRow = manager.find(SjukfallPerManadRow.class, new SjukfallPerManadKey(period, hsaId));
        int female = Kon.Female.equals(sex) ? 1 : 0;
        int male = Kon.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            SjukfallPerManadRow row = new SjukfallPerManadRow(period, hsaId, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(String hsaId, Range range) {
        TypedQuery<SjukfallPerManadRow> query = manager.createQuery("SELECT c FROM SjukfallPerManadRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to", SjukfallPerManadRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return translateForOutput(range, query.getResultList());
    }

    private SimpleKonResponse<SimpleKonDataRow> translateForOutput(Range range, List<SjukfallPerManadRow> list) {
        List<SimpleKonDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        Map<String, KonField> map = new DefaultHashMap<>(new KonField(0, 0));
        for (SjukfallPerManadRow row: list) {
            map.put(row.getPeriod(), new KonField(row.getFemale(), row.getMale()));
        }

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = ReportUtil.toDiagramPeriod(currentPeriod);
            String period = ReportUtil.toPeriod(currentPeriod);
            KonField konField = map.get(period);
            translatedCasesPerMonthRows.add(new SimpleKonDataRow(displayDate, konField.getFemale(), konField.getMale()));
        }

        return new SimpleKonResponse<SimpleKonDataRow>(translatedCasesPerMonthRows, range.getMonths());
    }
}
