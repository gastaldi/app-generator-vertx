package io.fabric8.launcher.vertx;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import io.fabric8.launcher.util.Zip;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.ScriptContextBuilder;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();
        try {
            ClassLoaders.executeIn(FurnaceVerticle.furnace.getRuntimeClassLoader(), () -> {
                try {
                    ResourceFactory resourceFactory = (ResourceFactory) FurnaceVerticle.furnace.getAddonRegistry().getServices(ResourceFactory.class.getName()).get();
                    ScriptEngineFactory scriptEngineFactory = (ScriptEngineFactory) FurnaceVerticle.furnace.getAddonRegistry().getServices(ForgeScriptEngineFactory.class.getName()).get();

                    ScriptEngine engine = scriptEngineFactory.getScriptEngine();
                    Path tempDirectory = Files.createTempDirectory("tmp");
                    Resource<File> resource = resourceFactory.create(tempDirectory.toFile());
                    ScriptContext context = ScriptContextBuilder.create()
                            .currentResource(resource)
                            .build();
                    engine.eval(body, context);
                    byte[] contents = Zip.zip("application", tempDirectory);
                    Path tempFile = Files.createTempFile("project", ".zip");
                    Files.write(tempFile, contents);
                    HttpServerResponse response = routingContext.response();
                    response.headers().set(HttpHeaders.CONTENT_TYPE, "application/zip");
                    response.sendFile(tempFile.toString()).end();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
