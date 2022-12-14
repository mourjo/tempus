package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.mourjo.tempus.Server;
import org.junit.jupiter.api.Test;
import ratpack.http.Status;
import ratpack.test.embed.EmbeddedApp;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountryHandlerTest {

    private static Type typeTokenList() {
        return new TypeToken<Map<String, Object>>() {
        }.getType();
    }

    @Test
    public void landingPageTest() throws Exception {
        try (var server = EmbeddedApp.fromServer(Server.buildServer())) {

            for (String path : List.of("/", "/some-path-that-does-not-exist")) {
                server.test(httpClient -> {
                    var response = httpClient.get(path);
                    assertTrue(response.getBody().getText().contains("Time and Timezone Calculator"));
                    assertEquals(Status.OK, response.getStatus());
                });
            }
        }
    }

    @Test
    public void countriesTest() throws Exception {
        try (var server = EmbeddedApp.fromServer(Server.buildServer())) {
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
    }
}