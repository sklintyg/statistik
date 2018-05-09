/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.message;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.WidelineLoader;
import se.inera.statistics.service.warehouse.query.MessagesFilter;

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

    @Autowired
    private Icd10 icd10;

    public List<CountDTO> getAntalMeddelandenPerMonth(LocalDate from, LocalDate to) {
        return getAntalMeddelandenPerMonth(from, to, null, null);
    }

    public List<CountDTO> getAntalMeddelandenPerMonth(LocalDate from, LocalDate to, HsaIdVardgivare vardgivare,
            Collection<HsaIdEnhet> enheter) {

        boolean hasVardgivare = vardgivare != null;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = prepareStatement(connection, hasVardgivare, enheter)) {
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

    public List<CountDTOAmne> getAntalMeddelandenPerAmne(MessagesFilter filter) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = prepareStatementAmne(connection, filter)) {

            ResultSet resultSet = stmt.executeQuery();

            List<CountDTOAmne> dtos = new ArrayList<>();

            while (resultSet.next()) {
                dtos.add(toCountAmne(resultSet));
            }

            dtos = applyAgeGroupFilter(filter, dtos);
            dtos = applyDxFilter(filter, dtos);

            return dtos;
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }

        return null;
    }

    public List<CountDTOAmne> getKompletteringarPerIntyg(MessagesFilter filter) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = prepareStatementKompletteringarPerIntyg(connection, filter)) {

            ResultSet resultSet = stmt.executeQuery();

            List<CountDTOAmne> dtos = new ArrayList<>();

            while (resultSet.next()) {
                dtos.add(toCountAmne(resultSet));
            }

            dtos = applyAgeGroupFilter(filter, dtos);
            dtos = applyDxFilter(filter, dtos);

            return dtos;
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }

        return null;
    }

    private List<CountDTOAmne> applyAgeGroupFilter(MessagesFilter filter, List<CountDTOAmne> dtos) {
        final Collection<String> aldersgrupp = filter.getAldersgrupp();
        if (aldersgrupp != null && !aldersgrupp.isEmpty() && new HashSet<>(aldersgrupp).size() != AgeGroup.values().length) {
            final List<AgeGroup> ageGroups = aldersgrupp.stream()
                    .map(AgeGroup::getByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            return dtos.stream().filter(countDTOAmne -> {
                final int patientAge = countDTOAmne.getPatientAge();
                final Optional<AgeGroup> groupForAge = AgeGroup.getGroupForAge(patientAge);
                return ageGroups.contains(groupForAge.orElse(null));
            }).collect(Collectors.toList());
        }
        return dtos;
    }

    private List<CountDTOAmne> applyDxFilter(MessagesFilter filter, List<CountDTOAmne> dtos) {
        final boolean applyDiagnosFilter = filter.getDiagnoser() != null && !filter.getDiagnoser().isEmpty();
        if (applyDiagnosFilter) {
            return dtos.stream().filter(countDTOAmne -> {
                final String dxString = countDTOAmne.getDx();
                final Icd10.Id dx = icd10.findFromIcd10Code(dxString);
                return isDxMatchInCollection(dx, filter.getDiagnoser());
                }).collect(Collectors.toList());
            }
        return dtos;
    }

    private boolean isDxMatchInCollection(Icd10.Id dx, Collection<String> diagnoser) {
        Optional<Icd10.Id> dxlevel = Optional.ofNullable(dx);
        while (dxlevel.isPresent()) {
            final Icd10.Id currentDx = dxlevel.get();
            final boolean contains = diagnoser.contains(String.valueOf(currentDx.toInt()));
            if (contains) {
                return true;
            }
            dxlevel = currentDx.getParent();
        }
        return false;
    }

    private CountDTOAmne toCountAmne(ResultSet resultSet) throws SQLException {
        CountDTOAmne dto = new CountDTOAmne();

        final String signeringsdatumStr = resultSet.getString("signeringsdatum");
        final LocalDate signDate = LocalDate.parse(signeringsdatumStr).withDayOfMonth(1);
        dto.setCount(1);
        dto.setDate(signDate);
        dto.setKon(Kon.byNumberRepresentation(resultSet.getInt("kon")));
        final String amneCode = resultSet.getString("amneCode");
        dto.setAmne(amneCode != null ? MsgAmne.parse(amneCode) : null);
        dto.setEnhet(resultSet.getString("enhet"));
        final String patientid = resultSet.getString("patientid");
        final int alder = ConversionHelper.extractAlderSafe(patientid, signDate);
        dto.setPatientAge(alder);
        dto.setIntygTyp(resultSet.getString("intygstyp"));
        dto.setDx(resultSet.getString("dx"));
        dto.setIntygid(resultSet.getString("intygid"));
        return dto;
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

    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification = "We know what we're doing. No user supplied data.")
    private PreparedStatement prepareStatement(Connection connection, boolean vardgivare, Collection<HsaIdEnhet> enheter)
            throws SQLException {

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

    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification = "We know what we're doing. No user supplied data.")
    private PreparedStatement prepareStatementAmne(Connection connection, MessagesFilter filter)
            throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT "
                + "mwl.intygid, mwl.amnecode, mwl.kon, mwl.amneCode, mwl.enhet AS enhet, mwl.alder, "
                + "mwl.intygstyp, mwl.patientid, ic.signeringsdatum, ic.dx "
                + "FROM messagewideline AS mwl "
                + "INNER JOIN intygcommon AS ic "
                + "ON mwl.intygid = ic.intygid "
                + "WHERE (ic.signeringsdatum between ? AND ?)  ");

        boolean hasVardgivare = filter.getVardgivarId() != null;
        if (hasVardgivare) {
            sql.append(" AND mwl.vardgivareid = ? ");
        }

        final Collection<HsaIdEnhet> enheter = filter.getEnheter();
        if (enheter != null && !enheter.isEmpty()) {
            String enhetSql = enheter.stream().map(HsaIdEnhet::getId).collect(Collectors.joining("' , '"));
            sql.append(" AND mwl.enhet IN ('").append(enhetSql).append("')");
        }

        final Collection<String> intygstyper = filter.getIntygstyper();
        if (intygstyper != null && !intygstyper.isEmpty()) {
            List<IntygType> intygTypeFilter =  filter.getIntygstyper().stream()
                    .map(IntygType::getByName).filter(Optional::isPresent).map(Optional::get)
                    .flatMap(intygType -> intygType.getUnmappedTypes().stream())
                    .collect(Collectors.toList());
            String intygstyperSql = intygTypeFilter.stream().map(Enum::name).collect(Collectors.joining("' , '"));
            sql.append(" AND `intygstyp` IN ('").append(intygstyperSql).append("')");
        }

        LOG.debug("sql: {}", sql);

        PreparedStatement stmt = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);

        stmt.setDate(COLUMN_FROM, java.sql.Date.valueOf(filter.getFrom()));
        stmt.setDate(COLUMN_TO, java.sql.Date.valueOf(filter.getTo()));

        if (hasVardgivare) {
            stmt.setString(COLUMN_VARDGIVARE, filter.getVardgivarId().getId());
        }

        return stmt;
    }

    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification = "We know what we're doing. No user supplied data.")
    private PreparedStatement prepareStatementKompletteringarPerIntyg(Connection connection, MessagesFilter filter)
            throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT "
                + "ic.intygid, ic.kon, ic.intygtyp as intygstyp, ic.patientid, ic.dx, ic.enhet, ic.signeringsdatum, "
                + "mwl.amneCode "
                + "FROM intygcommon ic "
                + "LEFT JOIN messagewideline mwl "
                + "ON ic.intygid = mwl.intygid "
                + "WHERE (ic.signeringsdatum between ? AND ?) AND ic.sentToFk ");

        boolean hasVardgivare = filter.getVardgivarId() != null;
        if (hasVardgivare) {
            sql.append(" AND ic.vardgivareid = ? ");
        }

        final Collection<HsaIdEnhet> enheter = filter.getEnheter();
        if (enheter != null && !enheter.isEmpty()) {
            String enhetSql = enheter.stream().map(HsaIdEnhet::getId).collect(Collectors.joining("' , '"));
            sql.append(" AND ic.enhet IN ('").append(enhetSql).append("')");
        }

        final Collection<String> intygstyper = filter.getIntygstyper();
        if (intygstyper != null && !intygstyper.isEmpty()) {
            List<IntygType> intygTypeFilter =  filter.getIntygstyper().stream()
                    .map(IntygType::getByName).filter(Optional::isPresent).map(Optional::get)
                    .flatMap(intygType -> intygType.getUnmappedTypes().stream())
                    .collect(Collectors.toList());
            String intygstyperSql = intygTypeFilter.stream().map(Enum::name).collect(Collectors.joining("' , '"));
            sql.append(" AND ic.intygtyp IN ('").append(intygstyperSql).append("')");
        }

        final Collection<String> amne = filter.getAmne();
        if (amne != null && !amne.isEmpty()) {
            String amneSql = amne.stream().collect(Collectors.joining("' , '"));
            sql.append(" AND (amneCode IN ('").append(amneSql).append("') OR amneCode IS NULL) ");
        }

        sql.append(" GROUP BY ic.intygid, mwl.amneCode, ic.kon, ic.intygtyp, ic.patientid, ic.dx, ic.enhet, ic.signeringsdatum");

        LOG.debug("sql: {}", sql);

        PreparedStatement stmt = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);

        stmt.setDate(COLUMN_FROM, java.sql.Date.valueOf(filter.getFrom()));
        stmt.setDate(COLUMN_TO, java.sql.Date.valueOf(filter.getTo()));

        if (hasVardgivare) {
            stmt.setString(COLUMN_VARDGIVARE, filter.getVardgivarId().getId());
        }

        return stmt;
    }

    public static class CountDTO {
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
