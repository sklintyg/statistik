package se.inera.statistics.service.report.model;

public class AgeGroupsRow extends SimpleDualSexDataRow {

    public AgeGroupsRow(String group, int female, int male) {
        super(group, new DualSexField(female, male));
    }

}
