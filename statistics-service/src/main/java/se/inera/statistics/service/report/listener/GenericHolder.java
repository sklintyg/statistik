package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter PERIOD_DATE_TIME_FORMATTERFORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    private static final LocalDate HARD_LIMIT_FROM = new LocalDate("2010-01");
    public static final int MAX_ID_LENGTH = 60;
    private final LocalDate limitTo = new LocalDate().plusYears(5);

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
        enhetId = HSAServiceHelper.getEnhetId(hsa);
        vardgivareId = HSAServiceHelper.getVardgivarId(hsa);
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
        boolean valid = true;

        valid &= validateId("vardgivareId", vardgivareId);
        valid &= validateId("enhetId", enhetId);

        valid &= isValidDate("forsta nedsattning", DocumentHelper.getForstaNedsattningsdag(utlatande));
        valid &= isValidDate("sista nedsattning", DocumentHelper.getSistaNedsattningsdag(utlatande));

        return valid;
    }

    private boolean validateId(String key, String id) {
        if (id == null) {
            LOG.error("Not found {} '{}'.", key, id);
            return false;
        } else if (id.length() > MAX_ID_LENGTH) {
            LOG.error("Invalid {} '{}'.", key, id);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidDate(String key, String dateText) {
        LocalDate date = PERIOD_DATE_TIME_FORMATTERFORMATTER.parseLocalDate(dateText);
        if (date.isBefore(HARD_LIMIT_FROM) || date.isAfter(limitTo)) {
            LOG.error("Date {} not in range '{}'", key, date);
            return false;
        } else {
            return true;
        }
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
