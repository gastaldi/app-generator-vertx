package io.fabric8.launcher.vertx;

import java.io.File;

import io.vertx.core.AbstractVerticle;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.impl.FurnaceImpl;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.util.AddonCompatibilityStrategies;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class FurnaceVerticle extends AbstractVerticle {

    public static Furnace furnace;

    @Override
    public void start() {
        furnace = new FurnaceImpl();
        furnace.addRepository(AddonRepositoryMode.IMMUTABLE, new File("/home/ggastald/workspace/forge-core/dist/target/forge-distribution-3.9.1-SNAPSHOT/addons"));
        furnace.setAddonCompatibilityStrategy(AddonCompatibilityStrategies.LENIENT);
        furnace.startAsync(getClass().getClassLoader());
        Projects.disableCache();
    }

    @Override
    public void stop() {
        furnace.stop();
    }
}
