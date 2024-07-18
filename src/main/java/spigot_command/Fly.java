package spigot_command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly
{
	public common.Main plugin;
	
	public Fly(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args)
	{
		this.plugin = common.Main.getInstance();
		
		if(!(sender instanceof Player))
  	  	{
  		  	sender.sendMessage(ChatColor.GREEN + "このプラグインはプレイヤーでなければ実行できません。");
  		  	return;
  	  	}
  	  	Player player = (Player) sender;
  	  	//we see sender is player, by doing so, it can substitute player variable for sender
  	  	player.setAllowFlight(true);
  	  	player.sendMessage(ChatColor.GREEN+"サバイバルで飛べるようになりました。");
  	  	return;
	}
}
