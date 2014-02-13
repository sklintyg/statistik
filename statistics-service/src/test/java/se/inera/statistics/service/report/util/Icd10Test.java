package se.inera.statistics.service.report.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kapitel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:icd10-test.xml" })
public class Icd10Test {

    @Autowired
    private Icd10 icd10;

    @Test
    public void start() {
        assertNotNull(icd10);
    }

    @Test
    public void hasKapitel() {
        assertEquals(22, icd10.getKapitel().size());
    }

    @Test
    public void kapitel1HasCorrectNameAndRange() {
        Kapitel kapitel = icd10.getKapitel().get(0);
        assertEquals("A00-B99", kapitel.getId());
        assertEquals("Vissa infektionssjukdomar och parasitsjukdomar", kapitel.getName());
    }

    @Test
    public void kapitel22HasCorrectNameAndRange() {
        Kapitel kapitel = icd10.getKapitel().get(21);
        assertEquals("Z00-Z99", kapitel.getId());
        assertEquals("Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", kapitel.getName());
    }

    @Test
    public void avsnitt() {
        assertEquals(267, icd10.getAvsnitt().size());
    }

    @Test
    public void avsnittInKapitel1() {
        Kapitel kapitel = icd10.getKapitel().get(0);
        List<Avsnitt> avsnitt = kapitel.getAvsnitt();
        assertEquals(21, avsnitt.size());
    }
}
