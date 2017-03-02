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
package se.inera.statistics.service.liquibase;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class DbChecker {
    private static final Logger LOG = LoggerFactory.getLogger(DbChecker.class);

    @java.lang.SuppressWarnings("squid:S1118") // Suppress Sonar warning for "Utility classes should not have public
                                               // constructors" since this contructor is actually invoked by Spring
    public DbChecker(DataSource dataSource, String script) {
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            Liquibase liquibase = new Liquibase(script, new ClassLoaderResourceAccessor(), database);
            LOG.info(database.getConnection().getURL());
            List<ChangeSet> changeSets = liquibase.listUnrunChangeSets("none");
            if (!changeSets.isEmpty()) {
                StringBuilder errors = new StringBuilder();
                for (ChangeSet changeSet : changeSets) {
                    errors.append('>').append(changeSet.toString()).append('\n');
                }
                throw new DbCheckError("Database version mismatch. Check liquibase status. Errors:\n" + errors.toString()
                        + database.getDatabaseProductName() + ", " + database);
            }
        } catch (liquibase.exception.LiquibaseException | SQLException e) {
            throw new DbCheckError("Database not ok, aborting startup.", e);
        }
        LOG.info("Liquibase ok");
    }

    @java.lang.SuppressWarnings("squid:S1194") // I assume there is a reason for throwing and Error and do not dare to
                                               // change it
    private static class DbCheckError extends Error {

        DbCheckError(String s) {
            super(s);
        }

        DbCheckError(String s, Exception e) {
            super(s, e);
        }

    }

}
