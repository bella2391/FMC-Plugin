package forge;

import org.slf4j.Logger;

import com.google.inject.Guice;
import com.mojang.brigadier.CommandDispatcher;

import forge_command.FMCCommand;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ServerModEvent 
{
	private static LuckPerms luckperm;
	private static final Logger logger = Main.logger;
	private static Config config;
	
	@SubscribeEvent
    public static void onServerStarting(ServerStartingEvent e)
	{
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
		Main.injector = Guice.createInjector(new ForgeModule(logger, luckperm, config));
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent e) 
    {
    	CommandDispatcher<CommandSourceStack> dispatcher = e.getDispatcher();
        new FMCCommand(logger).registerCommand(dispatcher);
        logger.info("FMC command registered.");
    }
}
