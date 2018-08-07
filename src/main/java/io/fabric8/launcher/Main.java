package io.fabric8.launcher;

import io.fabric8.launcher.vertx.FurnaceVerticle;
import io.fabric8.launcher.vertx.ScriptHandler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Main {

    public static void main(String[] args) {
        final VertxOptions vertOptions = new VertxOptions()
                .setMaxEventLoopExecuteTime(3000000000L)
                .setMaxWorkerExecuteTime(3000000000L);
        Vertx vertx = Vertx.vertx(vertOptions);
        vertx.deployVerticle(FurnaceVerticle.class.getName());
        Router router = Router.router(vertx);
        // enable parsing of request bodies
        router.route().handler(BodyHandler.create());
        router.post("/api/forge/zip")
                .handler(new ScriptHandler());
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
        System.out.println("Started!!");
    }


}

