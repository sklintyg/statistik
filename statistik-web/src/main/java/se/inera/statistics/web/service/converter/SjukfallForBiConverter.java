/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.helper.SjukskrivningsGrad;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Lakare;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineConverter;

/**
 * This class is created to handle the GUI-less report used to export data for a BI-tool handled in INTYG-3056.
 */
@Component
public class SjukfallForBiConverter {

    @Autowired
    private Icd10 icd10;

    @Autowired
    private EnhetManager enhetManager;

    private int currentMaxDate = 0;

    public String convert(List<Sjukfall> sjukfalls) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("CREATE TABLE dim_date (id INT, date DATE, PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_dx_kapitel (id INT, icd10 varchar(7), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(
                "CREATE TABLE dim_dx_avsnitt (id INT, icd10 varchar(7), kapitel INT, PRIMARY KEY (id), CONSTRAINT fk_avsnitt_kapitel "
                        + "FOREIGN KEY (kapitel) REFERENCES dim_dx_kapitel(id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(
                "CREATE TABLE dim_dx_kategori (id INT, icd10 varchar(7), kapitel INT, avsnitt INT, PRIMARY KEY (id), CONSTRAINT "
                        + "fk_kategori_kapitel FOREIGN KEY (kapitel) REFERENCES dim_dx_kapitel(id), CONSTRAINT fk_kategori_avsnitt "
                        + "FOREIGN KEY (avsnitt) REFERENCES dim_dx_avsnitt(id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(
                "CREATE TABLE dim_dx_kod (id INT, icd10 varchar(7), kapitel INT, avsnitt INT, kategori INT, PRIMARY KEY (id), CONSTRAINT "
                        + "fk_kod_kapitel FOREIGN KEY (kapitel) REFERENCES dim_dx_kapitel(id), CONSTRAINT fk_kod_avsnitt FOREIGN KEY "
                        + "(avsnitt) REFERENCES dim_dx_avsnitt(id), CONSTRAINT fk_kod_kategori FOREIGN KEY (kategori) REFERENCES "
                        + "dim_dx_kategori(id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_sjukskrivningsgrad (id INT, text varchar(20), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_age (id INT, text varchar(7), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_gender (id INT, text varchar(7), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_lan (id INT, text varchar(21), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_enheter (id INT, hsaid varchar(100), PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE dim_sjukfalllakare (id INT, hsaid varchar(100), gender INT, age INT, PRIMARY KEY (id));");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("CREATE TABLE fact_sjukfall ("
                + "id INT NOT NULL AUTO_INCREMENT, "
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
                + "lakare INT, "
                + "PRIMARY KEY (id), "
                + "CONSTRAINT fk_StartDate FOREIGN KEY (startdate) REFERENCES dim_date(id), "
                + "CONSTRAINT fk_EndDate FOREIGN KEY (enddate) REFERENCES dim_date(id), "
                + "CONSTRAINT fk_DxKapitel FOREIGN KEY (dxkapitel) REFERENCES dim_dx_kapitel(id), "
                + "CONSTRAINT fk_DxAvsnitt FOREIGN KEY (dxavsnitt) REFERENCES dim_dx_avsnitt(id), "
                + "CONSTRAINT fk_DxKategori FOREIGN KEY (dxkategori) REFERENCES dim_dx_kategori(id), "
                + "CONSTRAINT fk_DxKod FOREIGN KEY (dxkod) REFERENCES dim_dx_kod(id), "
                + "CONSTRAINT fk_Sjukskrivningsgrad FOREIGN KEY (sjukskrivningsgrad) REFERENCES dim_sjukskrivningsgrad(id), "
                + "CONSTRAINT fk_Age FOREIGN KEY (age) REFERENCES dim_age(id), "
                + "CONSTRAINT fk_Gender FOREIGN KEY (gender) REFERENCES dim_gender(id), "
                + "CONSTRAINT fk_Lan FOREIGN KEY (lan) REFERENCES dim_lan(id), "
                + "CONSTRAINT fk_Enhet FOREIGN KEY (enhet) REFERENCES dim_enheter(id), "
                + "CONSTRAINT fk_Lakare FOREIGN KEY (lakare) REFERENCES dim_sjukfalllakare(id)"
                + ");");
        stringBuilder.append(System.lineSeparator());

        includeIcdStructure(stringBuilder, icd10.getKapitel(true));

