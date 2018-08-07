package io.fabric8.launcher.vertx;

import java.nio.file.Files;
import java.nio.file.Path;

import io.fabric8.launcher.util.Zip;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.jboss.forge.addon.script.ScriptOperations;
import org.jboss.forge.addon.ui.result.Result;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();
        try {
            ScriptOperations scriptOperations = FurnaceVerticle.furnace.getAddonRegistry().getServices(ScriptOperations.class).get();
            Path tempDirectory = Files.createTempDirectory("tmp");
            Result result = scriptOperations.executeScript(tempDirectory.toFile(), body, null, null);
            byte[] contents = Zip.zip("application", tempDirectory);
            Path tempFile = Files.createTempFile("project", ".zip");
            Files.write(tempFile, contents);
            HttpServerResponse response = routingContext.response();
            response.headers().set(HttpHeaders.CONTENT_TYPE, "application/zip");
            response.sendFile(tempFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
