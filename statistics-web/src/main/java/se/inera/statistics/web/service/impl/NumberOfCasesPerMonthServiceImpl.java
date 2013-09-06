package se.inera.statistics.web.service.impl;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.service.ChartDataService;

@Service("chartService")
public class NumberOfCasesPerMonthServiceImpl implements ChartDataService {

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public ChartData getChartData() {
        return createMockData();
    }

    private ChartData createMockData() {
        ChartData chartData = new ChartData();
        chartData.setTitle("Antal sjukfall per månad");
        ArrayList<Integer> serie1 = new ArrayList<Integer>();
        serie1.add(1);
        serie1.add(2);
        serie1.add(3);
        serie1.add(3);
        serie1.add(1);
        serie1.add(1);
        serie1.add(1);
        chartData.addDataSeries("Antal sjukskrivna Män", serie1);
        ArrayList<Integer> serie2 = new ArrayList<Integer>();
        serie2.add(2);
        serie2.add(2);
        serie2.add(4);
        serie2.add(1);
        serie2.add(2);
        serie2.add(2);
        serie2.add(2);
        chartData.addDataSeries("Antal sjukskrivna Kvinnor", serie2);
        ArrayList<Integer> serie3 = new ArrayList<Integer>();
        serie3.add(3);
        serie3.add(4);
        serie3.add(7);
        serie3.add(4);
        serie3.add(3);
        serie3.add(3);
        serie3.add(3);
        chartData.addDataSeries("Antal sjukskrivna Totalt", serie3);
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Januari 2013");
        categories.add("Februari 2013");
        categories.add("Mars 2013");
        categories.add("April 2013");
        categories.add("Maj 2013");
        categories.add("Juni 2013");
        categories.add("Juli 2013");
        chartData.setCategories(categories);
        return chartData;
    }
}
