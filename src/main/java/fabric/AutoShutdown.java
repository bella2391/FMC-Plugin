package fabric;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class AutoShutdown 
{
    private final FabricLoader fabric;
    private final MinecraftServer server;
    private final Config config;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean isShutdown;

    @Inject
    public AutoShutdown(FabricLoader fabric, MinecraftServer server, Config config) 
    {
        this.fabric = fabric;
        this.server = server;
        this.config = config;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.isShutdown = new AtomicBoolean(false);
    }

    public void start() 
    {
    	if(config.getBoolean("AutoStop.Mode", false))
    	{
    		Long delayTime = config.getLong("AutoStop.Interval", 3) * 60 * 1000;
    		scheduler.scheduleAtFixedRate(new CountdownTask(server, isShutdown, delayTime), 0, 10, TimeUnit.SECONDS);
    	}
    }

    public void stop() 
    {
    	if(config.getBoolean("AutoStop.Mode", false))
    	{
    		scheduler.shutdownNow();
    	}
    }
}