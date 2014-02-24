/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import se.inera.statistics.service.report.model.Avsnitt;

public class DiagnosUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosUtil.class);

    @Autowired
    private Resource icd10ChaptersAnsiFile;

    private static final Pattern ICD10_ANSI_FILE_LINE_PATTERN = Pattern.compile("(^[A-Z][0-9][0-9]-[A-Z][0-9][0-9])(.*)$");
    private Map<String, Collection<Avsnitt>> subgroups;
    private static final List<Avsnitt> GROUPS = initGroups();

    public String getKapitelIdForCode(String icd10Code) {
        String normalizedIcd10 = normalize(icd10Code);
        for (Entry<String, Collection<Avsnitt>> entry : getSubGroups().entrySet()) {
            for (Avsnitt avsnitt : entry.getValue()) {
                if (avsnitt.isCodeInGroup(normalizedIcd10)) {
                    return entry.getKey();
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }

    protected String normalize(String icd10Code) {
        StringBuilder normalized = new StringBuilder(icd10Code.length());
        for (char c: icd10Code.toUpperCase().toCharArray()) {
            if ('A' <= c && c <= 'Z' || '0' <= c && c <= '9') {
                normalized.append(c);
            }
        }
        return normalized.toString();
    }

    public String getSubGroupIdForCode(String icd10Code) {
        return getAvsnittForCode(icd10Code).getId();
    }

    public Avsnitt getAvsnittForCode(String icd10Code) {
        for (Entry<String, Collection<Avsnitt>> entry : getSubGroups().entrySet()) {
            for (Avsnitt avsnitt : entry.getValue()) {
                if (avsnitt.isCodeInGroup(icd10Code)) {
                    return avsnitt;
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }

    public static List<Avsnitt> getKapitel() {
        return GROUPS;
    }

    private static List<Avsnitt> initGroups() {
        ArrayList<Avsnitt> groups = new ArrayList<>();
        groups.add(avsnitt("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"));
        groups.add(avsnitt("C00-D48", "Tumörer"));
        groups.add(avsnitt("D50-D89", "Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet"));
        groups.add(avsnitt("E00-E90", "Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar"));
        groups.add(avsnitt("F00-F99", "Psykiska sjukdomar och syndrom samt beteendestörningar"));
        groups.add(avsnitt("G00-G99", "Sjukdomar i nervsystemet"));
        groups.add(avsnitt("H00-H59", "Sjukdomar i ögat och närliggande organ"));
        groups.add(avsnitt("H60-H95", "Sjukdomar i örat och mastoidutskottet"));
        groups.add(avsnitt("I00-I99", "Cirkulationsorganens sjukdomar"));
        groups.add(avsnitt("J00-J99", "Andningsorganens sjukdomar"));
        groups.add(avsnitt("K00-K93", "Matsmältningsorganens sjukdomar"));
        groups.add(avsnitt("L00-L99", "Hudens och underhudens sjukdomar"));
        groups.add(avsnitt("M00-M99", "Sjukdomar i muskuloskeletala systemet och bindväven"));
        groups.add(avsnitt("N00-N99", "Sjukdomar i urin- och könsorganen"));
        groups.add(avsnitt("O00-O99", "Graviditet, förlossning och barnsängstid"));
        groups.add(avsnitt("P00-P96", "Vissa perinatala tillstånd"));
        groups.add(avsnitt("Q00-Q99", "Medfödda missbildningar, deformiteter och kromosomavvikelser"));
        groups.add(avsnitt("R00-R99", "Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes"));
        groups.add(avsnitt("S00-T98", "Skador, förgiftningar och vissa andra följder av yttre orsaker"));
        groups.add(avsnitt("V01-Y98", "Yttre orsaker till sjukdom och död"));
        groups.add(avsnitt("Z00-Z99", "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården"));
        groups.add(avsnitt("U00-U99", "Koder för särskilda ändamål"));
        return groups;
    }

    public List<Avsnitt> getAvsnittForKapitel(String groupId) {
        Collection<Avsnitt> avsnitt = getSubGroups().get(groupId);
        if (avsnitt != null) {
            return new ArrayList<>(avsnitt);
        } else {
            return Collections.emptyList();
        }
    }

    public Collection<Avsnitt> getGroupsInChapter(String chapter) {
        return subgroups.get(chapter);
    }
    private static Avsnitt avsnitt(String code, String description) {
        return new Avsnitt(code, description);
    }

    private static Map<String, Collection<Avsnitt>> initSubGroups() {
        Map<String, Collection<Avsnitt>> subGroups = new HashMap<>();
        String[] groups = new String[] {"A00-B99", "C00-D48", "D50-D89", "E00-E90", "F00-F99", "G00-G99", "H00-H59", "H60-H95", "I00-I99", "J00-J99",
                "K00-K93", "L00-L99", "M00-M99", "N00-N99", "O00-O99", "P00-P96", "Q00-Q99", "R00-R99", "S00-T98", "V01-Y98", "Z00-Z99", "U00-U99" };
        for (String group : groups) {
            subGroups.put(group, new TreeSet<Avsnitt>());
        }
        return subGroups;
    }

    private static String getChapterNameForIcd10Code(String icd10Code, Collection<String> groupNames) throws Icd10ChapterNotFoundException {
        List<Avsnitt> avsnitts = new ArrayList<>();
        for (String groupName : groupNames) {
            avsnitts.add(new Avsnitt(groupName, ""));
        }

        for (Avsnitt avsnitt : avsnitts) {
            if (avsnitt.isCodeInGroup(icd10Code)) {
                return avsnitt.getId();
            }
        }
        LOG.error("Failed to parse diagnosis groups definition file. Could not find chapter for code: " + icd10Code);
        throw new Icd10ChapterNotFoundException("Could not find chapter for code: " + icd10Code);
    }

    private Map<String, Collection<Avsnitt>> getSubGroups() {
        if (subgroups == null) {
            subgroups = setupSubGroups();
        }
        return subgroups;
    }

    private Map<String, Collection<Avsnitt>> setupSubGroups() {
        Map<String, Collection<Avsnitt>> subGroups = initSubGroups();
        final Set<String> groupNames = subGroups.keySet();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(icd10ChaptersAnsiFile.getInputStream(), "UTF-8"))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                Avsnitt group = getDiagnosisGroupFromIcd10AnsiFileLine(line);
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

    private static Avsnitt getDiagnosisGroupFromIcd10AnsiFileLine(String line) {
        Matcher m = ICD10_ANSI_FILE_LINE_PATTERN.matcher(line);
        if (!m.matches()) {
            return null;
        }
        return avsnitt(m.group(1), m.group(2));
    }

}
