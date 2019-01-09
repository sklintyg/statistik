/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.countypopulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
public class CountyPopulationManagerImpl implements CountyPopulationManagerForTest {

    private static final Logger LOG = LoggerFactory.getLogger(CountyPopulationManagerImpl.class);
    public static final String COUNTY_POPULATION_IS_MISSING = "County population is missing!";

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private CountyPopulationFetcher countyPopulationFetcher;

    @Autowired
    private Clock clock;

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public CountyPopulation getCountyPopulation(Range range) {
        final LocalDate date = getPopulationFromDate(range);
        final Optional<CountyPopulation> countyPopulation = getCountyPopulation(date);
        return countyPopulation.isPresent()
                ? countyPopulation.get()
                : new CountyPopulation(Collections.emptyMap(),
                        LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
    }

    LocalDate getPopulationFromDate(Range range) {
        return range.getTo().withDayOfYear(1).minusDays(1);
    }

    private Optional<CountyPopulation> getCountyPopulation(LocalDate date) {
        final Optional<CountyPopulationRow> preFetchedCountyPopulation = getPreFetchedCountyPopulationForDate(date);
        if (preFetchedCountyPopulation.isPresent()) {
            return Optional.of(parsePopulationRow(preFetchedCountyPopulation.get()));
        }
        LOG.info("County population for requested date was not found in local db. Fetching from SCB. Date: " + date);
        Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(date.getYear());
        if (countyPopulation.isPresent()) {
            insertCountyPopulation(countyPopulation.get());
            return countyPopulation;
        }
        LOG.warn("County population for requested date could not be fetched from SCB. Using the last known population.");
        final Optional<CountyPopulationRow> latestPreFetchedCountyPopulation = getLatestPreFetchedCountyPopulation();
        if (latestPreFetchedCountyPopulation.isPresent()) {
            return Optional.of(parsePopulationRow(latestPreFetchedCountyPopulation.get()));
        }
        LOG.error("County population could not be fetched from either SCB or local db. Fix connection or populate db manually");
        return Optional.empty();
    }

    private CountyPopulation parsePopulationRow(CountyPopulationRow populationRow) {
        try {
            final MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, KonField.class);
            final Map<String, KonField> populationData = mapper.readValue(new StringReader(populationRow.getData()), mapType);
            return new CountyPopulation(populationData, populationRow.getDate());
        } catch (NoResultException e) {
            LOG.error(COUNTY_POPULATION_IS_MISSING);
            LOG.debug(COUNTY_POPULATION_IS_MISSING, e);
            return CountyPopulation.empty();
        } catch (IOException e) {
            LOG.error("Could not parse population data!");
            LOG.debug("Could not parse population data!", e);
            return CountyPopulation.empty();
        }
    }

    private Optional<CountyPopulationRow> getPreFetchedCountyPopulationForDate(LocalDate date) {
        return getPreFetchedCountyPopulation(date, "SELECT r FROM CountyPopulationRow r WHERE r.date = :fromDate");
    }

    private Optional<CountyPopulationRow> getLatestPreFetchedCountyPopulation() {
        return getPreFetchedCountyPopulation(LocalDate.now(clock), "SELECT r FROM CountyPopulationRow r ORDER BY r.date DESC");
    }

    private Optional<CountyPopulationRow> getPreFetchedCountyPopulation(LocalDate from, String ql) {
        final Date fromDate = localDateToDate(from);
        final Query query = manager.createQuery(ql);
        if (ql.contains(":fromDate")) {
            query.setParameter("fromDate", fromDate);
        }
        query.setMaxResults(1);
        final List resultList = query.getResultList();
        if (resultList .isEmpty()) {
            LOG.info("County population has not yet been fetched for " + from);
            return Optional.empty();
        }
        return Optional.of((CountyPopulationRow) resultList.get(0));
    }

    private static java.sql.Date localDateToDate(LocalDate ld) {
        if (ld == null) {
            return null;
        }
        return Date.valueOf(ld);
    }

    private boolean insertCountyPopulation(Map<String, KonField> countyPopulation, LocalDate date) {
        try {
            final String populationData = mapper.writeValueAsString(countyPopulation);
            final CountyPopulationRow entity = new CountyPopulationRow(populationData, date);
            manager.persist(entity);
            return true;
        } catch (JsonProcessingException e) {
            LOG.error("Could not insert county population!");
            LOG.debug("Could not insert county population!", e);
            return false;
        }
    }

    private void insertCountyPopulation(CountyPopulation countyPopulation) {
        insertCountyPopulation(countyPopulation.getPopulationPerCountyCode(), countyPopulation.getDate());
    }

    @Override
    public void clearCountyPopulation() {
        manager.createQuery("DELETE FROM CountyPopulationRow").executeUpdate();
    }

}
