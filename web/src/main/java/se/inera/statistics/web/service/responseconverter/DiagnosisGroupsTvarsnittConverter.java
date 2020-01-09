/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import com.google.common.collect.HashMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.web.model.ChartData;

public class DiagnosisGroupsTvarsnittConverter extends SimpleDualSexConverter {

    public DiagnosisGroupsTvarsnittConverter() {
        super("", "%1$s");
    }

    @Override
    protected ChartData convertToChartData(SimpleKonResponse inputData) {
        HashMultimap<String, KonField> mergedGroups = getMergedGroups(inputData);
        final ArrayList<SimpleKonDataRow> mergedGroupSums = calculateMergedGroupSums(mergedGroups);
        final SimpleKonResponse merged = new SimpleKonResponse(inputData.getAvailableFilters(), mergedGroupSums);
        return super.convertToChartData(merged);
    }

    private ArrayList<SimpleKonDataRow> calculateMergedGroupSums(HashMultimap<String, KonField> mergedGroups) {
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        final List<String> diagnosisChartGroupsAsList = DiagnosisGroupsConverter.getDiagnosisChartGroupsAsList(true);
        for (String mergeGroupName : diagnosisChartGroupsAsList) {
            final KonField sumOfMergedGroupData = sum(mergedGroups.get(mergeGroupName));
            if (!Icd10.UNKNOWN_CODE_NAME.equals(mergeGroupName) || totalSum(sumOfMergedGroupData) > 0) {
                simpleKonDataRows.add(new SimpleKonDataRow(mergeGroupName, sumOfMergedGroupData));
            }
        }
        return simpleKonDataRows;
    }

    private HashMultimap<String, KonField> getMergedGroups(SimpleKonResponse inputData) {
        HashMultimap<String, KonField> mergedGroups = HashMultimap.create();
        final Map<Integer, String> mergeGroupMapping = DiagnosisGroupsConverter.DIAGNOSKAPITEL_TO_DIAGNOSGRUPP;
        for (SimpleKonDataRow row : inputData.getRows()) {
            final int icdIntId = Icd10.icd10ToInt(row.getName(), Icd10RangeType.KAPITEL);
            final String mergedGroup = mergeGroupMapping.get(icdIntId);
            final String mergedGroupName = mergedGroup != null ? mergedGroup : row.getName();
            mergedGroups.put(mergedGroupName, row.getData());
        }
        return mergedGroups;
    }

    private int totalSum(KonField konField) {
        return konField.getFemale() + konField.getMale();
    }

    private KonField sum(Set<KonField> konFields) {
        int female = 0;
        int male = 0;
        for (KonField konField : konFields) {
            female += konField.getFemale();
            male += konField.getMale();
        }
        return new KonField(female, male);
    }

}
