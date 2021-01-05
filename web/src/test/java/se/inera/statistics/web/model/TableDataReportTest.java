/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.service.FilterDataResponse;

public class TableDataReportTest {

    @Test
    public void testIsEmptyTrue() {
        //Given
        final TableDataReport tableDataReport = new TableDataReport() {

            @Override
            public TableData getTableData() {
                return null;
            }

            @Override
            public String getPeriod() {
                return null;
            }

            @Override
            public FilterDataResponse getFilter() {
                return null;
            }

            @Override
            public List<Message> getMessages() {
                return null;
            }

            @Override
            public List<ChartData> getChartDatas() {
                return Collections.emptyList();
            }

            @Override
            public AvailableFilters getAvailableFilters() {
                return null;
            }
        };

        //When
        final boolean empty = tableDataReport.isEmpty();

        //Then
        assertTrue(empty);
    }

    @Test
    public void testIsEmptyFalse() {
        //Given
        final TableDataReport tableDataReport = new TableDataReport() {

            @Override
            public TableData getTableData() {
                return null;
            }

            @Override
            public String getPeriod() {
                return null;
            }

            @Override
            public FilterDataResponse getFilter() {
                return null;
            }

            @Override
            public List<Message> getMessages() {
                return null;
            }

            @Override
            public List<ChartData> getChartDatas() {
                return Arrays.asList(new ChartData(Arrays.asList(new ChartSeries("", Arrays.asList(1, 2))),
                    Arrays.asList(new ChartCategory("cat1"), new ChartCategory("cat2"))));
            }

            @Override
            public AvailableFilters getAvailableFilters() {
                return null;
            }
        };

        //When
        final boolean empty = tableDataReport.isEmpty();

        //Then
        assertFalse(empty);
    }

    @Test
    public void testIsEmptyTrueWithZeroValues() {
        //Given
        final TableDataReport tableDataReport = new TableDataReport() {

            @Override
            public TableData getTableData() {
                return null;
            }

            @Override
            public String getPeriod() {
                return null;
            }

            @Override
            public FilterDataResponse getFilter() {
                return null;
            }

            @Override
            public List<Message> getMessages() {
                return null;
            }

            @Override
            public List<ChartData> getChartDatas() {
                return Arrays.asList(new ChartData(Arrays.asList(new ChartSeries("", Arrays.asList(0, 0))),
                    Arrays.asList(new ChartCategory("cat1"), new ChartCategory("cat2"))));
            }

            @Override
            public AvailableFilters getAvailableFilters() {
                return null;
            }
        };

        //When
        final boolean empty = tableDataReport.isEmpty();

        //Then
        assertTrue(empty);
    }

}
