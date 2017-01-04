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
package se.inera.statistics.service.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EnhetLoader {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetLoader.class);

    private static final int FETCH_SIZE = 10000;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Warehouse warehouse;

    public int populateWarehouse() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatement(connection);
                ResultSet resultSet = stmt.executeQuery()
        ) {
            int lineNo = 0;
            while (resultSet.next()) {
                lineNo++;
                Enhet enhet = toEnhet(resultSet);
                warehouse.accept(enhet);
            }
            return lineNo;
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }

        return -1;
    }

    private Enhet toEnhet(ResultSet resultSet) throws SQLException {
        String vardgivareId = resultSet.getString("vardgivareId");
        String enhetId = resultSet.getString("enhetId");
        String enhetNamn = resultSet.getString("namn");
        String lansId = resultSet.getString("lansId");
        String kommunId = resultSet.getString("kommunId");
        String verksamhetsTyper = resultSet.getString("verksamhetsTyper");

        return new Enhet(new HsaIdVardgivare(vardgivareId), new HsaIdEnhet(enhetId), enhetNamn, lansId, kommunId, verksamhetsTyper);
    }

    private PreparedStatement prepareStatement(Connection connection) throws SQLException {
        String sql = "select vardgivareId, enhetId, namn, lansId, kommunid, verksamhetstyper from enhet";
        PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }

}
