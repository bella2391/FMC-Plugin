package fabric;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Main implements ModInitializer
{
	private static Injector injector = null;
	private FabricLoader fabric;
	private Logger logger;
	private Config config;
	private MinecraftServer server;
	
	public Main()
	{
		this.fabric = FabricLoader.getInstance();
		this.logger = LoggerFactory.getLogger("FMC");
	}
	
    @Override
    public void onInitialize()
    {
    	// サーバーが起動したときに呼ばれるイベントフック
        ServerLifecycleEvents.SERVER_STARTED.register(server -> 
        {
            this.server = server;
            
            injector = Guice.createInjector(new FabricModule(fabric, logger, server));
            
            System.out.println("Hello, Fabric world!");
            
            
            this.config = getInjector().getInstance(Config.class);
            logger.info(config.getString("MySQL.Host"));
            
            getInjector().getInstance(AutoShutdown.class).startCheckForPlayers();
        });
    }
    
    public static synchronized Injector getInjector()
    {
    	if(Objects.isNull(injector))
    	{
    		throw new IllegalStateException("Injector has not been initialized yet.");
    	}
    	
        return injector;
    }
}
