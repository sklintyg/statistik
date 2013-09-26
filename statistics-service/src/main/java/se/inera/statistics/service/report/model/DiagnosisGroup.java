package se.inera.statistics.service.report.model;

public class DiagnosisGroup {

    private final String id;
    private final String name;

    public DiagnosisGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }

    public boolean isCodeInGroup(String icd10Code) {
        String[] split = id.split("-");
        return split[0].length() == icd10Code.length() && split[0].compareTo(icd10Code.toUpperCase()) <= 0 && split[1].length() == icd10Code.length()
                && split[1].compareTo(icd10Code.toUpperCase()) >= 0;
    }

}
