package io.fabric8.launcher.vertx;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import io.fabric8.launcher.util.Zip;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.ScriptContextBuilder;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();
        try {
            ResourceFactory resourceFactory = FurnaceVerticle.furnace.getAddonRegistry().getServices(ResourceFactory.class).get();
            ForgeScriptEngineFactory scriptEngineFactory = FurnaceVerticle.furnace.getAddonRegistry().getServices(ForgeScriptEngineFactory.class).get();

            ScriptEngine engine = scriptEngineFactory.getScriptEngine();
            Path tempDirectory = Files.createTempDirectory("tmp");
            DirectoryResource resource = resourceFactory.create(DirectoryResource.class, tempDirectory.toFile());
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
    }
}
