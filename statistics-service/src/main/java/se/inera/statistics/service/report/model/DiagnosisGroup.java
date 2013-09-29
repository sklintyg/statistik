package se.inera.statistics.service.report.model;

public class DiagnosisGroup {

    public static final int ICD10_CODE_MAX_LEN = 3;
    private final String id;
    private final String name;
    private final String firstId;
    private final String lastId;

    public DiagnosisGroup(String id, String name) {
        this.id = id;
        this.name = name;
        String[] split = id.split("-");
        firstId = split[0];
        lastId = split[1];
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
        String normalizedCode = normalizeCode(icd10Code);
        return firstId.compareTo(normalizedCode) <= 0 && lastId.compareTo(normalizedCode) >= 0;
    }

    private String normalizeCode(String icd10Code) {
        String normalizedCode = icd10Code.toUpperCase();
        if (icd10Code.length() > ICD10_CODE_MAX_LEN) {
            normalizedCode = normalizedCode.substring(0, ICD10_CODE_MAX_LEN);
        }
        return normalizedCode;
    }

}
