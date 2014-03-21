/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.listener;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.DiagnosUtil;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

public class GenericHolder {
    private static final Logger LOG = LoggerFactory.getLogger(GenericHolder.class);

    private final SjukfallInfo sjukfallInfo;
    private final JsonNode utlatande;
    private final String enhetId;
    private final String vardgivareId;
    private final String lanId;
    private final Kon kon;
    private String diagnos;
    private String diagnosgrupp;
    private String diagnosundergrupp;
    private final int age;

    public GenericHolder(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, DiagnosUtil diagnosUtil) {
        this.sjukfallInfo = sjukfallInfo;
        this.utlatande = utlatande;
        enhetId = DocumentHelper.getEnhetId(utlatande);
        vardgivareId = DocumentHelper.getVardgivareId(utlatande);
        lanId = HSAServiceHelper.getLan(hsa);
        kon = "man".equalsIgnoreCase(DocumentHelper.getKon(utlatande)) ? Kon.Male : Kon.Female;
        diagnos = DocumentHelper.getDiagnos(utlatande);
        try {
            diagnosgrupp = diagnosUtil.getKapitelIdForCode(diagnos);
            diagnosundergrupp = diagnosUtil.getAvsnittForCode(diagnos).getId();
        } catch (IllegalArgumentException e) {
            LOG.warn("Parse error: {}. (Ignoring ICD10)", e.getMessage());
            diagnosgrupp = "UNKNOWN";
            diagnosundergrupp = "UNKNOWN";
        }
        age = DocumentHelper.getAge(utlatande);
    }

    public boolean validate() {
        boolean result = true;
        if (vardgivareId == null || vardgivareId.length() > 60) {
            LOG.error("Invalid vardgivarid '{}'.", vardgivareId);
            result = false;
        }
        if (enhetId == null || enhetId.length() > 60) {
            LOG.error("Invalid enhetid '{}'.", enhetId);
            result = false;
        }
        return result;
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

    public Kon getKon() {
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
