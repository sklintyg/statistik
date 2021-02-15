/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.testsupport.socialstyrelsenspecial;

import com.google.common.collect.HashMultiset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import se.inera.statistics.integration.hsa.model.HsaIdAny;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.EnhetUtil;
import se.inera.statistics.service.warehouse.IntygCommonFilter;
import se.inera.statistics.service.warehouse.IntygCommonGroup;
import se.inera.statistics.service.warehouse.IntygCommonManager;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.ResponseUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineConverter;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.inera.statistics.service.warehouse.query.CounterFunctionIntyg;

/**
 * Based on a copy of {@link se.inera.statistics.service.warehouse.IntygCommonManager} and
 * is therefore a lot more complex then it should have to be. To be used
 * for the ME/CFS-report requested by Socialstyrelsen (see INTYG-6994).
 */
public class IntygCommonSosManager {

    private Icd10 icd10;

    private EntityManager manager;

    private Warehouse warehouse;

    public IntygCommonSosManager(Icd10 icd10, EntityManager manager, Warehouse warehouse) {
        this.icd10 = icd10;
        this.manager = manager;
        this.warehouse = warehouse;
    }

    private KonDataResponse getIntygPerAgeGroupSoc(HsaIdVardgivare vardgivarId, int year) {
        final List<IntygType> intygTypes = new ArrayList<>(IntygType.SJUKPENNING.getUnmappedTypes());
        final AgeGroupSoc[] groups = AgeGroupSoc.values();
        final List<String> names = Arrays.stream(groups).map(AgeGroupSoc::getGroupName).collect(Collectors.toList());
        final List<Integer> ids = Arrays.stream(groups).map(Enum::ordinal).collect(Collectors.toList());
        final Set<String> uniquePnrs = new HashSet<>();

        final CounterFunctionIntyg<Integer> counterFunction = (intyg, counter) -> {
            final String patientid = intyg.getPatientid();
            final boolean isUniquePnr = uniquePnrs.add(patientid);
            if (isUniquePnr) {
                final String birthYearString = patientid.substring(0, 4);
                final int birthYear = Integer.parseInt(birthYearString);
                final int ageAtEndOfYear = year - birthYear;
                final AgeGroupSoc ageGroup = AgeGroupSoc.getGroupForAge(ageAtEndOfYear).orElse(AgeGroupSoc.GROUP1_0TO16);
                counter.add(ageGroup.ordinal());
            }
        };
        final Range range = new Range(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        final List<String> dxs = Collections.singletonList(String.valueOf(icd10.findFromIcd10Code("G933").toInt()));
        final IntygCommonFilter filter = new IntygCommonFilter(range, null, dxs, null, null);
        return calculateKonDataResponseSoc(vardgivarId, filter, true, names, ids, counterFunction, intygTypes);
    }

    public KonDataResponse getIntygPerAgeGroupSocAggregate(KonDataResponse resultToAggregateIn, HsaIdVardgivare vardgivarId,
        int year, int cutoff) {
        final KonDataResponse messagesTvarsnittPerAmne = getIntygPerAgeGroupSoc(vardgivarId, year);
        final KonDataResponse resultToAggregate = resultToAggregateIn != null
            ? resultToAggregateIn : ResponseUtil.createEmptyKonDataResponse(messagesTvarsnittPerAmne);
        Iterator<KonDataRow> rowsNew = messagesTvarsnittPerAmne.getRows().iterator();
        Iterator<KonDataRow> rowsOld = resultToAggregate.getRows().iterator();
        List<KonDataRow> list = ResponseUtil.getKonDataRows(1, rowsNew, rowsOld, cutoff);
        return new KonDataResponse(AvailableFilters.getForIntyg(), messagesTvarsnittPerAmne.getGroups(), list);
    }

    private <T> KonDataResponse calculateKonDataResponseSoc(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
        boolean isTvarsnitt, List<String> groupNames, List<T> groupIds,
        CounterFunctionIntyg<T> counterFunction, List<IntygType> intygTypes) {
        List<KonDataRow> rows = new ArrayList<>();
        final List<IntygCommonGroup> intygCommonGroups = getIntygCommonGroupsSoc(vardgivarId, intygFilter, isTvarsnitt, intygTypes);
        for (IntygCommonGroup intygCommonGroup : intygCommonGroups) {
            final HashMultiset<T> maleCounter = HashMultiset.create();
            final HashMultiset<T> femaleCounter = HashMultiset.create();
            for (IntygCommon intyg : intygCommonGroup.getIntyg()) {
                final boolean isFemale = Kon.FEMALE.getNumberRepresentation() == intyg.getKon();
                final HashMultiset<T> currentCounter = isFemale ? femaleCounter : maleCounter;
                counterFunction.addCount(intyg, currentCounter);
            }
            List<KonField> list = new ArrayList<>(groupIds.size());
            for (T id : groupIds) {
                list.add(new KonField(femaleCounter.count(id), maleCounter.count(id)));
            }
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(intygCommonGroup.getRange().getFrom()), list));
        }

