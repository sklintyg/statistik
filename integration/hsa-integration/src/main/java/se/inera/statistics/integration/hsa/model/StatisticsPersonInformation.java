package se.inera.statistics.integration.hsa.model;

import lombok.Data;
import se.inera.intyg.infra.integration.hsatk.model.HCPSpecialityCodes;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StatisticsPersonInformation {
    protected String personHsaId;
    protected String givenName;
    protected String middleAndSurName;
    protected List<String> healthCareProfessionalLicence;
    protected List<PaTitle> paTitle;
    protected List<String> specialityName;
    protected List<String> specialityCode;
    protected Boolean protectedPerson;
    protected LocalDateTime personStartDate;
    protected LocalDateTime personEndDate;
    protected Boolean feignedPerson;
    protected List<HCPSpecialityCodes> healthCareProfessionalLicenceSpeciality;
    protected String age;
    protected String gender;

    @Data
    public static class PaTitle {
        private String paTitleName;
        private String paTitleCode;
    }
}
