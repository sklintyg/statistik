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

package se.inera.statistics.service.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@Component
public class FactPopulator {

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Icd10 icd10;

    public void accept(WideLine wideline) {
        Fact fact = toFact(wideline);
        String vardgivare = wideline.getVardgivareId();
        warehouse.accept(fact, vardgivare);
    }

    public Fact toFact(WideLine wideline) {
        long id = wideline.getId();
        String lkf = wideline.getLkf();
        int enhet = warehouse.getEnhetAndRemember(wideline.getEnhet());
        long intyg = wideline.getLakarintyg();
        int patientid = ConversionHelper.patientIdToInt(wideline.getPatientid());
        int startdatum = wideline.getStartdatum();
        int slutdatum = wideline.getSlutdatum();
        int sjukskrivningslangd = slutdatum - startdatum + 1;
        int kon = wideline.getKon();
        int alder = wideline.getAlder();
        String diagnoskapitel = wideline.getDiagnoskapitel();
        String diagnosavsnitt = wideline.getDiagnosavsnitt();
        String diagnoskategori = wideline.getDiagnoskategori();
        int sjukskrivningsgrad = wideline.getSjukskrivningsgrad();
        int lakarkon = wideline.getLakarkon();
        int lakaralder = wideline.getLakaralder();
        int lakarbefattning = parseBefattning(wideline);

        return new Fact(ConversionHelper.extractLan(lkf), ConversionHelper.extractKommun(lkf), ConversionHelper.extractForsamling(lkf), enhet, intyg, patientid, startdatum, kon, alder, extractKapitel(diagnoskapitel), extractAvsnitt(diagnosavsnitt), extractKategori(diagnoskategori), sjukskrivningsgrad, sjukskrivningslangd, lakarkon, lakaralder, lakarbefattning);
    }

    private int parseBefattning(WideLine wideline) {
        String lakarbefattning = wideline.getLakarbefattning();
        if (lakarbefattning != null && lakarbefattning.length() > 0) {
            return Integer.parseInt(lakarbefattning);
        } else {
            return 0;
        }
    }

    private int extractKategori(String diagnoskategori) {
        return icd10.getKategori(diagnoskategori).toInt();
    }

    private int extractAvsnitt(String diagnosavsnitt) {
        return icd10.getAvsnitt(diagnosavsnitt).toInt();
    }

    private int extractKapitel(String diagnoskapitel) {
        return icd10.getKapitel(diagnoskapitel).toInt();
    }
}
