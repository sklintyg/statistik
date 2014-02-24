package se.inera.statistics.service.report.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AvsnittTest {

    @Test
    public void test() {
        assertCodeInGroup("A00-A99", "A00", true);
        assertCodeInGroup("A00-A99", "A99", true);
        assertCodeInGroup("A00-A99", "A05", true);
        assertCodeInGroup("A00-B99", "A05", true);
        assertCodeInGroup("A00-B99", "B05", true);
        assertCodeInGroup("B00-B99", "A05", false);
        assertCodeInGroup("B00-B49", "B50", false);
        assertCodeInGroup("B00-B49", "B150", true);
    }

    private void assertCodeInGroup(String groupId, String code, boolean isInGroup) {
        Avsnitt avsnitt = new Avsnitt(groupId, "");
        assertEquals(isInGroup, avsnitt.isCodeInGroup(code));
    }

}
