/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

import com.google.common.collect.HashMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.inera.statistics.integration.hsa.model.HsaIdAny;

public final class KonDataResponses {

    private KonDataResponses() {
    }

    public static KonDataResponse changeIdGroupsToNamesAndAddIdsToDuplicates(final KonDataResponse response,
        Map<? extends HsaIdAny, String> idsToNames) {
        final List<HsaIdAny> idsToCompare = response.getGroups().stream()
            .map(HsaIdAny::createEvenThoughSubclassesArePreferred)
            .collect(Collectors.toList());
        final Map<HsaIdAny, String> hsaIdsToNames = new HashMap<>();
        for (Map.Entry<? extends HsaIdAny, String> entry : idsToNames.entrySet()) {
            hsaIdsToNames.put(HsaIdAny.createEvenThoughSubclassesArePreferred(entry.getKey().getId()), entry.getValue());
        }
        final HashMap<HsaIdAny, Integer> duplicatesPerId = getDuplicatesPerId(hsaIdsToNames, idsToCompare);

        final ArrayList<String> finalNames = new ArrayList<>();
        for (String id : response.getGroups()) {
            final HsaIdAny key = HsaIdAny.createEvenThoughSubclassesArePreferred(id);
            final Integer duplicates = duplicatesPerId.get(key);
            final String nameSuffix = duplicates != null && duplicates > 1 ? " " + id : "";
            finalNames.add(hsaIdsToNames.get(key) + nameSuffix);
        }

        return new KonDataResponse(response.getAvailableFilters(), finalNames, response.getRows());
    }

    private static HashMap<HsaIdAny, Integer> getDuplicatesPerId(Map<HsaIdAny, String> idsToNames, List<HsaIdAny> idsToCompare) {
        HashMultimap<CaseInsensiviteString, HsaIdAny> namesToIdsForIdsToCompare = HashMultimap.create();
        for (HsaIdAny id : idsToCompare) {
            final String name = idsToNames.get(id);
            namesToIdsForIdsToCompare.put(new CaseInsensiviteString(name), id);
        }
        final HashMap<HsaIdAny, Integer> duplicatesPerId = new HashMap<>();
        for (Map.Entry<CaseInsensiviteString, Collection<HsaIdAny>> entry : namesToIdsForIdsToCompare.asMap().entrySet()) {
            final Collection<HsaIdAny> ids = entry.getValue();
            for (HsaIdAny id : ids) {
                duplicatesPerId.put(id, ids.size());
            }
        }
        return duplicatesPerId;
    }

}
