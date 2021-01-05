/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.io.Serializable;

public final class AvailableFilters implements Serializable {

    private boolean diagnos;
    private boolean enhets;
    private boolean sjukskrivningslangds;
    private boolean ageGroups;
    private boolean intygTypes;

    private AvailableFilters(boolean diagnos, boolean enhets, boolean ageGroups, boolean sjukskrivningslangds, boolean intygTypes) {
        this.diagnos = diagnos;
        this.enhets = enhets;
        this.sjukskrivningslangds = sjukskrivningslangds;
        this.ageGroups = ageGroups;
        this.intygTypes = intygTypes;
    }

    public boolean isDiagnos() {
        return diagnos;
    }

    public boolean isEnhets() {
        return enhets;
    }

    public boolean isSjukskrivningslangds() {
        return sjukskrivningslangds;
    }

    public boolean isAgeGroups() {
        return ageGroups;
    }

    public boolean isIntygTypes() {
        return intygTypes;
    }


    public static AvailableFilters getForIntyg() {
        return new AvailableFilters(true, true, true, false, false);
    }

    public static AvailableFilters getForSjukfall() {
        return new AvailableFilters(true, true, true, true, false);
    }

    public static AvailableFilters getForMeddelanden() {
        return new AvailableFilters(true, true, true, false, true);
    }

    public static AvailableFilters getForNationell() {
        return new AvailableFilters(false, false, false, false, false);
    }
}
