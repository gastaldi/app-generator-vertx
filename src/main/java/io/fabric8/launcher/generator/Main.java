package io.fabric8.launcher.generator;

import io.fabric8.launcher.generator.handlers.ScriptHandler;
import io.fabric8.launcher.generator.verticles.FurnaceVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(FurnaceVerticle.class.getName());
        Router router = Router.router(vertx);
        // enable parsing of request bodies
        router.route().handler(BodyHandler.create());
        router.post("/api/forge/zip").blockingHandler(new ScriptHandler());
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
        System.out.println("Started!!");
    }


}

