package se.inera.statistics.fileservice;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ListHsaUnitType {
    protected String hsaIdentity;
    protected String dn;
    protected String name;
    protected Boolean isHsaHealthCareProvider;
    protected Boolean isHsaHealthCareUnit;
    protected String hsaResponsibleHealthCareProvider;
    protected List<String> hsaHealthCareUnitMembers;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;
}
