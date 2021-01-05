/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.IntygDTO;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Svar;

@Component
public class Ag7804RegisterCertificateHelper extends AbstractRegisterCertificateHelper {

    public static final String DIAGNOS_SVAR_ID_6 = "6";
    public static final String DIAGNOS_DELSVAR_ID_6 = "6.2";

    @Override
    public LocalDate getDateForPatientAge(RegisterCertificateType intyg) {
        return getSigneringsTidpunkt(intyg).toLocalDate();
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

    @Override
    public IntygDTO convertToDTO(RegisterCertificateType intyg) {
        IntygDTO dto = super.convertToDTO(intyg);

        if (dto == null) {
            return null;
        }

        String diagnos = getDx(intyg);

        dto.setDiagnoskod(diagnos);

        return dto;
    }

}
