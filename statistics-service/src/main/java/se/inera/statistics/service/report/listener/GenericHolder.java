package se.inera.statistics.service.report.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

public class GenericHolder {
    private static final Logger LOG = LoggerFactory.getLogger(GenericHolder.class);

    private final SjukfallInfo sjukfallInfo;
    private final JsonNode utlatande;
    private final String enhetId;
    private final String vardgivareId;
    private final String lanId;
    private final Sex kon;
    private final String diagnos;
    private String diagnosgrupp;
    private String diagnosundergrupp;
    private final int age;

    public GenericHolder(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, DiagnosisGroupsUtil diagnosisGroupsUtil) {
        this.sjukfallInfo = sjukfallInfo;
        this.utlatande = utlatande;
        enhetId = DocumentHelper.getEnhetId(utlatande);
        vardgivareId = DocumentHelper.getVardgivareId(utlatande);
        lanId = HSAServiceHelper.getLan(hsa);
        kon = "man".equalsIgnoreCase(DocumentHelper.getKon(utlatande)) ? Sex.Male : Sex.Female;
        diagnos = DocumentHelper.getDiagnos(utlatande);
        try {
            diagnosgrupp = diagnosisGroupsUtil.getGroupIdForCode(diagnos);
            diagnosundergrupp = diagnosisGroupsUtil.getSubGroupForCode(diagnos).getId();
        } catch (IllegalArgumentException e) {
            LOG.warn("Parse error: {}. (Ignoring ICD10)", e.getMessage());
            diagnosgrupp = "UNKNOWN";
            diagnosundergrupp = "UNKNOWN";
        }
        age = DocumentHelper.getAge(utlatande);
    }

    public boolean validate() {
        boolean result = true;
        if (vardgivareId == null || vardgivareId.length() > 60) {
            LOG.error("Invalid vardgivarid '{}'.", vardgivareId);
            result = false;
        }
        if (enhetId == null || enhetId.length() > 60) {
            LOG.error("Invalid enhetid '{}'.", enhetId);
            result = false;
        }
        return result;
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

    public String getLanId() {
        return lanId;
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
