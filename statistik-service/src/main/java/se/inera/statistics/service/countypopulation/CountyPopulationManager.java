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
package se.inera.statistics.service.countypopulation;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

@Component
public class CountyPopulationManager {

    private static final Logger LOG = LoggerFactory.getLogger(CountyPopulationManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static ObjectMapper mapper = new ObjectMapper();

    public CountyPopulation getCountyPopulation() {
        final Query query = manager.createQuery("SELECT r FROM CountyPopulationRow r ORDER BY r.date desc");
        query.setMaxResults(1);
        try {
            final CountyPopulationRow populationRow = (CountyPopulationRow) query.getSingleResult();
            final MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Integer.class);
            final Map<String, Integer> populationData = mapper.readValue(new StringReader(populationRow.getData()), mapType);
            return new CountyPopulation(populationData, populationRow.getDate());
        } catch (NoResultException e) {
            LOG.error("County population is missing!");
            return CountyPopulation.empty();
        } catch (IOException e) {
            LOG.error("Could not parse population data!");
            return CountyPopulation.empty();
        }
    }

    /**
     * Currently only used for testing.
     */
    public boolean insertCountyPopulation(Map<String, Integer> countyPopulation, String date) {
        try {
            final String populationData = mapper.writeValueAsString(countyPopulation);
            final CountyPopulationRow entity = new CountyPopulationRow(populationData, LocalDate.parse(date));
            manager.persist(entity);
            return true;
        } catch (JsonProcessingException e) {
            LOG.error("Could not insert county population!");
            return false;
        }
    }

    /**
     * Currently only used for testing.
     */
    public void clearCountyPopulation() {
        manager.createQuery("DELETE FROM CountyPopulationRow").executeUpdate();
    }

}
