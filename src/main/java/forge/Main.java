package forge;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "fmc";
    public static Injector injector = null;
    public static final Logger logger = LoggerFactory.getLogger("fmc");
    public static Config config = null;
    public static Path gameDir = null;
    public Path configDir = null;
    
    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

    	logger.info("THIS IS COMMON SETUP.");
        
        this.configDir = FMLPaths.CONFIGDIR.get();
        gameDir = configDir.getParent();
        Path modConfigDir = configDir.resolve(MODID);
        Main.config = new Config(logger, modConfigDir);
        try {
            config.loadConfig();
        } catch (IOException e1) {
            logger.error("Error loading config", e1);
        }
    }

    public static synchronized Injector getInjector() {
    	if (Objects.isNull(injector)) {
    		throw new IllegalStateException("Injector has not been initialized yet.");
    	}
    	
        return injector;
    }
    
    public static Config getConfig() {
    	return config;
    }
    
    public static Path getGameDir() {
    	return gameDir;
    }
}
