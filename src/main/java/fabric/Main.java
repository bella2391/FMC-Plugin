package fabric;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import fabric_command.FMCCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
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
	private AutoShutdown autoShutdown = null;
	private ServerStatus status;
	private FMCCommand fmcCommand;
	private LuckPerms luckperm;
	private Rcon rcon;
	
	public Main()
	{
		this.fabric = FabricLoader.getInstance();
		this.logger = LoggerFactory.getLogger("FMC");
	}
	
    @Override
    public void onInitialize()
    {
    	// サーバー起動後ではなく、MOD読み込み時に行う
    	this.config = new Config(fabric, logger);
    	try
        {
            config.loadConfig(); // 一度だけロードする
        }
        catch (IOException e1)
        {
            logger.error("Error loading config", e1);
        }
    	
    	CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> 
        {
            this.fmcCommand = new FMCCommand(logger);
            fmcCommand.registerCommand(dispatcher, registryAccess, environment);
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
            
            injector = Guice.createInjector(new FabricModule(fabric, logger, server, luckperm, config));
            
            System.out.println("Hello, Fabric world!");
            
            this.config = getInjector().getInstance(Config.class);
            logger.info(config.getString("MySQL.Host"));
            
            this.autoShutdown = getInjector().getInstance(AutoShutdown.class);
            autoShutdown.start();
            
            this.status = getInjector().getInstance(ServerStatus.class);
            status.doServerOnline();
            
            this.rcon = getInjector().getInstance(Rcon.class);
            rcon.startMCVC();
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
        	
        	rcon.stopMCVC();
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
