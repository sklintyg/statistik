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
package se.inera.statistics.web.service;

import java.util.Collections;
import java.util.Comparator;

import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.SimpleDetailsData;

public class GroupedSjukfallConverter extends SimpleDualSexConverter {

    public GroupedSjukfallConverter(String tableGroupTitle) {
        super(tableGroupTitle, false, "%1$s");
    }

    @Override
    public SimpleDetailsData convert(SimpleKonResponse<SimpleKonDataRow> casesPerMonth, FilterSettings filterSettings, Message message) {
        Collections.sort(casesPerMonth.getRows(), new Comparator<SimpleKonDataRow>() {
            @Override
            public int compare(SimpleKonDataRow o1, SimpleKonDataRow o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return super.convert(casesPerMonth, filterSettings, message);
    }

}
