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
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.service.FilterSettings;

public class GroupedSjukfallConverter extends SimpleDualSexConverter {

    public GroupedSjukfallConverter(String tableGroupTitle) {
        super(tableGroupTitle, "%1$s");
    }

    @Override
    public SimpleDetailsData convert(SimpleKonResponse casesPerMonth, FilterSettings filterSettings, Message message) {
        Collections.sort(casesPerMonth.getRows(), (SimpleKonDataRow o1, SimpleKonDataRow o2) -> o1.getName().compareTo(o2.getName()));
        return super.convert(casesPerMonth, filterSettings, message);
    }

}
