package io.fabric8.launcher.generator.verticles;

import java.io.File;

import io.vertx.core.AbstractVerticle;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.FurnaceFactory;
import org.jboss.forge.furnace.util.AddonCompatibilityStrategies;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class FurnaceVerticle extends AbstractVerticle {

    public static Furnace furnace;

    @Override
    public void start() {
        furnace = FurnaceFactory.getInstance(getClass().getClassLoader());
        furnace.addRepository(AddonRepositoryMode.IMMUTABLE, new File("target/addons"));
        furnace.setAddonCompatibilityStrategy(AddonCompatibilityStrategies.LENIENT);
        furnace.startAsync();
        Projects.disableCache();
    }

    @Override
    public void stop() {
        furnace.stop();
    }
}
