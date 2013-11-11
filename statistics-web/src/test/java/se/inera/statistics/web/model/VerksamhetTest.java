package se.inera.statistics.web.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VerksamhetTest {

    @Test
    public void encodeEmptyId() {
        assertEquals("", Verksamhet.encodeId(""));
    }

    @Test
    public void encodeIdSimple() {
        assertEquals("Verksamhet1", Verksamhet.encodeId("Verksamhet1"));
    }

    @Test
    public void encodeId() {
        assertEquals("Verksamhet1_u002FStora_u0020huset", Verksamhet.encodeId("Verksamhet1/Stora huset"));
    }

    @Test
    public void encodeBackslash() {
        assertEquals("_u005C", Verksamhet.encodeId("\\"));
    }

    @Test
    public void encodeUnderscore() {
        assertEquals("_u005F", Verksamhet.encodeId("_"));
    }

    @Test
    public void encodeIdAllAllowed() {
        assertEquals("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-", Verksamhet.encodeId("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-"));
    }

    @Test
    public void encodeIdBorderCases() {
        assertEquals("_u0021_u002F_u003A_u0040_u005B_u0060_u007B", Verksamhet.encodeId("!/:@[`{"));
    }

    @Test
    public void decodeEmptyId() {
        assertEquals("", Verksamhet.decodeId(""));
    }

    @Test
    public void decodeIdSimple() {
        assertEquals("Verksamhet1", Verksamhet.decodeId("Verksamhet1"));
    }

    @Test
    public void decodeId() {
        assertEquals("Verksamhet1/Stora huset", Verksamhet.decodeId("Verksamhet1_u002FStora_u0020huset"));
    }

    @Test
    public void decodeIdAllAllowed() {
        assertEquals("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-", Verksamhet.decodeId("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-"));
    }

    @Test
    public void decodeIdBorderCases() {
        assertEquals("!/:@[`{", Verksamhet.decodeId("_u0021_u002F_u003A_u0040_u005B_u0060_u007B"));
    }

    @Test
    public void decodeBackslash() {
        assertEquals("\\", Verksamhet.decodeId("_u005C"));
    }

    @Test
    public void decodeUnderscore() {
        assertEquals("_", Verksamhet.decodeId("_u005F"));
    }
}
