package me.mourjo.tempus.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationTest {
    @Test
    public void locationEqualityCheck() {
        Map<Location, String> map = new HashMap<>();
        map.put(Location.of(1, 2), "1,-1");
        map.put(Location.of(1, 3), "1,3");
        map.put(Location.of(1, 2), "1,2");

        assertEquals("1,2", map.get(Location.of(1, 2)));

        List<Location> locations = new ArrayList<>(List.of(Location.of(1, 2), Location.of(1, 2), Location.of(1, 2)));
        locations = locations.stream().distinct().collect(Collectors.toList());
        assertEquals(List.of(Location.of(1, 2)), locations);
    }
}