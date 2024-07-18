package bungee_command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Cancel
{
	public Cancel(CommandSender sender, String[] args)
	{
		sender.sendMessage(new ComponentBuilder("キャンセルしました。").color(ChatColor.WHITE).create());
	}
}