package se.inera.statistics.service.report.model;

public class CasesPerCountyRow extends SimpleDualSexDataRow {

    public CasesPerCountyRow(String group, int female, int male) {
        super(group, new DualSexField(female, male));
    }

}
