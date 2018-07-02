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
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Sjukskrivningsperiod {

    private int start;
    private int length;

    Sjukskrivningsperiod(int start, int length) {
        this.start = start;
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public int getEnd() {
        return start + length;
    }

    static int getLengthOfJoinedPeriods(Collection<Sjukskrivningsperiod> periods) {
        List<Sjukskrivningsperiod> mergedPeriods = mergePeriods(periods);
        int sum = 0;
        final int size = mergedPeriods.size();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            final int length = mergedPeriods.get(i).length;
            sum += length > 0 ? length : 0;

        }
        return sum;
    }

    private static List<Sjukskrivningsperiod> mergePeriods(Collection<Sjukskrivningsperiod> periodsIn) {
        if (periodsIn.size() < 2) {
            return new ArrayList<>(periodsIn);
        }
        final List<Sjukskrivningsperiod> periods = new LinkedList<>(periodsIn);
        periods.sort(Comparator.comparingInt(Sjukskrivningsperiod::getStart));
        for (int i = 1; i < periods.size(); i++) {
            final Sjukskrivningsperiod p1 = periods.get(i - 1);
            final Sjukskrivningsperiod p2 = periods.get(i);
            if (p1.getEnd() >= p2.getStart() - 1) {
                final int mergedLength = Math.max(p1.getEnd(), p2.getEnd()) - p1.getStart();
                final Sjukskrivningsperiod mergedPeriod = new Sjukskrivningsperiod(p1.getStart(), mergedLength);
                periods.remove(i);
                periods.remove(i - 1);
                periods.add(i - 1, mergedPeriod);
                i--;
            }
            if (periods.size() == 1) {
                break;
            }
        }
        return periods;
    }


}
