package se.inera.statistics.service.report.listener;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

public class GenericHolder {

    private final SjukfallInfo sjukfallInfo;
    private final JsonNode utlatande;
    private final String enhetId;
    private final String vardgivareId;
    private final Sex kon;
    private String diagnos;
    private String diagnosgrupp;
    private String diagnosundergrupp;
    private int age;

    public GenericHolder(SjukfallInfo sjukfallInfo, JsonNode utlatande) {
        this.sjukfallInfo = sjukfallInfo;
        this.utlatande = utlatande;
        enhetId = DocumentHelper.getEnhetId(utlatande);
        vardgivareId = DocumentHelper.getVardgivareId(utlatande);
        kon = "man".equalsIgnoreCase(DocumentHelper.getKon(utlatande)) ? Sex.Male : Sex.Female;
        diagnos = DocumentHelper.getDiagnos(utlatande);
        diagnosgrupp = DiagnosisGroupsUtil.getGroupIdForCode(diagnos);
        diagnosundergrupp = DiagnosisGroupsUtil.getSubGroupForCode(diagnos).getId();
        age = DocumentHelper.getAge(utlatande);
    }

    public SjukfallInfo getSjukfallInfo() {
        return sjukfallInfo;
    }

    public JsonNode getUtlatande() {
        return utlatande;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public Sex getKon() {
        return kon;
    }

    public String getDiagnos() {
        return diagnos;
    }

    public String getDiagnosgrupp() {
        return diagnosgrupp;
    }

    public String getDiagnosundergrupp() {
        return diagnosundergrupp;
    }

    public int getAge() {
        return age;
    }
}
