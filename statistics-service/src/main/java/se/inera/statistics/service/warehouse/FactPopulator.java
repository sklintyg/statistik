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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FactPopulator {

    private static final int FETCH_SIZE = 10000;
    public static final int UNKNOWN = 0;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Icd10 icd10;

    public void populateWarehouse() {

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatement(connection);
                ResultSet resultSet = stmt.executeQuery();

        ) {
            int lineNo = UNKNOWN;
            Fact line = null;
            while (resultSet.next()) {
                System.out.println("Reading line: " + ++lineNo);
                int id = resultSet.getInt("id");
                String lkf = resultSet.getString("lkf");
                int enhet = ConversionHelper.getEnhetAndRemember(resultSet.getString("enhet"));
                int intyg = resultSet.getInt("lakarintyg");
                int patientid = ConversionHelper.patientIdToInt(resultSet.getString("patientid"));
                int startdatum = resultSet.getInt("startdatum");
                int slutdatum = resultSet.getInt("slutdatum");
                int sjukskrivningslangd = slutdatum - startdatum + 1;
                int kon = resultSet.getInt("kon");
                int alder = resultSet.getInt("alder");
                String diagnoskapitel = resultSet.getString("diagnoskapitel");
                String diagnosavsnitt = resultSet.getString("diagnosavsnitt");
                String diagnoskategori = resultSet.getString("diagnoskategori");
                int sjukskrivningsgrad = resultSet.getInt("sjukskrivningsgrad");
                int lakarkon = resultSet.getInt("lakarkon");
                int lakaralder = resultSet.getInt("lakaralder");
                int lakarbefattning = Integer.parseInt(resultSet.getString("lakarbefattning"));
                String vardgivare = resultSet.getString("vardgivareid");
                Fact fact = new Fact(extractLan(lkf), extractKommun(lkf), extractForsamling(lkf), enhet, intyg, patientid, startdatum, kon, alder, extractKapitel(diagnoskapitel), extractAvsnitt(diagnosavsnitt), extractKategori(diagnoskategori), sjukskrivningsgrad, sjukskrivningslangd, lakarkon, lakaralder, lakarbefattning);
                warehouse.accept(fact, vardgivare);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private int extractLan(String lkf) {
            return extractLKF(lkf, 2);
    }

    private int extractKommun(String lkf) {
        return extractLKF(lkf, 4);
    }

    private int extractForsamling(String lkf) {
        return extractLKF(lkf, 6);
    }

    private int extractLKF(String lkf, int length) {
        if (lkf.length() < length) {
            return UNKNOWN;
        } else {
            return Integer.parseInt(lkf.substring(0, length));
        }
    }

    private PreparedStatement prepareStatement(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        connection.setReadOnly(true);
        PreparedStatement stmt = connection.prepareStatement("select id, lkf, enhet, lakarintyg, patientid, startdatum, slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattning, vardgivareid from wideline", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }
}
