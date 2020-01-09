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
package se.inera.statistics.service.report.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.util.Icd10;

public class IcdTest {

    @Test
    public void testContructorConvertFromIdOnlyIncludesExpectedDepthKapiteli() throws Exception {
        //Given
        final Icd10.Kapitel kapitel = new Icd10.Kapitel("A00-A90", "a");
        final Icd10.Avsnitt avsnitt = new Icd10.Avsnitt("A00-A10", "aa", kapitel);
        final Icd10.Kategori kategori = new Icd10.Kategori("A00", "aaa", avsnitt);
        final Icd10.Kod kod = new Icd10.Kod("A000", "aaaa", kategori);

        //When
        final Icd icd = new Icd(kapitel, Icd10.Kapitel.class);

        //Then
        List<Icd> allIcds = extractAllIcds(icd);
        assertEquals(1, allIcds.size());
        assertEquals(kapitel.getName(), allIcds.get(0).getName());
    }

    @Test
    public void testContructorConvertFromIdOnlyIncludesExpectedDepthAvsnitt() throws Exception {
        //Given
        final Icd10.Kapitel kapitel = new Icd10.Kapitel("A00-A90", "a");
        final Icd10.Avsnitt avsnitt = new Icd10.Avsnitt("A00-A10", "aa", kapitel);
        final Icd10.Kategori kategori = new Icd10.Kategori("A00", "aaa", avsnitt);
        final Icd10.Kod kod = new Icd10.Kod("A000", "aaaa", kategori);

        //When
        final Icd icd = new Icd(kapitel, Icd10.Avsnitt.class);

        //Then
        List<Icd> allIcds = extractAllIcds(icd);
        assertEquals(2, allIcds.size());
        assertEquals(kapitel.getName(), allIcds.get(0).getName());
        assertEquals(avsnitt.getName(), allIcds.get(1).getName());
    }

    @Test
    public void testContructorConvertFromIdOnlyIncludesExpectedDepthKategori() throws Exception {
        //Given
        final Icd10.Kapitel kapitel = new Icd10.Kapitel("A00-A90", "a");
        final Icd10.Avsnitt avsnitt = new Icd10.Avsnitt("A00-A10", "aa", kapitel);
        final Icd10.Kategori kategori = new Icd10.Kategori("A00", "aaa", avsnitt);
        final Icd10.Kod kod = new Icd10.Kod("A000", "aaaa", kategori);

        //When
        final Icd icd = new Icd(kapitel, Icd10.Kategori.class);

        //Then
        List<Icd> allIcds = extractAllIcds(icd);
        assertEquals(3, allIcds.size());
        assertEquals(kapitel.getName(), allIcds.get(0).getName());
        assertEquals(avsnitt.getName(), allIcds.get(1).getName());
        assertEquals(kategori.getName(), allIcds.get(2).getName());
    }

    @Test
    public void testContructorConvertFromIdOnlyIncludesExpectedDepthKod() throws Exception {
        //Given
        final Icd10.Kapitel kapitel = new Icd10.Kapitel("A00-A90", "a");
        final Icd10.Avsnitt avsnitt = new Icd10.Avsnitt("A00-A10", "aa", kapitel);
        final Icd10.Kategori kategori = new Icd10.Kategori("A00", "aaa", avsnitt);
        final Icd10.Kod kod = new Icd10.Kod("A000", "aaaa", kategori);

        //When
        final Icd icd = new Icd(kapitel, Icd10.Kod.class);

        //Then
        List<Icd> allIcds = extractAllIcds(icd);
        assertEquals(4, allIcds.size());
        assertEquals(kapitel.getName(), allIcds.get(0).getName());
        assertEquals(avsnitt.getName(), allIcds.get(1).getName());
        assertEquals(kategori.getName(), allIcds.get(2).getName());
        assertEquals(kod.getName(), allIcds.get(3).getName());
    }

    @Test
    public void testContructorConvertFromIdOnlyIncludesExpectedDepthNullIncludesAll() throws Exception {
        //Given
        final Icd10.Kapitel kapitel = new Icd10.Kapitel("A00-A90", "a");
        final Icd10.Avsnitt avsnitt = new Icd10.Avsnitt("A00-A10", "aa", kapitel);
        final Icd10.Kategori kategori = new Icd10.Kategori("A00", "aaa", avsnitt);
        final Icd10.Kod kod = new Icd10.Kod("A000", "aaaa", kategori);

        //When
        final Icd icd = new Icd(kapitel, null);

        //Then
        List<Icd> allIcds = extractAllIcds(icd);
        assertEquals(4, allIcds.size());
        assertEquals(kapitel.getName(), allIcds.get(0).getName());
        assertEquals(avsnitt.getName(), allIcds.get(1).getName());
        assertEquals(kategori.getName(), allIcds.get(2).getName());
        assertEquals(kod.getName(), allIcds.get(3).getName());
    }

    private List<Icd> extractAllIcds(Icd icd) {
        final List<Icd> subItems = icd.getSubItems();
        final ArrayList<Icd> icds = new ArrayList<>();
        icds.add(icd);
        if (!subItems.isEmpty()) {
            for (Icd subIcd : subItems) {
                icds.addAll(extractAllIcds(subIcd));
            }
        }
        return icds;
    }

}
