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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WidelineLoader {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineLoader.class);

    private static final int FETCH_SIZE = 10000;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private FactPopulator factPopulator;

    public int populateWarehouse() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatement(connection);
                ResultSet resultSet = stmt.executeQuery()
        ) {
            int lineNo = 0;
            while (resultSet.next()) {
                lineNo++;
                WideLine wideline = toWideline(resultSet);
                factPopulator.accept(wideline);
            }
            return lineNo;
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }

        return -1;
    }

    public WideLine getOne(String intygsId) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatementOne(connection)
        ) {
            stmt.setString(1, intygsId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return toWideline(resultSet);
            }
        } catch (SQLException e) {
            LOG.error("Could not fetch wideline", e);
        }

        return null;
    }

    private WideLine toWideline(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String correlationId = resultSet.getString("correlationId");
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
        String diagnoskod = resultSet.getString("diagnoskod");
        int sjukskrivningsgrad = resultSet.getInt("sjukskrivningsgrad");
        int lakarkon = resultSet.getInt("lakarkon");
        int lakaralder = resultSet.getInt("lakaralder");
        String lakarbefattning = resultSet.getString("lakarbefattning");
        String vardgivare = resultSet.getString("vardgivareid");
        String lakareId = resultSet.getString("lakareid");
        boolean enkeltIntyg = resultSet.getBoolean("enkelt");

        return new WideLine(id, correlationId, lkf, new HsaIdEnhet(enhet), intyg, EventType.CREATED, patientid, startdatum, slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattning, new HsaIdVardgivare(vardgivare), new HsaIdLakare(lakareId), enkeltIntyg);
    }

    private PreparedStatement prepareStatement(Connection connection) throws SQLException {
        String sql = "select id, correlationid, lkf, enhet, lakarintyg, patientid, startdatum,"
                + " slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon, lakaralder,"
                + " lakarbefattning, vardgivareid, lakareid, enkelt from wideline w1 where w1.correlationid not in (select correlationid from wideline where intygtyp = " + EventType.REVOKED.ordinal() + " )";

        int maxIntyg = Integer.parseInt(System.getProperty("statistics.test.max.fact", "0"));
        if (maxIntyg > 0) {
            sql += " limit " + maxIntyg;
            LOG.error("Only reading first " + maxIntyg + " intyg.");
        }
        PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }

    private PreparedStatement prepareStatementOne(Connection connection) throws SQLException {
        String sql = "select id, correlationid, lkf, enhet, lakarintyg, patientid, startdatum,"
                + " slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon, lakaralder,"
                + " lakarbefattning, vardgivareid, lakareid, enkelt from wideline w1 where intygtyp != " + EventType.REVOKED.ordinal() + " AND correlationid = ? LIMIT 1";

        PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }
}
