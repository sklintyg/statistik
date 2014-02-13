/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.repository.RollingLength;

public class SjukfallPerLanMock implements SjukfallPerLan {

    private Random random = new Random();
    public static final List<String> GROUPS = Arrays.asList("Blekinge län", "Dalarnas län", "Gotlands län", "Gävleborgs län", "Hallands län", "Jämtlands län",
            "Jönköpings län", "Kalmar län", "Kronobergs län", "Norrbottens län", "Skåne län", "Stockholms län", "Södermanlands län", "Uppsala län",
            "Värmlands län", "Västerbottens län", "Västernorrlands län", "Västmanlands län", "Västra Götalands län", "Örebro län", "Östergötlands län");

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SimpleKonResponse<SimpleKonDataRow> getStatistics(Range range) {
        final List<SimpleKonDataRow> rows = new ArrayList<>();
        for (String group : GROUPS) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleKonDataRow(group, women, men));
        }
        final int monthsInPeriod = range.getMonths();
        return new SimpleKonResponse<SimpleKonDataRow>(rows, monthsInPeriod);
    }

    // CHECKSTYLE:ON

    @Override
    public void count(String period, String enhetId, String lanId, RollingLength length, Kon kon) {
    }

}
