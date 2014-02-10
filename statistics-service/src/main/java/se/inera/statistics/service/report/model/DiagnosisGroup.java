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

package se.inera.statistics.service.report.model;

public class DiagnosisGroup implements Comparable<DiagnosisGroup> {

    public static final int ICD10_CODE_MAX_LEN = 3;
    private final String id;
    private final String name;
    private final String firstId;
    private final String lastId;

    public DiagnosisGroup(String id, String name) {
        this.id = id;
        this.name = name;
        String[] split = id.split("-");
        firstId = split[0];
        lastId = split[1];
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String asString() {
        return id + " " + name;
    }

    @Override
    public String toString() {
        return "{\"DiagnosisGroup\":{" + "\"id\":\"" + id + '"' + ", \"name\":\"" + name + '"' + ", \"firstId\":\"" + firstId + '"' + ", \"lastId\":\"" + lastId + '"' + "}}";
    }

    public boolean isCodeInGroup(String icd10Code) {
        String normalizedCode = normalizeCode(icd10Code);
        return firstId.compareTo(normalizedCode) <= 0 && lastId.compareTo(normalizedCode) >= 0;
    }

    private String normalizeCode(String icd10Code) {
        String normalizedCode = icd10Code.toUpperCase();
        if (icd10Code.length() > ICD10_CODE_MAX_LEN) {
            normalizedCode = normalizedCode.substring(0, ICD10_CODE_MAX_LEN);
        }
        return normalizedCode;
    }

    @Override
    public int compareTo(DiagnosisGroup o) {
        return id.compareTo(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiagnosisGroup) {
            return isEqual((DiagnosisGroup) obj);
        }
        return false;
    }

    private boolean isEqual(DiagnosisGroup other) {
        return id.equals(other.id);
    }

}
