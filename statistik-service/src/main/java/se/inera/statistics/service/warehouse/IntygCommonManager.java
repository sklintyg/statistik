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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v2.RegisterCertificateType;

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

    /**
     * @param utlatande
     * @param patientData
     * @param hsa
     * @param logId
     * @param correlationId
     * @param type
     */
    public void accept(RegisterCertificateType utlatande, Patientdata patientData, HsaInfo hsa, long logId, String correlationId, EventType type) {
        LOG.info("accepting a new entry into table IntygCommon");
        final String intygid = registerCertificateHelper.getIntygId(utlatande);
        final String intygtyp = registerCertificateHelper.getIntygtyp(utlatande);
        if (!isSupportedIntygType(intygtyp)) {
            LOG.info("Intygtype not supported. Ignoring intyg: " + intygid);
            return;
        }
        IntygCommon line = intygCommonConverter.toIntygCommon(utlatande, patientData, hsa, logId, correlationId, type);
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

    public SimpleKonResponse<SimpleKonDataRow> getIntyg(HsaIdVardgivare vardgivarId, Range range, int periodlangd) {
        final Function<IntygCommonGroup, String> rowNameFunction = new Function<IntygCommonGroup, String>() {
            @Override
            public String apply(IntygCommonGroup intygCommonGroup) {
                return ReportUtil.toDiagramPeriod(intygCommonGroup.getRange().getFrom());
            }
        };
        return getIntygCommonMaleFemale(range, vardgivarId, rowNameFunction, periodlangd);
    }

    private SimpleKonResponse<SimpleKonDataRow> getIntygCommonMaleFemale(Range range, HsaIdVardgivare vardgivarId, Function<IntygCommonGroup, String> rowName, int periodLangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (IntygCommonGroup intygCommonGroup : getIntygCommonGroups(range, vardgivarId, periodLangd)) {
            int male = countMale(intygCommonGroup.getIntyg());
            int female = intygCommonGroup.getIntyg().size() - male;
            result.add(new SimpleKonDataRow(rowName.apply(intygCommonGroup), female, male));
        }

        return new SimpleKonResponse<>(result);
    }

    private List<IntygCommonGroup> getIntygCommonGroups(Range range, HsaIdVardgivare vardgivarId, int periodLangd) {
        List<IntygCommonGroup> intygCommonGroups = new ArrayList<IntygCommonGroup>();
        for (int i = 0; i < range.getMonths(); i++) {
            LocalDate periodStart = range.getFrom().plusMonths(i); //1 month
            Range newRange = new Range(periodStart, periodStart.plusMonths(periodLangd));
            IntygCommonGroup intygCommonGroup = getIntygCommonGroup(newRange, vardgivarId);
            intygCommonGroups.add(intygCommonGroup);
        }
        return intygCommonGroups;
    }

    private IntygCommonGroup getIntygCommonGroup(Range range, HsaIdVardgivare vardgivarId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<IntygCommon> cq = builder.createQuery(IntygCommon.class);
        Root<IntygCommon> root = cq.from(IntygCommon.class);
        cq.where(getConditions(range, vardgivarId, builder, root));
        Query query = manager.createQuery(cq);
        List<IntygCommon> intygCommons = query.getResultList();
        return new IntygCommonGroup(range, intygCommons);
    }

    private Predicate getConditions(Range range, HsaIdVardgivare vardgivarId, CriteriaBuilder builder, Root<IntygCommon> root) { //TODO: Implement search conditions
        Predicate pred = builder.conjunction();
        return pred;
    }

    public static int countMale(Collection<IntygCommon> intygs) {
        int count = 0;
        for (IntygCommon intyg : intygs) {
            if (intyg.getKon() == 0) { // TODO: 0 or 1 really?
                count++;
            }
        }
        return count;
    }

}
