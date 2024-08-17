package forge;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;

import net.luckperms.api.LuckPerms;
import net.minecraft.server.MinecraftServer;

public class ForgeModule extends AbstractModule 
{
	private final Logger logger;
	private final LuckPerms luckperm;
	private final Config config;
	private final MinecraftServer server;
	
	public ForgeModule
	(
		Logger logger, LuckPerms luckperm, Config config,
		MinecraftServer server
	)
	{
		this.logger = logger;
		this.luckperm = luckperm;
		this.config = config;
		this.server = server;
	}
	
	@Override
    protected void configure()
    {
		bind(Logger.class).toInstance(logger);
		bind(LuckPerms.class).toInstance(luckperm);
		bind(LuckPermUtil.class);
		bind(Config.class).toInstance(config);
		bind(Database.class);
		bind(ServerStatus.class);
		bind(Rcon.class);
		bind(MinecraftServer.class).toInstance(server);
    }
}
