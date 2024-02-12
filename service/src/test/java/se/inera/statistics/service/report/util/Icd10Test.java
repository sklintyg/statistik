/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kapitel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:icd10.xml"})
public class Icd10Test {

    @Autowired
    private IcdCodeConverter codeConverter;
    @Autowired
    private Icd10 icd10;

    @Test
    public void start() {
        assertNotNull(icd10);
    }

    @Test
    public void hasKapitel() {
        assertEquals(23, icd10.getKapitel(true).size());
    }

    @Test
    public void kapitel1HasCorrectNameAndRange() {
        Kapitel kapitel = icd10.getKapitel(true).get(0);
        assertEquals("A00-B99", kapitel.getId());
        assertEquals("Vissa infektionssjukdomar och parasitsjukdomar", kapitel.getName());
    }

    @Test
    public void kapitelsIsSorted() {
        List<Kapitel> kapitels = icd10.getKapitel(false);
        List<Kapitel> expected = new ArrayList<>(kapitels);
        expected.sort(Comparator.comparing(Kapitel::getId));

        assertEquals(expected, kapitels);
    }

    @Test
    public void kapitelZHasCorrectNameAndRange() {
        Kapitel kapitel = icd10.getKapitel(true).stream().filter(k -> k.getId().startsWith("Z")).findFirst().orElse(null);
        assertEquals("Z00-Z99", kapitel.getId());
        assertEquals("Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", kapitel.getName());
    }

    @Test
    public void avsnitt() {
        assertEquals(21, icd10.getKapitel(true).get(0).getAvsnitt().size());
    }

    @Test
    public void avsnittInKapitel1() {
        Kapitel kapitel = icd10.getKapitel(true).get(0);
        List<Avsnitt> avsnitt = kapitel.getAvsnitt();
        assertEquals(21, avsnitt.size());
    }

    @Test
    public void normalizeIcd10Code() {
        assertEquals("", icd10.normalize(". -_+?="));
        assertEquals("A10", icd10.normalize("a 1.0"));
        assertEquals("B12", icd10.normalize(" B12.3 # "));
    }

    @Test
    public void hasKategoriG01() {
        assertEquals("G01", icd10.getKategori("G01").getId());
    }

    @Test
    public void kategoriIsTruncatedIfTooLong() {
        assertEquals("G01", icd10.findKategori("G01.1").getId());
    }

    @Test
    public void kategoriIsFoundEvenIfBadlyFormatted() {
        assertEquals("G01", icd10.findKategori("-G 0, 1.1AndMore").getId());
    }

    @Test
    public void getFirstKapitelFromFile() {
        Icd10.Kapitel kapitel = icd10.getKapitel("A00-B99");
        assertEquals("A00-B99", kapitel.getId());
        assertEquals("Vissa infektionssjukdomar och parasitsjukdomar", kapitel.getName());
    }

    @Test
    public void getLastKapitelFromFile() {
        Icd10.Kapitel kapitel = icd10.getKapitel("Z00-Z99");
        assertEquals("Z00-Z99", kapitel.getId());
        assertEquals("Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", kapitel.getName());
    }

    @Test
    public void getFirstAvsnittFromFile() {
        Icd10.Avsnitt avsnitt = icd10.getAvsnitt("A00-A09");
        assertEquals("A00-A09", avsnitt.getId());
        assertEquals("Infektionssjukdomar utgående från mag-tarmkanalen", avsnitt.getName());
    }

    @Test
    public void getLastAvsnittFromFile() {
        Icd10.Avsnitt avsnitt = icd10.getAvsnitt("Z80-Z99");
        assertEquals("Z80-Z99", avsnitt.getId());
        assertEquals(
            "Potentiella hälsorisker i familjens och patientens sjukhistoria samt vissa tillstånd och förhållanden som påverkar hälsan",
            avsnitt.getName());
    }

    @Test
    public void getFirstKategoriFromFile() {
        Icd10.Kategori kategori = icd10.getKategori("A00");
        assertEquals("A00", kategori.getId());
        assertEquals("Kolera", kategori.getName());
    }

