package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import me.mourjo.tempus.utils.Environment;
import me.mourjo.tempus.utils.HttpClientUtils;
import me.mourjo.tempus.utils.LocationTranslator;
import me.mourjo.tempus.utils.StringUtils;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;
import ratpack.http.client.HttpClient;

import java.net.URI;
import java.util.Map;

public class TimeHandler implements Handler {
    private static final String badRequestResponse = "{\"status:\":\"error\"}";
    private static final Gson gson = new Gson();
    private static final String url = "http://api.timezonedb.com/v2.1/get-time-zone?key=%s&format=json&by=position&lat=%s&lng=%s";
    private static HttpClient httpClient = null;

    @Override
    public void handle(Context ctx) {
        var params = ctx.getRequest().getQueryParams().getAll();
        if (!params.containsKey("city") || params.get("city").size() == 0 ||
                !params.containsKey("country") || params.get("country").size() == 0) {
            ctx.getResponse().status(Status.NOT_FOUND).send(badRequestResponse);
            return;
        }
        var city = params.get("city").get(0);
        var country = params.get("country").get(0);
        var maybeLocation = LocationTranslator.getLatLong(country, city);

        if (maybeLocation.isEmpty()) {
            ctx.getResponse().status(Status.NOT_FOUND).send(badRequestResponse);
            return;
        }

        var location = maybeLocation.get();
        var uri = URI.create(String.format(url, Environment.apiKey(), location.latitude, location.longitude));

        getHttpClient()
                .get(uri)
                .map(response -> response.getBody().getText())
                .map(txt -> gson.fromJson(txt, StringUtils.gsonStringTypeToken()))
                .map(jsonData -> Map.of("status", "ok", "data", jsonData))
                .then(map -> ctx.getResponse().send(gson.toJson(map)));
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClientUtils.buildHttpClient();
        }
        return httpClient;
    }
}
