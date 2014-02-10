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

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

public class GenericHolder {

    private final SjukfallInfo sjukfallInfo;
    private final JsonNode utlatande;
    private final String enhetId;
    private final String vardgivareId;
    private final String lanId;
    private final Sex kon;
    private String diagnos;
    private String diagnosgrupp;
    private String diagnosundergrupp;
    private int age;

    public GenericHolder(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, DiagnosisGroupsUtil diagnosisGroupsUtil) {
        this.sjukfallInfo = sjukfallInfo;
        this.utlatande = utlatande;
        enhetId = DocumentHelper.getEnhetId(utlatande);
        vardgivareId = DocumentHelper.getVardgivareId(utlatande);
        lanId = HSAServiceHelper.getLan(hsa);
        kon = "man".equalsIgnoreCase(DocumentHelper.getKon(utlatande)) ? Sex.Male : Sex.Female;
        diagnos = DocumentHelper.getDiagnos(utlatande);
        diagnosgrupp = diagnosisGroupsUtil.getGroupIdForCode(diagnos);
        diagnosundergrupp = diagnosisGroupsUtil.getSubGroupForCode(diagnos).getId();
        age = DocumentHelper.getAge(utlatande);
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

    public Sex getKon() {
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
