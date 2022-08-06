package me.mourjo.tempus;

import me.mourjo.tempus.handlers.TimeHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.http.MutableHeaders;
import ratpack.http.Status;
import ratpack.registry.Registry;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class Launcher {
    public static void main(String... args) throws Exception {

        try {
            var reg = Registry.of(r -> r.add(new TimeHandler()));
            var s = RatpackServer.start(server ->
                    server
                            .serverConfig(c -> c.baseDir(BaseDir.find()))
                            .registry(reg)
                            .handlers(chain ->
                                    chain
                                            .get("oopsie", ctx -> ctx.getResponse().contentType("application/json").status(Status.OK).send("{\"data\":[\"v1\",\"v2\",\"v33\"]}"))
                                            .get("time", TimeHandler.class)
                                            .get(ctx -> ctx.render(ctx.file("public/index.html")))
                                            .files(f -> f.dir("public"))
                                            .register(r -> r.add(ServerErrorHandler.class, (context, throwable) -> context.render("api error: " + throwable.getMessage())))


                            ));


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

