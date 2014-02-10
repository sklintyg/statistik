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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.model.db.SjukfallPerLanKey;
import se.inera.statistics.service.report.model.db.SjukfallPerLanRow;
import se.inera.statistics.service.report.util.ReportUtil;

public class SjukfallPerLanPersistenceHandler implements SjukfallPerLan {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private Lan lans;

    @Override
    @Transactional
    public SimpleDualSexResponse<SimpleDualSexDataRow> getStatistics(Range range) {
        TypedQuery<SjukfallPerLanRow> query = manager.createQuery("SELECT new SjukfallPerLanRow(c.key.period, 'dummy', c.key.lanId, SUM (c.female), sum (c.male)) FROM SjukfallPerLanRow c WHERE c.key.period = :to GROUP BY c.key.period, c.key.lanId", SjukfallPerLanRow.class);
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return translateForOutput(range, query.getResultList());
    }

    @Override
    @Transactional
    public void count(String period, String enhetId, String lanId, RollingLength length, Sex kon) {
        SjukfallPerLanRow existingRow = manager.find(SjukfallPerLanRow.class, new SjukfallPerLanKey(period, enhetId, lanId));
        int female = Sex.Female.equals(kon) ? 1 : 0;
        int male = Sex.Male.equals(kon) ? 1 : 0;

        if (existingRow == null) {
            SjukfallPerLanRow row = new SjukfallPerLanRow(period, enhetId, lanId, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale((int) (existingRow.getFemale() + female));
            existingRow.setMale((int) (existingRow.getMale() + male));
            manager.merge(existingRow);
        }
    }

    private SimpleDualSexResponse<SimpleDualSexDataRow> translateForOutput(Range range, List<SjukfallPerLanRow> list) {
        List<SimpleDualSexDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        Map<String, DualSexField> map = new DefaultHashMap<>(new DualSexField(0, 0));
        for (SjukfallPerLanRow row: list) {
            map.put(row.getLanId(), new DualSexField((int) row.getFemale(), (int) row.getMale()));
        }

        for (String lanId : lans) {
            String displayLan = lans.getNamn(lanId);
            DualSexField dualSexField = map.get(lanId);
            translatedCasesPerMonthRows.add(new SimpleDualSexDataRow(displayLan, dualSexField.getFemale(), dualSexField.getMale()));
        }

        return new SimpleDualSexResponse<>(translatedCasesPerMonthRows, range.getMonths());
    }

}
