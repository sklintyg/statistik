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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FactPopulator {

    private static final int FETCH_SIZE = 10000;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Warehouse warehouse;

    public void populateWarehouse() {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        try {
            connection = dataSource.getConnection();
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setReadOnly(true);
            stmt = connection.prepareStatement("select id, lkf, enhet, lakarintyg, patientid, startdatum, slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattning, vardgivareid from wideline", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(FETCH_SIZE);
            resultSet = stmt.executeQuery();
            int lineNo = 0;
            Fact line = null;
            while (resultSet.next()) {
                System.out.println("Reading line: " + ++lineNo);
                int id = resultSet.getInt("id");
                String lkf = resultSet.getString("lkf");
                String enhet = resultSet.getString("enhet");
                int intyg = resultSet.getInt("lakarintyg");
                String patientid = resultSet.getString("patientid");
                int startdatum = resultSet.getInt("startdatum");
                int slutdatum = resultSet.getInt("slutdatum");
                int kon = resultSet.getInt("kon");
                int alder = resultSet.getInt("alder");
                String diagnoskapitel = resultSet.getString("diagnoskapitel");
                String diagnosavsnitt = resultSet.getString("diagnosavsnitt");
                String diagnoskategori = resultSet.getString("diagnoskategori");
                int sjukskrivningsgrad = resultSet.getInt("sjukskrivningsgrad");
                int lakarkon = resultSet.getInt("lakarkon");
                int lakaralder = resultSet.getInt("lakaralder");
                String lakarbefattning = resultSet.getString("lakarbefattning");
                String vardgivare = resultSet.getString("vardgivareid");
//                Fact fact = new Fact(id, kommun, forsamling, enhetId, intyg, patient, startdatum, kon, alder, diagnoskapitelid, diagnosavsnittid, diagnoskategoriid, sjukskrivningsgrad, sjukskrivningsgslangd, lakarkon, lakaralder, lakarbefattningid);
                Fact fact = new Fact(id, 0, 0, 0, intyg, 0, startdatum, kon, alder, 0, 0, 0, sjukskrivningsgrad, 0, lakarkon, lakaralder, 0);
                warehouse.accept(fact, vardgivare);
            }
            connection.commit();
            connection.setAutoCommit(previousAutoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
