package me.mourjo.tempus.routing;

import me.mourjo.tempus.handlers.CountryHandler;
import me.mourjo.tempus.handlers.TimeHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Handler;
import ratpack.registry.Registry;

public class Router implements Action<Chain> {
    private static Registry registry;
    private static final Handler timeHandler = new TimeHandler();
    private static final Handler countryHandler = new CountryHandler();

    public static Registry buildRegistry() {
        if (registry == null) {
            try {
                registry = Registry.of(r -> r.add(timeHandler).add(countryHandler));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return registry;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.get("api/v1/countries/list", CountryHandler.class);
        chain.get("api/v1/time", TimeHandler.class);
        chain.all(ctx -> ctx.render(ctx.file("public/index.html")));
        chain.files(f -> f.dir("public"));
    }
}
