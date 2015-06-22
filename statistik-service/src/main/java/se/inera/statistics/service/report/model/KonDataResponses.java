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
package se.inera.statistics.service.report.model;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import se.inera.statistics.hsa.model.HsaId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KonDataResponses {

    private KonDataResponses() {
    }

    public static KonDataResponse changeIdGroupsToNamesAndAddIdsToDuplicates(final KonDataResponse response, Map<? extends HsaId, String> idsToNames) {
        final List<HsaId> idsToCompare = Lists.transform(response.getGroups(), new Function<String, HsaId>() {
            @Override
            public HsaId apply(String id) {
                return new HsaId(id);
            }
        });
        final Map<HsaId, String> hsaIdsToNames = new HashMap<>();
        for (Map.Entry<? extends HsaId, String> entry : idsToNames.entrySet()) {
            hsaIdsToNames.put(new HsaId(entry.getKey().getId()), entry.getValue());
        }
        final HashMap<HsaId, Integer> duplicatesPerId = getDuplicatesPerId(hsaIdsToNames, idsToCompare);

        final ArrayList<String> finalNames = new ArrayList<>();
        for (String id : response.getGroups()) {
            final HsaId key = new HsaId(id);
            final Integer duplicates = duplicatesPerId.get(key);
            final String nameSuffix = duplicates != null && duplicates > 1 ? " " + id : "";
            finalNames.add(hsaIdsToNames.get(key) + nameSuffix);
        }

        return new KonDataResponse(finalNames, response.getRows());
    }

    private static HashMap<HsaId, Integer> getDuplicatesPerId(Map<HsaId, String> idsToNames, List<HsaId> idsToCompare) {
        HashMultimap<CaseInsensiviteString, HsaId> namesToIdsForIdsToCompare = HashMultimap.create();
        for (HsaId id : idsToCompare) {
            final String name = idsToNames.get(id);
            namesToIdsForIdsToCompare.put(new CaseInsensiviteString(name), id);
        }
        final HashMap<HsaId, Integer> duplicatesPerId = new HashMap<>();
        for (Map.Entry<CaseInsensiviteString, Collection<HsaId>> entry : namesToIdsForIdsToCompare.asMap().entrySet()) {
            final Collection<HsaId> ids = entry.getValue();
            for (HsaId id : ids) {
                duplicatesPerId.put(id, ids.size());
            }
        }
        return duplicatesPerId;
    }

}
