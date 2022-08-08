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
import ratpack.func.BiAction;
import ratpack.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimezoneDB {
    private static final String url = "http://api.timezonedb.com/v2.1/get-time-zone?key=%s&format=json&by=position&lat=%s&lng=%s&time=%s";
    private static final Gson gson = new Gson();
    private HttpClient httpClient = null;

    public Promise<List<Map<String, String>>> getTimezones(Collection<Location> locations) throws Exception {
        var unixTs = System.currentTimeMillis() / 1000L;
//        List<Promise<Map<String, String>>> promises = new ArrayList<>();
//
//        for (Location location : locations) {
//            promises.add(timezoneInfo(location, unixTs));
//        }

        var promises = locations.stream().map(location -> timezoneInfo(location, unixTs)).collect(Collectors.toList());

        return SerialBatch.of(promises).publisher().toList();
    }

    private Promise<Map<String, String>> timezoneInfo(Location location, long unixTs) {
        var uri = URI.create(String.format(url, Environment.apiKey(), location.latitude, location.longitude, unixTs));

        try {
            return getHttpClient()
                    .get(uri)
                    .map(response -> response.getBody().getText())
                    .map(txt -> (Map<String, String>) gson.fromJson(txt, StringUtils.gsonStringTypeToken()))
                    .map(result -> {
                        result.putIfAbsent("originalCity", location.city);
                        return result;
                    })
                    .retry(retryPolicy(), BiAction.noop());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClientUtils.buildHttpClient();
        }
        return httpClient;
    }

    private RetryPolicy retryPolicy() throws Exception {
        return AttemptRetryPolicy.of(builder -> builder
                .delay(FixedDelay.of(Duration.ofSeconds(1)))
                .maxAttempts(3));
    }
}
