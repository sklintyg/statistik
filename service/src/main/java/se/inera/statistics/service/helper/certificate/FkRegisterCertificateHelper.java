/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import se.inera.statistics.service.helper.SjukskrivningsGrad;
import se.inera.statistics.service.processlog.Arbetsnedsattning;
import se.inera.statistics.service.processlog.IntygDTO;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Svar;

@Component
public class FkRegisterCertificateHelper extends AbstractRegisterCertificateHelper {

    public static final String DIAGNOS_SVAR_ID_6 = "6";
    public static final String DIAGNOS_DELSVAR_ID_6 = "6.2";
    public static final String BEHOV_AV_SJUKSKRIVNING_SVAR_ID_32 = "32";
    public static final String BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32 = "32.1";
    public static final String BEHOV_AV_SJUKSKRIVNING_PERIOD_DELSVARSVAR_ID_32 = "32.2";

    @Override
    public LocalDate getDateForPatientAge(RegisterCertificateType intyg) {
        LocalDate date = getSistaNedsattningsdag(intyg);

        // Use signeringsTidpunkt when sistaNedsattningsdag is missing in intyg.
        if (date == null) {
            date = getSigneringsTidpunkt(intyg).toLocalDate();
        }

        return date;
    }

    @SuppressWarnings("squid:S134") //I can't see a better way to write this with fewer nested statements
    private String getDx(RegisterCertificateType intyg) {

        for (Svar svar : intyg.getIntyg().getSvar()) {
            if (DIAGNOS_SVAR_ID_6.equals(svar.getId())) {
                for (Svar.Delsvar delsvar : svar.getDelsvar()) {
                    if (DIAGNOS_DELSVAR_ID_6.equals(delsvar.getId())) {
                        CVType diagnos = getCVSvarContent(delsvar);
                        return diagnos.getCode();
                    }
                }
            }
        }
        return null;
    }

    private Arbetsnedsattning getArbetsnedsattning(Svar svar) {
        int nedsattning = -1;
        DatePeriodType datePeriod = new DatePeriodType();

        for (Svar.Delsvar delsvar : svar.getDelsvar()) {
            switch (delsvar.getId()) {
                case BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32:
                    String sjukskrivningsnivaString = getCVSvarContent(delsvar).getCode();
                    final SjukskrivningsGrad sjukskrivningsGrad = SjukskrivningsGrad.valueOf(sjukskrivningsnivaString);
                    nedsattning = sjukskrivningsGrad.getNedsattning();
                    break;
                case BEHOV_AV_SJUKSKRIVNING_PERIOD_DELSVARSVAR_ID_32:
                    datePeriod = getDatePeriodTypeContent(delsvar);
                    break;
                default:
                    break;
            }
        }
        return new Arbetsnedsattning(nedsattning, datePeriod.getStart(), datePeriod.getEnd());
    }

    private LocalDate getSistaNedsattningsdag(RegisterCertificateType document) {
        final List<Arbetsnedsattning> arbetsnedsattnings = getArbetsnedsattning(document);
        LocalDate to = null;
        for (Arbetsnedsattning arbetsnedsattning : arbetsnedsattnings) {
            final LocalDate candidate = arbetsnedsattning.getSlut();
            if (to == null || candidate.isAfter(to)) {
                to = candidate;
            }
        }
        return to;
    }

    private List<Arbetsnedsattning> getArbetsnedsattning(RegisterCertificateType intyg) {
        final List<Arbetsnedsattning> arbetsnedsattnings = new ArrayList<>();
        for (Svar svar : intyg.getIntyg().getSvar()) {
            if (BEHOV_AV_SJUKSKRIVNING_SVAR_ID_32.equals(svar.getId())) {
                final Arbetsnedsattning arbetsnedsattning = getArbetsnedsattning(svar);
                arbetsnedsattnings.add(arbetsnedsattning);
            }
        }
        return arbetsnedsattnings;
    }

    @Override
    public IntygDTO convertToDTO(RegisterCertificateType intyg) {
        IntygDTO dto = super.convertToDTO(intyg);

        if (dto == null) {
            return null;
        }

        String diagnos = getDx(intyg);
        List<Arbetsnedsattning> arbetsnedsattnings = getArbetsnedsattning(intyg);

        dto.setDiagnoskod(diagnos);
        dto.setArbetsnedsattnings(arbetsnedsattnings);

        return dto;
    }

}
