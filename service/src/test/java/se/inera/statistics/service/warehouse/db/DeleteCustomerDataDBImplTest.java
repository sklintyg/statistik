/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCustomerDataDBImplTest {

    private static final String ENHETS_ID = "Något enhetsid";
    private static final String INTYGSID = "Något intygsid";
    private static final String VARDGIVARE_ID = "Något vardgivare Id";

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @InjectMocks
    private DeleteCustomerDataDBImpl deleteCustomerDataDB;

    @Before
    public void setUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    public void testDeleteFromEnhet() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromEnhet(ENHETS_ID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM enhet WHERE vardgivareId = ?");
        verify(preparedStatement, times(1)).setString(1, ENHETS_ID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromEnhetException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromEnhet(ENHETS_ID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM enhet WHERE vardgivareId = ?");
        verify(preparedStatement, times(1)).setString(1, ENHETS_ID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromHsa() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromHsa(INTYGSID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM hsa WHERE id = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromHsaException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromHsa(INTYGSID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM hsa WHERE id = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromIntygcommon() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromIntygcommon(INTYGSID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM intygcommon WHERE intygid = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromIntygcommonException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromIntygcommon(INTYGSID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM intygcommon WHERE intygid = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromIntyghandelse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromIntyghandelse(INTYGSID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM intyghandelse WHERE correlationId = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromIntyghandelseException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromIntyghandelse(INTYGSID).intValue();
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM intyghandelse WHERE correlationId = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromLakare() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromLakare(VARDGIVARE_ID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM lakare WHERE vardgivareid = ?");
        verify(preparedStatement, times(1)).setString(1, VARDGIVARE_ID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromLakareException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromLakare(VARDGIVARE_ID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM lakare WHERE vardgivareid = ?");
        verify(preparedStatement, times(1)).setString(1, VARDGIVARE_ID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromMeddelandehandelseAndMessagewideline() throws SQLException {
        Integer deleteFromMeddelandehandelseResult = 1;
        Integer deleteFromMessagewidelineResult = 2;
        when(preparedStatement.executeUpdate()).thenReturn(deleteFromMeddelandehandelseResult, deleteFromMessagewidelineResult);

        MeddelandehandelseMessagewidelineResult result = deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(INTYGSID);
        assertEquals(result.getMeddelandehandelseResult(), deleteFromMeddelandehandelseResult);
        assertEquals(result.getMessagewidelineResult(), deleteFromMessagewidelineResult);

        verify(connection, times(2)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement(
            "DELETE FROM meddelandehandelse WHERE correlationId IN (SELECT meddelandeId FROM messagewideline WHERE intygid = ?)");
        verify(connection, times(1)).prepareStatement("DELETE FROM messagewideline WHERE intygid = ?");
        verify(preparedStatement, times(2)).setString(1, INTYGSID);
        verify(preparedStatement, times(2)).executeUpdate();
        verify(preparedStatement, times(2)).close();
    }

    @Test
    public void testDeleteFromMeddelandehandelseAndMessagewidelineMeddelandehandelseException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(INTYGSID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement(
            "DELETE FROM meddelandehandelse WHERE correlationId IN (SELECT meddelandeId FROM messagewideline WHERE intygid = ?)");
        verify(connection, never()).prepareStatement("DELETE FROM messagewideline WHERE intygid = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromMeddelandehandelseAndMessagewidelineMessagewidelineException() throws SQLException {
        Integer deleteFromMeddelandehandelseResult = 1;
        when(preparedStatement.executeUpdate()).thenReturn(deleteFromMeddelandehandelseResult).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(INTYGSID);
        });

        verify(connection, times(2)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement(
            "DELETE FROM meddelandehandelse WHERE correlationId IN (SELECT meddelandeId FROM messagewideline WHERE intygid = ?)");
        verify(connection, times(1)).prepareStatement("DELETE FROM messagewideline WHERE intygid = ?");
        verify(preparedStatement, times(2)).setString(1, INTYGSID);
        verify(preparedStatement, times(2)).executeUpdate();
        verify(preparedStatement, times(2)).close();
    }

    @Test
    public void testDeleteFromWideline() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromWideline(INTYGSID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM wideline WHERE correlationId = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromWidelineException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromWideline(INTYGSID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM wideline WHERE correlationId = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromIntygsenthandelse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertEquals(deleteCustomerDataDB.deleteFromIntygsenthandelse(INTYGSID).intValue(), 1);

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM intygsenthandelse WHERE correlationId = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }

    @Test
    public void testDeleteFromIntygsenthandelseException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> {
            deleteCustomerDataDB.deleteFromIntygsenthandelse(INTYGSID);
        });

        verify(connection, times(1)).setAutoCommit(true);
        verify(connection, times(1)).prepareStatement("DELETE FROM intygsenthandelse WHERE correlationId = ?");
        verify(preparedStatement, times(1)).setString(1, INTYGSID);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
    }
}