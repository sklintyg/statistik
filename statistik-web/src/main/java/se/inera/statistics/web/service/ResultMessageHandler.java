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
package se.inera.statistics.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.util.Icd10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ResultMessageHandler {

    @Autowired
    private Icd10 icd10;

    boolean isDxFilterDisableAllSelectedDxs(List<String> selectedDxs, Collection<String> filterDiagnoser) {
        if (filterDiagnoser == null) {
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
        if (icd instanceof Icd10.Kategori) {
            final Icd10.Avsnitt avsnitt = ((Icd10.Kategori) icd).getAvsnitt();
            icd10Ints.add(String.valueOf(avsnitt.toInt()));
            icd10Ints.add(String.valueOf(avsnitt.getKapitel().toInt()));
        } else if (icd instanceof Icd10.Avsnitt) {
            icd10Ints.add(String.valueOf(((Icd10.Avsnitt) icd).getKapitel().toInt()));
        }
        return icd10Ints;
    }

}
