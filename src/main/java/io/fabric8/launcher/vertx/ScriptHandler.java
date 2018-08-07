package io.fabric8.launcher.vertx;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext routingContext) {
        ScriptEngineFactory scriptEngineFactory = (ScriptEngineFactory) FurnaceVerticle.furnace.getAddonRegistry().getServices(ForgeScriptEngineFactory.class.getName()).get();
        try {
            ScriptEngine engine = scriptEngineFactory.getScriptEngine();
            routingContext.response().end("" + engine.eval("pwd"));
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }
}
