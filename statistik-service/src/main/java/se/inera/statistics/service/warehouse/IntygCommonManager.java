/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

@Component
public class IntygCommonManager {
    private static final Logger LOG = LoggerFactory.getLogger(IntygCommonManager.class);
    private static int errCount;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

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
        for (IntygType type : IntygType.values()) {
            if (type.name().equalsIgnoreCase(intygType)) {
                return true;
            }
        }
        return false;
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
        return getIntygCommonMaleFemale(range, vardgivarId, enheter);
    }

    private SimpleKonResponse<SimpleKonDataRow> getIntygCommonMaleFemale(Range range, HsaIdVardgivare vardgivarId, Collection<HsaIdEnhet> enheter) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (IntygCommonGroup intygCommonGroup : getIntygCommonGroups(range, vardgivarId, enheter)) {
            int male = countMale(intygCommonGroup.getIntyg());
            int female = intygCommonGroup.getIntyg().size() - male;
            final String periodName = ReportUtil.toDiagramPeriod(intygCommonGroup.getRange().getFrom());
            result.add(new SimpleKonDataRow(periodName, female, male));
        }

        return new SimpleKonResponse<>(result);
    }

    private List<IntygCommonGroup> getIntygCommonGroups(Range range, HsaIdVardgivare vardgivarId, Collection<HsaIdEnhet> enheter) {
        List<IntygCommonGroup> intygCommonGroups = new ArrayList<IntygCommonGroup>();
        for (int i = 0; i < range.getMonths(); i++) {
            LocalDate periodStart = range.getFrom().plusMonths(i);
            final LocalDate to = periodStart.plusMonths(1);
            Range newRange = new Range(periodStart, to);
            IntygCommonGroup intygCommonGroup = getIntygCommonGroup(newRange, vardgivarId, enheter);
            intygCommonGroups.add(intygCommonGroup);
        }
        return intygCommonGroups;
    }

    private IntygCommonGroup getIntygCommonGroup(Range range, HsaIdVardgivare vardgivarId, Collection<HsaIdEnhet> enheter) {
        final StringBuilder ql = new StringBuilder();
        ql.append("SELECT r FROM IntygCommon r"
                + " WHERE r.vardgivareId = :vardgivarId"
                + " AND r.signeringsdatum >= :fromDate"
                + " AND r.signeringsdatum <= :toDate");
        if (enheter != null) {
            ql.append(" AND r.enhet IN :enhetIds");
        }

        final TypedQuery<IntygCommon> q = manager.createQuery(ql.toString(), IntygCommon.class);
        q.setParameter("vardgivarId", vardgivarId.getId());
        final int fromDay = WidelineConverter.toDay(range.getFrom());
        q.setParameter("fromDate", fromDay);
        final int toDay = WidelineConverter.toDay(range.getTo());
        q.setParameter("toDate", toDay);
        if (enheter != null) {
            final List<String> enhetIds = enheter.stream().map(HsaIdAny::getId).collect(Collectors.toList());
            q.setParameter("enhetIds", enhetIds);
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
