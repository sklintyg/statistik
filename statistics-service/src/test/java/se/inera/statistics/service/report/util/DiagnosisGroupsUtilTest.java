package se.inera.statistics.service.report.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import se.inera.statistics.service.report.model.DiagnosisGroup;

public class DiagnosisGroupsUtilTest {

    @Test
    public void testGetGroupIdForCode() {
        String groupIdForCode = DiagnosisGroupsUtil.getGroupIdForCode("R15");
        assertEquals("R00-R99", groupIdForCode);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGroupIdForIllegalCode() {
        DiagnosisGroupsUtil.getGroupIdForCode("D99");
    }

    @Test
    public void testGetSubGroupForCode() {
        DiagnosisGroup subGroupForCode = DiagnosisGroupsUtil.getSubGroupForCode("T57");
        assertEquals("T51-T65", subGroupForCode.getId());
    }

    @Test
    public void testGetSubGroupForLongCode() {
        DiagnosisGroup subGroupForCode = DiagnosisGroupsUtil.getSubGroupForCode("T5712");
        assertEquals("T51-T65", subGroupForCode.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSubGroupForIllegalCode() {
        DiagnosisGroupsUtil.getSubGroupForCode("T99");
    }

    @Test
    public void testGetAllDiagnosisGroups() {
        List<DiagnosisGroup> allDiagnosisGroups = DiagnosisGroupsUtil.getAllDiagnosisGroups();
        String expectedResult = "[A00-B99 Vissa infektionssjukdomar och parasitsjukdomar, C00-D48 Tumörer, D50-D89 Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet, E00-E90 Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar, F00-F99 Psykiska sjukdomar och syndrom samt beteendestörningar, G00-G99 Sjukdomar i nervsystemet, H00-H59 Sjukdomar i ögat och närliggande organ, H60-H95 Sjukdomar i örat och mastoidutskottet, I00-I99 Cirkulationsorganens sjukdomar, J00-J99 Andningsorganens sjukdomar, K00-K93 Matsmältningsorganens sjukdomar, L00-L99 Hudens och underhudens sjukdomar, M00-M99 Sjukdomar i muskuloskeletala systemet och bindväven, N00-N99 Sjukdomar i urin- och könsorganen, O00-O99 Graviditet, förlossning och barnsängstid, P00-P96 Vissa perinatala tillstånd, Q00-Q99 Medfödda missbildningar, deformiteter och kromosomavvikelser, R00-R99 Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes, S00-T98 Skador, förgiftningar och vissa andra följder av yttre orsaker, V01-Y98 Yttre orsaker till sjukdom och död, Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården, U00-U99 Koder för särskilda ändamål]";
        assertEquals(expectedResult, allDiagnosisGroups.toString());
    }

}
