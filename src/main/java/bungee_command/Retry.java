package bungee_command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import bungee.Database;
import bungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Retry
{
	public Main plugin;
	public Connection conn = null;
	public PreparedStatement ps = null;

	public Retry(CommandSender sender, String[] args)
	{
		if (sender instanceof ProxiedPlayer)
		{
			// プレイヤーがコマンドを実行した場合の処理
			ProxiedPlayer player = (ProxiedPlayer) sender;
			try
			{
				conn = Database.getConnection();
				
				// 6桁の乱数を生成
		        Random rnd = new Random();
		        int ranum = 100000 + rnd.nextInt(900000);
		        String ranumstr = Integer.toString(ranum);
		        
		        String sql = "UPDATE minecraft SET secret2=? WHERE uuid=?;";
		        ps = conn.prepareStatement(sql);
		        ps.setInt(1,ranum);
				ps.setString(2,player.getUniqueId().toString());
				ps.executeUpdate();
				
		        ComponentBuilder component =
	    			    new ComponentBuilder(ChatColor.GREEN+"認証コードを再生成しました。")
	    			    	.append(ChatColor.WHITE+"\n認証コードは ")
	    			    	.append(ChatColor.BLUE+ranumstr)
	    			    	.event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ranumstr))
	    			    	.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("(クリックして)コピー")))
	    			    	.append(ChatColor.WHITE+" です。");
		        
		        // BaseComponent[]に変換
				BaseComponent[] messageComponents = component.create();
				player.sendMessage(messageComponents);
			}
			catch (SQLException | ClassNotFoundException e)
	        {
	        	e.printStackTrace();
	        }
			finally
			{
				Database.close_resorce(null, conn, ps);
			}
		}
		else
		{
			sender.sendMessage(new TextComponent(ChatColor.RED+"このコマンドはプレイヤーのみが実行できます。"));
			return;
		}
	}
}