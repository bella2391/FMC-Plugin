package bungee_command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bungee.Database;
import bungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.chat.TextComponent;

public class ServerTeleport
{
	public Main plugin;
	public Connection conn = null;
	public ResultSet minecrafts = null;
	public ResultSet[] resultsets = {minecrafts};
	public PreparedStatement ps = null;
	
	public ServerTeleport(CommandSender sender, String[] args)
	{
		this.plugin = Main.getInstance();
		
		if (sender instanceof ProxiedPlayer)
		{
            // プレイヤーがコマンドを実行した場合の処理
			ProxiedPlayer player = (ProxiedPlayer) sender;
        
            if(args.length == 1 || Objects.isNull(args[1]) || args[1].isEmpty())
            {
            	player.sendMessage(new TextComponent(ChatColor.RED+"サーバー名を入力してください。"));
            	return;
            }
            boolean containsServer = false;
            for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values())
            {
            	if(serverInfo.getName().equalsIgnoreCase(args[1]))
            	{
            		containsServer = true;
            		break;
            	}
            }
            if(!containsServer)
            {
            	player.sendMessage(new TextComponent(ChatColor.RED+"サーバー名が違います。"));
            	this.plugin.getLogger().info(ChatColor.RED+"サーバー名が違います。");
            	return;
            }
            
            try
            {
            	conn = Database.getConnection();
            	String sql = "SELECT * FROM minecraft WHERE uuid=?;";
    			ps = conn.prepareStatement(sql);
    			ps.setString(1,player.getUniqueId().toString());
    			ResultSet minecrafts = ps.executeQuery();
    			if(minecrafts.next())
    			{
    				long now_timestamp = Instant.now().getEpochSecond();
					
					// /ssで発行したセッションタイムをみる
	                Timestamp sst_timeget = minecrafts.getTimestamp("sst");
	                long sst_timestamp = sst_timeget.getTime() / 1000L;
					
					long ss_sa = now_timestamp-sst_timestamp;
					long ss_sa_minute = ss_sa/60;
					if(ss_sa_minute>3)
					{
						player.sendMessage(new TextComponent(ChatColor.RED+"セッションが無効です。"));
						return;
					}
    			}
    			else
    			{
    				// MySQLサーバーにサーバーが登録されてなかった場合
    				this.plugin.getLogger().info(ChatColor.RED+"このサーバーは、データベースに登録されていません。");
    				player.sendMessage(new TextComponent(ChatColor.RED+"このサーバーは、データベースに登録されていません。"));
    				return;
    			}
            }
            catch (SQLException | ClassNotFoundException e)
            {
            	e.printStackTrace();
            }
            finally
            {
            	Database.close_resorce(resultsets, conn, ps);
            }
            
            ServerInfo target = ProxyServer.getInstance().getServerInfo(args[1].toString());
            player.connect(target);
        }
		else
		{
			sender.sendMessage(new TextComponent(ChatColor.RED+"このコマンドはプレイヤーのみが実行できます。"));
			return;
		}
        return;
	}
}