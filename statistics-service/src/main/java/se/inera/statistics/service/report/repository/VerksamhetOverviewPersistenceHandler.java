package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewSexProportion;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;

public class VerksamhetOverviewPersistenceHandler implements VerksamhetOverview {

    public static final int DISPLAYED_DIAGNOSIS_GROUPS = 5;
    public static final int PERCENT = 100;
    private static final int DISPLAYED_AGE_GROUPS = 7;
    private static final int DISPLAYED_SJUKFALLSLANGD_GROUPS = 5;
    public static final int LONG_SICKLEAVE_CUTOFF = 90;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {
        OverviewSexProportion sexProportion = getSexProportion(verksamhetId, range);
        OverviewSexProportion prevSexProportion = getSexProportion(verksamhetId, ReportUtil.getPreviousPeriod(range));
        List<OverviewChartRowExtended> diagnosisGroups = getDiagnosisGroups(verksamhetId, range);
        List<OverviewChartRowExtended> ageGroups = getAgeGroups(verksamhetId, range);
        List<OverviewChartRowExtended> degreeOfSickLeaveGroups = getDegreeOfSickLeaveGroups(verksamhetId, range);
        List<OverviewChartRow> sickLeaveLengthGroups = getSickLeaveLengthGroups(verksamhetId, range);
        int longSickLeaves = getLongSickLeaves(verksamhetId, range);
        int longSickLeavesPrevious = getLongSickLeaves(verksamhetId, ReportUtil.getPreviousPeriod(range));
        int longSickLeavesChange = changeInPercent(longSickLeaves, longSickLeavesPrevious);
        int casesPerMonth = getCasesPerMonth(verksamhetId, range);

        return new VerksamhetOverviewResponse(casesPerMonth, sexProportion, prevSexProportion, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups,
                sickLeaveLengthGroups, longSickLeaves, longSickLeavesChange);
    }

    private int getLongSickLeaves(String verksamhetId, Range range) {
        TypedQuery<Long> query = manager
                .createQuery(
                        "SELECT sum(r.female) + sum(r.male) FROM SickLeaveLengthRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period = :period AND r.key.grupp IN :grupper",
                        Long.class);
        query.setParameter("periods", range.getMonths());
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("period", ReportUtil.toPeriod(range.getTo()));
        query.setParameter("grupper", SjukfallslangdUtil.lookupGroupsLongerThan(LONG_SICKLEAVE_CUTOFF));

        return asInt(query.getSingleResult());
    }

