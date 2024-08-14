package fabric;

import java.io.IOException;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;

import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.minecraft.server.MinecraftServer;


public class FabricModule extends AbstractModule
{
	private final FabricLoader fabric;
	private final Config config;
	private final Logger logger;
	private final MinecraftServer server;
	private final LuckPerms luckperm;
	public FabricModule
	(
		FabricLoader fabric, Logger logger, MinecraftServer server, 
		LuckPerms luckperm
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
    	this.luckperm = luckperm;
	}
	
	@Override
    protected void configure()
    {
		bind(Config.class).toInstance(config);
		bind(FabricLoader.class).toInstance(fabric);
		bind(Logger.class).toInstance(logger);
		bind(MinecraftServer.class).toInstance(server);
		bind(AutoShutdown.class);
		bind(SocketSwitch.class);
		bind(ServerStatus.class);
		bind(LuckPerms.class).toInstance(luckperm);
		bind(LuckPermUtil.class);
    }
}
