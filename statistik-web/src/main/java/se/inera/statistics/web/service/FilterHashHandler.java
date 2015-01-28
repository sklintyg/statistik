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

import com.google.common.base.Optional;
import org.apache.commons.codec.digest.DigestUtils;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.userselection.UserSelection;
import se.inera.statistics.service.userselection.UserSelectionManager;

public class FilterHashHandler {

    @Autowired
    private UserSelectionManager userSelectionManager;

    synchronized String getHash(String filterData) {
        try {
            final String hash = DigestUtils.md5Hex(filterData);
            final UserSelection userSelection = userSelectionManager.find(hash);
            if (userSelection == null) {
                userSelectionManager.persist(hash, filterData);
            }
            return hash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Optional<String> getFilterData(String hash) {
        final UserSelection userSelection = userSelectionManager.find(hash);
        if (userSelection == null) {
            return Optional.absent();
        }
        return Optional.of(userSelection.getValue());
    }

}
