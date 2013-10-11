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
        assertEquals("Verksamhet1\\u002FStora\\u0020huset", Verksamhet.encodeId("Verksamhet1/Stora huset"));
    }

    @Test
    public void encodeIdAllAllowed() {
        assertEquals("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", Verksamhet.encodeId("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    @Test
    public void encodeIdBorderCases() {
        assertEquals("\\u0021\\u002F\\u003A\\u0040\\u005B\\u0060\\u007B", Verksamhet.encodeId("!/:@[`{"));
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
        assertEquals("Verksamhet1/Stora huset", Verksamhet.decodeId("Verksamhet1\\u002FStora\\u0020huset"));
    }

    @Test
    public void decodeIdAllAllowed() {
        assertEquals("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", Verksamhet.decodeId("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    @Test
    public void decodeIdBorderCases() {
        assertEquals("!/:@[`{", Verksamhet.decodeId("\\u0021\\u002F\\u003A\\u0040\\u005B\\u0060\\u007B"));
    }

}
