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
