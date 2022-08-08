package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.mourjo.tempus.Server;
import org.junit.jupiter.api.Test;
import ratpack.http.Status;
import ratpack.test.embed.EmbeddedApp;
import ratpack.test.http.TestHttpClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeHandlerTest {

    private static Type typeTokenList() {
        return new TypeToken<Map<String, Object>>() {
        }.getType();
    }

    @Test
    public void timezoneTest() throws Exception {
        try (var server = EmbeddedApp.fromServer(Server.buildServer())) {
            server.test(httpClient -> {
                locationSpecificTzResultsMatch(httpClient, "kolkata");
                locationSpecificTzResultsMatch(httpClient, "kolkata*");
                locationSpecificTzResultsMatch(httpClient, "*kolkata");
                locationSpecificTzResultsMatch(httpClient, "kol*ata");
                locationSpecificTzResultsMatch(httpClient, "*kolkata*");

                cityTzResultsMatch(httpClient, "athens", "athens");
                cityTzResultsMatch(httpClient, "athens*", "athens.*");
                cityTzResultsMatch(httpClient, "*athens", ".*athens");
                cityTzResultsMatch(httpClient, "*a*", ".*a.*");
            });
        }
    }

    private void locationSpecificTzResultsMatch(TestHttpClient client, String requestedCity) {
        var gson = new Gson();
        String url = String.format("/api/v1/time?city=%s&country=India", requestedCity);
        var response = client.get(url);
        assertEquals(Status.OK, response.getStatus());

        var tzResponse = (Map<String, Object>) gson.fromJson(response.getBody().getText(), typeTokenList());
        assertEquals("ok", tzResponse.get("status"));

        var timezones = (List<Map<String, String>>) tzResponse.get("data");
        assertTrue(15 >= timezones.size());
        assertEquals(1, timezones.size());
        assertEquals("Kolkata", timezones.get(0).get("cityName"));
        assertEquals("West Bengal", timezones.get(0).get("regionName"));
    }

    private void cityTzResultsMatch(TestHttpClient client, String requestedCity, String responseCityMatchRegex) {
        var gson = new Gson();
        Pattern pattern = Pattern.compile(responseCityMatchRegex, Pattern.CASE_INSENSITIVE);
        String url = String.format("/api/v1/time?city=%s&country=United+States", requestedCity);

        var response = client.get(url);
        assertEquals(Status.OK, response.getStatus());

        var tzResponse = (Map<String, Object>) gson.fromJson(response.getBody().getText(), typeTokenList());
        assertEquals("ok", tzResponse.get("status"));

        var timezones = (List<Map<String, String>>) tzResponse.get("data");
        assertTrue(15 >= timezones.size());
        for (Map<String, String> tzInfo : timezones) {
            String cityName = tzInfo.get("cityName");
            String regionName = tzInfo.get("regionName");
            String originalCity = tzInfo.get("originalCity");

            boolean cityMatches = pattern.matcher(cityName).matches();
            boolean regionMatches = pattern.matcher(regionName).matches();
            boolean originalCityMatches = pattern.matcher(originalCity).matches();

            assertTrue(cityMatches || regionMatches || originalCityMatches,
                    String.format("Response (%s / %s / %s) did not match %s",
                            cityName,
                            regionName,
                            originalCity,
                            responseCityMatchRegex));
        }
    }
}