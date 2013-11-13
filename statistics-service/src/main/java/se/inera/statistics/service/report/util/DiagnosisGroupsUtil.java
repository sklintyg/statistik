package se.inera.statistics.service.report.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.statistics.service.report.model.DiagnosisGroup;

public final class DiagnosisGroupsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosisGroupsUtil.class);

    private final static Pattern ICD10_ANSI_FILE_LINE_PATTERN = Pattern.compile("(^[A-Z][0-9][0-9]-[A-Z][0-9][0-9])(.*)$");
    static final Map<String, Collection<DiagnosisGroup>> SUB_GROUPS = getSubGroups();
    private static final List<DiagnosisGroup> GROUPS = initGroups();
    

    private DiagnosisGroupsUtil() {
    }

    public static String getGroupIdForCode(String icd10Code) {
        String normalizedIcd10 = normalize(icd10Code);
        for (Entry<String, Collection<DiagnosisGroup>> entry : SUB_GROUPS.entrySet()) {
            for (DiagnosisGroup diagnosisGroup : entry.getValue()) {
                if (diagnosisGroup.isCodeInGroup(normalizedIcd10)) {
                    return entry.getKey();
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }

    public static String normalize(String icd10Code) {
        StringBuilder normalized = new StringBuilder(icd10Code.length());
        for (char c: icd10Code.toUpperCase().toCharArray()) {
            if ('A' <= c && c <= 'Z' || '0' <= c && c <= '9') {
                normalized.append(c);
            }
        }
        return normalized.toString();
    }

    public static String getSubGroupIdForCode(String icd10Code) {
        return getSubGroupForCode(icd10Code).getId();
    }

    public static DiagnosisGroup getSubGroupForCode(String icd10Code) {
        for (Entry<String, Collection<DiagnosisGroup>> entry : SUB_GROUPS.entrySet()) {
            for (DiagnosisGroup diagnosisGroup : entry.getValue()) {
                if (diagnosisGroup.isCodeInGroup(icd10Code)) {
                    return diagnosisGroup;
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }

    public static List<DiagnosisGroup> getAllDiagnosisGroups() {
        return GROUPS;
    }

    private static List<DiagnosisGroup> initGroups() {
        ArrayList<DiagnosisGroup> groups = new ArrayList<>();
        groups.add(group("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"));
        groups.add(group("C00-D48", "Tumörer"));
        groups.add(group("D50-D89", "Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet"));
        groups.add(group("E00-E90", "Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar"));
        groups.add(group("F00-F99", "Psykiska sjukdomar och syndrom samt beteendestörningar"));
        groups.add(group("G00-G99", "Sjukdomar i nervsystemet"));
        groups.add(group("H00-H59", "Sjukdomar i ögat och närliggande organ"));
        groups.add(group("H60-H95", "Sjukdomar i örat och mastoidutskottet"));
        groups.add(group("I00-I99", "Cirkulationsorganens sjukdomar"));
        groups.add(group("J00-J99", "Andningsorganens sjukdomar"));
        groups.add(group("K00-K93", "Matsmältningsorganens sjukdomar"));
        groups.add(group("L00-L99", "Hudens och underhudens sjukdomar"));
        groups.add(group("M00-M99", "Sjukdomar i muskuloskeletala systemet och bindväven"));
        groups.add(group("N00-N99", "Sjukdomar i urin- och könsorganen"));
        groups.add(group("O00-O99", "Graviditet, förlossning och barnsängstid"));
        groups.add(group("P00-P96", "Vissa perinatala tillstånd"));
        groups.add(group("Q00-Q99", "Medfödda missbildningar, deformiteter och kromosomavvikelser"));
        groups.add(group("R00-R99", "Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes"));
        groups.add(group("S00-T98", "Skador, förgiftningar och vissa andra följder av yttre orsaker"));
        groups.add(group("V01-Y98", "Yttre orsaker till sjukdom och död"));
        groups.add(group("Z00-Z99", "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården"));
        groups.add(group("U00-U99", "Koder för särskilda ändamål"));
        return groups;
    }

    public static List<DiagnosisGroup> getSubGroups(String groupId) {
        return new ArrayList<>(SUB_GROUPS.get(groupId));
    }

    private static DiagnosisGroup group(String code, String description) {
        return new DiagnosisGroup(code, description);
    }

    private static Map<String, Collection<DiagnosisGroup>> initSubGroups() {
        Map<String, Collection<DiagnosisGroup>> subGroups = new HashMap<>();
        String[] groups = new String[] { "A00-B99", "C00-D48", "D50-D89", "E00-E90", "F00-F99", "G00-G99", "H00-H59", "H60-H95", "I00-I99", "J00-J99",
                "K00-K93", "L00-L99", "M00-M99", "N00-N99", "O00-O99", "P00-P96", "Q00-Q99", "R00-R99", "S00-T98", "V01-Y98", "Z00-Z99", "U00-U99" };
        for (String group : groups) {
            subGroups.put(group, new TreeSet<DiagnosisGroup>());
        }
        return subGroups;
    }
    
    private static String getChapterNameForIcd10Code(String icd10Code, Collection<String> groupNames) throws Icd10ChapterNotFoundException{
        List<DiagnosisGroup> diagnosisGroups = new ArrayList<>();
        for (String groupName : groupNames) {
            diagnosisGroups.add(new DiagnosisGroup(groupName, ""));
        }
        
        for (DiagnosisGroup diagnosisGroup : diagnosisGroups) {
            if (diagnosisGroup.isCodeInGroup(icd10Code)) {
                return diagnosisGroup.getId();
            }
        }
        LOG.error("Failed to parse diagnosis groups definition file. Could not find chapter for code: " + icd10Code);
        throw new Icd10ChapterNotFoundException("Could not find chapter for code: " + icd10Code);
    }
    
    private static Map<String, Collection<DiagnosisGroup>> getSubGroups() {
        Map<String, Collection<DiagnosisGroup>> subGroups = initSubGroups();
        final Set<String> groupNames = subGroups.keySet();

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(DiagnosisGroupsUtil.class.getClassLoader().getResourceAsStream("KSH97_AVS.ANS"), "UTF-8"));
            for (String line = br.readLine(); line != null; line = br.readLine()){
                DiagnosisGroup group = getDiagnosisGroupFromIcd10AnsiFileLine(line);
                if (group != null) {
                    String chapterNameForIcd10Code = getChapterNameForIcd10Code(group.getId(), groupNames);
                    subGroups.get(chapterNameForIcd10Code).add(group);
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed to read diagnosis groups definition file", e);
        } catch (IOException e) {
            LOG.error("Failed to read diagnosis groups definition file", e);
        } catch (Icd10ChapterNotFoundException e) {
            LOG.error("Failed to parse diagnosis groups definition file", e);
        }

        return subGroups;
    }

    private static DiagnosisGroup getDiagnosisGroupFromIcd10AnsiFileLine(String line) {
        Matcher m = ICD10_ANSI_FILE_LINE_PATTERN.matcher(line);
        if (!m.matches()) {
            return null;
        }
        return group(m.group(1), m.group(2));
    }

}
