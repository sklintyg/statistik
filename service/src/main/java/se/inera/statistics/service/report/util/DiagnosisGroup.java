/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;


import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import se.inera.statistics.service.report.common.ReportColor;

public enum DiagnosisGroup {

    A00_B99(ReportColor.ST_COLOR_01.getColor(), "A00-E90, G00-L99, N00-N99 Somatiska sjukdomar",
        "A00-B99", "C00-D48", "D50-D89", "E00-E90", "G00-G99", "H00-H59", "H00-H59", "H60-H95", "I00-I99",
        "J00-J99", "K00-K93", "L00-L99", "N00-N99"),
    F00_F99(ReportColor.ST_COLOR_02.getColor(), "F00-F99 Psykiska sjukdomar", "F00-F99"),
    M00_M99(ReportColor.ST_COLOR_03.getColor(), "M00-M99 Muskuloskeletala sjukdomar", "M00-M99"),
    O00_O99(ReportColor.ST_COLOR_04.getColor(), "O00-O99 Graviditet och förlossning", "O00-O99"),
    P00_P96(ReportColor.ST_COLOR_05.getColor(), "P00-P96, Q00-Q99, S00-Y98 Övrigt", "P00-P96", "Q00-Q99", "S00-T98", "U00-U99", "V01-Y98"),
    R00_R99(ReportColor.ST_COLOR_06.getColor(), "R00-R99 Symtomdiagnoser", "R00-R99"),
    Z00_Z99(ReportColor.ST_COLOR_07.getColor(),
        "Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", "Z00-Z99"),
    NO_GROUP("#FB7F4D", Icd10.UNKNOWN_CODE_NAME, Icd10.OTHER_KAPITEL);

    private final String color;
    private final String name;
    private final ImmutableList<String> chapters;

    DiagnosisGroup(String color, String name, String... chapters) {
        this.color = color;
        this.name = name;
        this.chapters = ImmutableList.copyOf(chapters);
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String[] getChapters() {
        return chapters.toArray(new String[chapters.size()]);
    }

    public static Map<String, String> getColors() {
        return Arrays.stream(values()).collect(Collectors.toMap(DiagnosisGroup::getName, DiagnosisGroup::getColor));
    }

}
