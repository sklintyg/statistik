/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import com.google.common.collect.HashMultiset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.processlog.intygsent.IntygSentEvent;
import se.inera.statistics.service.processlog.intygsent.IntygSentHelper;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.message.MessageWidelineLoader;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.inera.statistics.service.warehouse.query.CounterFunctionIntyg;

@Component
public class IntygCommonManager {

    private static final Logger LOG = LoggerFactory.getLogger(IntygCommonManager.class);
    private static int errCount;

    @Autowired
    private IntygCommonConverter intygCommonConverter;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private Warehouse warehouse;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    public void accept(IntygDTO dto, HsaInfo hsa, long logId, String correlationId, EventType type) {
        LOG.info("accepting a new entry into table IntygCommon");
        final String intygid = dto.getIntygid();
        final IntygType intygtyp = dto.getIntygtyp();
        if (!intygtyp.isSupportedIntyg()) {
            LOG.info("Intygtype not supported. Ignoring intyg: " + intygid);
            return;
        }
        final boolean sentToFk = isIntygSentToFk(correlationId);
        IntygCommon line = intygCommonConverter.toIntygCommon(dto, hsa, correlationId, type, sentToFk);
        persistIfValid(logId, intygid, line);
    }

    @Transactional
    public void persistIfValid(long logId, String intygid, IntygCommon line) {
        List<String> errors = intygCommonConverter.validate(line);

        if (errors.isEmpty()) {
            save(line);
        } else {
            StringBuilder errorBuilder = new StringBuilder("Faulty intyg logid ").append(logId).append(" id ").append(intygid)
                .append(" error count ")
                .append(increaseAndGetErrCount());
            for (String error : errors) {
                errorBuilder.append('\n').append(error);
            }
            LOG.error(errorBuilder.toString());
        }
    }

    @Transactional
    public void save(IntygCommon line) {
        manager.persist(line);
        if (revokeIntygsId(line)) {
            inactivateIntyg(line.getIntygid());
        }
    }

    private boolean revokeIntygsId(IntygCommon line) {
        return EventType.REVOKED.equals(line.getEventType()) || isIntygsIdAlreadyRevoked(line.getIntygid());
    }

    private boolean isIntygsIdAlreadyRevoked(String intygid) {
        final int revokedOrdinal = EventType.REVOKED.ordinal();
        final Query query = manager.createQuery("SELECT intygid "
            + "FROM IntygCommon "
            + "WHERE eventType = " + revokedOrdinal + " AND intygid = :intygid");
        query.setParameter("intygid", intygid);
        return !query.getResultList().isEmpty();
    }

    private void inactivateIntyg(String intygid) {
        final Query query = manager.createQuery("UPDATE IntygCommon SET active = false WHERE intygid = :intygid");
        query.setParameter("intygid", intygid);
        query.executeUpdate();
    }

    private static int increaseAndGetErrCount() {
        return errCount++;
    }

    public SimpleKonResponse getIntyg(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter) {
        final Function<IntygCommonGroup, String> rowNameFunction = intygCommonGroup -> ReportUtil
            .toDiagramPeriod(intygCommonGroup.getRange().getFrom());
        return getIntygCommonMaleFemale(vardgivarId, intygFilter, rowNameFunction, false);
    }

    public SimpleKonResponse getIntygTvarsnitt(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter) {
        final Function<IntygCommonGroup, String> rowNameFunction = intygCommonGroup -> "Totalt";
        return getIntygCommonMaleFemale(vardgivarId, intygFilter, rowNameFunction, true);
    }

    public KonDataResponse getIntygPerTypeTidsserie(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter) {
        return getIntygPerType(vardgivarId, intygFilter, false);
    }

    public KonDataResponse getIntygPerTypeTidsserieAggregated(KonDataResponse resultToAggregateIn, HsaIdVardgivare vardgivarId,
        IntygCommonFilter intygFilter, int cutoff) {
        final KonDataResponse messagesTvarsnittPerAmne = getIntygPerType(vardgivarId, intygFilter, false);
        final KonDataResponse resultToAggregate = resultToAggregateIn != null
            ? resultToAggregateIn : ResponseUtil.createEmptyKonDataResponse(messagesTvarsnittPerAmne);
        Iterator<KonDataRow> rowsNew = messagesTvarsnittPerAmne.getRows().iterator();
        Iterator<KonDataRow> rowsOld = resultToAggregate.getRows().iterator();
        List<KonDataRow> list = ResponseUtil.getKonDataRows(intygFilter.getRange().getNumberOfMonths(), rowsNew, rowsOld, cutoff);
        return new KonDataResponse(AvailableFilters.getForIntyg(), messagesTvarsnittPerAmne.getGroups(), list);
    }

