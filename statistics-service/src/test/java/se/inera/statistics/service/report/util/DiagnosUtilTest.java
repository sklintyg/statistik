package se.inera.statistics.service.report.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

import se.inera.statistics.service.report.model.Avsnitt;

@RunWith(MockitoJUnitRunner.class)
public class DiagnosUtilTest {

    @Mock
    private Resource icd10ChaptersAnsiFile = mock(Resource.class);

    @InjectMocks
    private DiagnosUtil util = new DiagnosUtil();

    @Before
    public void setUp() throws IOException {
        Mockito.when(icd10ChaptersAnsiFile.getInputStream()).thenReturn(new ByteArrayInputStream("R10-R19Symtom och sjukdomstecken från matsmältningsorganen och buken\nT51-T65Toxisk effekt av substanser med i huvudsak icke-medicinsk användning\nC15-C26Maligna tumörer i matsmältningsorganen\nC30-C39Maligna tumörer i andningsorganen och brösthålans organ".getBytes("UTF-8")));
    }

    @Test
    public void testGetGroupIdForCode() {
        String kapitelId = util.getKapitelIdForCode("R15");
        assertEquals("R00-R99", kapitelId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetKapitelIdForShortCode() {
        util.getKapitelIdForCode("A1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetKapitelIdForIllegalCode() {
        util.getKapitelIdForCode("D99");
    }

    @Test
    public void testGetSubGroupForCode() {
        Avsnitt avsnitt = util.getAvsnittForCode("T57");
        assertEquals("T51-T65", avsnitt.getId());
    }

    @Test
    public void testGetSubGroupForLongCode() {
        Avsnitt avsnitt = util.getAvsnittForCode("T5712");
        assertEquals("T51-T65", avsnitt.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAvsnittForIllegalCode() {
        util.getAvsnittForCode("T99");
    }

    @Test
    public void testGetKapitel() {
        List<Avsnitt> allAvsnitts = DiagnosUtil.getKapitel();
        String expectedResult = "[{\"Avsnitt\":{\"id\":\"A00-B99\", \"name\":\"Vissa infektionssjukdomar och parasitsjukdomar\", \"firstId\":\"A00\", \"lastId\":\"B99\"}}, {\"Avsnitt\":{\"id\":\"C00-D48\", \"name\":\"Tumörer\", \"firstId\":\"C00\", \"lastId\":\"D48\"}}, {\"Avsnitt\":{\"id\":\"D50-D89\", \"name\":\"Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet\", \"firstId\":\"D50\", \"lastId\":\"D89\"}}, {\"Avsnitt\":{\"id\":\"E00-E90\", \"name\":\"Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar\", \"firstId\":\"E00\", \"lastId\":\"E90\"}}, {\"Avsnitt\":{\"id\":\"F00-F99\", \"name\":\"Psykiska sjukdomar och syndrom samt beteendestörningar\", \"firstId\":\"F00\", \"lastId\":\"F99\"}}, {\"Avsnitt\":{\"id\":\"G00-G99\", \"name\":\"Sjukdomar i nervsystemet\", \"firstId\":\"G00\", \"lastId\":\"G99\"}}, {\"Avsnitt\":{\"id\":\"H00-H59\", \"name\":\"Sjukdomar i ögat och närliggande organ\", \"firstId\":\"H00\", \"lastId\":\"H59\"}}, {\"Avsnitt\":{\"id\":\"H60-H95\", \"name\":\"Sjukdomar i örat och mastoidutskottet\", \"firstId\":\"H60\", \"lastId\":\"H95\"}}, {\"Avsnitt\":{\"id\":\"I00-I99\", \"name\":\"Cirkulationsorganens sjukdomar\", \"firstId\":\"I00\", \"lastId\":\"I99\"}}, {\"Avsnitt\":{\"id\":\"J00-J99\", \"name\":\"Andningsorganens sjukdomar\", \"firstId\":\"J00\", \"lastId\":\"J99\"}}, {\"Avsnitt\":{\"id\":\"K00-K93\", \"name\":\"Matsmältningsorganens sjukdomar\", \"firstId\":\"K00\", \"lastId\":\"K93\"}}, {\"Avsnitt\":{\"id\":\"L00-L99\", \"name\":\"Hudens och underhudens sjukdomar\", \"firstId\":\"L00\", \"lastId\":\"L99\"}}, {\"Avsnitt\":{\"id\":\"M00-M99\", \"name\":\"Sjukdomar i muskuloskeletala systemet och bindväven\", \"firstId\":\"M00\", \"lastId\":\"M99\"}}, {\"Avsnitt\":{\"id\":\"N00-N99\", \"name\":\"Sjukdomar i urin- och könsorganen\", \"firstId\":\"N00\", \"lastId\":\"N99\"}}, {\"Avsnitt\":{\"id\":\"O00-O99\", \"name\":\"Graviditet, förlossning och barnsängstid\", \"firstId\":\"O00\", \"lastId\":\"O99\"}}, {\"Avsnitt\":{\"id\":\"P00-P96\", \"name\":\"Vissa perinatala tillstånd\", \"firstId\":\"P00\", \"lastId\":\"P96\"}}, {\"Avsnitt\":{\"id\":\"Q00-Q99\", \"name\":\"Medfödda missbildningar, deformiteter och kromosomavvikelser\", \"firstId\":\"Q00\", \"lastId\":\"Q99\"}}, {\"Avsnitt\":{\"id\":\"R00-R99\", \"name\":\"Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes\", \"firstId\":\"R00\", \"lastId\":\"R99\"}}, {\"Avsnitt\":{\"id\":\"S00-T98\", \"name\":\"Skador, förgiftningar och vissa andra följder av yttre orsaker\", \"firstId\":\"S00\", \"lastId\":\"T98\"}}, {\"Avsnitt\":{\"id\":\"V01-Y98\", \"name\":\"Yttre orsaker till sjukdom och död\", \"firstId\":\"V01\", \"lastId\":\"Y98\"}}, {\"Avsnitt\":{\"id\":\"Z00-Z99\", \"name\":\"Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården\", \"firstId\":\"Z00\", \"lastId\":\"Z99\"}}, {\"Avsnitt\":{\"id\":\"U00-U99\", \"name\":\"Koder för särskilda ändamål\", \"firstId\":\"U00\", \"lastId\":\"U99\"}}]";
        assertEquals(expectedResult, allAvsnitts.toString());
    }

    @Test
    public void testGetAvsnitt() {
        List<Avsnitt> allAvsnitts = util.getAvsnittForKapitel("C00-D48");
        String expectedResult = "[{\"Avsnitt\":{\"id\":\"C15-C26\", \"name\":\"Maligna tumörer i matsmältningsorganen\", \"firstId\":\"C15\", \"lastId\":\"C26\"}}, {\"Avsnitt\":{\"id\":\"C30-C39\", \"name\":\"Maligna tumörer i andningsorganen och brösthålans organ\", \"firstId\":\"C30\", \"lastId\":\"C39\"}}]";
        assertEquals(expectedResult, allAvsnitts.toString());
    }

    @Test
    public void normalizeIcd10Code() {
        assertEquals("", util.normalize(". -_+?="));
        assertEquals("A10", util.normalize("a 1.0"));
        assertEquals("B123", util.normalize(" B12.3 # "));
    }
}
