package se.inera.statistics.hsa.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HsaIdAnyTest {

    @Test
    public void testNewConvertsToUpperAndRemovesBlanks() throws Exception {
        final HsaIdAny hsaIdAny = new HsaIdAny("asd f");
        assertEquals("ASDF", hsaIdAny.getId());
    }

}
