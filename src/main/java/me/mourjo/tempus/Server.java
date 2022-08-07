package me.mourjo.tempus;

import me.mourjo.tempus.routing.Router;
import me.mourjo.tempus.utils.Environment;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class Server {

    public static RatpackServer buildServer() {

        try {
/*
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
*/

            /*
            RatpackServer.start(cfg -> cfg
                    .serverConfig(c -> c.baseDir(BaseDir.find()))
                    .registry(Registry.of(r -> r.add(new TimeHandler()).add(new CountryHandler())))
                    .handlers(chain -> chain
                            .get("api/v1/countries/list", CountryHandler.class)
                            .get("api/v1/time", TimeHandler.class)
                            .get(ctx -> ctx.render(ctx.file("public/index.html")))
                            .files(f -> f.dir("public")))
                    .serverConfig(c -> c.port(Environment.getPort()))

            );
*/
            return RatpackServer.of(spec -> spec
                    .serverConfig(c -> c.baseDir(BaseDir.find()).port(Environment.getPort()))
                    .registry(Router.buildRegistry())
                    .handlers(new Router()));







                            //.handlers(chain -> new Router())



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
