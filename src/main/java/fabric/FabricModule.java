package fabric;

import java.io.IOException;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;

import net.fabricmc.loader.api.FabricLoader;


public class FabricModule extends AbstractModule
{
	private final FabricLoader fabric;
	private final Config config;
	private final Logger logger;
	
	public FabricModule(FabricLoader fabric, Logger logger)
	{
		this.fabric = fabric;
		this.logger = logger;
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
    }
}
