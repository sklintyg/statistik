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

import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallPerManadMock implements SjukfallPerManad {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public SimpleDualSexResponse<SimpleDualSexDataRow> getCasesPerMonth(String hsaId, Range range) {
        List<SimpleDualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new SimpleDualSexDataRow(periodName, women, men));
        }
        return new SimpleDualSexResponse<SimpleDualSexDataRow>(rows, 18);
    }

    @Override
    public void count(String hsaId, String period, Verksamhet typ, Sex sex) {
    }
    // CHECKSTYLE:ON

}
