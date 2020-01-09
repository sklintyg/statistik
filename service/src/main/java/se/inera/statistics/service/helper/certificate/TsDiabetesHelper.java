/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import se.inera.intygstjanster.ts.services.RegisterTSDiabetesResponder.v1.RegisterTSDiabetesType;
import se.inera.statistics.service.helper.IntygHelper;
import se.inera.statistics.service.warehouse.IntygType;

@Component
public class TsDiabetesHelper extends IntygHelper<RegisterTSDiabetesType> {

    @Override
    protected Class<RegisterTSDiabetesType> getIntygClass() {
        return RegisterTSDiabetesType.class;
    }

    @Override
    public String getEnhetId(RegisterTSDiabetesType utlatande) {
        return utlatande.getIntyg().getGrundData().getSkapadAv().getVardenhet().getEnhetsId().getExtension();
    }

    @Override
    public String getLakareId(RegisterTSDiabetesType document) {
        return document.getIntyg().getGrundData().getSkapadAv().getPersonId().getExtension();
    }

    @Override
    public String getVardgivareId(RegisterTSDiabetesType document) {
        return document.getIntyg().getGrundData().getSkapadAv().getVardenhet().getVardgivare().getVardgivarid().getExtension();
    }

    @Override
    public String getPatientId(RegisterTSDiabetesType intyg) {
        return intyg.getIntyg().getGrundData().getPatient().getPersonId().getExtension();
    }

    @Override
    public String getIntygId(RegisterTSDiabetesType intyg) {
        return intyg.getIntyg().getIntygsId();
    }

    @Override
    public IntygType getIntygtyp(RegisterTSDiabetesType intyg) {
        return IntygType.getByItIntygType(intyg.getIntyg().getIntygsTyp().trim());
    }

    @Override
    public LocalDateTime getSigneringsTidpunkt(RegisterTSDiabetesType intyg) {
        String tidpunkt = intyg.getIntyg().getGrundData().getSigneringsTidstampel();

        return LocalDateTime.parse(tidpunkt, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public LocalDate getDateForPatientAge(RegisterTSDiabetesType intyg) {
        return getSigneringsTidpunkt(intyg).toLocalDate();
    }
}
