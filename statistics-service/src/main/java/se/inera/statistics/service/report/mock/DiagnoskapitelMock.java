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

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnoskapitelMock implements Diagnoskapitel {

    @Autowired
    private DiagnosisGroupsUtil diagnosisGroupsUtil;

    @Override
    public DiagnosgruppResponse getDiagnosisGroups(String hsaId, Range range, String diagnosisGroupId) {
        List<Diagnosgrupp> headers = diagnosisGroupsUtil.getSubGroups(diagnosisGroupId);
        List<KonDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new KonDataRow(periodName, randomData(headers.size())));
        }
        return new DiagnosgruppResponse(headers, rows);
    }

    private List<KonField> randomData(int size) {
        KonField[] data = new KonField[size];
        for (int i = 0; i < size; i++) {
            data[i] = new KonField(g(), g());
        }
        return Arrays.asList(data);
    }

    private int g() {
        final int maxNumber = 100;
        return new Random().nextInt(maxNumber);
    }

    @Override
    public void count(String hsaId, String period, String diagnosgrupp, String undergrupp, Verksamhet typ, Kon sex) {
    }

}
