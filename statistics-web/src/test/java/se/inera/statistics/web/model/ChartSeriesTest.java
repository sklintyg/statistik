package se.inera.statistics.web.model;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class ChartSeriesTest {

    @Test
    public void testStackDifferesOnChartSeriesIfNotStacked() {
        ChartSeries chartSeries1 = new ChartSeries("", Collections.<Integer>emptyList(), false);
        ChartSeries chartSeries2 = new ChartSeries("", Collections.<Integer>emptyList(), false);

        assertTrue(chartSeries1.getStack() != chartSeries2.getStack());
    }

    @Test
    public void testStackIsSameOnChartSeriesIfStacked() {
        ChartSeries chartSeries1 = new ChartSeries("", Collections.<Integer>emptyList(), true);
        ChartSeries chartSeries2 = new ChartSeries("", Collections.<Integer>emptyList(), true);

        assertTrue(chartSeries1.getStack() == chartSeries2.getStack());
    }

}
