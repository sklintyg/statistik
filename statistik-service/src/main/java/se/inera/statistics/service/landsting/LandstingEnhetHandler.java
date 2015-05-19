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
package se.inera.statistics.service.landsting;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.landsting.persistance.landsting.Landsting;
import se.inera.statistics.service.landsting.persistance.landsting.LandstingManager;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhetManager;

@Component
public class LandstingEnhetHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LandstingEnhetHandler.class);

    @Autowired
    private LandstingManager landstingManager;

    @Autowired
    private LandstingEnhetManager landstingEnhetManager;

    public void update(LandstingEnhetFileData data) throws NoLandstingSetForVgException {
        final Optional<Landsting> landstingOptional = landstingManager.getForVg(data.getVgId());
        if (!landstingOptional.isPresent()) {
            LOG.warn("There is no landsting connected to vg: " + data.getVgId());
            throw new NoLandstingSetForVgException();
        }
        landstingEnhetManager.update(landstingOptional.get().getId(), data.getRows());
    }

}
