package me.mourjo.tempus.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    public void cleanseTest() {
        assertEquals("this is a test", StringUtils.cleanse("This is a test"));
        assertEquals("this is a test", StringUtils.cleanse("this is a test"));
        assertEquals("this is a test", StringUtils.cleanse("  This is a     TEST!  "));
        assertEquals("this is a test", StringUtils.cleanse("this is a test "));
        assertEquals("this is a test", StringUtils.cleanse(" this is a test"));
        assertEquals("this is a test", StringUtils.cleanse("this is a teSt"));
        assertEquals("this is a test", StringUtils.cleanse("this is a teést"));
    }

    @Test
    public void globMatchTest() {
        assertEquals("^.*athe.*$", StringUtils.globToRegex("*athe*").toString());
        assertEquals("^.*athens.*$", StringUtils.globToRegex("*athens*").toString());
        assertEquals("^.*athens.*$", StringUtils.globToRegex("*ATHENS*").toString());

        assertTrue(StringUtils.globToRegex("athen*").matcher("athens").matches());
        assertFalse(StringUtils.globToRegex("athen*").matcher("rome").matches());
        assertTrue(StringUtils.globToRegex("*en*").matcher("athens").matches());
        assertTrue(StringUtils.globToRegex("athens").matcher("athens").matches());
        assertTrue(StringUtils.globToRegex("athenS").matcher("athens").matches());
    }
}
