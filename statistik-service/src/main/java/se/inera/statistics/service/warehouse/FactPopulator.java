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
        String lkf = wideline.getLkf();
        int enhet = Warehouse.getEnhetAndRemember(wideline.getEnhet());
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
        int[] lakarbefattnings = parseBefattning(wideline);
        int lakare = Warehouse.getNumLakarIdAndRemember(wideline.getLakareId());

        return new Fact(ConversionHelper.extractLan(lkf), ConversionHelper.extractKommun(lkf), ConversionHelper.extractForsamling(lkf), enhet, intyg, patientid, startdatum, kon, alder, extractKapitel(diagnoskapitel), extractAvsnitt(diagnosavsnitt), extractKategori(diagnoskategori), sjukskrivningsgrad, sjukskrivningslangd, lakarkon, lakaralder, lakarbefattnings, lakare);
    }

    private int[] parseBefattning(WideLine wideline) {
        String lakarbefattningString = wideline.getLakarbefattning();
        if (lakarbefattningString != null && lakarbefattningString.length() > 0) {
            final String[] befattningStrings = lakarbefattningString.split(",");
            final int[] befattnings = new int[befattningStrings.length];
            for (int i = 0; i < befattningStrings.length; i++) {
                befattnings[i] = Integer.parseInt(befattningStrings[i].trim());
            }
            return befattnings;
        } else {
            return new int[0];
        }
    }

    private int extractKategori(String diagnoskategori) {
        Icd10.Kategori kategori = icd10.getKategori(diagnoskategori);
        if (kategori == null) {
            return icd10.getKategori("Ö00").toInt();
        }
        return kategori.toInt();
    }

    private int extractAvsnitt(String diagnosavsnitt) {
        Icd10.Avsnitt avsnitt = icd10.getAvsnitt(diagnosavsnitt);
        if (avsnitt == null) {
            return icd10.getAvsnitt("Ö00-Ö00").toInt();
        }
        return avsnitt.toInt();
    }

    private int extractKapitel(String diagnoskapitel) {
        Icd10.Kapitel kapitel = icd10.getKapitel(diagnoskapitel);
        if (kapitel == null) {
            return icd10.getKapitel("Ö00-Ö00").toInt();
        }
        return kapitel.toInt();
    }
}
