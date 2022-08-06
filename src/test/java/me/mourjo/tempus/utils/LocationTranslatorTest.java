package me.mourjo.tempus.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationTranslatorTest {

    @Test
    public void countryCityLocationTest() {
        assertEquals(Location.of(22.5727, 88.3639), LocationTranslator.getLatLong("India", "Kolkata").get());
        assertEquals(Location.of(41.4274, -87.9805), LocationTranslator.getLatLong("United States", "Manhattan").get());
        assertEquals(Location.of(41.4274, -87.9805), LocationTranslator.getLatLong(" UnitEd States    ", " manhaTTAN       ").get());

        assertEquals(Optional.empty(), LocationTranslator.getLatLong("India", "Calcutta"));
        assertEquals(Optional.empty(), LocationTranslator.getLatLong("Trantor", "Terminus"));
        assertEquals(Optional.empty(), LocationTranslator.getLatLong("United States of AMERICA", "Kolkata"));
    }
}