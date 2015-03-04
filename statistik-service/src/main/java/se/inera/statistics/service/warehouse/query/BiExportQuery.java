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
package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.BiExportData;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.List;

@Component
public final class BiExportQuery {

    @Autowired
    private LakareManager lakareManager;

    @Autowired
    private Warehouse warehouse;

    public List<BiExportData> getBiExportData(String vardgivarId, Aisle aisle, Predicate<Fact> filter) {
        final Iterable<Fact> filteredAisle = Iterables.filter(aisle, filter);
        final List<Enhet> enhets = warehouse.getEnhets(vardgivarId);
        final List<Lakare> allLakares = lakareManager.getAllLakares();

        List<BiExportData> exportData = new ArrayList<>();
        for (Fact fact : filteredAisle) {
            final Enhet enhet = getEnhet(enhets, fact.getEnhet());
            final Lakare lakare = getLakare(allLakares, fact.getLakarid());
            exportData.add(new BiExportData(fact, enhet, lakare));
        }
        return exportData;
    }

    private Enhet getEnhet(List<Enhet> enhets, final int enhetId) {
        final Optional<String> enhetId1 = Warehouse.getEnhetId(enhetId);
        if (!enhetId1.isPresent()) {
            return new Enhet();
        }
        return Iterables.find(enhets, new Predicate<Enhet>() {
            @Override
            public boolean apply(Enhet enhet) {
                return enhetId1.get().equals(enhet.getEnhetId());
            }
        }, new Enhet());
    }

    private Lakare getLakare(List<Lakare> lakares, final Integer lakarId) {
        return Iterables.find(lakares, new Predicate<Lakare>() {
            @Override
            public boolean apply(Lakare lakare) {
                return lakarId == Warehouse.getNumLakarId(lakare.getLakareId());
            }
        }, null);
    }

}
