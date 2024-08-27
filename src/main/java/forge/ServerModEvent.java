package forge;

import org.slf4j.Logger;

import com.google.inject.Guice;
import com.mojang.brigadier.CommandDispatcher;

import forge.Module;
import forge_command.FMCCommand;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ServerModEvent 
{
	private static MinecraftServer server;
	private static LuckPerms luckperm;
	private static final Logger logger = Main.logger;
	private static Config config;
	private static ServerStatus status;
	private static Rcon rcon;
	private static AutoShutdown autoshutdown;
	
	@SubscribeEvent
    public static void onServerStarting(ServerStartingEvent e)
	{
		server = e.getServer();
		
		try 
        {
            luckperm = LuckPermsProvider.get();
        } 
        catch (IllegalStateException e1) 
        {
            logger.error("LuckPermsが見つかりませんでした。");
            return;
        }
		
		config = Main.getConfig();
		Main.injector = Guice.createInjector(new Module(logger, luckperm, config, server));
		
		
		
		status = Main.getInjector().getInstance(ServerStatus.class);
		status.doServerOnline();
		
		rcon = Main.getInjector().getInstance(Rcon.class);
		rcon.startMCVC();
		
		autoshutdown = Main.getInjector().getInstance(AutoShutdown.class);
		autoshutdown.start();
    }
	
	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent e)
	{
		status.doServerOffline();
		
		rcon.stopMCVC();
		
		autoshutdown.stop();
	}
	
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent e) 
    {
    	CommandDispatcher<CommandSourceStack> dispatcher = e.getDispatcher();
    	//CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        new FMCCommand(logger).registerCommand(dispatcher);
        logger.info("FMC command registered.");
    }
}
