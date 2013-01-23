/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).
 *
 * Inera Statistics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Inera Statistics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IcdGroupList {

    private static final int MINIMUM_ICD_LENGTH = 3;

    private static final Logger LOG = LoggerFactory.getLogger(IcdGroupList.class);

    private List<IcdGroup> mapping = new ArrayList<IcdGroup>();
    public final static IcdGroup UNKNOWN_ICD = new IcdGroup("Okänd", null, null, "Okända ICDtexter");

    public IcdGroup getGroup(String icd10) {
        if (icd10 != null && icd10.length() >= MINIMUM_ICD_LENGTH) {
            final String icd10FirstThreeCharacters = icd10.substring(0, MINIMUM_ICD_LENGTH);
            for (IcdGroup group : mapping) {
                if (inRange(icd10FirstThreeCharacters, group)) {
                    return group;
                }
            }
        }

        LOG.warn("Could not find icd code '{0}'", icd10);
        return UNKNOWN_ICD;
    }

    private boolean inRange(final String icd10FirstThreeCharacters, IcdGroup group) {
        return group.getIcd10RangeStart().compareTo(icd10FirstThreeCharacters) <= 0
                && group.getIcd10RangeEnd().compareTo(icd10FirstThreeCharacters) >= 0;
    }

    public void add(IcdGroup icdGroup) {
        mapping.add(icdGroup);
    }
}