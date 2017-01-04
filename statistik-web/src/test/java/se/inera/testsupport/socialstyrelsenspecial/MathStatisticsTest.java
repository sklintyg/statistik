/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.socialstyrelsenspecial;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MathStatisticsTest {

    @Test
    public void testGetStdDevAndMedianCalculatesCorrect() throws Exception {
        final List<Double> numbers = Arrays.asList(22D, 18D, 25D, 10D, 11D, 15D, 15D, 21D, 22D, 15D, 21D, 19D, 28D, 29D, 30D, 27D, 29D, 28D, 30D, 28D, 25D);
        final MathStatistics mathStatistics = new MathStatistics(numbers);
        assertEquals(6.226998491, mathStatistics.getStdDev(), 0.000001);
        assertEquals(22, mathStatistics.median(), 0.000001);
    }

}
