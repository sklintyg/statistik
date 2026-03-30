/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.integration.hsa.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import se.inera.intyg.infra.integration.hsatk.model.HCPSpecialityCodes;

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