        return new KonDataResponse(AvailableFilters.getForIntyg(), groupNames, rows);
    }

    private List<IntygCommonGroup> getIntygCommonGroupsSoc(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
        boolean isTvarsnitt, List<IntygType> intygTypes) {
        List<IntygCommonGroup> intygCommonGroups = new ArrayList<>();
        final Range range = intygFilter.getRange();
        IntygCommonGroup intygCommonGroup = getIntygCommonGroupSoc(range, vardgivarId, intygFilter, intygTypes);
        int periods = isTvarsnitt ? 1 : range.getNumberOfMonths();
        int periodLength = isTvarsnitt ? range.getNumberOfMonths() : 1;
        for (int i = 0; i < periods; i++) {
            LocalDate periodStart = range.getFrom().plusMonths(i * periodLength).withDayOfMonth(1);
            final LocalDate to = periodStart.plusMonths(periodLength).minusDays(1);
            Range newRange = new Range(periodStart, to);
            final IntygCommonGroup periodGroup = new IntygCommonGroup(newRange, intygCommonGroup.getIntyg());
            intygCommonGroups.add(periodGroup);
        }
        return intygCommonGroups;
    }

    private IntygCommonGroup getIntygCommonGroupSoc(Range range, HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
        List<IntygType> intygTypes) {

        final List<IntygType> allIntygTypes = Arrays.asList(IntygType.values());
        List<IntygType> intygTypeFilter = intygFilter.getIntygstyper() != null && !intygFilter.getIntygstyper().isEmpty()
            ? intygFilter.getIntygstyper().stream()
            .map(IntygType::getByName).filter(Optional::isPresent).map(Optional::get)
            .flatMap(intygType -> intygType.getUnmappedTypes().stream())
            .collect(Collectors.toList())
            : allIntygTypes;
        final List<IntygType> intygTypesToQuery = (intygTypes != null ? intygTypes : allIntygTypes).stream()
            .filter(intygTypeFilter::contains).collect(Collectors.toList());

        final int fromDay = WidelineConverter.toDay(range.getFrom());
        final int toDay = WidelineConverter.toDay(range.getTo());

        final StringBuilder ql = new StringBuilder();
        ql.append("SELECT r FROM IntygCommon r, WideLine w ");
        ql.append("WHERE r.vardgivareId = :vardgivarId ");
        ql.append("AND r.intygid = w.correlationId ");
        ql.append("AND ((w.startdatum >= :fromDate ");
        ql.append("AND w.startdatum <= :toDate) ");
        ql.append("OR (w.slutdatum >= :fromDate ");
        ql.append("AND w.slutdatum <= :toDate) ");
        ql.append("OR (w.startdatum < :fromDate ");
        ql.append("AND w.slutdatum > :toDate)) ");
        ql.append("AND w.active = TRUE ");

        final Collection<HsaIdEnhet> enheter = getAllEnheterInVardenheter(intygFilter.getEnheter());
        if (enheter != null) {
            ql.append(" AND r.enhet IN :enhetIds");
        }
        if (intygTypesToQuery != null) {
            ql.append(" AND r.intygtyp IN :intygTypes");
        }

        final TypedQuery<IntygCommon> q = manager.createQuery(ql.toString(), IntygCommon.class);
        q.setParameter("vardgivarId", vardgivarId.getId());
        q.setParameter("fromDate", fromDay);
        q.setParameter("toDate", toDay);
        if (enheter != null) {
            final List<String> enhetIds = enheter.stream().map(HsaIdAny::getId).collect(Collectors.toList());
            q.setParameter("enhetIds", enhetIds);
        }
        if (intygTypesToQuery != null) {
            q.setParameter("intygTypes", intygTypesToQuery);
        }

        List<IntygCommon> resultList = q.getResultList();

        final Collection<String> aldersgrupp = intygFilter.getAldersgrupp();
        final boolean isAgeFilterActive = aldersgrupp != null && !aldersgrupp.isEmpty() && aldersgrupp.size() != AgeGroup.values().length;
        resultList = isAgeFilterActive ? IntygCommonManager.applyAgeFilter(resultList, aldersgrupp) : resultList;

        resultList = applyDxFilter(intygFilter.getDiagnoser(), resultList);

        return new IntygCommonGroup(range, resultList);
    }

    private Collection<HsaIdEnhet> getAllEnheterInVardenheter(Collection<HsaIdEnhet> enheter) {
        return EnhetUtil.getAllEnheterInVardenheter(enheter, warehouse);
    }

    private List<IntygCommon> applyDxFilter(Collection<String> dxFilter, List<IntygCommon> dtos) {
        return IntygCommonManager.applyDxFilter(dxFilter, dtos, icd10);
    }

}
