package fabric;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fabric_command.FMCCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Main implements ModInitializer
{
	private static Injector injector = null;
	private FabricLoader fabric;
	private Logger logger;
	private Config config;
	private MinecraftServer server;
	private AutoShutdown autoShutdown = null;
	private ServerStatus status;
	private FMCCommand fmcCommand;
	private LuckPerms luckperm;
	
	public Main()
	{
		this.fabric = FabricLoader.getInstance();
		this.logger = LoggerFactory.getLogger("FMC");
	}
	
    @Override
    public void onInitialize()
    {
    	CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> 
        {
            this.fmcCommand = new FMCCommand();
            
            dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("fmc")
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("reload")
                            .executes(context -> fmcCommand.execute(context, "reload")))
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("potion")
                            .then(CommandManager.argument("effect", StringArgumentType.string())
                                    .executes(context -> fmcCommand.execute(context, "potion"))))
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("medic")
                            .executes(context -> fmcCommand.execute(context, "medic")))
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("fly")
                            .executes(context -> fmcCommand.execute(context, "fly")))
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("test")
                            .then(CommandManager.argument("arg", StringArgumentType.string())
                                    .executes(context -> fmcCommand.execute(context, "test"))))
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("fv")
                            .executes(context -> fmcCommand.execute(context, "fv")))
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("mcvc")
                            .executes(context -> fmcCommand.execute(context, "mcvc")))
            );
            
            System.out.println("Command registered: ");
        });
    	
    	// サーバーが起動したときに呼ばれるイベントフック
        ServerLifecycleEvents.SERVER_STARTED.register(server -> 
        {
            this.server = server;
            
            try 
            {
                this.luckperm = LuckPermsProvider.get();
            } 
            catch (IllegalStateException e) 
            {
                System.err.println("LuckPermsが見つかりませんでした。");
                return;
            }
            
            injector = Guice.createInjector(new FabricModule(fabric, logger, server, luckperm));
            
            System.out.println("Hello, Fabric world!");
            
            this.config = getInjector().getInstance(Config.class);
            logger.info(config.getString("MySQL.Host"));
            
            this.autoShutdown = getInjector().getInstance(AutoShutdown.class);
            autoShutdown.start();
            
            this.status = getInjector().getInstance(ServerStatus.class);
            status.doServerOnline();
        });
        
        // ServerLifecycleEvents.SERVER_STOPPING イベントでタスクを停止
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> 
        {
        	server.sendMessage(Text.literal("サーバーが停止中です...").formatted(Formatting.RED));
        	
        	if(Objects.nonNull(autoShutdown))
        	{
        		autoShutdown.stop();
        	}
        	
        	status.doServerOffline();
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
