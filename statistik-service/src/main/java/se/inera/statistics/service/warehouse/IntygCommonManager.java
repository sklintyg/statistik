/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.inera.statistics.service.warehouse.query.CounterFunctionIntyg;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

@Component
public class IntygCommonManager {
    private static final Logger LOG = LoggerFactory.getLogger(IntygCommonManager.class);
    private static int errCount;

    @Autowired
    private IntygCommonConverter intygCommonConverter;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    public void accept(IntygDTO dto, HsaInfo hsa, long logId, String correlationId, EventType type) {
        LOG.info("accepting a new entry into table IntygCommon");
        final String intygid = dto.getIntygid();
        final String intygtyp = dto.getIntygtyp();
        if (!isSupportedIntygType(intygtyp)) {
            LOG.info("Intygtype not supported. Ignoring intyg: " + intygid);
            return;
        }
        IntygCommon line = intygCommonConverter.toIntygCommon(dto, hsa, correlationId, type);
        persistIfValid(logId, intygid, line);
    }

    private boolean isSupportedIntygType(String intygType) {
        return IntygType.parseString(intygType).isSupportedIntyg();
    }

    private void persistIfValid(long logId, String intygid, IntygCommon line) {
        List<String> errors = intygCommonConverter.validate(line);

        if (errors.isEmpty()) {
            manager.persist(line);
        } else {
            StringBuilder errorBuilder = new StringBuilder("Faulty intyg logid ").append(logId).append(" id ").append(intygid).append(" error count ")
                    .append(errCount++);
            for (String error : errors) {
                errorBuilder.append('\n').append(error);
            }
            LOG.error(errorBuilder.toString());
        }
    }

    public SimpleKonResponse<SimpleKonDataRow> getIntyg(HsaIdVardgivare vardgivarId, Range range, Collection<HsaIdEnhet> enheter) {
        final Function<IntygCommonGroup, String> rowNameFunction = intygCommonGroup ->
                ReportUtil.toDiagramPeriod(intygCommonGroup.getRange().getFrom());
        return getIntygCommonMaleFemale(range, vardgivarId, enheter, rowNameFunction, false);
    }

    public SimpleKonResponse<SimpleKonDataRow> getIntygTvarsnitt(HsaIdVardgivare vardgivarId, Range range, Collection<HsaIdEnhet> enheter) {
        final Function<IntygCommonGroup, String> rowNameFunction = intygCommonGroup -> "Totalt";
        return getIntygCommonMaleFemale(range, vardgivarId, enheter, rowNameFunction, true);
    }

    public KonDataResponse getIntygPerTypeTidsserie(HsaIdVardgivare vardgivarId, Range range, Collection<HsaIdEnhet> enheter) {
        return getIntygPerType(vardgivarId, range, enheter, false);

    }

    private KonDataResponse getIntygPerType(HsaIdVardgivare vardgivarId, Range range, Collection<HsaIdEnhet> enheter, boolean isTvarsnitt) {
        final List<IntygType> intygTypes = Arrays.asList(IntygType.FK7263, IntygType.LISJP, IntygType.LUSE, IntygType.LUAE_NA, IntygType.LUAE_FS);
        final List<String> names = intygTypes.stream().map(IntygType::getText).collect(Collectors.toList());
        final List<Integer> ids = intygTypes.stream().map(Enum::ordinal).collect(Collectors.toList());
        final CounterFunctionIntyg<Integer> counterFunction = (intyg, counter) -> {
            final IntygType intygType = IntygType.parseString(intyg.getIntygtyp());
            counter.add(intygType.ordinal());
        };
        return calculateKonDataResponse(vardgivarId, range, enheter, isTvarsnitt, names, ids, counterFunction);
    }

