/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;

@Component
public class WidelineLoader {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineLoader.class);

    private static final int FETCH_SIZE = 10000;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private FactConverter factConverter;

    public List<Aisle> getAilesForVgs(List<HsaIdVardgivare> vgids) {
        LOG.info("Get facts for vgs: " + String.join(", ", vgids.stream().map(HsaIdAny::getId).collect(Collectors.toList())));
        final Map<HsaIdVardgivare, List<Fact>> facts = new HashMap<>(vgids.size());
        for (HsaIdVardgivare vgid : vgids) {
            facts.put(vgid, new ArrayList<>());
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = prepareStatementForVg(connection, vgids);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String vardgivare = resultSet.getString("vardgivareid");
                final Fact fact = toFact(resultSet);
                facts.get(new HsaIdVardgivare(vardgivare)).add(fact);
            }
        } catch (SQLException e) {
            LOG.error("Could not populate warehouse", e);
        }
        return toAisles(facts);
    }

    private Fact toFact(ResultSet resultSet) throws SQLException {
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
        String lakareId = resultSet.getString("lakareid");
        String vardenhet = resultSet.getString("vardenhet");

        return new Fact(id, ConversionHelper.extractLan(lkf), ConversionHelper.extractKommun(lkf),
            ConversionHelper.extractForsamling(lkf), new HsaIdEnhet(vardenhet), new HsaIdEnhet(enhet), intyg,
            ConversionHelper.patientIdToInt(patientid), startdatum, slutdatum, kon, alder,
            factConverter.extractKapitel(diagnoskapitel), factConverter.extractAvsnitt(diagnosavsnitt),
            factConverter.extractKategori(diagnoskategori), factConverter.extractKod(diagnoskod, diagnoskategori),
            sjukskrivningsgrad, lakarkon, lakaralder, factConverter.parseBefattning(lakarbefattning, correlationId),
            new HsaIdLakare(lakareId));
    }

    private List<Aisle> toAisles(Map<HsaIdVardgivare, List<Fact>> facts) {
        List<Aisle> aisles = new ArrayList<>();
        for (Map.Entry<HsaIdVardgivare, List<Fact>> entry : facts.entrySet()) {
            final Aisle aisle = new Aisle(entry.getKey(), entry.getValue());
            aisles.add(aisle);
        }
        return aisles;
    }

    @SuppressFBWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
        justification = "We know what we're doing. No user supplied data.")
    private PreparedStatement prepareStatementForVg(Connection connection, List<HsaIdVardgivare> vgids) throws SQLException {
        final String vgidsJoined = String.join("', '", vgids.stream().map(HsaIdAny::getId).collect(Collectors.toList()));
        String sql = "SELECT id, correlationid, lkf, enhet, vardenhet, lakarintyg, patientid, startdatum, slutdatum, kon, alder,"
                + " diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon, lakaralder, "
                + "lakarbefattning, vardgivareid, lakareid, active FROM wideline WHERE active AND vardgivareid IN ('" + vgidsJoined + "')";

        int maxIntyg = Integer.parseInt(System.getProperty("statistics.test.max.fact", "0"));
        if (maxIntyg > 0) {
            sql += " limit " + maxIntyg;
            LOG.error("Only reading first " + maxIntyg + " intyg.");
        }
        PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(FETCH_SIZE);
        return stmt;
    }

    List<VgNumber> getAllVgs() {
        LOG.info("Get all vgs from db");
        final ArrayList<VgNumber> vgs = new ArrayList<>();

        final String sql = "SELECT vardgivareid, count(vardgivareid) AS antal FROM intygcommon w1 "
            + "GROUP BY vardgivareid ORDER BY antal ASC;";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final String vardgivareid = resultSet.getString("vardgivareid");
                final int antal = resultSet.getInt("antal");
                final HsaIdVardgivare vgId = new HsaIdVardgivare(vardgivareid);
                vgs.add(new VgNumber(vgId, antal));
            }
        } catch (SQLException e) {
            LOG.error("Could not get all vgs from DB", e);
        }
        return vgs;
    }

}