    private KonDataResponse getIntygPerType(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter, boolean isTvarsnitt) {
        final List<IntygType> intygTypes = new ArrayList<>(IntygType.getInIntygtypTotal());
        intygTypes.retainAll(warehouse.getAllExistingIntygTypes());
        // Create new filter without intygstyper
        IntygCommonFilter newIntygFilter = new IntygCommonFilter(intygFilter.getRange(), intygFilter.getEnheter(),
            intygFilter.getDiagnoser(), intygFilter.getAldersgrupp(), null);

        final List<String> names = intygTypes.stream().map(intygType -> {
            //AT2002: FK7263 and LISJP should be combined in one group
            if (IntygType.LISJP.equals(intygType)) {
                return IntygType.SJUKPENNING.getText();
            }
            return intygType.getText();
        }).collect(Collectors.toList());
        final List<Integer> ids = intygTypes.stream().map(Enum::ordinal).collect(Collectors.toList());
        final CounterFunctionIntyg<Integer> counterFunction = (intyg, counter) -> {
            final IntygType intygType = intyg.getIntygtyp();
            final int id = IntygType.FK7263.equals(intygType) ? IntygType.LISJP.ordinal() : intygType.ordinal();
            counter.add(id);
        };
        return calculateKonDataResponse(vardgivarId, newIntygFilter, isTvarsnitt, names, ids, counterFunction);
    }

    public SimpleKonResponse getIntygPerTypeTvarsnitt(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter) {
        final KonDataResponse intygPerTypeTidsserie = getIntygPerType(vardgivarId, intygFilter, true);
        return SimpleKonResponse.create(intygPerTypeTidsserie);
    }

    private <T> KonDataResponse calculateKonDataResponse(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
        boolean isTvarsnitt, List<String> groupNames, List<T> groupIds, CounterFunctionIntyg<T> counterFunction) {
        List<KonDataRow> rows = new ArrayList<>();
        final List<IntygCommonGroup> intygCommonGroups = getIntygCommonGroups(vardgivarId, intygFilter, isTvarsnitt, null);
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

    private SimpleKonResponse getIntygCommonMaleFemale(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
        Function<IntygCommonGroup, String> rowNameFunction, boolean isTvarsnitt) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        final List<IntygType> intygTypes = Arrays.asList(IntygType.FK7263, IntygType.LISJP, IntygType.LUSE, IntygType.LUAE_NA,
            IntygType.LUAE_FS);
        for (IntygCommonGroup intygCommonGroup : getIntygCommonGroups(vardgivarId, intygFilter, isTvarsnitt, intygTypes)) {
            int male = countMale(intygCommonGroup.getIntyg());
            int female = intygCommonGroup.getIntyg().size() - male;
            final String periodName = rowNameFunction.apply(intygCommonGroup);
            result.add(new SimpleKonDataRow(periodName, female, male));
        }
        return new SimpleKonResponse(AvailableFilters.getForIntyg(), result);
    }

    private List<IntygCommonGroup> getIntygCommonGroups(HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
        boolean isTvarsnitt, List<IntygType> intygTypes) {
        List<IntygCommonGroup> intygCommonGroups = new ArrayList<>();
        final Range range = intygFilter.getRange();
        IntygCommonGroup intygCommonGroup = getIntygCommonGroup(range, vardgivarId, intygFilter, intygTypes);
        int periods = isTvarsnitt ? 1 : range.getNumberOfMonths();
        int periodLength = isTvarsnitt ? range.getNumberOfMonths() : 1;
        for (int i = 0; i < periods; i++) {
            LocalDate periodStart = range.getFrom().plusMonths(i * periodLength).withDayOfMonth(1);
            final LocalDate to = periodStart.plusMonths(periodLength).minusDays(1);
            Range newRange = new Range(periodStart, to);
            final IntygCommonGroup periodGroup = new IntygCommonGroup(newRange, intygCommonGroup.getIntyg().stream()
                .filter(intygCommon -> newRange.isDateInRange(intygCommon.getSigneringsdatum()))
                .collect(Collectors.toList()));
            intygCommonGroups.add(periodGroup);
        }
        return intygCommonGroups;
    }

    private IntygCommonGroup getIntygCommonGroup(Range range, HsaIdVardgivare vardgivarId, IntygCommonFilter intygFilter,
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

        final StringBuilder ql = new StringBuilder();
        ql.append("SELECT r FROM IntygCommon r ");
        ql.append("WHERE r.vardgivareId = :vardgivarId ");
        ql.append("AND r.signeringsdatum >= :fromDate ");
        ql.append("AND r.signeringsdatum <= :toDate ");
        ql.append("AND r.active = true ");

        final Collection<HsaIdEnhet> enheter = getAllEnheterInVardenheter(intygFilter.getEnheter());
        if (enheter != null) {
            ql.append(" AND r.enhet IN :enhetIds");
        }
        if (intygTypesToQuery != null) {
            ql.append(" AND r.intygtyp IN :intygTypes");
        }

        final TypedQuery<IntygCommon> q = manager.createQuery(ql.toString(), IntygCommon.class);
        q.setParameter("vardgivarId", vardgivarId.getId());
        q.setParameter("fromDate", range.getFrom());
        q.setParameter("toDate", range.getTo());
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
        resultList = isAgeFilterActive ? applyAgeFilter(resultList, aldersgrupp) : resultList;

        resultList = applyDxFilter(intygFilter.getDiagnoser(), resultList, icd10);

        return new IntygCommonGroup(range, resultList);
    }

