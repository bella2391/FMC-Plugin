package forge;

import com.google.inject.Injector;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "fmc";
    public static Injector injector = null;
    public static final Logger logger = LogUtils.getLogger();
    public static Config config = null;
    public static Path gameDir = null;
    public Path configDir = null;
    
    public Main()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        logger.info("HELLO FROM COMMON SETUP");
        this.configDir = FMLPaths.CONFIGDIR.get();  // これが config ディレクトリの Path
        gameDir = configDir.getParent();
        Path modConfigDir = configDir.resolve(MODID); // MODID に基づくディレクトリ
        this.config = new Config(logger, modConfigDir);
        try
        {
            config.loadConfig(); // 一度だけロードする
        }
        catch (IOException e1)
        {
            logger.error("Error loading config", e1);
        }
    }

    public static synchronized Injector getInjector()
    {
    	if(Objects.isNull(injector))
    	{
    		throw new IllegalStateException("Injector has not been initialized yet.");
    	}
    	
        return injector;
    }
    
    public static Config getConfig()
    {
    	return config;
    }
    
    public static Path getGameDir()
    {
    	return gameDir;
    }
}
