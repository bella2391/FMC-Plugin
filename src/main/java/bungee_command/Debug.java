package bungee_command;

import bungee.Config;
import bungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class Debug
{
	public Main plugin;
	public String value1 = null, value2 = null;
	
	public Debug(CommandSender sender, String[] args)
	{
		this.plugin = Main.getInstance();
		
		if
		(
			!(Main.motdConfig.getConfig().getString("Debug.Webhook_URL","").isEmpty()) && 
			!(Main.motdConfig.getConfig().getString("Discord.Webhook_URL","").isEmpty())
		)
		{
			value1 = Main.motdConfig.getConfig().getString("Debug.Webhook_URL");
			value2 = Main.motdConfig.getConfig().getString("Discord.Webhook_URL");
			
			Main.motdConfig.getConfig().set("Discord.Webhook_URL", value1);
			Main.motdConfig.getConfig().set("Debug.Webhook_URL", value2);
			
			
			
			if(Main.motdConfig.getConfig().getBoolean("Debug.Mode"))
			{
				sender.sendMessage(new TextComponent(ChatColor.GREEN+"デバッグモードがOFFになりました。"));
				Main.motdConfig.getConfig().set("Debug.Mode", false);
				Main.motdConfig.save();
			}
			else
			{
				sender.sendMessage(new TextComponent(ChatColor.GREEN+"デバッグモードがONになりました。"));
				Main.motdConfig.getConfig().set("Debug.Mode", true);
				Main.motdConfig.save();
			}
			
			Main.motdConfig = new Config("bungee-config.yml",this.plugin);
		}
		else
		{
			sender.sendMessage(new TextComponent(ChatColor.RED+"コンフィグの設定が不十分です。"));
		}
	}
}
