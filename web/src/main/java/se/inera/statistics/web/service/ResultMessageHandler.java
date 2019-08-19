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
package se.inera.statistics.web.service;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.util.Icd10;

public class ResultMessageHandler {

    @Autowired
    private Icd10 icd10;

    public boolean isDxFilterDisableAllSelectedDxs(List<String> selectedDxs, Collection<String> filterDiagnoser) {
        if (filterDiagnoser == null || filterDiagnoser.isEmpty()) {
            return false;
        }
        for (String dx : selectedDxs) {
            List<String> leaves = getAllLeavesForDx(dx);
            for (String leaf : leaves) {
                List<String> hirarcicalDx = getDxPlusHigherLevelIcd10(leaf);
                for (String dxIntCode : hirarcicalDx) {
                    if (filterDiagnoser.contains(dxIntCode)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private List<String> getAllLeavesForDx(String dx) {
        final Icd10.Id icd = icd10.findIcd10FromNumericId(Integer.valueOf(dx));
        if (icd.getSubItems().isEmpty()) {
            return Arrays.asList(String.valueOf(icd.toInt()));
        }
        List<String> icd10Ints = new ArrayList<>();
        for (Icd10.Id id : icd.getSubItems()) {
            icd10Ints.addAll(getAllLeavesForDx(String.valueOf(id.toInt())));
        }
        return icd10Ints;
    }

    private List<String> getDxPlusHigherLevelIcd10(String dx) {
        List<String> icd10Ints = new ArrayList<>();
        icd10Ints.add(dx);
        final Icd10.Id icd = icd10.findIcd10FromNumericId(Integer.valueOf(dx));
        icd10Ints.addAll(getAllParents(icd));
        return icd10Ints;
    }

    private Collection<String> getAllParents(Icd10.Id icd) {
        final Optional<? extends Icd10.Id> parent = icd.getParent();
        if (!parent.isPresent()) {
            return Collections.emptyList();
        }
        final ArrayList<String> icd10Ints = new ArrayList<>();
        final Icd10.Id id = parent.get();
        icd10Ints.add(String.valueOf(id.toInt()));
        icd10Ints.addAll(getAllParents(id));
        return ImmutableList.copyOf(icd10Ints);
    }

}
