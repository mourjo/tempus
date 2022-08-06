package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import io.netty.buffer.PooledByteBufAllocator;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.client.HttpClient;
import ratpack.server.ServerConfig;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class TimeHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        Gson gson = new Gson();
        var data = Map.of("data", Map.of("key1", "value1", "key2", "value"));
        HttpClient httpClient = HttpClient.of(httpClientSpec -> {
            httpClientSpec.poolSize(10)
                    .connectTimeout(Duration.of(60, ChronoUnit.SECONDS))
                    .maxContentLength(ServerConfig.DEFAULT_MAX_CONTENT_LENGTH)
                    .responseMaxChunkSize(16384)
                    .readTimeout(Duration.of(60, ChronoUnit.SECONDS))
                    .byteBufAllocator(PooledByteBufAllocator.DEFAULT);
        });
        httpClient.get(URI.create("http://api.timezonedb.com/v2.1/get-time-zone?key=3IEQDGPTOLJY&format=json&by=position&lat=35.6839&lng=139.7744"))
                .map(response -> response.getBody().getText())
                .then(response -> ctx.getResponse().send(response));
        //String json = gson.toJson(data);
        //ctx.getResponse().send(json);
    }
}
