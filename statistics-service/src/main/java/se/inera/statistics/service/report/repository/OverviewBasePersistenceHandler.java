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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;

public class OverviewBasePersistenceHandler {
    public static final int PERCENT = 100;


    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    protected EntityManager getManager() {
        return manager;
    }

    protected int getLongSickLeaves(String verksamhetId, Range range, int cutoff) {
        TypedQuery<Long> query = getManager().createQuery("SELECT sum(r.female) + sum(r.male) FROM SjukfallslangdRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period = :period AND r.key.grupp IN :grupper", Long.class);
        query.setParameter("periods", range.getMonths());
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("period", ReportUtil.toPeriod(range.getTo()));
        query.setParameter("grupper", names(SjukfallslangdUtil.RANGES.lookupRangesLongerThan(cutoff)));

        return asInt(query.getSingleResult());
    }

    private List<String> names(List<se.inera.statistics.service.report.util.Ranges.Range> ranges) {
        List<String> names = new ArrayList<>(ranges.size());
        for (se.inera.statistics.service.report.util.Ranges.Range range: ranges) {
            names.add(range.getName());
        }
        return names;
    }

    protected List<OverviewChartRow> getSickLeaveLengthGroups(String verksamhetId, Range range, int numberOfGroups) {
        List<OverviewChartRow> queryResult = getSickLeaveLengthGroupsFromDb(verksamhetId, range);

        Collections.sort(queryResult, CHART_ROW_COMPARATOR);

        ArrayList<OverviewChartRow> result = new ArrayList<>();
        for (OverviewChartRow row: queryResult) {
            result.add(row);
            if (result.size() == numberOfGroups) {
                break;
            }
        }

        Comparator<? super OverviewChartRow> lengthComparator = new Comparator<OverviewChartRow>() {
            @Override
            public int compare(OverviewChartRow o1, OverviewChartRow o2) {
                return SjukfallslangdUtil.RANGES.rangeFor(o1.getName()).getCutoff() - SjukfallslangdUtil.RANGES.rangeFor(o2.getName()).getCutoff();
            }
        };
        Collections.sort(result, lengthComparator);

        return result;
    }

    protected List<OverviewChartRowExtended> getDegreeOfSickLeaveGroups(String verksamhetId, Range range) {
        List<OverviewChartRowExtended> result = new ArrayList<>();
        Map<String, Integer> queryResult = getDegreeOfSickLeaveGroupsFromDb(verksamhetId, range);
        Map<String, Integer> queryResultPreviousPeriod = getDegreeOfSickLeaveGroupsFromDb(verksamhetId, ReportUtil.getPreviousPeriod(range));

        for (String grad : SjukskrivningsgradPersistenceHandler.GRAD) {
            int current = queryResult.get(grad);
            int previous = queryResultPreviousPeriod.get(grad);
            int change = changeInPercent(current, previous);
            result.add(new OverviewChartRowExtended(grad + "%", current, change));
        }
        return result;
    }

    private Map<String, Integer> getDegreeOfSickLeaveGroupsFromDb(String verksamhetId, Range range) {
        TypedQuery<OverviewChartRow> query = getManager().createQuery("SELECT NEW se.inera.statistics.service.report.model.OverviewChartRow(c.key.grad, SUM(c.male) + SUM(c.female)) FROM SjukskrivningsgradData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to GROUP BY c.key.grad", OverviewChartRow.class);
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        Map<String, Integer> result = new DefaultHashMap<>(0);
        for (OverviewChartRow row : query.getResultList()) {
            result.put(row.getName(), row.getQuantity());
        }
        return result;
    }

    protected OverviewKonsfordelning getSexProportion(String verksamhetId, Range range) {
        Query query = getManager()
                .createQuery("SELECT SUM(c.male), SUM(c.female) FROM SjukfallPerManadRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to");
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        Object[] row = (Object[]) query.getSingleResult();
        if (row == null || row[0] == null || row[1] == null) {
            return new OverviewKonsfordelning(0, 0, range);
        }
        return new OverviewKonsfordelning(asInt(row[0]), asInt(row[1]), range);
    }

    protected List<OverviewChartRowExtended> getDiagnosisGroups(String verksamhetId, Range range, int numberOfGroups) {
        List<OverviewChartRow> queryResult = getDiagnosisGroupsFromDb(verksamhetId, range);
        List<OverviewChartRow> queryResultForPrevPeriod = getDiagnosisGroupsFromDb(verksamhetId, ReportUtil.getPreviousPeriod(range));

        Collections.sort(queryResult, CHART_ROW_COMPARATOR);
        ArrayList<OverviewChartRowExtended> result = new ArrayList<>();

        for (int x = 0; x < numberOfGroups && x < queryResult.size(); x++) {
            OverviewChartRow row = queryResult.get(x);

            int change = lookupChange(row, queryResultForPrevPeriod);
            result.add(new OverviewChartRowExtended(row.getName(), row.getQuantity(), change));
        }

        return result;
    }

