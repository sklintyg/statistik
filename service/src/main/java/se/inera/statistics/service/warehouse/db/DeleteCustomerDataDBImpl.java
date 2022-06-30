/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteCustomerDataDBImpl implements DeleteCustomerDataDB {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteCustomerDataDBImpl.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public Integer deleteFromEnhet(String enhetId) {
        String sql = "DELETE FROM enhet WHERE enhetId = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, enhetId);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from enhet where enhetId = {}", numberOfRowsDeleted, enhetId);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteFromHsa(String intygsid) {
        String sql = "DELETE FROM hsa WHERE id = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from hsa where intygsid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteFromIntygcommon(String intygsid) {
        String sql = "DELETE FROM intygcommon WHERE intygid = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from iwntygcommon where intygsid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteFromIntyghandelse(String intygsid) {
        String sql = "DELETE FROM intyghandelse WHERE correlationId = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from intyghandelse where intygsid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteFromLakare(String vardgivareId) {
        String sql = "DELETE FROM lakare WHERE vardgivareid = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vardgivareId);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from lakare where vardgivareId = {}", numberOfRowsDeleted, vardgivareId);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * Always execute before deleteFromMessagewideline
     */
    @Override
    public MeddelandehandelseMessagewidelineResultDao deleteFromMeddelandehandelseAndMessagewideline(String intygsid) {
        return new MeddelandehandelseMessagewidelineResultDao(deleteFromMeddelandehandelse(intygsid), deleteFromMessagewideline(intygsid));
    }

    private Integer deleteFromMeddelandehandelse(String intygsid) {
        String sql = "DELETE FROM meddelandehandelse WHERE correlationId IN (SELECT meddelandeId FROM messagewideline WHERE intygid = ?)";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from meddelandehandelse where intygid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * Newer execute before deleteFromMeddelandehandelse
     * @param intygsid
     */
    private Integer deleteFromMessagewideline(String intygsid) {
        String sql = "DELETE FROM messagewideline WHERE intygid = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from messagewideline where intygid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteFromWideline(String intygsid) {
        String sql = "DELETE FROM wideline WHERE correlationId = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from wideline where intygsid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteFromIntygsenthandelse(String intygsid) {
        String sql = "DELETE FROM intygsenthandelse WHERE correlationId = ?";
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, intygsid);
            Integer numberOfRowsDeleted = preparedStatement.executeUpdate();
            LOG.info("Deleted {} rows from intygsenthandelse where intygsid = {}", numberOfRowsDeleted, intygsid);
            return numberOfRowsDeleted;
        } catch (SQLException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
