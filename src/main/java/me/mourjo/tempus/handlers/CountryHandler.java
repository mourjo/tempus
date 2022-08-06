package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import me.mourjo.tempus.utils.LocationTranslator;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.util.Map;

public class CountryHandler implements Handler {
    private static final Gson gson = new Gson();

    @Override
    public void handle(Context ctx) {
        var data = Map.of("status", "ok", "data", LocationTranslator.getAllCountries());
        ctx.getResponse().send(gson.toJson(data));
    }
}
