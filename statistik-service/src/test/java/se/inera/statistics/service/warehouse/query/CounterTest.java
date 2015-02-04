package se.inera.statistics.service.warehouse.query;

import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.Sjukfall;

import static org.junit.Assert.*;

public class CounterTest {

    @Test
    public void testCompareTo1() throws Exception {
        //Given
        final Counter counter1 = new Counter("1");
        counter1.increase(createSjukfall(Kon.Female));
        final Counter counter2 = new Counter("2");
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));

        //When
        final int i = counter1.compareTo(counter2);

        //Then
        assertTrue(i > 0);
    }

    @Test
    public void testCompareTo2() throws Exception {
        //Given
        final Counter counter1 = new Counter("1");
        counter1.increase(createSjukfall(Kon.Female));
        final Counter counter2 = new Counter("2");
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));
        counter2.increase(createSjukfall(Kon.Male));

        //When
        final int i = counter2.compareTo(counter1);

        //Then
        assertTrue(i < 0);
    }

    @Test
    public void testCompareToSameEquals() throws Exception {
        //Given
        final Counter counter = new Counter("2");
        counter.increase(createSjukfall(Kon.Female));
        counter.increase(createSjukfall(Kon.Male));
        counter.increase(createSjukfall(Kon.Male));
        counter.increase(createSjukfall(Kon.Male));

        //When
        final int i = counter.compareTo(counter);

        //Then
        assertTrue(i == 0);
    }

    private Sjukfall createSjukfall(Kon kon) {
        final Sjukfall sjukfall = Mockito.mock(Sjukfall.class);
        Mockito.when(sjukfall.getKon()).thenReturn(kon);
        return sjukfall;
    }
}
