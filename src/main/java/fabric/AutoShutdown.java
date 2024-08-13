package fabric;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AutoShutdown
{
	private final FabricLoader fabric;
    private final MinecraftServer server;
    private final Config config;
    private AutoShutdownTask task = null;
    
    @Inject
    public AutoShutdown
    (
    	FabricLoader fabric, MinecraftServer server, Config config
    )
    {
    	this.fabric = fabric;
    	this.server = server;
    	this.config = config;
    }
    
    public void startCheckForPlayers() 
    {
        if (!isAutoStopEnabled()) 
        {
            server.sendMessage(Text.literal("Auto-Stopはキャンセルされました。").formatted(Formatting.GREEN));
            return;
        }

        server.sendMessage(Text.literal("Auto-Stopが有効になりました。").formatted(Formatting.GREEN));

        long NO_PLAYER_THRESHOLD = getNoPlayerThreshold();

        task = new AutoShutdownTask(server, NO_PLAYER_THRESHOLD);
        task.start();
    }

    private boolean isAutoStopEnabled() {
        // Auto-Stopが有効かどうかをチェックするメソッド
        return config.getBoolean("AutoStop.Mode", false); // サーバーの設定から値を取得するように調整してください
    }

    private long getNoPlayerThreshold() {
        // 無人プレイヤーのしきい値を取得するメソッド
        return 3 * 60 * 20; // サーバーの設定から値を取得するように調整してください
    }

    public void stopCheckForPlayers() {
        if (Objects.nonNull(task) && !task.isCancelled()) {
            task.cancel();
        }
    }

    private static class AutoShutdownTask {

        private final MinecraftServer server;
        private final long noPlayerThreshold;
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        public AutoShutdownTask(MinecraftServer server, long noPlayerThreshold) {
            this.server = server;
            this.noPlayerThreshold = noPlayerThreshold;
        }

        public void start() {
            scheduler.scheduleAtFixedRate(() -> {
                if (server.getPlayerManager().getPlayerList().isEmpty()) {
                    server.sendMessage(Text.literal("プレイヤー不在のため、サーバーを5秒後に停止します。").formatted(Formatting.RED));
                    countdownAndShutdown(5);
                }
            }, noPlayerThreshold, noPlayerThreshold, TimeUnit.MILLISECONDS);
        }

        private void countdownAndShutdown(int seconds) {
            scheduler.scheduleAtFixedRate(new CountdownTask(server, seconds), 0, 1, TimeUnit.SECONDS);
        }

        public void cancel() {
            scheduler.shutdownNow();
        }

        public boolean isCancelled() {
            return scheduler.isShutdown();
        }
    }

    private static class CountdownTask implements Runnable {

        private final MinecraftServer server;
        private int countdown;

        public CountdownTask(MinecraftServer server, int seconds) {
            this.server = server;
            this.countdown = seconds;
        }

        @Override
        public void run() {
            if (countdown <= 0) {
                server.sendMessage(Text.literal("サーバーを停止します。"));
                server.shutdown();
            } else {
                server.sendMessage(Text.literal(String.valueOf(countdown)));
                countdown--;
            }
        }
    }
}
