package se.inera.statistics.service.warehouse;

import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class AisleTest {

    private Aisle aisle = new Aisle();

    @Test
    public void outOfOrderFactsGetsSorted() {
        Fact fact1 = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(1).
                withPatient(1).withKon(Kon.Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(4010).withSjukskrivningslangd(47).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefatttning(201010).withLakarid(1).build();
        aisle.addLine(fact1);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(2).
                withPatient(1).withKon(Kon.Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(4000).withSjukskrivningslangd(47).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefatttning(201010).withLakarid(1).build();
        aisle.addLine(fact2);
        aisle.sort();
        Iterator<Fact> iterator = aisle.iterator();
        assertEquals(2, iterator.next().getLakarintyg());
        assertEquals(1, iterator.next().getLakarintyg());
    }
}
