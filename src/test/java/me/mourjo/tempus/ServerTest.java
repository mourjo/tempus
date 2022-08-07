package me.mourjo.tempus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

class ServerTest {
    public static Type typeTokenList() {
        return new TypeToken<Map<String, Object>>() {
        }.getType();
    }

    @Test
    public void landingPageTest() throws Exception {
        var server = EmbeddedApp.fromServer(Server.buildServer());

        for (String path : List.of("/", "/some-path-that-does-not-exist")) {
            server.test(httpClient -> {
                var response = httpClient.get(path);
                assertTrue(response.getBody().getText().contains("Time and Timezone Calculator"));
                assertEquals(Status.OK, response.getStatus());
            });
        }
    }

    @Test
    public void countriesTest() throws Exception {
        var server = EmbeddedApp.fromServer(Server.buildServer());
        Gson gson = new Gson();

        server.test(httpClient -> {
            var response = httpClient.get("api/v1/countries/list");
            assertEquals(Status.OK, response.getStatus());
            var countriesResponse = (Map<String, Object>) gson.fromJson(response.getBody().getText(), typeTokenList());
            assertEquals("ok", countriesResponse.get("status"));
            var countries = (List<String>) countriesResponse.get("data");
            assertTrue(countries.contains("India"));
            assertTrue(countries.contains("Sweden"));
            assertTrue(countries.contains("United States"));
        });
    }

    private void timezoneKolkata(TestHttpClient client, String requestedCity) {
        var gson = new Gson();
        String url = String.format("/api/v1/time?city=%s&country=India", requestedCity);
        var response = client.get(url);
        assertEquals(Status.OK, response.getStatus());

        var countriesResponse = (Map<String, Object>) gson.fromJson(response.getBody().getText(), typeTokenList());
        assertEquals("ok", countriesResponse.get("status"));

        var timezones = (List<Map<String, String>>) countriesResponse.get("data");
        assertTrue(15 >= timezones.size());
        assertEquals(1, timezones.size());
        assertEquals("Kolkata", timezones.get(0).get("cityName"));
        assertEquals("West Bengal", timezones.get(0).get("regionName"));
    }

    private void timezoneAthens(TestHttpClient client, String requestedCity, String responseCityMatchRegex) {
        var gson = new Gson();
        Pattern pattern = Pattern.compile(responseCityMatchRegex, Pattern.CASE_INSENSITIVE);
        String url = String.format("/api/v1/time?city=%s&country=United+States", requestedCity);

        var response = client.get(url);
        assertEquals(Status.OK, response.getStatus());

        var countriesResponse = (Map<String, Object>) gson.fromJson(response.getBody().getText(), typeTokenList());
        assertEquals("ok", countriesResponse.get("status"));

        var timezones = (List<Map<String, String>>) countriesResponse.get("data");
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

    @Test
    public void timezoneTest() throws Exception {
        try (var server = EmbeddedApp.fromServer(Server.buildServer())) {
            server.test(httpClient -> {
                timezoneKolkata(httpClient, "kolkata");
                timezoneKolkata(httpClient, "kolkata*");
                timezoneKolkata(httpClient, "*kolkata");
                timezoneKolkata(httpClient, "kol*ata");
                timezoneKolkata(httpClient, "*kolkata*");

                timezoneAthens(httpClient, "athens", "athens");
                timezoneAthens(httpClient, "athens*", "athens.*");
                timezoneAthens(httpClient, "*athens", ".*athens");
                timezoneAthens(httpClient, "*a*", ".*a.*");
            });
        }
    }
}