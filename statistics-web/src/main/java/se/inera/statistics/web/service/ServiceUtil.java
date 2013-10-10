package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;

public final class ServiceUtil {

    private ServiceUtil() {
    }

    static List<Integer> getAppendedSum(List<Integer> data) {
        int sum = 0;
        for (Integer dataField : data) {
            sum += dataField;
        }
        List<Integer> dataWithSum = new ArrayList<Integer>(data);
        dataWithSum.add(sum);
        return dataWithSum;
    }

    static List<Integer> getMergedSexData(DualSexDataRow row) {
        List<Integer> data = new ArrayList<>();
        for (DualSexField dualSexField : row.getData()) {
            data.add(dualSexField.getFemale());
            data.add(dualSexField.getMale());
        }
        return data;
    }
}
