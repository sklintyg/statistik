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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.model.Diagnosgrupp;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.DiagnosUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnossubgroupPersistenceHandler implements Diagnoskapitel {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private DiagnosUtil diagnosUtil;

    @Transactional
    public void count(String hsaId, String period, String diagnosgrupp, String undergrupp, Verksamhet typ, Kon sex) {
        DiagnosisSubGroupData existingRow = manager.find(DiagnosisSubGroupData.class, new DiagnosundergruppKey(period, hsaId, diagnosgrupp, undergrupp));
        int female = Kon.Female.equals(sex) ? 1 : 0;
        int male = Kon.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            DiagnosisSubGroupData row = new DiagnosisSubGroupData(period, hsaId, diagnosgrupp, undergrupp, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public DiagnosgruppResponse getDiagnosisGroups(String hsaId, Range range, String group) {
        TypedQuery<DiagnosisSubGroupData> query = manager.createQuery("SELECT c FROM DiagnosisSubGroupData c WHERE c.key.hsaId = :hsaId AND c.key.diagnosgrupp = :group and c.key.period BETWEEN :from AND :to", DiagnosisSubGroupData.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("group", group);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        List<Diagnosgrupp> header = diagnosUtil.getSubGroups(group);
        return new DiagnosgruppResponse(header, translateForOutput(range, header, query.getResultList()));
    }

    private List<KonDataRow> translateForOutput(Range range, List<Diagnosgrupp> header, List<DiagnosisSubGroupData> list) {
        List<KonDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        Map<String, KonField> map = map(list);

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = ReportUtil.toDiagramPeriod(currentPeriod);
            String period = ReportUtil.toPeriod(currentPeriod);
            List<KonField> values = new ArrayList<>(header.size());
            for (Diagnosgrupp group: header) {
                values.add(map.get(period + group.getId()));
            }
            translatedCasesPerMonthRows.add(new KonDataRow(displayDate, values));
        }
        return translatedCasesPerMonthRows;
    }

    private static Map<String, KonField> map(List<DiagnosisSubGroupData> list) {
        Map<String, KonField> resultMap = new DefaultHashMap<>(new KonField(0, 0));

        for (DiagnosisSubGroupData item: list) {
            resultMap.put(item.getPeriod() + item.getSubGroup(), new KonField(item.getFemale(), item.getMale()));
        }
        return resultMap;
    }

}
