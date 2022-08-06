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
}