package forge;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;

import net.luckperms.api.LuckPerms;

public class ForgeModule extends AbstractModule 
{
	private final Logger logger;
	private final LuckPerms luckperm;
	private final Config config;
	
	public ForgeModule(Logger logger, LuckPerms luckperm, Config config)
	{
		this.logger = logger;
		this.luckperm = luckperm;
		this.config = config;
	}
	
	@Override
    protected void configure()
    {
		bind(Logger.class).toInstance(logger);
		bind(LuckPerms.class).toInstance(luckperm);
		bind(LuckPermUtil.class);
		bind(Config.class).toInstance(config);
    }
}
