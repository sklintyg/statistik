package se.inera.statistics.service.report.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
        String expectedResult = "[DiagnosisGroup{id='A00-B99', name='Vissa infektionssjukdomar och parasitsjukdomar', firstId='A00', lastId='B99'}, DiagnosisGroup{id='C00-D48', name='Tumörer', firstId='C00', lastId='D48'}, DiagnosisGroup{id='D50-D89', name='Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet', firstId='D50', lastId='D89'}, DiagnosisGroup{id='E00-E90', name='Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar', firstId='E00', lastId='E90'}, DiagnosisGroup{id='F00-F99', name='Psykiska sjukdomar och syndrom samt beteendestörningar', firstId='F00', lastId='F99'}, DiagnosisGroup{id='G00-G99', name='Sjukdomar i nervsystemet', firstId='G00', lastId='G99'}, DiagnosisGroup{id='H00-H59', name='Sjukdomar i ögat och närliggande organ', firstId='H00', lastId='H59'}, DiagnosisGroup{id='H60-H95', name='Sjukdomar i örat och mastoidutskottet', firstId='H60', lastId='H95'}, DiagnosisGroup{id='I00-I99', name='Cirkulationsorganens sjukdomar', firstId='I00', lastId='I99'}, DiagnosisGroup{id='J00-J99', name='Andningsorganens sjukdomar', firstId='J00', lastId='J99'}, DiagnosisGroup{id='K00-K93', name='Matsmältningsorganens sjukdomar', firstId='K00', lastId='K93'}, DiagnosisGroup{id='L00-L99', name='Hudens och underhudens sjukdomar', firstId='L00', lastId='L99'}, DiagnosisGroup{id='M00-M99', name='Sjukdomar i muskuloskeletala systemet och bindväven', firstId='M00', lastId='M99'}, DiagnosisGroup{id='N00-N99', name='Sjukdomar i urin- och könsorganen', firstId='N00', lastId='N99'}, DiagnosisGroup{id='O00-O99', name='Graviditet, förlossning och barnsängstid', firstId='O00', lastId='O99'}, DiagnosisGroup{id='P00-P96', name='Vissa perinatala tillstånd', firstId='P00', lastId='P96'}, DiagnosisGroup{id='Q00-Q99', name='Medfödda missbildningar, deformiteter och kromosomavvikelser', firstId='Q00', lastId='Q99'}, DiagnosisGroup{id='R00-R99', name='Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes', firstId='R00', lastId='R99'}, DiagnosisGroup{id='S00-T98', name='Skador, förgiftningar och vissa andra följder av yttre orsaker', firstId='S00', lastId='T98'}, DiagnosisGroup{id='V01-Y98', name='Yttre orsaker till sjukdom och död', firstId='V01', lastId='Y98'}, DiagnosisGroup{id='Z00-Z99', name='Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården', firstId='Z00', lastId='Z99'}, DiagnosisGroup{id='U00-U99', name='Koder för särskilda ändamål', firstId='U00', lastId='U99'}]";
        assertEquals(expectedResult, allDiagnosisGroups.toString());
    }

    @Test
    public void testGetSubDiagnosisGroups() {
        List<DiagnosisGroup> allDiagnosisGroups = util.getSubGroups("C00-D48");
        String expectedResult = "[DiagnosisGroup{id='C15-C26', name='Maligna tumörer i matsmältningsorganen', firstId='C15', lastId='C26'}, DiagnosisGroup{id='C30-C39', name='Maligna tumörer i andningsorganen och brösthålans organ', firstId='C30', lastId='C39'}]";
        assertEquals(expectedResult, allDiagnosisGroups.toString());
    }

    @Test
    public void normalizeIcd10Code() {
        assertEquals("", DiagnosisGroupsUtil.normalize(". -_+?="));
        assertEquals("A10", DiagnosisGroupsUtil.normalize("a 1.0"));
        assertEquals("B123", DiagnosisGroupsUtil.normalize(" B12.3 # "));
    }
}
