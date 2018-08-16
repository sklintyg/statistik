/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

public class ActiveFilters {

    private boolean diagnos;
    private boolean enhets;
    private boolean sjukskrivningslangds;
    private boolean ageGroups;
    private boolean intygTypes;

    public ActiveFilters(boolean diagnos, boolean enhets, boolean ageGroups, boolean sjukskrivningslangds, boolean intygTypes) {
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


    public static ActiveFilters getForIntyg() {
        return new ActiveFilters(true, true, true, false, false);
    }

    public static ActiveFilters getForSjukfall() {
        return new ActiveFilters(true, true, true, true, false);
    }

    public static ActiveFilters getForMeddelanden() {
        return new ActiveFilters(true, true, true, false, true);
    }
}
