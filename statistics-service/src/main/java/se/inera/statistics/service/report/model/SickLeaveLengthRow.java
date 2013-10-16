package se.inera.statistics.service.report.model;

public class SickLeaveLengthRow extends SimpleDualSexDataRow {

    public SickLeaveLengthRow(String group, int female, int male) {
        super(group, new DualSexField(female, male));
    }

}
