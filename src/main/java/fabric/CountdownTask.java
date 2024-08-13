package fabric;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.MinecraftServer;

public class CountdownTask implements Runnable 
{
    private final MinecraftServer server;
    private final AtomicBoolean isShutdown;
    private final long delayMillis; // タイマーの遅延時間

    public CountdownTask(MinecraftServer server, AtomicBoolean isShutdown, long delayMillis) 
    {
        this.server = server;
        this.isShutdown = isShutdown;
        this.delayMillis = delayMillis;
    }

    @Override
    public void run() 
    {
        if (isShutdown.get()) return;

        long startTime = System.currentTimeMillis();
        while (true) 
        {
            // シャットダウンフラグが立っている場合は中断
            if (isShutdown.get()) return;

            // プレイヤーがいない場合にカウントダウンを開始
            if (server.getCurrentPlayerCount() == 0) 
            {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= delayMillis) 
                {
                    System.out.println("プレイヤー不在のため、サーバーを5秒後に停止します。");

                    try 
                    {
                        for (int i = 5; i > 0; i--) 
                        {
                            if (isShutdown.get()) return;
                            
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                    } 
                    catch (InterruptedException e) 
                    {
                        Thread.currentThread().interrupt();
                    }

                    // シャットダウンフラグを設定し、サーバーを停止
                    isShutdown.set(true);
                    System.out.println("サーバーを停止します。");
                    server.stop(false);
                }
            } 
            else 
            {
                // プレイヤーがいる場合はタイマーをリセット
                startTime = System.currentTimeMillis();
            }

            // 1秒待機してから再度チェック
            try 
            {
                Thread.sleep(1000);
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
