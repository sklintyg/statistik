/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.inera.intygstjanster.ts.services.RegisterTSDiabetesResponder.v1.RegisterTSDiabetesType;
import se.inera.statistics.service.report.model.Kon;

@Component
public class TsDiabetesHelper extends IntygHelper<RegisterTSDiabetesType> {

    private static final Logger LOG = LoggerFactory.getLogger(TsDiabetesHelper.class);

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
    public String getIntygtyp(RegisterTSDiabetesType intyg) {
        return intyg.getIntyg().getIntygsTyp().trim();
    }

    @Override
    public LocalDateTime getSigneringsTidpunkt(RegisterTSDiabetesType intyg) {
        String tidpunkt = intyg.getIntyg().getGrundData().getSigneringsTidstampel();

        return LocalDateTime.parse(tidpunkt, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public Patientdata getPatientData(RegisterTSDiabetesType intyg) {
        String patientIdRaw;
        try {
            patientIdRaw = getPatientId(intyg);
        } catch (Exception ignore) {
            patientIdRaw = "?Unknown?";
        }

        int alder = ConversionHelper.NO_AGE;
        Kon kon = Kon.UNKNOWN;
        try {
            final String personId = DocumentHelper.getUnifiedPersonId(patientIdRaw);
            try {
                final LocalDate signeringsTidpunkt = getSigneringsTidpunkt(intyg).toLocalDate();
                if (signeringsTidpunkt != null) {
                    alder = ConversionHelper.extractAlder(personId, signeringsTidpunkt);
                }
            } finally {
                kon = Kon.parse(ConversionHelper.extractKon(personId));
            }
        } catch (Exception e) {
            LOG.error("Could not extract age and/or gender: '{}'", patientIdRaw);
            LOG.debug("Could not extract age and/or gender: '{}'", patientIdRaw, e);
        }
        return new Patientdata(alder, kon);
    }
}
