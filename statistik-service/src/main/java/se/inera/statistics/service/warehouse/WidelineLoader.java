/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.util.ArrayList;
import java.util.List;

@Component
public class WidelineLoader {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineLoader.class);

    private static final int FETCH_SIZE = 10000;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private FactConverter factConverter;

    public List<Fact> getFactsForVg(HsaIdVardgivare vgid) {
        final ArrayList<Fact> facts = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatementForVg(connection, vgid);
                ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                WideLine wideline = toWideline(resultSet);
                facts.add(factConverter.toFact(wideline));
            }
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }
        return facts;
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

        return new WideLine(id, correlationId, lkf, new HsaIdEnhet(enhet), intyg, EventType.CREATED, patientid, startdatum, slutdatum, kon,
                alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon, lakaralder,
                lakarbefattning, new HsaIdVardgivare(vardgivare), new HsaIdLakare(lakareId));
    }

    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification = "We know what we're doing. No user supplied data.")
    private PreparedStatement prepareStatementForVg(Connection connection, HsaIdVardgivare vgid) throws SQLException {
        String sql = "select id, correlationid, lkf, enhet, lakarintyg, patientid, startdatum, slutdatum, kon, alder, diagnoskapitel, "
                + "diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattning, vardgivareid, "
                + "lakareid from wideline w1 where w1.correlationid not in (select correlationid from wideline where intygtyp = "
                + EventType.REVOKED.ordinal() + " ) AND w1.vardgivareid = '" + vgid.getId() + "'";

        int maxIntyg = Integer.parseInt(System.getProperty("statistics.test.max.fact", "0"));
        if (maxIntyg > 0) {
            sql += " limit " + maxIntyg;
            LOG.error("Only reading first " + maxIntyg + " intyg.");
        }
        PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }

    public List<HsaIdVardgivare> getAllVgs() {
        final ArrayList<HsaIdVardgivare> vgs = new ArrayList<>();
        final String sql = "SELECT distinct vardgivareid from wideline w1 where w1.correlationid not in "
                + "(select correlationid from wideline where intygtyp = " + EventType.REVOKED.ordinal() + " )";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final String vardgivareid = resultSet.getString("vardgivareid");
                    final HsaIdVardgivare vgId = new HsaIdVardgivare(vardgivareid);
                    vgs.add(vgId);
                }
        } catch (SQLException e) {
            LOG.error("Could not get all vgs from DB", e);
        }
        return vgs;
    }

}
