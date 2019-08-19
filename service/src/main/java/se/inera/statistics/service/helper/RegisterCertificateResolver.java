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
package se.inera.statistics.service.helper;

import javax.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.certificate.AbstractRegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.Ag114RegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.Ag7804RegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.FkRegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Component
public class RegisterCertificateResolver {

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    @Autowired
    private FkRegisterCertificateHelper fkRegisterCertificateHelper;

    @Autowired
    private Ag7804RegisterCertificateHelper ag7804RegisterCertificateHelper;

    @Autowired
    private Ag114RegisterCertificateHelper ag114RegisterCertificateHelper;

    public AbstractRegisterCertificateHelper resolveIntygHelper(IntygType intygType) {

        switch (intygType) {
            case LISJP:
            case LUSE:
            case FK7263:
            case LUAE_NA:
            case LUAE_FS:
                return fkRegisterCertificateHelper;
            case AG114:
                return ag114RegisterCertificateHelper;
            case AG7804:
                return ag7804RegisterCertificateHelper;
            case DB:
            case DOI:
            case AF00213:
            case AF00251:
            case TSTRK1007:
            case TSTRK1031:
            case TSTRK1009:
            case TSTRK1062:
                return registerCertificateHelper;
            default:
                throw new RuntimeException("Unknown certificate type: " + intygType);
        }
    }

    public IntygType getIntygtyp(RegisterCertificateType intyg) {
        return registerCertificateHelper.getIntygtyp(intyg);
    }

    public RegisterCertificateType unmarshalXml(String data) throws JAXBException {
        return registerCertificateHelper.unmarshalXml(data);
    }

    public HSAKey extractHSAKey(RegisterCertificateType intyg) {
        return registerCertificateHelper.extractHSAKey(intyg);
    }

    public IntygDTO convertToDTO(RegisterCertificateType intyg) {
        IntygType intygType = getIntygtyp(intyg);

        return resolveIntygHelper(intygType).convertToDTO(intyg);
    }
}
