package me.mourjo.tempus.handlers;

import com.google.gson.Gson;
import me.mourjo.tempus.models.LocationTranslator;
import me.mourjo.tempus.models.TimezoneDB;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import java.util.Map;


/**
 * Handler responsible for serving requests to convert city/country to its timezone
 */
public class TimeHandler implements Handler {
    private static final String badRequestResponse = "{\"status\":\"error\"}";
    private final Gson gson = new Gson();
    private final TimezoneDB tzModel = new TimezoneDB();

    @Override
    public void handle(Context ctx) {
        try {
            var params = ctx.getRequest().getQueryParams().getAll();
            if (!params.containsKey("city") ||
                    params.get("city").size() == 0 ||
                    !params.containsKey("country") ||
                    params.get("country").size() == 0) {
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

            // the model returns a promise, respond to the user when the promise is ready
            tzModel.getTimezones(locations)
                    .map(raw -> Map.of("status", "ok", "data", raw))
                    .then(map -> ctx.getResponse().send(gson.toJson(map)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
