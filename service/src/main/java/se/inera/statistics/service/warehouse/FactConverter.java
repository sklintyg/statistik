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
package se.inera.statistics.service.warehouse;

import com.google.common.base.Splitter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;

@Component
class FactConverter {

    private static final Logger LOG = LoggerFactory.getLogger(FactConverter.class);

    @Autowired
    private Icd10 icd10;

    Fact toFact(WideLine wideline) {
        String lkf = wideline.getLkf();
        HsaIdEnhet vardenhet = wideline.getVardenhet();
        HsaIdEnhet enhet = wideline.getEnhet();
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
        int[] lakarbefattnings = parseBefattning(wideline.getLakarbefattning(), wideline.getCorrelationId());
        HsaIdLakare lakare = wideline.getLakareId();

        return new Fact(wideline.getId(), ConversionHelper.extractLan(lkf), ConversionHelper.extractKommun(lkf),
            ConversionHelper.extractForsamling(lkf), vardenhet, enhet, intyg, patientid, startdatum, slutdatum, kon, alder,
            extractKapitel(diagnoskapitel), extractAvsnitt(diagnosavsnitt), extractKategori(diagnoskategori),
            extractKod(diagnoskod, diagnoskategori), sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattnings,
            lakare);
    }

    int[] parseBefattning(String lakarbefattningString, String correlationId) {
        if (lakarbefattningString == null || lakarbefattningString.isEmpty()) {
            return new int[]{LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE};
        }
        final List<String> befattningStrings = Splitter.on(',').splitToList(lakarbefattningString);
        final int[] befattnings = new int[befattningStrings.size()];
        int parsedBefattningsAdded = 0;
        for (String befattningString : befattningStrings) {
            String befattning = befattningString.trim();
            final Integer parsed = ConversionHelper.parseInt(befattning);
            if (parsed != null) {
                befattnings[parsedBefattningsAdded] = parsed;
                parsedBefattningsAdded++;
            } else {
                LOG.info("Unknown befattning: '" + befattning + "' for doctor in intyg: " + correlationId);
            }
        }
        if (parsedBefattningsAdded == 0) {
            return new int[]{LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE};
        }
        return Arrays.copyOfRange(befattnings, 0, parsedBefattningsAdded); //ArrayUtils.toPrimitive(befattnings.toArray(new Integer[0]));
    }

    int extractKategori(String diagnoskategori) {
        Icd10.Kategori kategori = icd10.getKategori(diagnoskategori);
        if (kategori == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_KATEGORI, Icd10RangeType.KATEGORI);
        }
        return kategori.toInt();
    }

    int extractKod(String diagnoskod, String diagnoskategori) {
        Icd10.Kod kod = icd10.getKod(diagnoskod);
        if (kod == null) {
            Icd10.Kategori kategori = icd10.getKategori(diagnoskategori);
            final Optional<Icd10.Kod> unknownKodInKatergori = kategori != null ? kategori.getUnknownKod() : Optional.empty();
            return unknownKodInKatergori.map(Icd10.Kod::toInt)
                .orElseGet(() -> Icd10.icd10ToInt(Icd10.OTHER_KOD, Icd10RangeType.KOD));
        }
        return kod.toInt();
    }

    int extractAvsnitt(String diagnosavsnitt) {
        Icd10.Avsnitt avsnitt = icd10.getAvsnitt(diagnosavsnitt);
        if (avsnitt == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_AVSNITT, Icd10RangeType.AVSNITT);
        }
        return avsnitt.toInt();
    }

    int extractKapitel(String diagnoskapitel) {
        Icd10.Kapitel kapitel = icd10.getKapitel(diagnoskapitel);
        if (kapitel == null) {
            return Icd10.icd10ToInt(Icd10.OTHER_KAPITEL, Icd10RangeType.KAPITEL);
        }
        return kapitel.toInt();
    }
}
