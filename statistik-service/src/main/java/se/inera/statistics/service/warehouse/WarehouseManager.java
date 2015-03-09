/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class WarehouseManager {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseManager.class);

    @Autowired
    private WidelineLoader loader;

    @Autowired
    private EnhetLoader enhetLoader;

    @Autowired
    private Warehouse warehouse;

    @PostConstruct
    public int loadEnhetAndWideLines() {
        LOG.info("Reloading warehouse");
        int lines = loader.populateWarehouse();
        LOG.info("Reloaded warehouse {} lines", lines);
        warehouse.complete(LocalDateTime.now());
        LOG.info("Prepared warehouse with ailes {}", warehouse.getAllVardgivare().keySet());
        LOG.info("Reloading enhet");
        lines = enhetLoader.populateWarehouse();
        warehouse.completeEnhets(LocalDateTime.now());
        LOG.info("Reloaded enhet {} lines", lines);
        SjukfallUtil.clearSjukfallGroupCache();
        return lines;
    }

    public int countAisles() {
        return warehouse.getAllVardgivare().size();
    }

    public int getAisleSize(String vardgivareId) {
        Aisle aisle = warehouse.get(vardgivareId);
        return aisle.getSize();
    }
}
