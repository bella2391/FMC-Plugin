package fabric;

import java.io.IOException;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;


public class FabricModule extends AbstractModule
{
	private final FabricLoader fabric;
	private final Config config;
	private final Logger logger;
	private final MinecraftServer server;
	
	public FabricModule
	(
		FabricLoader fabric, Logger logger, MinecraftServer server
	)
	{
		this.fabric = fabric;
		this.logger = logger;
		this.server = server;
		this.config = new Config(fabric, logger);
    	try
        {
            config.loadConfig(); // 一度だけロードする
        }
        catch (IOException e1)
        {
            logger.error("Error loading config", e1);
        }
	}
	
	@Override
    protected void configure()
    {
		bind(Config.class).toInstance(config);
		bind(FabricLoader.class).toInstance(fabric);
		bind(Logger.class).toInstance(logger);
		bind(MinecraftServer.class).toInstance(server);
		bind(AutoShutdown.class);
    }
}
