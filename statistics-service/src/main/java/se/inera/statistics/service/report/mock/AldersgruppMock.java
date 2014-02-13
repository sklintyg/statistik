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
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.api.Aldersgrupp;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges.Range;
import se.inera.statistics.service.report.util.Verksamhet;

public class AldersgruppMock implements Aldersgrupp {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    public SimpleKonResponse<SimpleKonDataRow> getAgeGroups(int periods) {
        final List<SimpleKonDataRow> rows = new ArrayList<>();
        for (Range group : AldersgroupUtil.RANGES) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleKonDataRow(group.getName(), women, men));
        }
        return new SimpleKonResponse<>(rows, periods);
    }

    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Kon sex) {
    }

    @Override
    public SimpleKonResponse<SimpleKonDataRow> getCurrentAgeGroups(String hsaId) {
        return getAgeGroups(1);
    }

    @Override
    public SimpleKonResponse<SimpleKonDataRow> getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rollignLength) {
        return getAgeGroups(12);
    }
    // CHECKSTYLE:ON
}
