package se.inera.statistics.service.report.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import se.inera.statistics.service.demo.UtlatandeBuilder;
import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class AldersgruppListenerTest {

    private UtlatandeBuilder utlatandeBuilder = new UtlatandeBuilder();
    private JsonNode utlatande;

    @Mock
    private DiagnosisGroupsUtil util = mock(DiagnosisGroupsUtil.class);

    @Mock
    private AgeGroups agegroups = mock(AgeGroups.class);

    @InjectMocks
    private AldersGruppListener listener = new AldersGruppListener();

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    
    @Before 
    public void setup() throws UnsupportedEncodingException, IOException {
        Mockito.when(util.getGroupIdForCode(Mockito.anyString())).thenReturn("A10");
        Mockito.when(util.getSubGroupForCode(Mockito.anyString())).thenReturn(new DiagnosisGroup("A10-A11", "Test group"));
        
        doNothing().when(agegroups).count(captor.capture(), eq("enhetId"), anyString(), eq(RollingLength.YEAR), any(Verksamhet.class), any(Sex.class));

        utlatande = utlatandeBuilder.build("patientId", new LocalDate("2011-01-05"), new LocalDate("2011-03-27"), "enhetId", "A00", 0);
    }
    
    @Test
    public void threeMonthsNoExistingSjukfall() {
        SjukfallInfo sjukfallInfo = new SjukfallInfo("sjukfallid", null, null, null);
        listener.accept(sjukfallInfo, utlatande, null);
        LocalDate period = new LocalDate("2011-01");
        List<String> allValues = captor.getAllValues();
        assertEquals(14,  allValues.size());
        for (int i = 0; i < 14; i++, period = period.plusMonths(1)) {
            String periodId = ReportUtil.toPeriod(period);
            assertEquals(periodId, allValues.get(i));
        }
    }

    @Test
    public void twoThreeMonthsIntygNoExistingSjukfall() {
        SjukfallInfo sjukfallInfo = new SjukfallInfo("sjukfallid", null, null, null);

        listener.accept(sjukfallInfo, utlatande, null);
        utlatande = utlatandeBuilder.build("patientId2", new LocalDate("2011-02-05"), new LocalDate("2011-03-27"), "enhetId", "A00", 0);
        listener.accept(sjukfallInfo, utlatande, null);

        LocalDate period = new LocalDate("2011-01");
        List<String> allValues = captor.getAllValues();
        assertEquals(14 + 13,  allValues.size());
        for (int i = 0; i < 14; i++, period = period.plusMonths(1)) {
            String periodId = ReportUtil.toPeriod(period);
            assertEquals(periodId, allValues.get(i));
        }

        period = new LocalDate("2011-02");
        for (int i = 14; i < 14 + 13; i++, period = period.plusMonths(1)) {
            String periodId = ReportUtil.toPeriod(period);
            assertEquals(periodId, allValues.get(i));
        }
    }

    @Test
    public void threeMonthsWithExistingSjukfall() {
        SjukfallInfo sjukfallInfo = new SjukfallInfo("sjukfallid", new LocalDate("2010-11-01"), new LocalDate("2010-11-01"), new LocalDate("2011-02-01"));
        listener.accept(sjukfallInfo, utlatande, null);
        LocalDate period = new LocalDate("2012-02");
        List<String> allValues = captor.getAllValues();
        assertEquals(1,  allValues.size());
        for (int i = 0; i < 1; i++, period = period.plusMonths(1)) {
            String periodId = ReportUtil.toPeriod(period);
            assertEquals(periodId, allValues.get(i));
        }
    }

    @Test
    public void threeMonthsWithExistingFullyOverlappingSjukfall() {
        
        SjukfallInfo sjukfallInfo = new SjukfallInfo("sjukfallid", new LocalDate("2010-11-01"), new LocalDate("2010-11-01"), new LocalDate("2011-03-20"));
        listener.accept(sjukfallInfo, utlatande, null);
        List<String> allValues = captor.getAllValues();
        assertEquals(0,  allValues.size());
    }
}