    private Collection<HsaIdEnhet> getAllEnheterInVardenheter(Collection<HsaIdEnhet> enheter) {
        if (enheter == null || enheter.isEmpty()) {
            return enheter;
        }

        return Stream.concat(enheter.stream(),
            warehouse.getEnhetsWithHsaId(enheter).stream()
                .filter(Enhet::isVardenhet)
                .flatMap(enhet -> warehouse.getEnhetsOfVardenhet(enhet.getEnhetId()).stream())
                .map(Enhet::getEnhetId))
            .collect(Collectors.toSet());
    }

    public static List<IntygCommon> applyDxFilter(Collection<String> dxFilter, List<IntygCommon> dtos, Icd10 icd10) {
        final boolean applyDiagnosFilter = dxFilter != null && !dxFilter.isEmpty();
        if (applyDiagnosFilter) {
            return dtos.stream().filter(countDTOAmne -> {
                final String dxString = countDTOAmne.getDx();
                try {
                    final Icd10.Id dx = icd10.findFromIcd10Code(dxString);
                    return MessageWidelineLoader.isDxMatchInCollection(dx, dxFilter);
                } catch (Icd10.Icd10NotFoundException e) {
                    return false;
                }
            }).collect(Collectors.toList());
        }
        return dtos;
    }

    public static List<IntygCommon> applyAgeFilter(List<IntygCommon> resultList, Collection<String> aldersgrupp) {
        final List<AgeGroup> ageGroupFilters = aldersgrupp.stream()
            .map(AgeGroup::getByName).filter(Optional::isPresent).map(Optional::get)
            .collect(Collectors.toList());
        return resultList.stream().filter(intygCommon -> {
            final String patientid = intygCommon.getPatientid();
            final int alder = ConversionHelper.extractAlder(patientid, intygCommon.getSigneringsdatum());
            return ageGroupFilters.contains(AgeGroup.getGroupForAge(alder).orElse(null));
        }).collect(Collectors.toList());
    }

    private static int countMale(Collection<IntygCommon> intygs) {
        int count = 0;
        final int maleNumberRepresentation = Kon.MALE.getNumberRepresentation();
        for (IntygCommon intyg : intygs) {
            if (intyg.getKon() == maleNumberRepresentation) {
                count++;
            }
        }
        return count;
    }

    public IntygCommon getOne(String intygsId) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();

        CriteriaQuery<IntygCommon> q = cb.createQuery(IntygCommon.class);
        Root<IntygCommon> c = q.from(IntygCommon.class);
        ParameterExpression<String> p = cb.parameter(String.class);
        q.select(c).where(cb.equal(c.get("intygid"), p));

        TypedQuery<IntygCommon> query = manager.createQuery(q);
        query.setParameter(p, intygsId);

        List<IntygCommon> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public void acceptIntygSentToFk(String correlationId) {
        final IntygCommon intyg = getOne(correlationId);
        if (intyg != null) {
            intyg.setSentToFk(true);
            manager.merge(intyg);
            LOG.info("Intyg sent accepted: {}", correlationId);
        } else {
            LOG.info("Could not accept intyg sent. Intyg not found: {}", correlationId);
        }
    }

    private boolean isIntygSentToFk(String correlationId) {
        final CriteriaBuilder cb = manager.getCriteriaBuilder();
        final CriteriaQuery<IntygSentEvent> q = cb.createQuery(IntygSentEvent.class);
        final Root<IntygSentEvent> from = q.from(IntygSentEvent.class);
        final CriteriaQuery<IntygSentEvent> query = q.select(from).where(cb.equal(from.get("correlationId"), correlationId));
        final List<IntygSentEvent> resultList = manager.createQuery(query).getResultList();
        if (resultList.size() > 1) {
            LOG.warn("Too many rows in IntygSentEvent found for {}", correlationId);
        }
        for (IntygSentEvent intygSentEvent : resultList) {
            if (IntygSentHelper.isFkRecipient(intygSentEvent.getRecipient())) {
                return true;
            }
        }
        return false;
    }

    public Collection<IntygType> getAllExistingIntygTypes() {
        final TypedQuery<IntygType> q = manager.createQuery("SELECT DISTINCT intygtyp FROM IntygCommon", IntygType.class);
        return q.getResultList();
    }

}
