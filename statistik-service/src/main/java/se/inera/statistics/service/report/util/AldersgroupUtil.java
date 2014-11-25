/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.util;

import static se.inera.statistics.service.report.util.Ranges.range;

public final class AldersgroupUtil {
    public static final Ranges RANGES = new Ranges(range("Under 21 år", 21), range("21-25 år", 26), range("26-30 år", 31), range("31-35 år", 36), range("36-40 år", 41), range("41-45 år", 46), range("46-50 år", 51), range("51-55 år", 56), range("56-60 år", 61), range("Över 60 år", Integer.MAX_VALUE));

    private AldersgroupUtil() {
    }
}
