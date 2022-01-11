/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper.certificate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import se.inera.intygstjanster.ts.services.RegisterTSBasResponder.v1.RegisterTSBasType;
import se.inera.statistics.service.helper.IntygHelper;
import se.inera.statistics.service.warehouse.IntygType;

@Component
public class TsBasHelper extends IntygHelper<RegisterTSBasType> {

    @Override
    protected Class<RegisterTSBasType> getIntygClass() {
        return RegisterTSBasType.class;
    }

    @Override
    public String getEnhetId(RegisterTSBasType utlatande) {
        return utlatande.getIntyg().getGrundData().getSkapadAv().getVardenhet().getEnhetsId().getExtension();
    }

    @Override
    public String getLakareId(RegisterTSBasType document) {
        return document.getIntyg().getGrundData().getSkapadAv().getPersonId().getExtension();
    }

    @Override
    public String getVardgivareId(RegisterTSBasType document) {
        return document.getIntyg().getGrundData().getSkapadAv().getVardenhet().getVardgivare().getVardgivarid().getExtension();
    }

    @Override
    public String getPatientId(RegisterTSBasType intyg) {
        return intyg.getIntyg().getGrundData().getPatient().getPersonId().getExtension();
    }

    @Override
    public String getIntygId(RegisterTSBasType intyg) {
        return intyg.getIntyg().getIntygsId();
    }

    @Override
    public IntygType getIntygtyp(RegisterTSBasType intyg) {
        return IntygType.getByItIntygType(intyg.getIntyg().getIntygsTyp().trim());
    }

    @Override
    public String getCertificateVersion(RegisterTSBasType certificate) {
        return certificate.getIntyg().getVersion();
    }

    @Override
    public LocalDateTime getSigneringsTidpunkt(RegisterTSBasType intyg) {
        String tidpunkt = intyg.getIntyg().getGrundData().getSigneringsTidstampel();

        return LocalDateTime.parse(tidpunkt, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public LocalDate getDateForPatientAge(RegisterTSBasType intyg) {
        return getSigneringsTidpunkt(intyg).toLocalDate();
    }
}