        for (SjukskrivningsGrad grad : SjukskrivningsGrad.values()) {
            stringBuilder.append(
                    "INSERT INTO dim_sjukskrivningsgrad(id, text) VALUES (" + grad.getNedsattning() + ", '" + grad.getLabel() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        final int maxAge = 200;
        for (int i = 0; i < maxAge; i++) {
            stringBuilder.append("INSERT INTO dim_age(id, text) VALUES (" + i + ", '" + i + " år');");
            stringBuilder.append(System.lineSeparator());
        }

        for (Kon kon : Kon.values()) {
            stringBuilder.append("INSERT INTO dim_gender(id, text) VALUES (" + kon.getNumberRepresentation() + ", '" + kon.name() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        for (Map.Entry<String, String> lan : new Lan().getCodeToLanMap().entrySet()) {
            stringBuilder
                    .append("INSERT INTO dim_lan(id, text) VALUES (" + Integer.parseInt(lan.getKey()) + ", '" + lan.getValue() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        final Map<HsaIdEnhet, Integer> enhetIds = Warehouse.getEnhetsView();
        for (Map.Entry<HsaIdEnhet, Integer> enhet : enhetIds.entrySet()) {
            stringBuilder
                    .append("INSERT INTO dim_enheter(id, hsaid) VALUES (" + enhet.getValue() + ", '" + enhet.getKey().toString() + "');");
            stringBuilder.append(System.lineSeparator());
        }

        List<Integer> addedLakare = new ArrayList<>();

        for (Sjukfall sjukfall : sjukfalls) {
            insertDatesUpTo(stringBuilder, Math.max(sjukfall.getStart(), sjukfall.getEnd()));

            final Lakare lakare = sjukfall.getLastLakare();
            if (!addedLakare.contains(lakare.getId())) {
                stringBuilder.append("INSERT INTO dim_sjukfalllakare(id, hsaid, gender, age) VALUES (" + lakare.getId() + ", '"
                        + Warehouse.getLakarId(lakare.getId()).get().toString() + "', " + lakare.getKon().getNumberRepresentation() + ", "
                        + lakare.getAge() + ");");
                stringBuilder.append(System.lineSeparator());
                addedLakare.add(lakare.getId());
            }

            stringBuilder.append(
                    "INSERT INTO fact_sjukfall(startdate, enddate, length, dxkapitel, dxavsnitt, dxkategori, dxkod, sjukskrivningsgrad, "
                            + "age, gender, lan, enhet, lakare) VALUES ("
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
                            + sjukfall.getLastLakare().getId()
                            + ");");
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private void insertDatesUpTo(StringBuilder stringBuilder, int date) {
        for (; currentMaxDate <= date; currentMaxDate++) {
            stringBuilder.append("INSERT INTO dim_date(id, date) VALUES (" + currentMaxDate + ", '"
                    + WidelineConverter.toDate(currentMaxDate).toString() + "');");
            stringBuilder.append(System.lineSeparator());
        }
    }

    private void includeIcdStructure(StringBuilder stringBuilder, List<? extends Icd10.Id> icdStructure) {
        for (Icd10.Id icd : icdStructure) {
            if (icd instanceof Icd10.Kapitel) {
                stringBuilder.append("INSERT INTO dim_dx_kapitel(id, icd10) VALUES (" + icd.toInt() + ", '" + icd.getId() + "');");
                stringBuilder.append(System.lineSeparator());
            } else if (icd instanceof Icd10.Avsnitt) {
                final Icd10.Id kapitel = icd.getParent().get();
                stringBuilder.append("INSERT INTO dim_dx_avsnitt(id, icd10, kapitel) VALUES (" + icd.toInt() + ", '" + icd.getId() + "', "
                        + kapitel.toInt() + ");");
                stringBuilder.append(System.lineSeparator());
            } else if (icd instanceof Icd10.Kategori) {
                final Icd10.Id avsnitt = icd.getParent().get();
                final Icd10.Id kapitel = avsnitt.getParent().get();
                stringBuilder.append("INSERT INTO dim_dx_kategori(id, icd10, kapitel, avsnitt) VALUES (" + icd.toInt() + ", '" + icd.getId()
                        + "', " + kapitel.toInt() + ", " + avsnitt.toInt() + ");");
                stringBuilder.append(System.lineSeparator());
            } else if (icd instanceof Icd10.Kod) {
                final Icd10.Id kategori = icd.getParent().get();
                final Icd10.Id avsnitt = kategori.getParent().get();
                final Icd10.Id kapitel = avsnitt.getParent().get();
                stringBuilder.append("INSERT INTO dim_dx_kod(id, icd10, kapitel, avsnitt, kategori) VALUES (" + icd.toInt() + ", '"
                        + icd.getId() + "', " + kapitel.toInt() + ", " + avsnitt.toInt() + ", " + kategori.toInt() + ");");
                stringBuilder.append(System.lineSeparator());
            } else {
                throw new RuntimeException("Unknown icd type: " + icd);
                // Unknown type
            }
            includeIcdStructure(stringBuilder, icd.getSubItems());
        }
    }

}
