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

import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnosgruppMock implements se.inera.statistics.service.report.api.Diagnosgrupp {

    private static final int MAX_VALUE = 100;

    @Override
    public DiagnosgruppResponse getDiagnosisGroups(String hsaId, Range range) {
        List<Diagnosgrupp> headers = DiagnosisGroupsUtil.getAllDiagnosisGroups();
        List<DualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new DualSexDataRow(periodName, randomData(headers.size())));
        }
        return new DiagnosgruppResponse(headers, rows);
    }

    private List<DualSexField> randomData(int size) {
        DualSexField[] data = new DualSexField[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DualSexField(randomValue(), randomValue());
        }
        return Arrays.asList(data);
    }

    private int randomValue() {
        return new Random().nextInt(MAX_VALUE);
    }

    @Override
    public void count(String hsaId, String period, String diagnosgrupp, Verksamhet typ, Sex sex) {
    }

}
