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

package se.inera.statistics.service.warehouse;

public class Convert {
    public static final int ICD10_CODE_MAX_LEN = 3;

    public static int toInt(String diagnos) {
        String normalized = normalizeCode(diagnos);
        return (normalized.charAt(0) - 'A') * 100 + (normalized.charAt(1) - '0') * 10 + (normalized.charAt(2) - '0');
    }

    private static String normalizeCode(String icd10Code) {
        String normalizedCode = icd10Code.toUpperCase();
        if (icd10Code.length() > ICD10_CODE_MAX_LEN) {
            normalizedCode = normalizedCode.substring(0, ICD10_CODE_MAX_LEN);
        }
        return normalizedCode;
    }

}
