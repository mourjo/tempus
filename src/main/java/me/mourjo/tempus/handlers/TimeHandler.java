package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import me.mourjo.tempus.utils.*;
import ratpack.exec.Promise;
import ratpack.exec.util.ParallelBatch;
import ratpack.exec.util.SerialBatch;
import ratpack.exec.util.retry.AttemptRetryPolicy;
import ratpack.exec.util.retry.DurationRetryPolicy;
import ratpack.exec.util.retry.FixedDelay;
import ratpack.exec.util.retry.RetryPolicy;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;
import ratpack.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TimeHandler implements Handler {
    private static final String badRequestResponse = "{\"status\":\"error\"}";
    private static final Gson gson = new Gson();
    private static final String url = "http://api.timezonedb.com/v2.1/get-time-zone?key=%s&format=json&by=position&lat=%s&lng=%s&time=%s";
    private static HttpClient httpClient = null;

    @Override
    public void handle(Context ctx) {
        try {
            var unixTs = System.currentTimeMillis() / 1000L;
            var params = ctx.getRequest().getQueryParams().getAll();
            if (!params.containsKey("city") || params.get("city").size() == 0 ||
                    !params.containsKey("country") || params.get("country").size() == 0) {
                ctx.getResponse().status(Status.NOT_FOUND).send(badRequestResponse);
                return;
            }
            var city = params.get("city").get(0);
            var country = params.get("country").get(0);
            var locations = LocationTranslator.getLatLong(country, city);

            if (locations.isEmpty()) {
                ctx.getResponse().status(Status.NOT_FOUND).send(badRequestResponse);
                return;
            }

            List<Promise<Map<String, String>>> promises = new ArrayList<>();

            for (Location location : locations.subList(0, Math.min(15, locations.size()))) {
                promises.add(tzQuery(location, unixTs));
            }

            SerialBatch.of(promises)
                    .publisher()
                    .reduce(new ArrayList<Map<String, String>>(), (acc, item) -> {
                        acc.add(item);
                        return acc;
                    })
                    .map(raw -> Map.of("status", "ok", "data", raw))
                    .then(map -> ctx.getResponse().send(gson.toJson(map)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Promise<Map<String, String>> tzQuery(Location location, long unixTs) throws Exception {
        var uri = URI.create(String.format(url, Environment.apiKey(), location.latitude, location.longitude, unixTs));

        RetryPolicy retryPolicy = AttemptRetryPolicy.of(b -> b
                .delay(FixedDelay.of(Duration.ofSeconds(1)))
                .maxAttempts(3));

        return getHttpClient()
                .get(uri)
                .map(response -> response.getBody().getText())
                .map(txt -> (Map<String, String>) gson.fromJson(txt, StringUtils.gsonStringTypeToken()))
                .retry(retryPolicy, (x,i) -> {});
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClientUtils.buildHttpClient();
        }
        return httpClient;
    }
}
