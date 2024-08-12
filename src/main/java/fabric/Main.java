package fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Main implements ModInitializer
{
	private static Injector injector = null;
	private final FabricLoader fabric;
	private final Logger logger;
	private Config config;
	
	public Main()
	{
		this.fabric = FabricLoader.getInstance();
		this.logger = LoggerFactory.getLogger("FMC");
	}
	
    @Override
    public void onInitialize()
    {
        System.out.println("Hello, Fabric world!");
        injector = Guice.createInjector(new FabricModule(fabric, logger));
        
        this.config = getInjector().getInstance(Config.class);
        logger.info(config.getString("MySQL.Host"));
    }
    
    public static Injector getInjector()
    {
        return injector;
    }
}
