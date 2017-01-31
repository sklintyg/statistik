/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;

@Component
public class FactPopulator {
    private static final Logger LOG = LoggerFactory.getLogger(FactPopulator.class);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Icd10 icd10;

    public void accept(WideLine wideline) {
        Fact fact = toFact(wideline);
        HsaIdVardgivare vardgivare = wideline.getVardgivareId();
        warehouse.accept(fact, vardgivare);
    }

    public Fact toFact(WideLine wideline) {
        String lkf = wideline.getLkf();
        int enhet = Warehouse.getEnhetAndRemember(wideline.getEnhet());
        long intyg = wideline.getLakarintyg();
        long patientid = ConversionHelper.patientIdToInt(wideline.getPatientid());
        int startdatum = wideline.getStartdatum();
        int slutdatum = wideline.getSlutdatum();
        int kon = wideline.getKon();
        int alder = wideline.getAlder();
        String diagnoskapitel = wideline.getDiagnoskapitel();
        String diagnosavsnitt = wideline.getDiagnosavsnitt();
        String diagnoskategori = wideline.getDiagnoskategori();
        String diagnoskod = wideline.getDiagnoskod();
        int sjukskrivningsgrad = wideline.getSjukskrivningsgrad();
        int lakarkon = wideline.getLakarkon();
        int lakaralder = wideline.getLakaralder();
        int[] lakarbefattnings = parseBefattning(wideline);
        int lakare = Warehouse.getNumLakarIdAndRemember(wideline.getLakareId());
        boolean enkelt = wideline.isEnkelt();

        return new Fact(wideline.getId(), ConversionHelper.extractLan(lkf), ConversionHelper.extractKommun(lkf), ConversionHelper.extractForsamling(lkf), enhet, intyg, patientid, startdatum, slutdatum, kon, alder, extractKapitel(diagnoskapitel), extractAvsnitt(diagnosavsnitt), extractKategori(diagnoskategori), extractKod(diagnoskod), sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattnings, lakare, enkelt);
    }

    private int[] parseBefattning(WideLine wideline) {
        String lakarbefattningString = wideline.getLakarbefattning();
        if (lakarbefattningString != null && lakarbefattningString.length() > 0) {
            final String[] befattningStrings = lakarbefattningString.split(",");
            final ArrayList<Integer> befattnings = new ArrayList<>();
            for (String befattningString : befattningStrings) {
                String befattning = befattningString.trim();
                try {
                    befattnings.add(Integer.parseInt(befattning));
                } catch (NumberFormatException nfe) {
                    LOG.info("Unknown befattning: '" + befattning + "' for doctor in intyg: " + wideline.getCorrelationId());
                }
            }
            if (befattnings.isEmpty()) {
                return new int[]{LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE};
            }
            return ArrayUtils.toPrimitive(befattnings.toArray(new Integer[0]));
        }
        return new int[]{LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE};
    }

    private int extractKategori(String diagnoskategori) {
        Icd10.Kategori kategori = icd10.getKategori(diagnoskategori);
        if (kategori == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_KATEGORI, Icd10RangeType.KATEGORI);
        }
        return kategori.toInt();
    }

    private int extractKod(String diagnoskod) {
        Icd10.Kod kod = icd10.getKod(diagnoskod);
        if (kod == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_KOD, Icd10RangeType.KOD);
        }
        return kod.toInt();
    }

    private int extractAvsnitt(String diagnosavsnitt) {
        Icd10.Avsnitt avsnitt = icd10.getAvsnitt(diagnosavsnitt);
        if (avsnitt == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_AVSNITT, Icd10RangeType.AVSNITT);
        }
        return avsnitt.toInt();
    }

    private int extractKapitel(String diagnoskapitel) {
        Icd10.Kapitel kapitel = icd10.getKapitel(diagnoskapitel);
        if (kapitel == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_KAPITEL, Icd10RangeType.KAPITEL);
        }
        return kapitel.toInt();
    }
}
