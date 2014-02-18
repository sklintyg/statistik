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

import se.inera.statistics.service.report.api.RollingLength;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.db.SjukfallslangdRow;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppMock implements SjukfallslangdGrupp {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SjukfallslangdResponse getHistoricalStatistics(String hsaId, LocalDate when, RollingLength length) {
        final List<SjukfallslangdRow> rows = new ArrayList<>();
        for (se.inera.statistics.service.report.util.Ranges.Range group : SjukfallslangdUtil.RANGES) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SjukfallslangdRow(null, group.getName(), length.getPeriods(), women, men));
        }
        return new SjukfallslangdResponse(rows, length.getPeriods());
    }

    @Override
    public SjukfallslangdResponse getCurrentStatistics(String hsaId) {
        return getHistoricalStatistics(hsaId, null, RollingLength.SINGLE_MONTH);
    }

    @Override
    public SimpleKonResponse<SimpleKonDataRow> getLongSickLeaves(String decodeId, Range range) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleKonDataRow(periodName, women, men));
        }
        return new SimpleKonResponse<SimpleKonDataRow>(rows, 18);
    }

    @Override
    public void recount(String period, String vardgivareId, String group, String newGroup, RollingLength length, Verksamhet vardgivare, Kon kon) {
    }

    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Kon sex) {
    }
    // CHECKSTYLE:ON

}
