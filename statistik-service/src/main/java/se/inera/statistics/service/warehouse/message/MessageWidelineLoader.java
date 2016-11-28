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
package se.inera.statistics.service.warehouse.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.WidelineLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageWidelineLoader {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineLoader.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final int COLUMN_FROM = 1;
    private static final int COLUMN_TO = 2;
    private static final int COLUMN_VARDGIVARE = 3;

    private static final int FETCH_SIZE = 10000;
    @Autowired
    private DataSource dataSource;

    public List<CountDTO> getAntalMeddelandenPerMonth(LocalDate from, LocalDate to) {
        return getAntalMeddelandenPerMonth(from, to, null, null);
    }

    public List<CountDTO> getAntalMeddelandenPerMonth(LocalDate from, LocalDate to, HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter) {

        boolean hasVardgivare = vardgivare != null;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatement(connection, hasVardgivare, enheter)
        ) {
            stmt.setString(COLUMN_FROM, from.format(FORMATTER));
            stmt.setString(COLUMN_TO, to.format(FORMATTER));

            if (hasVardgivare) {
                stmt.setString(COLUMN_VARDGIVARE, vardgivare.getId());
            }

            ResultSet resultSet = stmt.executeQuery();

            List<CountDTO> dtos = new ArrayList<>();

            while (resultSet.next()) {
                dtos.add(toCount(resultSet));
            }

            return dtos;
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }

        return null;
    }

    private CountDTO toCount(ResultSet resultSet) throws SQLException {
        CountDTO dto = new CountDTO();

        int year = resultSet.getInt("year");
        int month = resultSet.getInt("month");

        dto.setCount(resultSet.getInt("count"));
        dto.setDate(LocalDate.of(year, month, 1));

        dto.setKon(Kon.byNumberRepresentation(resultSet.getInt("kon")));

        return dto;
    }

    private PreparedStatement prepareStatement(Connection connection, boolean vardgivare, Collection<HsaIdEnhet> enheter) throws SQLException {

        String sql = "select count(*) as count, YEAR(skickatDate) as `year`, MONTH(skickatDate) as `month`, kon "
                + " FROM messagewideline "
                + " WHERE (skickatDate between ? AND ?)  ";

        if (vardgivare) {
            sql += " AND vardgivareid = ? ";
        }

        if (enheter != null && !enheter.isEmpty()) {
            String enhetSql = enheter.stream().map(HsaIdEnhet::getId).collect(Collectors.joining("' , '"));

            sql += " AND `enhet` IN ('" + enhetSql + "')";
        }

        sql += " GROUP BY `year`, `month`, kon";

        LOG.debug("sql: {}", sql);

        PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }

    public class CountDTO {
        private int count;
        private LocalDate date;
        private Kon kon;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Kon getKon() {
            return kon;
        }

        public void setKon(Kon kon) {
            this.kon = kon;
        }
    }
}
