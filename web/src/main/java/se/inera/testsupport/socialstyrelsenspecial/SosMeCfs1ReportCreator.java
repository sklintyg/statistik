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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.warehouse.VgNumber;
import se.inera.statistics.service.warehouse.Warehouse;

public class SosMeCfs1ReportCreator {

    private static final Logger LOG = LoggerFactory.getLogger(SosMeCfs1ReportCreator.class);
    private final Warehouse warehouse;
    private final IntygCommonSosManager intygCommonManager;

    public SosMeCfs1ReportCreator(IntygCommonSosManager intygCommonManager, Warehouse warehouse) {
        this.intygCommonManager = intygCommonManager;
        this.warehouse = warehouse;
    }

    public List<SosMeCfs1Row> getSosReport(int cutoff) {
        final List<HsaIdVardgivare> vgids = warehouse.getAllVardgivare().stream().map(VgNumber::getVgid).collect(Collectors.toList());
        final int vgsSize = vgids.size();
        final int logInterval = 1 + vgsSize / 100;
        final ArrayList<SosMeCfs1Row> sosRows = new ArrayList<>();
        final List<Integer> years = Arrays.asList(2013, 2014, 2015, 2016, 2017, 2018);
        final List<Kon> genders = Arrays.asList(Kon.FEMALE, Kon.MALE);
        for (Integer year : years) {
            LOG.info("Start calculating year: " + year + " with number of vgs: " + vgsSize);
            KonDataResponse intygPerAgeGroupSos = null;
            int counter = 0;
            for (HsaIdVardgivare vgid : vgids) {
                intygPerAgeGroupSos = intygCommonManager.getIntygPerAgeGroupSocAggregate(intygPerAgeGroupSos, vgid, year, cutoff);
                if (counter++ % logInterval == 0) {
                    LOG.info("Number of vgs done: " + counter + " for year: " + year);
                }
            }
            final List<KonDataRow> rows = intygPerAgeGroupSos != null ? intygPerAgeGroupSos.getRows() : Collections.emptyList();
            final List<String> groups = intygPerAgeGroupSos != null ? intygPerAgeGroupSos.getGroups() : Collections.emptyList();
            for (KonDataRow row : rows) {
                for (Kon gender : genders) {
                    final List<Integer> dataForGender = row.getDataForSex(gender);
                    for (int i = 0; i < groups.size(); i++) {
                        final String group = groups.get(i);
                        sosRows.add(new SosMeCfs1Row(year, gender, group, dataForGender.get(i)));
                    }
                }
            }
        }
        return sosRows;
    }

}
