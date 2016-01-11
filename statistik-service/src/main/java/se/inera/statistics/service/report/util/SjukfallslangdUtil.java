/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;

import static se.inera.statistics.service.report.util.Ranges.range;

public final class SjukfallslangdUtil {
    public static final Ranges RANGES = new Ranges(range("Under 15 dagar", 15), range("15-30 dagar", 31), range("31-90 dagar", 91), range("91-180 dagar", 181), range("181-365 dagar", 366), range("Över 365 dagar", Integer.MAX_VALUE));
    private SjukfallslangdUtil() {
    }
}
