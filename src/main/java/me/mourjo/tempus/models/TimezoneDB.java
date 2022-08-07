package me.mourjo.tempus.models;

import com.google.gson.Gson;
import me.mourjo.tempus.utils.Environment;
import me.mourjo.tempus.utils.HttpClientUtils;
import me.mourjo.tempus.utils.StringUtils;
import ratpack.exec.Promise;
import ratpack.exec.util.SerialBatch;
import ratpack.exec.util.retry.AttemptRetryPolicy;
import ratpack.exec.util.retry.FixedDelay;
import ratpack.exec.util.retry.RetryPolicy;
import ratpack.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimezoneDB {
    static final String url = "http://api.timezonedb.com/v2.1/get-time-zone?key=%s&format=json&by=position&lat=%s&lng=%s&time=%s";
    static final Gson gson = new Gson();
    static HttpClient httpClient = null;

    public static Promise<List<Map<String, String>>> getTimezones(List<Location> locations) throws Exception {
        var unixTs = System.currentTimeMillis() / 1000L;
        List<Promise<Map<String, String>>> promises = new ArrayList<>();

        for (Location location : locations.subList(0, Math.min(15, locations.size()))) {
            promises.add(tzQuery(location, unixTs));
        }

        return SerialBatch.of(promises)
                .publisher()
                .reduce(new ArrayList<>(), (acc, item) -> {
                    acc.add(item);
                    return acc;
                });
    }

    private static Promise<Map<String, String>> tzQuery(Location location, long unixTs) throws Exception {
        var uri = URI.create(String.format(url, Environment.apiKey(), location.latitude, location.longitude, unixTs));

        RetryPolicy retryPolicy = AttemptRetryPolicy.of(builder -> builder
                .delay(FixedDelay.of(Duration.ofSeconds(1)))
                .maxAttempts(3));

        return getHttpClient()
                .get(uri)
                .map(response -> response.getBody().getText())
                .map(txt -> (Map<String, String>) gson.fromJson(txt, StringUtils.gsonStringTypeToken()))
                .map(result -> {
                    result.putIfAbsent("originalCity", location.city);
                    return result;
                })
                .retry(retryPolicy, (x, i) -> {
                });
    }

    private static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClientUtils.buildHttpClient();
        }
        return httpClient;
    }
}
