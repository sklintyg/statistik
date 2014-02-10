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

package se.inera.statistics.service.sjukfall;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class SjukfallInfo {

    private String id;
    private LocalDate start;
    private LocalDate end;
    private LocalDate prevEnd;

    public SjukfallInfo(String id, LocalDate start, LocalDate end, LocalDate prevEnd) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.prevEnd = prevEnd;
    }

    public String getId() {
        return id;
    }

    public LocalDate getPrevEnd() {
        return prevEnd;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SjukfallInfo) {
            return id.equals(((SjukfallInfo) obj).getId());
        } else {
            return false;
        }
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public int getLangd() {
        return Days.daysBetween(start, end).getDays() + 1;
    }

    @Override
    public String toString() {
        return "SjukfallInfo{" + "id='" + id + '\'' + ", start=" + start + ", end=" + end + ", prevEnd=" + prevEnd + '}';
    }
}
