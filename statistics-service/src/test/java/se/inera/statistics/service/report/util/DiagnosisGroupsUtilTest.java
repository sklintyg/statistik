package se.inera.statistics.service.report.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

import se.inera.statistics.service.report.model.DiagnosisGroup;

@RunWith(MockitoJUnitRunner.class)
public class DiagnosisGroupsUtilTest {

    @Mock
    private Resource icd10ChaptersAnsiFile = mock(Resource.class);

    @InjectMocks
    private DiagnosisGroupsUtil util = new DiagnosisGroupsUtil();

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException{
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
        DiagnosisGroup subGroupForCode = util.getSubGroupForCode("T57");
        assertEquals("T51-T65", subGroupForCode.getId());
    }

    @Test
    public void testGetSubGroupForLongCode() {
        DiagnosisGroup subGroupForCode = util.getSubGroupForCode("T5712");
        assertEquals("T51-T65", subGroupForCode.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSubGroupForIllegalCode() {
        util.getSubGroupForCode("T99");
    }

    @Test
    public void testGetAllDiagnosisGroups() {
        List<DiagnosisGroup> allDiagnosisGroups = DiagnosisGroupsUtil.getAllDiagnosisGroups();
        String expectedResult = "[A00-B99 Vissa infektionssjukdomar och parasitsjukdomar, C00-D48 Tumörer, D50-D89 Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet, E00-E90 Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar, F00-F99 Psykiska sjukdomar och syndrom samt beteendestörningar, G00-G99 Sjukdomar i nervsystemet, H00-H59 Sjukdomar i ögat och närliggande organ, H60-H95 Sjukdomar i örat och mastoidutskottet, I00-I99 Cirkulationsorganens sjukdomar, J00-J99 Andningsorganens sjukdomar, K00-K93 Matsmältningsorganens sjukdomar, L00-L99 Hudens och underhudens sjukdomar, M00-M99 Sjukdomar i muskuloskeletala systemet och bindväven, N00-N99 Sjukdomar i urin- och könsorganen, O00-O99 Graviditet, förlossning och barnsängstid, P00-P96 Vissa perinatala tillstånd, Q00-Q99 Medfödda missbildningar, deformiteter och kromosomavvikelser, R00-R99 Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes, S00-T98 Skador, förgiftningar och vissa andra följder av yttre orsaker, V01-Y98 Yttre orsaker till sjukdom och död, Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården, U00-U99 Koder för särskilda ändamål]";
        assertEquals(expectedResult, allDiagnosisGroups.toString());
    }

    @Test
    public void testGetSubDiagnosisGroups() {
        List<DiagnosisGroup> allDiagnosisGroups = util.getSubGroups("C00-D48");
        String expectedResult = "[C15-C26 Maligna tumörer i matsmältningsorganen, C30-C39 Maligna tumörer i andningsorganen och brösthålans organ]";
        assertEquals(expectedResult, allDiagnosisGroups.toString());
    }
    
    @Test
    public void testGetAllSubDiagnosisGroups() {
        Map<String, Collection<DiagnosisGroup>> allDiagnosisGroups = DiagnosisGroupsUtil.SUB_GROUPS;
        String expectedResult = "{C00-D48=[C15-C26 Maligna tumörer i matsmältningsorganen, C30-C39 Maligna tumörer i andningsorganen och brösthålans organ], F00-F99=[], Q00-Q99=[], L00-L99=[], U00-U99=[], P00-P96=[], A00-B99=[], N00-N99=[], Z00-Z99=[], M00-M99=[], D50-D89=[], R00-R99=[R10-R19 Symtom och sjukdomstecken från matsmältningsorganen och buken], H00-H59=[], V01-Y98=[], E00-E90=[], I00-I99=[], H60-H95=[], G00-G99=[], S00-T98=[T51-T65 Toxisk effekt av substanser med i huvudsak icke-medicinsk användning], K00-K93=[], O00-O99=[], J00-J99=[]}";
        assertEquals(expectedResult, allDiagnosisGroups.toString());
    }
    
    @Test
    public void normalizeIcd10Code() {
        assertEquals("", DiagnosisGroupsUtil.normalize(". -_+?="));
        assertEquals("A10", DiagnosisGroupsUtil.normalize("a 1.0"));
        assertEquals("B123", DiagnosisGroupsUtil.normalize(" B12.3 # "));
    }
}
