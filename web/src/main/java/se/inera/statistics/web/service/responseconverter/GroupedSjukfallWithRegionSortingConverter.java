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
package se.inera.statistics.web.service.responseconverter;

import java.util.Collections;
import java.util.List;
import se.inera.statistics.integration.hsa.model.HsaIdAny;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.TableData;

public class GroupedSjukfallWithRegionSortingConverter extends SimpleDualSexConverter {

    private final List<HsaIdEnhet> connectedEnhetIds;

    public GroupedSjukfallWithRegionSortingConverter(String tableGroupTitle, List<HsaIdEnhet> connectedEnhetIds) {
        super(tableGroupTitle, "%1$s");
        this.connectedEnhetIds = connectedEnhetIds;
    }

    @Override
    protected TableData convertToTableData(List<SimpleKonDataRow> list) {
        Collections.sort(list, (SimpleKonDataRow o1, SimpleKonDataRow o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return super.convertToTableData(list);
    }

    @Override
    protected ChartData convertToChartData(SimpleKonResponse casesPerMonth) {
        Collections.sort(casesPerMonth.getRows(),
            (SimpleKonDataRow o1, SimpleKonDataRow o2) ->
                Math.max(o2.getMale(), o2.getFemale()) - Math.max(o1.getMale(), o1.getFemale()));
        return super.convertToChartData(casesPerMonth);
    }

    @Override
    protected boolean isMarked(SimpleKonDataRow row) {
        final Object extras = row.getExtras();
        return extras instanceof HsaIdAny && connectedEnhetIds.contains(extras);
    }

}
