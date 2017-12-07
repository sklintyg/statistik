package se.inera.statistics.hsa.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class HsaIdAnyTest {

    @Test
    public void testNewConvertsToUpperAndRemovesBlanks() throws Exception {
        final HsaIdAny hsaIdAny = new HsaIdAny("asd f");
        assertEquals("ASDF", hsaIdAny.getId());
    }

}
