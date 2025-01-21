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
package se.inera.statistics.service.warehouse.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegionCutoff {

    private static final Logger LOG = LoggerFactory.getLogger(RegionCutoff.class);

    private static final int DEFAULT_CUTOFF = 5;
    private int cutoff = DEFAULT_CUTOFF;

    @Autowired
    public void initProperty(@Value("${reports.landsting.cutoff}") int cutoff) {
        final int minimumCutoffValue = 3;
        if (cutoff < minimumCutoffValue) {
            LOG.warn(String.format("Region cutoff value is too low. Using minimum value: %d", minimumCutoffValue));
            this.cutoff = minimumCutoffValue;
            return;
        }
        this.cutoff = cutoff;
    }

    public int getCutoff() {
        return cutoff;
    }

    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

}