    public SimpleKonResponse<SimpleKonDataRow> getIntygPerTypeTvarsnitt(HsaIdVardgivare vardgivarId, Range range, Collection<HsaIdEnhet> enheter) {
        final KonDataResponse intygPerTypeTidsserie = getIntygPerType(vardgivarId, range, enheter, true);
        return SimpleKonResponse.create(intygPerTypeTidsserie);
    }

    private <T> KonDataResponse calculateKonDataResponse(HsaIdVardgivare vardgivarId, Range range, Collection<HsaIdEnhet> enheter, boolean isTvarsnitt, List<String> groupNames, List<T> groupIds, CounterFunctionIntyg<T> counterFunction) {
        List<KonDataRow> rows = new ArrayList<>();
        final List<IntygCommonGroup> intygCommonGroups = getIntygCommonGroups(range, vardgivarId, enheter, isTvarsnitt, null);
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

        return new KonDataResponse(groupNames, rows);
    }

    private SimpleKonResponse<SimpleKonDataRow> getIntygCommonMaleFemale(Range range, HsaIdVardgivare vardgivarId, Collection<HsaIdEnhet> enheter, Function<IntygCommonGroup, String> rowNameFunction, boolean isTvarsnitt) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        final List<IntygType> intygTypes = Arrays.asList(IntygType.FK7263, IntygType.LISJP, IntygType.LUSE, IntygType.LUAE_NA, IntygType.LUAE_FS);
        for (IntygCommonGroup intygCommonGroup : getIntygCommonGroups(range, vardgivarId, enheter, isTvarsnitt, intygTypes)) {
            int male = countMale(intygCommonGroup.getIntyg());
            int female = intygCommonGroup.getIntyg().size() - male;
            final String periodName = rowNameFunction.apply(intygCommonGroup);
            result.add(new SimpleKonDataRow(periodName, female, male));
        }
        return new SimpleKonResponse<>(result);
    }

    private List<IntygCommonGroup> getIntygCommonGroups(Range range, HsaIdVardgivare vardgivarId, Collection<HsaIdEnhet> enheter, boolean isTvarsnitt, List<IntygType> intygTypes) {
        List<IntygCommonGroup> intygCommonGroups = new ArrayList<>();
        int periods = isTvarsnitt ? 1 : range.getNumberOfMonths();
        int periodLength = isTvarsnitt ? range.getNumberOfMonths() : 1;
        for (int i = 0; i < periods; i++) {
            LocalDate periodStart = range.getFrom().plusMonths(i * periodLength);
            final LocalDate to = periodStart.plusMonths(periodLength);
            Range newRange = new Range(periodStart, to);
            IntygCommonGroup intygCommonGroup = getIntygCommonGroup(newRange, vardgivarId, enheter, intygTypes);
            intygCommonGroups.add(intygCommonGroup);
        }
        return intygCommonGroups;
    }

    private IntygCommonGroup getIntygCommonGroup(Range range, HsaIdVardgivare vardgivarId, Collection<HsaIdEnhet> enheter, List<IntygType> intygTypes) {
        final StringBuilder ql = new StringBuilder();
        ql.append("SELECT r FROM IntygCommon r ");
        ql.append("WHERE r.vardgivareId = :vardgivarId ");
        ql.append("AND r.signeringsdatum >= :fromDate ");
        ql.append("AND r.signeringsdatum <= :toDate ");
        ql.append("AND r.intygid not in ");
        ql.append("(select intygid from IntygCommon where eventType = ");
        ql.append(EventType.REVOKED.ordinal());
        ql.append(" )");

        if (enheter != null) {
            ql.append(" AND r.enhet IN :enhetIds");
        }
        if (intygTypes != null) {
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
        if (intygTypes != null) {
            final List<String> intygTypeStrings = intygTypes.stream().map(Enum::name).collect(Collectors.toList());
            q.setParameter("intygTypes", intygTypeStrings);
        }

        final List<IntygCommon> results = q.getResultList();
        return new IntygCommonGroup(range, results);
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

        return query.getSingleResult();
    }

}
