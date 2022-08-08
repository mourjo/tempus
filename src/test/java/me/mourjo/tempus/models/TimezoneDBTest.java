package me.mourjo.tempus.models;

import org.junit.jupiter.api.Test;
import ratpack.test.exec.ExecHarness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimezoneDBTest {
    @Test
    public void getTimezonesTest() throws Exception {
        var locations = List.of(
                Location.of(22.5727, 88.3639) // Maps to "Kolkata"
        );

        var tzData = ExecHarness.yieldSingle(execution -> new TimezoneDB().getTimezones(locations)).getValueOrThrow();
        assertEquals(1, tzData.size());
        assertEquals("Asia/Kolkata", tzData.get(0).get("zoneName"));
        assertEquals("India", tzData.get(0).get("countryName"));
        assertEquals("Kolkata", tzData.get(0).get("cityName"));

    }
}