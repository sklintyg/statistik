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

import se.inera.statistics.service.report.model.Diagnosgrupp;

@RunWith(MockitoJUnitRunner.class)
public class DiagnosgruppUtilTest {

    @Mock
    private Resource icd10ChaptersAnsiFile = mock(Resource.class);

    @InjectMocks
    private DiagnosisGroupsUtil util = new DiagnosisGroupsUtil();

    @Before
    public void setUp() throws IOException {
        Mockito.when(icd10ChaptersAnsiFile.getInputStream()).thenReturn(new ByteArrayInputStream("R10-R19Symtom och sjukdomstecken från matsmältningsorganen och buken\nT51-T65Toxisk effekt av substanser med i huvudsak icke-medicinsk användning\nC15-C26Maligna tumörer i matsmältningsorganen\nC30-C39Maligna tumörer i andningsorganen och brösthålans organ".getBytes("UTF-8")));
    }

    @Test
    public void testGetGroupIdForCode() {
        String groupIdForCode = util.getGroupIdForCode("R15");
        assertEquals("R00-R99", groupIdForCode);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGroupIdForShortCode() {
        util.getGroupIdForCode("A1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGroupIdForIllegalCode() {
        util.getGroupIdForCode("D99");
    }

    @Test
    public void testGetSubGroupForCode() {
        Diagnosgrupp subGroupForCode = util.getSubGroupForCode("T57");
        assertEquals("T51-T65", subGroupForCode.getId());
    }

    @Test
    public void testGetSubGroupForLongCode() {
        Diagnosgrupp subGroupForCode = util.getSubGroupForCode("T5712");
        assertEquals("T51-T65", subGroupForCode.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSubGroupForIllegalCode() {
        util.getSubGroupForCode("T99");
    }

    @Test
    public void testGetAllDiagnosisGroups() {
        List<Diagnosgrupp> allDiagnosgrupps = DiagnosisGroupsUtil.getAllDiagnosisGroups();
        String expectedResult = "[{\"Diagnosgrupp\":{\"id\":\"A00-B99\", \"name\":\"Vissa infektionssjukdomar och parasitsjukdomar\", \"firstId\":\"A00\", \"lastId\":\"B99\"}}, {\"Diagnosgrupp\":{\"id\":\"C00-D48\", \"name\":\"Tumörer\", \"firstId\":\"C00\", \"lastId\":\"D48\"}}, {\"Diagnosgrupp\":{\"id\":\"D50-D89\", \"name\":\"Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet\", \"firstId\":\"D50\", \"lastId\":\"D89\"}}, {\"Diagnosgrupp\":{\"id\":\"E00-E90\", \"name\":\"Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar\", \"firstId\":\"E00\", \"lastId\":\"E90\"}}, {\"Diagnosgrupp\":{\"id\":\"F00-F99\", \"name\":\"Psykiska sjukdomar och syndrom samt beteendestörningar\", \"firstId\":\"F00\", \"lastId\":\"F99\"}}, {\"Diagnosgrupp\":{\"id\":\"G00-G99\", \"name\":\"Sjukdomar i nervsystemet\", \"firstId\":\"G00\", \"lastId\":\"G99\"}}, {\"Diagnosgrupp\":{\"id\":\"H00-H59\", \"name\":\"Sjukdomar i ögat och närliggande organ\", \"firstId\":\"H00\", \"lastId\":\"H59\"}}, {\"Diagnosgrupp\":{\"id\":\"H60-H95\", \"name\":\"Sjukdomar i örat och mastoidutskottet\", \"firstId\":\"H60\", \"lastId\":\"H95\"}}, {\"Diagnosgrupp\":{\"id\":\"I00-I99\", \"name\":\"Cirkulationsorganens sjukdomar\", \"firstId\":\"I00\", \"lastId\":\"I99\"}}, {\"Diagnosgrupp\":{\"id\":\"J00-J99\", \"name\":\"Andningsorganens sjukdomar\", \"firstId\":\"J00\", \"lastId\":\"J99\"}}, {\"Diagnosgrupp\":{\"id\":\"K00-K93\", \"name\":\"Matsmältningsorganens sjukdomar\", \"firstId\":\"K00\", \"lastId\":\"K93\"}}, {\"Diagnosgrupp\":{\"id\":\"L00-L99\", \"name\":\"Hudens och underhudens sjukdomar\", \"firstId\":\"L00\", \"lastId\":\"L99\"}}, {\"Diagnosgrupp\":{\"id\":\"M00-M99\", \"name\":\"Sjukdomar i muskuloskeletala systemet och bindväven\", \"firstId\":\"M00\", \"lastId\":\"M99\"}}, {\"Diagnosgrupp\":{\"id\":\"N00-N99\", \"name\":\"Sjukdomar i urin- och könsorganen\", \"firstId\":\"N00\", \"lastId\":\"N99\"}}, {\"Diagnosgrupp\":{\"id\":\"O00-O99\", \"name\":\"Graviditet, förlossning och barnsängstid\", \"firstId\":\"O00\", \"lastId\":\"O99\"}}, {\"Diagnosgrupp\":{\"id\":\"P00-P96\", \"name\":\"Vissa perinatala tillstånd\", \"firstId\":\"P00\", \"lastId\":\"P96\"}}, {\"Diagnosgrupp\":{\"id\":\"Q00-Q99\", \"name\":\"Medfödda missbildningar, deformiteter och kromosomavvikelser\", \"firstId\":\"Q00\", \"lastId\":\"Q99\"}}, {\"Diagnosgrupp\":{\"id\":\"R00-R99\", \"name\":\"Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes\", \"firstId\":\"R00\", \"lastId\":\"R99\"}}, {\"Diagnosgrupp\":{\"id\":\"S00-T98\", \"name\":\"Skador, förgiftningar och vissa andra följder av yttre orsaker\", \"firstId\":\"S00\", \"lastId\":\"T98\"}}, {\"Diagnosgrupp\":{\"id\":\"V01-Y98\", \"name\":\"Yttre orsaker till sjukdom och död\", \"firstId\":\"V01\", \"lastId\":\"Y98\"}}, {\"Diagnosgrupp\":{\"id\":\"Z00-Z99\", \"name\":\"Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården\", \"firstId\":\"Z00\", \"lastId\":\"Z99\"}}, {\"Diagnosgrupp\":{\"id\":\"U00-U99\", \"name\":\"Koder för särskilda ändamål\", \"firstId\":\"U00\", \"lastId\":\"U99\"}}]";
        assertEquals(expectedResult, allDiagnosgrupps.toString());
    }

    @Test
    public void testGetSubDiagnosisGroups() {
        List<Diagnosgrupp> allDiagnosgrupps = util.getSubGroups("C00-D48");
        String expectedResult = "[{\"Diagnosgrupp\":{\"id\":\"C15-C26\", \"name\":\"Maligna tumörer i matsmältningsorganen\", \"firstId\":\"C15\", \"lastId\":\"C26\"}}, {\"Diagnosgrupp\":{\"id\":\"C30-C39\", \"name\":\"Maligna tumörer i andningsorganen och brösthålans organ\", \"firstId\":\"C30\", \"lastId\":\"C39\"}}]";
        assertEquals(expectedResult, allDiagnosgrupps.toString());
    }

    @Test
    public void normalizeIcd10Code() {
        assertEquals("", DiagnosisGroupsUtil.normalize(". -_+?="));
        assertEquals("A10", DiagnosisGroupsUtil.normalize("a 1.0"));
        assertEquals("B123", DiagnosisGroupsUtil.normalize(" B12.3 # "));
    }
}
