package me.mourjo.tempus;

import me.mourjo.tempus.routing.Router;
import me.mourjo.tempus.utils.Environment;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class Server {
    public static RatpackServer buildServer() {
        try {
            return RatpackServer.of(spec -> spec
                    .serverConfig(c -> c.baseDir(BaseDir.find()).port(Environment.port()))
                    .registry(Router.buildRegistry())
                    .handlers(new Router()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