    @Test
    public void getLastKategoriFromFile() {
        Icd10.Kategori kategori = icd10.getKategori("Z99");
        assertEquals("Z99", kategori.getId());
        assertEquals("Beroende av maskinella och andra hjälpmedel som ej klassificeras på annan plats", kategori.getName());
    }

    @Test
    public void getKategoriWithAsterisk() {
        Icd10.Kategori kategori = icd10.getKategori("L99");
        assertEquals("L99", kategori.getId());
        assertEquals("Andra tillstånd i hud och underhud vid sjukdomar som klassificeras på annan plats", kategori.getName());
    }

    @Test
    public void getKategoriWithDagger() {
        Icd10.Kategori kategori = icd10.getKategori("A17");
        assertEquals("A17", kategori.getId());
        assertEquals("Tuberkulos i nervsystemet", kategori.getName());
    }

    @Test
    public void getFirstkodFromIcd10File() {
        Icd10.Kod kod = icd10.getKod("A000");
        assertEquals("A000", kod.getId());
        assertEquals("Kolera orsakad av Vibrio cholerae 01, biovar cholerae", kod.getName());
    }

    @Test
    public void getLastKodFromFromIcd10File() {
        Icd10.Kod kod = icd10.getKod("Z999");
        assertEquals("Z999", kod.getId());
        assertEquals("Beroende av ospecificerade maskinella och andra hjälpmedel", kod.getName());
    }

    @Test
    public void getKodWithAsteriskFromIcd10File() {
        Icd10.Kod kod = icd10.getKod("D630");
        assertEquals("D630", kod.getId());
        assertEquals("Anemi vid tumörsjukdom", kod.getName());
    }

    @Test
    public void getKodWithDaggerFromIcd10File() {
        Icd10.Kod kod = icd10.getKod("A022");
        assertEquals("A022", kod.getId());
        assertEquals("Lokaliserade salmonellainfektioner", kod.getName());
    }

    @Test
    public void noDuplicateIcd10IntIds() {
        final List<Integer> allIntIds = getAllIntIds(icd10.getKapitel(true));
        final Set<Integer> duplicates = findDuplicates(allIntIds);
        assertEquals(0, duplicates.size());
    }

    public static <T> Set<T> findDuplicates(List<T> listContainingDuplicates) {
        final Set<T> setToReturn = new HashSet<>();
        final Set<T> set1 = new HashSet<>();

        for (T yourInt : listContainingDuplicates) {
            if (!set1.add(yourInt)) {
                setToReturn.add(yourInt);
            }
        }
        return setToReturn;
    }

    private List<Integer> getAllIntIds(List<? extends Icd10.Id> icd10s) {
        List<Integer> allIntIds = new ArrayList<>();
        for (Icd10.Id icd10 : icd10s) {
            allIntIds.add(icd10.toInt());
            allIntIds.addAll(getAllIntIds(icd10.getSubItems()));
        }
        return allIntIds;
    }


    @Test
    public void testIcd10ToInt() throws Exception {
        //Given
        //When
        for (int k = 0; k < 100; k++) {
            final char[] chars1 = "ABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray();
            for (int i = 0; i < chars1.length; i++) {
                char c = chars1[i];

                for (Icd10RangeType icd10RangeType : Icd10RangeType.values()) {
                    for (int j = 10; j < 10000; j++) {
                        Icd10.icd10ToInt(c + String.valueOf(j), icd10RangeType);
                    }
                }
            }
        }
        //Then

    }


    @Test
    public void testNumberOfKapitel() throws Exception {
        // In INTYG-4512 it was noticed that he number of kapitels mentioned in the information text with
        // keys "help.nationell.diagnosgroup" and "help.verksamhet.diagnosgroup" did not match the actual
        // number of kapitels. I have added this unit test so that it will be noticed automatically if the
        // number of kapitels changes, and the text therefore should be updated again. In other words, if
        // this test fails, please update the number in the info text with keys "help.nationell.diagnosgroup"
        // and "help.verksamhet.diagnosgroup" (in messages.js) and then also update this test to make it pass again.

        //When
        final List<Kapitel> kapitels = icd10.getKapitel(false);

        //Then
        assertEquals(22, kapitels.size());
    }
}
