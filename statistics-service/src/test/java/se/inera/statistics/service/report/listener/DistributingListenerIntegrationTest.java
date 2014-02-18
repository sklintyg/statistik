package se.inera.statistics.service.report.listener;

import com.fasterxml.jackson.databind.JsonNode;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.report.api.RollingLength;
import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:listener-impl-test.xml" })
@Transactional
@DirtiesContext
public class DistributingListenerIntegrationTest {
    @Autowired
    private SjukfallPerManad sjukfallPerManad;

    @Autowired
    private SjukfallslangdGrupp sjukfallslangdGrupp;

    @Autowired
    private ProcessorListener distributingListener;
    private Range rangeOneMonth;
    private Range rangeTwoMonth;
    private String personId;

    @Before
    public void setUp() {
        rangeOneMonth = new Range(new LocalDate("2013-03-01"), new LocalDate("2013-03-31"));
        rangeTwoMonth = new Range(new LocalDate("2013-03-01"), new LocalDate("2013-04-30"));
        personId = "19121212-1212";
    }

    @Test
    public void testCount1Intyg() {
        LocalDate from = new LocalDate("2013-03-01"), to = new LocalDate("2013-03-10");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from, to, null);
        JsonNode hsa = null;
        JsonNode utlatande = createUtlatande(from, to);

        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        SimpleKonResponse<SimpleKonDataRow> result = sjukfallPerManad.getCasesPerMonth("v1", rangeOneMonth);
        Assert.assertEquals(1, result.getRows().get(0).getMale().intValue());
    }

    @Test
    public void testCountMonthSpanningIntyg() {
        LocalDate from = new LocalDate("2013-03-01"), to = new LocalDate("2013-04-10");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from, to, null);
        JsonNode hsa = null;
        JsonNode utlatande = createUtlatande(from, to);

        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        SimpleKonResponse<SimpleKonDataRow> result = sjukfallPerManad.getCasesPerMonth("v1", rangeTwoMonth);
        Assert.assertEquals(2, result.getRows().size());
        Assert.assertEquals(1, result.getRows().get(0).getMale().intValue());
        Assert.assertEquals(1, result.getRows().get(1).getMale().intValue());
    }

    @Test
    public void testCountMonthSpanningIntygWithPreviousEndDateInFirstMonth() {
        LocalDate from = new LocalDate("2013-03-01"), to = new LocalDate("2013-04-10");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from, to, new LocalDate("2013-03-03"));
        JsonNode hsa = null;
        JsonNode utlatande = createUtlatande(from, to);

        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        SimpleKonResponse<SimpleKonDataRow> result = sjukfallPerManad.getCasesPerMonth("v1", rangeTwoMonth);
        Assert.assertEquals(2, result.getRows().size());
        Assert.assertEquals(0, result.getRows().get(0).getMale().intValue());
        Assert.assertEquals(1, result.getRows().get(1).getMale().intValue());
    }

    @Test
    public void testCountMonthSpanningIntygWithPreviousEndDateInSecondMonth() {
        LocalDate from = new LocalDate("2013-03-01"), to = new LocalDate("2013-04-10");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from, to, new LocalDate("2013-04-03"));
        JsonNode hsa = null;
        JsonNode utlatande = createUtlatande(from, to);

        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        SimpleKonResponse<SimpleKonDataRow> result = sjukfallPerManad.getCasesPerMonth("v1", rangeTwoMonth);
        Assert.assertEquals(2, result.getRows().size());
        Assert.assertEquals(0, result.getRows().get(0).getMale().intValue());
        Assert.assertEquals(0, result.getRows().get(1).getMale().intValue());
    }

    @Test
    public void testCountThreeIntygWith5DayGapBetweenLastOnes() {
        JsonNode hsa = null;

        LocalDate from1 = new LocalDate("2013-03-01"), to1 = new LocalDate("2013-03-02");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from1, to1, null);
        JsonNode utlatande = createUtlatande(from1, to1);
        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        LocalDate from2 = new LocalDate("2013-03-03"), to2 = new LocalDate("2013-03-10");
        SjukfallInfo sjukfallInfo2 = new SjukfallInfo(personId, from2, to2, new LocalDate("2013-03-02"));
        JsonNode utlatande2 = createUtlatande(from2, to2);
        distributingListener.accept(sjukfallInfo2, utlatande2, hsa, 1L);

        LocalDate from3 = new LocalDate("2013-03-16"), to3 = new LocalDate("2013-03-17");
        SjukfallInfo sjukfallInfo3 = new SjukfallInfo(personId, from3, to3, new LocalDate("2013-03-10"));
        JsonNode utlatande3 = createUtlatande(from3, to3);
        distributingListener.accept(sjukfallInfo3, utlatande3, hsa, 1L);

        SimpleKonResponse<SimpleKonDataRow> result = sjukfallPerManad.getCasesPerMonth("v1", rangeOneMonth);
        Assert.assertEquals(1, result.getRows().size());
        Assert.assertEquals(1, result.getRows().get(0).getMale().intValue());
    }

    @Test
    public void testSjukfalllangdFor21DayIntygFollowedBy30DayIntygWithNoGap() {
        JsonNode hsa = null;

        LocalDate from1 = new LocalDate("2013-03-11"), to1 = new LocalDate("2013-03-31");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from1, to1, null);
        JsonNode utlatande = createUtlatande(from1, to1);
        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        LocalDate from2 = new LocalDate("2013-04-01"), to2 = new LocalDate("2013-04-30");
        SjukfallInfo sjukfallInfo2 = new SjukfallInfo(personId, from1, to2, to1);
        JsonNode utlatande2 = createUtlatande(from2, to2);
        distributingListener.accept(sjukfallInfo2, utlatande2, hsa, 1L);

        check1FallLangd(new LocalDate("2013-03-01"), "15-30 dagar");
        check1FallLangd(new LocalDate("2013-04-01"), "31-90 dagar");
    }

    @Test
    public void testSjukfalllangdFor2MonthIntygFollowedBy30DayIntygWith1DayOverlap() {
        JsonNode hsa = null;

        LocalDate from1 = new LocalDate("2013-03-01"), to1 = new LocalDate("2013-04-30");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from1, to1, null);
        JsonNode utlatande = createUtlatande(from1, to1);
        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        LocalDate from2 = new LocalDate("2013-05-01"), to2 = new LocalDate("2013-06-30");
        SjukfallInfo sjukfallInfo2 = new SjukfallInfo(personId, from1, to2, to1);
        JsonNode utlatande2 = createUtlatande(from2, to2);
        distributingListener.accept(sjukfallInfo2, utlatande2, hsa, 1L);

        check1FallLangd(new LocalDate("2013-03-01"), "31-90 dagar");
        check1FallLangd(new LocalDate("2013-04-01"), "31-90 dagar");
        check1FallLangd(new LocalDate("2013-05-01"), "91-180 dagar");
    }

    @Test
    public void testSjukfalllangdFor21DayIntygFollowedBy30DayIntygWith1DayOverlap() {
        JsonNode hsa = null;

        LocalDate from1 = new LocalDate("2013-03-11"), to1 = new LocalDate("2013-04-01");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from1, to1, null);
        JsonNode utlatande = createUtlatande(from1, to1);
        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        LocalDate from2 = new LocalDate("2013-04-01"), to2 = new LocalDate("2013-04-30");
        SjukfallInfo sjukfallInfo2 = new SjukfallInfo(personId, from1, to2, to1);
        JsonNode utlatande2 = createUtlatande(from2, to2);
        distributingListener.accept(sjukfallInfo2, utlatande2, hsa, 1L);

        check1FallLangd(new LocalDate("2013-03-01"), "15-30 dagar");
        check1FallLangd(new LocalDate("2013-04-01"), "31-90 dagar");
    }

    @Test
    public void testSjukfalllangdFor21DayIntygFollowedBy31DayIntygWithSixDayGap() {
        JsonNode hsa = null;

        LocalDate from1 = new LocalDate("2013-03-11"), to1 = new LocalDate("2013-03-31");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from1, to1, null);
        JsonNode utlatande = createUtlatande(from1, to1);
        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        LocalDate from2 = new LocalDate("2013-04-07"), to2 = new LocalDate("2013-05-06");
        SjukfallInfo sjukfallInfo2 = new SjukfallInfo(personId, from1, to2, null);
        JsonNode utlatande2 = createUtlatande(from2, to2);
        distributingListener.accept(sjukfallInfo2, utlatande2, hsa, 1L);

        check1FallLangd(new LocalDate("2013-03-01"), "15-30 dagar");

        SjukfallslangdResponse result = sjukfallslangdGrupp.getHistoricalStatistics("v1", new LocalDate("2013-04-01"), RollingLength.YEAR);
        Assert.assertEquals(2, result.getRows().size());
        Assert.assertEquals("15-30 dagar", result.getRows().get(0).getGroup());
        Assert.assertEquals("31-90 dagar", result.getRows().get(1).getGroup());
        Assert.assertEquals(1, result.getRows().get(0).getMale());
        Assert.assertEquals(1, result.getRows().get(1).getMale());

        result = sjukfallslangdGrupp.getHistoricalStatistics("v1", new LocalDate("2013-05-01"), RollingLength.YEAR);
        Assert.assertEquals(2, result.getRows().size());
        Assert.assertEquals("15-30 dagar", result.getRows().get(0).getGroup());
        Assert.assertEquals("31-90 dagar", result.getRows().get(1).getGroup());
        Assert.assertEquals(1, result.getRows().get(0).getMale());
        Assert.assertEquals(1, result.getRows().get(1).getMale());

    }

    @Test
    public void testSjukfalllangdFor21DayIntygFollowedBy29DayIntygWithNoGapWithinMonth() {
        JsonNode hsa = null;

        LocalDate from1 = new LocalDate("2013-03-12"), to1 = new LocalDate("2013-04-01");
        SjukfallInfo sjukfallInfo = new SjukfallInfo(personId, from1, to1, null);
        JsonNode utlatande = createUtlatande(from1, to1);
        distributingListener.accept(sjukfallInfo, utlatande, hsa, 1L);

        LocalDate from2 = new LocalDate("2013-04-02"), to2 = new LocalDate("2013-04-30");
        SjukfallInfo sjukfallInfo2 = new SjukfallInfo(personId, from1, to2, to1);
        JsonNode utlatande2 = createUtlatande(from2, to2);
        distributingListener.accept(sjukfallInfo2, utlatande2, hsa, 1L);

        SjukfallslangdResponse result;

        check1FallLangd(new LocalDate("2013-03-01"), "15-30 dagar");
        check1FallLangd(new LocalDate("2013-04-01"), "31-90 dagar");
        check1FallLangd(new LocalDate("2013-05-01"), "31-90 dagar");
        check1FallLangd(new LocalDate("2014-03-01"), "31-90 dagar");

        result = sjukfallslangdGrupp.getHistoricalStatistics("v1", new LocalDate("2013-02-01"), RollingLength.YEAR);
        Assert.assertEquals(0, result.getRows().size());

        result = sjukfallslangdGrupp.getHistoricalStatistics("v1", new LocalDate("2014-04-01"), RollingLength.YEAR);
        Assert.assertEquals(0, result.getRows().size());
    }

    private void check1FallLangd(LocalDate checkDate, String expectedGroup) {
        SjukfallslangdResponse result;
        result = sjukfallslangdGrupp.getHistoricalStatistics("v1", checkDate, RollingLength.YEAR);
        Assert.assertEquals(1, result.getRows().size());
        Assert.assertEquals(expectedGroup, result.getRows().get(0).getGroup());
        Assert.assertEquals(1, result.getRows().get(0).getMale());
    }

    private JsonNode createUtlatande(LocalDate from, LocalDate to) {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        final JsonNode utlatande = builder.build(personId, from, to, "v1", "A00", 50);
        return DocumentHelper.anonymize(utlatande);
    }
}
