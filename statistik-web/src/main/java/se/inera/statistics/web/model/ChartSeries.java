/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model;

import java.util.List;

import se.inera.statistics.service.report.model.Kon;

public class ChartSeries {

    private static int stackCounter = 0;
    private static final String STACKED = "stacked";

    private final String name;
    private final List<Integer> data;
    private final String stack;
    private final Kon sex;

    public ChartSeries(String name, List<Integer> data, String stack, Kon sex) {
        this.name = name;
        this.data = data;
        this.stack = stack;
        this.sex = sex;
    }

    public ChartSeries(String name, List<Integer> data, boolean stacked) {
        this(name, data, getStackValue(stacked), null);
    }

    public ChartSeries(String name, List<Integer> data, String stack) {
        this(name, data, stack, null);
    }

    public ChartSeries(String name, List<Integer> data, boolean stacked, Kon sex) {
        this(name, data, getStackValue(stacked), sex);
    }

    private static String getStackValue(boolean stacked) {
        return stacked ? STACKED : String.valueOf(stackCounter++);
    }

    public String getName() {
        return name;
    }

    public List<Integer> getData() {
        return data;
    }

    public String getStack() {
        return stack;
    }

    public Kon getSex() {
        return sex;
    }

    @Override
    public String toString() {
        return name + ": " + data.toString();
    }

}