    protected List<OverviewChartRowExtended> getAgeGroups(String verksamhetId, Range range, int numberOfGroups) {
        List<OverviewChartRow> queryResult = getAgeGroupsFromDb(verksamhetId, range);
        List<OverviewChartRow> queryResultForPrevPeriod = getAgeGroupsFromDb(verksamhetId, ReportUtil.getPreviousPeriod(range));

        Collections.sort(queryResult, CHART_ROW_COMPARATOR);
        ArrayList<OverviewChartRowExtended> result = new ArrayList<>();
        for (int x = 0; x < numberOfGroups && x < queryResult.size(); x++) {
            OverviewChartRow row = queryResult.get(x);

            int change = lookupChange(row, queryResultForPrevPeriod);
            result.add(new OverviewChartRowExtended(row.getName(), row.getQuantity(), change));
        }

        Comparator<? super OverviewChartRowExtended> ageComparator = new Comparator<OverviewChartRowExtended>() {
            @Override
            public int compare(OverviewChartRowExtended o1, OverviewChartRowExtended o2) {
                return AldersgroupUtil.RANGES.rangeFor(o1.getName()).getCutoff() - AldersgroupUtil.RANGES.rangeFor(o2.getName()).getCutoff();
            }
        };
        Collections.sort(result, ageComparator);

        return result;
    }

    protected int lookupChange(OverviewChartRow row, List<OverviewChartRow> queryResultForPrevPeriod) {
        String key = row.getName();
        int current = row.getQuantity();
        int prev = 0;
        for (OverviewChartRow prevRow : queryResultForPrevPeriod) {
            if (key.equals(prevRow.getName())) {
                prev = prevRow.getQuantity();
                break;
            }
        }
        return prev > 0 ? (VerksamhetOverviewPersistenceHandler.PERCENT * current) / prev - VerksamhetOverviewPersistenceHandler.PERCENT : 0;
    }

    private List<OverviewChartRow> getDiagnosisGroupsFromDb(String verksamhetId, Range range) {
        TypedQuery<OverviewChartRow> query = getManager().createQuery("SELECT NEW se.inera.statistics.service.report.model.OverviewChartRow(c.key.diagnosgrupp, sum(c.female) + sum(c.male)) FROM DiagnosisGroupData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to  GROUP BY c.key.diagnosgrupp", OverviewChartRow.class);
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return query.getResultList();
    }

    private List<OverviewChartRow> getAgeGroupsFromDb(String verksamhetId, Range range) {
        TypedQuery<OverviewChartRow> query = getManager().createQuery("SELECT NEW se.inera.statistics.service.report.model.OverviewChartRow(r.key.grupp, sum(r.female) + sum(r.male)) FROM AldersgruppRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period = :period  GROUP BY r.key.grupp", OverviewChartRow.class);
        query.setParameter("periods", range.getMonths());
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("period", ReportUtil.toPeriod(range.getTo()));

        return query.getResultList();
    }

    private List<OverviewChartRow> getSickLeaveLengthGroupsFromDb(String verksamhetId, Range range) {
        TypedQuery<OverviewChartRow> query = getManager().createQuery("SELECT NEW se.inera.statistics.service.report.model.OverviewChartRow(r.key.grupp, sum(r.female) + sum(r.male)) FROM SjukfallslangdRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period = :period  GROUP BY r.key.grupp", OverviewChartRow.class);
        query.setParameter("periods", range.getMonths());
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("period", ReportUtil.toPeriod(range.getTo()));

        return query.getResultList();
    }

    protected int asInt(Object object) {
        if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof Long) {
            return ((Long) object).intValue();
        } else {
            return 0;
        }
    }

    protected int getCasesPerMonth(String verksamhetId, Range range) {
        TypedQuery<Long> query = getManager().createQuery("SELECT SUM(c.male) + SUM(c.female) FROM SjukfallPerManadRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND:to", Long.class);
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        return asInt(query.getSingleResult());
    }

    protected int changeInPercent(int current, int previous) {
        if (previous == 0) {
            return 0;
        }
        return Math.round(((float) current) * VerksamhetOverviewPersistenceHandler.PERCENT / previous - VerksamhetOverviewPersistenceHandler.PERCENT);
    }

    protected static final Comparator<OverviewChartRow> CHART_ROW_COMPARATOR = new Comparator<OverviewChartRow>() {
        @Override
        public int compare(OverviewChartRow o1, OverviewChartRow o2) {
            return o2.getQuantity() - o1.getQuantity();
        }
    };

    protected void sortWithCollation(List<OverviewChartRowExtended> rows) {
        final Collator collator = Collator.getInstance(new Locale("SV"));
        collator.setStrength(Collator.SECONDARY);

        Collections.sort(rows, new Comparator<OverviewChartRowExtended>() {
            @Override
            public int compare(OverviewChartRowExtended o1, OverviewChartRowExtended o2) {
                return collator.compare(o1.getName(), o2.getName());
            }
        });
    }
}
