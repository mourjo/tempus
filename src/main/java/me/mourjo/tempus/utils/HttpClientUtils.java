package me.mourjo.tempus.utils;

import ratpack.http.client.HttpClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class HttpClientUtils {
    public static HttpClient buildHttpClient() {
        try {
            return HttpClient.of(config -> config
                    .poolSize(3)
                    .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
                    .readTimeout(Duration.of(5, ChronoUnit.SECONDS)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
