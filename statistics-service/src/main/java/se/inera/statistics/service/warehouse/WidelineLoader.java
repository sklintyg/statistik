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
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WidelineLoader {

    private static final int FETCH_SIZE = 10000;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private FactPopulator factPopulator;

    public int populateWarehouse() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatement(connection);
                ResultSet resultSet = stmt.executeQuery();
        ) {
            int lineNo = 0;
            while (resultSet.next()) {
                System.out.println("Reading line: " + ++lineNo);
                WideLine wideline = toWideline(resultSet);
                factPopulator.accept(wideline);
            }
            return lineNo;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private WideLine toWideline(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String lkf = resultSet.getString("lkf");
        String enhet = resultSet.getString("enhet");
        long intyg = resultSet.getLong("lakarintyg");
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

        return new WideLine(id, lkf, enhet, intyg, patientid, startdatum, slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattning, vardgivare);
    }

    private PreparedStatement prepareStatement(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        connection.setReadOnly(true);
        PreparedStatement stmt = connection.prepareStatement("select id, lkf, enhet, lakarintyg, patientid, startdatum, slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattning, vardgivareid from wideline", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }
}
