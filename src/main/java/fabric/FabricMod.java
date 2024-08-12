package fabric;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

@SpringBootApplication
public class FabricMod implements ModInitializer
{
	private FabricLoader fabric;
	private Logger logger;
	private Config config;
	
	@Override
    public void onInitialize()
    {
		ApplicationContext context = SpringApplication.run(FabricApplication.class);
		this.config = context.getBean(Config.class);
        this.logger = context.getBean(Logger.class);
		this.fabric = context.getBean(FabricLoader.class);
        System.out.println("Hello, Fabric world!");
        
        logger.info(config.getString("MySQL.Host"));
    }
}
