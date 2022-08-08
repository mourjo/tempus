package me.mourjo.tempus.models;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationTranslatorTest {

    @Test
    public void countryCityLocationTest() {
        assertTrue(LocationTranslator.getLatLong("India", "Kolkata").contains(Location.of(22.5727, 88.3639)));
        assertTrue(LocationTranslator.getLatLong("United States", "Manhattan").contains(Location.of(41.4274, -87.9805)));
        assertTrue(LocationTranslator.getLatLong(" UnitEd States    ", " manhaTTAN       ").contains(Location.of(41.4274, -87.9805)));

        assertEquals(new HashSet<>(), LocationTranslator.getLatLong("India", "Calcutta"));
        assertEquals(new HashSet<>(), LocationTranslator.getLatLong("Trantor", "Terminus"));
        assertEquals(new HashSet<>(), LocationTranslator.getLatLong("United States of AMERICA", "Kolkata"));
    }

    @Test
    void globLocationTest() {
        assertEquals(new HashSet<>(), LocationTranslator.getLatLong("United States", "*athen"));
        assertEquals(new HashSet<>(), LocationTranslator.getLatLong("United States", "athe"));

        for (String glob : List.of("*athe*", "*ATHe*", "*aTHE*")) {
            var expected = Set.of(
                    Location.of(33.825, -117.3683),
                    Location.of(33.9508, -83.3689),
                    Location.of(34.7847, -86.951),
                    Location.of(39.3269, -82.0988),
                    Location.of(35.4573, -84.6045),
                    Location.of(32.2041, -95.8321),
                    Location.of(38.8832, -94.8198),
                    Location.of(32.7536, -97.7723),
                    Location.of(35.5384, -98.6872),
                    Location.of(33.8363, -116.4642),
                    Location.of(33.9235, -118.3033),
                    Location.of(37.4539, -122.2032));

            var actual = LocationTranslator.getLatLong("United States", glob);
            assertEquals(expected, actual);
        }
    }
}