    @SuppressWarnings("unchecked")
    private List<OverviewChartRow> getSickLeaveLengthGroups(String verksamhetId, Range range) {
        List<Object[]> queryResult = getSickLeaveLengthGroupsFromDb(verksamhetId, range);

        Comparator<? super Object[]> comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Long) ((Object[]) o2)[1]).intValue() - ((Long) ((Object[]) o1)[1]).intValue();
            }
        };

        Collections.sort(queryResult, comparator);

        ArrayList<OverviewChartRow> result = new ArrayList<>();

        for (int x = 0; x < DISPLAYED_SJUKFALLSLANGD_GROUPS && x < queryResult.size(); x++) {
            Object[] row = queryResult.get(x);

            result.add(new OverviewChartRow((String) row[0], ((Long) row[1]).intValue()));
        }

        Comparator<? super OverviewChartRow> lengthComparator = new Comparator<OverviewChartRow>() {
            @Override
            public int compare(OverviewChartRow o1, OverviewChartRow o2) {
                Integer i1 = SjukfallslangdUtil.GROUP_MAP.get(o1.getName());
                Integer i2 = SjukfallslangdUtil.GROUP_MAP.get(o2.getName());
                if (i1 == null || i2 == null) {
                    throw new IllegalStateException("Groups have not been defined correctly for : " + o1.getName() + " or " + o2.getName());
                }
                return i1 - i2;
            }
        };
        Collections.sort(result, lengthComparator);

        return result;
    }

    private List<OverviewChartRowExtended> getDegreeOfSickLeaveGroups(String verksamhetId, Range range) {
        List<OverviewChartRowExtended> result = new ArrayList<>();
        Map<Integer, Integer> queryResult = getDegreeOfSickLeaveGroupsFromDb(verksamhetId, range);
        Map<Integer, Integer> queryResultPreviousPeriod = getDegreeOfSickLeaveGroupsFromDb(verksamhetId, ReportUtil.getPreviousPeriod(range));

        for (int grad : SjukskrivningsgradPersistenceHandler.GRAD) {
            int current = queryResult.get(grad);
            int previous = queryResultPreviousPeriod.get(grad);
            int change = changeInPercent(current, previous);
            result.add(new OverviewChartRowExtended(grad + "%", current, change));
        }
        return result;
    }

    private int changeInPercent(int current, int previous) {
        return previous == 0 ? 0 : (current * PERCENT / previous - PERCENT);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getDegreeOfSickLeaveGroupsFromDb(String verksamhetId, Range range) {
        Query query = manager
                .createQuery("SELECT c.key.grad, SUM(c.male) + SUM(c.female) FROM SjukskrivningsgradData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to GROUP BY c.key.grad");
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        Map<Integer, Integer> result = new DefaultHashMap<>(0);
        for (Object[] row : ((List<Object[]>) query.getResultList())) {
            result.put(asInt(row[0]), asInt(row[1]));
        }
        return result;
    }

    private OverviewSexProportion getSexProportion(String verksamhetId, Range range) {
        Query query = manager
                .createQuery("SELECT SUM(c.male), SUM(c.female) FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to");
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        Object[] row = (Object[]) query.getSingleResult();
        if (row == null || row[0] == null || row[1] == null) {
            return new OverviewSexProportion(0, 0, range);
        }
        return new OverviewSexProportion(((Long) row[0]).intValue(), ((Long) row[1]).intValue(), range);
    }

    private int getCasesPerMonth(String verksamhetId, Range range) {
        TypedQuery<Long> query = manager.createQuery(
                "SELECT SUM(c.male) + SUM(c.female) FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND:to", Long.class);
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        return query.getSingleResult().intValue();
    }

    @SuppressWarnings("unchecked")
    private List<OverviewChartRowExtended> getDiagnosisGroups(String verksamhetId, Range range) {
        List<Object[]> queryResult = getDiagnosisGroupsFromDb(verksamhetId, range);
        List<Object[]> queryResultForPrevPeriod = getDiagnosisGroupsFromDb(verksamhetId, ReportUtil.getPreviousPeriod(range));

        Comparator<? super Object[]> comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Long) ((Object[]) o2)[1]).intValue() - ((Long) ((Object[]) o1)[1]).intValue();
            }
        };

        Collections.sort(queryResult, comparator);
        ArrayList<OverviewChartRowExtended> result = new ArrayList<>();

        for (int x = 0; x < DISPLAYED_DIAGNOSIS_GROUPS && x < queryResult.size(); x++) {
            Object[] row = queryResult.get(x);

            int change = lookupChange(row, queryResultForPrevPeriod);
            result.add(new OverviewChartRowExtended((String) row[0], ((Long) row[1]).intValue(), change));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private List<OverviewChartRowExtended> getAgeGroups(String verksamhetId, Range range) {
        List<Object[]> queryResult = getAgeGroupsFromDb(verksamhetId, range);
        List<Object[]> queryResultForPrevPeriod = getAgeGroupsFromDb(verksamhetId, ReportUtil.getPreviousPeriod(range));

        Comparator<? super Object[]> comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Long) ((Object[]) o2)[1]).intValue() - ((Long) ((Object[]) o1)[1]).intValue();
            }
        };

        Collections.sort(queryResult, comparator);
        ArrayList<OverviewChartRowExtended> result = new ArrayList<>();

        for (int x = 0; x < DISPLAYED_AGE_GROUPS && x < queryResult.size(); x++) {
            Object[] row = queryResult.get(x);

            int change = lookupChange(row, queryResultForPrevPeriod);
            result.add(new OverviewChartRowExtended((String) row[0], ((Long) row[1]).intValue(), change));
        }

        Comparator<? super OverviewChartRowExtended> ageComparator = new Comparator<OverviewChartRowExtended>() {
            @Override
            public int compare(OverviewChartRowExtended o1, OverviewChartRowExtended o2) {
                Integer i1 = AldersgroupUtil.GROUP_MAP.get(o1.getName());
                Integer i2 = AldersgroupUtil.GROUP_MAP.get(o2.getName());
                if (i1 == null || i2 == null) {
                    throw new IllegalStateException("Groups have not been defined correctly.");
                }
                return i1 - i2;
            }
        };
        Collections.sort(result, ageComparator);

        return result;
    }

    private int lookupChange(Object[] row, List<Object[]> queryResultForPrevPeriod) {
        String key = (String) row[0];
        int current = ((Long) row[1]).intValue();
        int prev = 0;
        for (Object[] prevRow : queryResultForPrevPeriod) {
            if (key.equals(prevRow[0])) {
                prev = ((Long) prevRow[1]).intValue();
                break;
            }
        }
        return prev > 0 ? (PERCENT * current) / prev - PERCENT : 0;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getDiagnosisGroupsFromDb(String verksamhetId, Range range) {
        Query query = manager
                .createQuery("SELECT c.key.diagnosgrupp, sum(c.female) + sum(c.male) FROM DiagnosisGroupData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to  GROUP BY c.key.diagnosgrupp");
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        List<Object[]> queryResult;
        queryResult = (List<Object[]>) query.getResultList();
        return queryResult;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getAgeGroupsFromDb(String verksamhetId, Range range) {
        Query query = manager
                .createQuery("SELECT r.key.grupp, sum(r.female) + sum(r.male) FROM AgeGroupsRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period = :period  GROUP BY r.key.grupp");
        query.setParameter("periods", range.getMonths());
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("period", ReportUtil.toPeriod(range.getTo()));

        List<Object[]> queryResult;
        queryResult = (List<Object[]>) query.getResultList();
        return queryResult;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getSickLeaveLengthGroupsFromDb(String verksamhetId, Range range) {
        Query query = manager
                .createQuery("SELECT r.key.grupp, sum(r.female) + sum(r.male) FROM SickLeaveLengthRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period = :period  GROUP BY r.key.grupp");
        query.setParameter("periods", range.getMonths());
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("period", ReportUtil.toPeriod(range.getTo()));

        List<Object[]> queryResult;
        queryResult = (List<Object[]>) query.getResultList();
        return queryResult;
    }

    private int asInt(Object object) {
        if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof Long) {
            return ((Long) object).intValue();
        } else {
            return 0;
        }
    }
}
