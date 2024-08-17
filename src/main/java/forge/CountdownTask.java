package forge;

import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
            if (server.getPlayerCount() == 0) 
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
                    //server.stopServer();
                    // コマンドを実行するためのコマンドディスパッチャーを取得
                    CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
                    CommandSourceStack source = server.createCommandSourceStack();
                    try
                    {
						dispatcher.execute("stop", source);
					}
                    catch (CommandSyntaxException e) 
                    {
						e.printStackTrace();
					}
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
