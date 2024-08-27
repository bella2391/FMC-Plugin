package fabric;


import org.slf4j.Logger;

import com.google.inject.AbstractModule;

import fabric_command.CommandForward;
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
		LuckPerms luckperm, Config config
	)
	{
		this.fabric = fabric;
		this.logger = logger;
		this.server = server;
		this.config = config;
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
		bind(CommandForward.class);
		bind(Rcon.class);
		bind(CountdownTask.class);
    }
}
