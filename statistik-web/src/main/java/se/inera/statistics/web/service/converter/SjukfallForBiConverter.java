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
package se.inera.statistics.web.service.converter;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.helper.SjukskrivningsGrad;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Lakare;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.util.List;
import java.util.Map;

/**
 * This class is created to handle the GUI-less report used to export data for a BI-tool handled in INTYG-3056.
 */
public class SjukfallForBiConverter {

    private Icd10 icd10;
    private int currentMaxDate = 0;

    public SjukfallForBiConverter(Icd10 icd10) {
        this.icd10 = icd10;
    }

    public String convert(List<Sjukfall> sjukfalls) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("CREATE TABLE date (id INT, date DATE, PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dx (id INT, icd10 varchar(7), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE sjukskrivningsgrad (id INT, text varchar(20), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE age (id INT, text varchar(7), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE gender (id INT, text varchar(7), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE lan (id INT, text varchar(21), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE enheter (id INT, hsaid varchar(100), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE sjukfalllakare (id INT, hsaid varchar(100), gender INT, age INT, PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE sjukfall ("
                + "startdate INT, "
                + "enddate INT, "
                + "length INT, "
                + "dxkapitel INT, "
                + "dxavsnitt INT, "
                + "dxkategori INT, "
                + "dxkod INT, "
                + "sjukskrivningsgrad INT, "
                + "age INT, "
                + "gender INT, "
                + "lan INT, "
                + "enhet INT, "
                + "enkelt BOOLEAN, "
                + "lakare INT, "
                + "CONSTRAINT fk_StartDate FOREIGN KEY (startdate) REFERENCES date(id), "
                + "CONSTRAINT fk_EndDate FOREIGN KEY (enddate) REFERENCES date(id), "
                + "CONSTRAINT fk_DxKapitel FOREIGN KEY (dxkapitel) REFERENCES dx(id), "
                + "CONSTRAINT fk_DxAvsnitt FOREIGN KEY (dxavsnitt) REFERENCES dx(id), "
                + "CONSTRAINT fk_DxKategori FOREIGN KEY (dxkategori) REFERENCES dx(id), "
                + "CONSTRAINT fk_DxKod FOREIGN KEY (dxkod) REFERENCES dx(id), "
                + "CONSTRAINT fk_Sjukskrivningsgrad FOREIGN KEY (sjukskrivningsgrad) REFERENCES sjukskrivningsgrad(id), "
                + "CONSTRAINT fk_Age FOREIGN KEY (age) REFERENCES age(id), "
                + "CONSTRAINT fk_Gender FOREIGN KEY (gender) REFERENCES gender(id), "
                + "CONSTRAINT fk_Lan FOREIGN KEY (lan) REFERENCES lan(id), "
                + "CONSTRAINT fk_Enhet FOREIGN KEY (enhet) REFERENCES enheter(id), "
                + "CONSTRAINT fk_Lakare FOREIGN KEY (lakare) REFERENCES sjukfalllakare(id)"
                + ");");
        stringBuilder.append(System.lineSeparator());

        includeIcdStructure(stringBuilder, icd10.getKapitel(true));

        for (SjukskrivningsGrad grad : SjukskrivningsGrad.values()) {
            stringBuilder.append("INSERT INTO sjukskrivningsgrad(id, text) VALUES (" + grad.getNedsattning() + ", '" + grad.getLabel() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        final int maxAge = 200;
        for (int i = 0; i < maxAge; i++) {
            stringBuilder.append("INSERT INTO age(id, text) VALUES (" + i + ", '" + i + " år');");
            stringBuilder.append(System.lineSeparator());
        }

        for (Kon kon : Kon.values()) {
            stringBuilder.append("INSERT INTO gender(id, text) VALUES (" + kon.getNumberRepresentation() + ", '" + kon.name() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        for (Map.Entry<String, String> lan : new Lan().getCodeToLanMap().entrySet()) {
            stringBuilder.append("INSERT INTO lan(id, text) VALUES (" + Integer.parseInt(lan.getKey()) + ", '" + lan.getValue() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        final Map<HsaIdEnhet, Integer> enhets = Warehouse.getEnhetsView();
        for (Map.Entry<HsaIdEnhet, Integer> enhet : enhets.entrySet()) {
            stringBuilder.append("INSERT INTO enheter(id, hsaid) VALUES (" + enhet.getValue() + ", '" + enhet.getKey().toString() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        for (Sjukfall sjukfall : sjukfalls) {
            insertDatesUpTo(stringBuilder, Math.max(sjukfall.getStart(), sjukfall.getEnd()));

            final Lakare lakare = sjukfall.getLastLakare();
            stringBuilder.append("INSERT INTO sjukfalllakare(id, hsaid, gender, age) VALUES (" + lakare.getId() + ", '" + Warehouse.getLakarId(lakare.getId()).get().toString() + "', " + lakare.getKon().getNumberRepresentation() + ", " + lakare.getAge() + ");");
            stringBuilder.append(System.lineSeparator());

            stringBuilder.append("INSERT INTO sjukfall(startdate, enddate, length, dxkapitel, dxavsnitt, dxkategori, dxkod, sjukskrivningsgrad, age, gender, lan, enhet, enkelt, lakare) VALUES ("
                    + sjukfall.getStart() + ", "
                    + sjukfall.getEnd() + ", "
                    + sjukfall.getRealDays() + ", "
                    + sjukfall.getDiagnoskapitel() + ", "
                    + sjukfall.getDiagnosavsnitt() + ", "
                    + sjukfall.getDiagnoskategori() + ", "
                    + sjukfall.getDiagnoskod() + ", "
                    + sjukfall.getSjukskrivningsgrad() + ", "
                    + sjukfall.getAlder() + ", "
                    + sjukfall.getKon().getNumberRepresentation() + ", "
                    + sjukfall.getLanskod() + ", "
                    + sjukfall.getLastEnhet() + ", "
                    + sjukfall.isEnkelt() + ", "
                    + sjukfall.getLastLakare().getId()
                    + ");");
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private void insertDatesUpTo(StringBuilder stringBuilder, int date) {
        for (; currentMaxDate <= date; currentMaxDate++) {
            stringBuilder.append("INSERT INTO date(id, date) VALUES (" + currentMaxDate + ", '" + WidelineConverter.toDate(currentMaxDate).toString() + "');");
            stringBuilder.append(System.lineSeparator());
        }
    }

    private void includeIcdStructure(StringBuilder stringBuilder, List<? extends Icd10.Id> icdStructure) {
        for (Icd10.Id icd : icdStructure) {
            stringBuilder.append("INSERT INTO dx(id, icd10) VALUES (" + icd.toInt() + ", '" + icd.getId() + "');");
            stringBuilder.append(System.lineSeparator());
            includeIcdStructure(stringBuilder, icd.getSubItems());
        }
    }

}
