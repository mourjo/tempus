package me.mourjo.tempus;

import me.mourjo.tempus.handlers.CountryHandler;
import me.mourjo.tempus.handlers.TimeHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.registry.Registry;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class Launcher {
    public static void main(String... args) throws Exception {

        try {
            var reg = Registry.of(r -> r.add(new TimeHandler()).add(new CountryHandler()));
            var s = RatpackServer.start(server ->
                    server
                            .serverConfig(c -> c.baseDir(BaseDir.find()))
                            .registry(reg)
                            .handlers(chain ->
                                    chain
                                            .get("api/v1/countries/list", CountryHandler.class)
                                            .get("api/v1/time", TimeHandler.class)
                                            .get(ctx -> ctx.render(ctx.file("public/index.html")))
                                            .files(f -> f.dir("public"))
                                            .register(r -> r.add(ServerErrorHandler.class, (context, throwable) -> context.render("api error: " + throwable.getMessage())))


                            ));


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

