package bungee_command;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bungee.Config;
import bungee.Main;
import bungee.PlayerList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class FMCCommand extends Command implements TabExecutor
{
	public Main plugin;
	public List<String> subcommands = new ArrayList<>(Arrays.asList("debug","hub","reload","ss","req","start","stp","retry","debug","cancel","perm"));
	public List<String> anylists = new ArrayList<>(Arrays.asList("true","false"));
	
    public FMCCommand(Main plugin) {
        super("fmcp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length == 0 || !subcommands.contains(args[0].toLowerCase()))
        {
        	if(sender.hasPermission("fmc.proxi.commandslist"))
        	{
        		BaseComponent[] component =
        			    new ComponentBuilder(ChatColor.YELLOW+"FMC COMMANDS LIST").bold(true).underlined(true)
        			    	.append(ChatColor.AQUA+"\n/hub")
        			    	.append(ChatColor.AQUA+"\n/fmcp hub")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp hub" ))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("ホームサーバーに帰還")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp ss <server>")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp ss "))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("サーバーステータス取得")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp start <server>")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp start "))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("サーバーを起動")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp req <server>")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp req"))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("サーバー起動リクエスト")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp cancel")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp cancel"))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("「キャンセルしました」メッセージ")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp debug")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp debug"))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("管理者専用デバッグモード")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp reload")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp reload"))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("コンフィグ、リロード")))
        			        .append(ChatColor.AQUA+"\n\n/fmcp perm <add|remove|list> [Short:permission] <player>")
        			        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/fmcp perm "))
        			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("ユーザーに対して権限の追加と除去")))
        			        .create();
        		sender.sendMessage(component);
        	}
        	else
        	{
        		sender.sendMessage(new TextComponent(ChatColor.RED+"You do not have permission for fmc commands."));
        	}
            return;
        }

        String subCommand = args[0];

        switch (subCommand.toLowerCase())
        {
            case "debug":
                new Debug(sender, args);
                break;
                
            case "start":
            	new StartServer(sender, args);
            	break;
            
            case "ss":
            	new SetServer(sender, args);
                break;
            	
            case "hub":
            	new Hub().execute(sender, args);
            	break;

            case "retry":
            	new Retry(sender, args);
            	break;
            
            case "reload":
            	new ReloadConfig(sender, args);
            	break;
            	
            case "stp":
            	new ServerTeleport(sender, args);
            	break;
            	
            case "req":
            	new Request(sender, args);
            	break;
            	
            case "cancel":
            	new Cancel(sender,args);
            	break;
            	
            case "perm":
            	new Perm(sender,args);
            	break;
            	
            default:
                sender.sendMessage(new TextComponent("Unknown subcommand: " + subCommand));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
    	List<String> ret = new ArrayList<>();
        
        switch (args.length)
    	{
	    	case 1:
	    		for (String subcmd : subcommands)
	    		{
	    			if (!sender.hasPermission("fmc.proxi." + subcmd)) continue;
	      
	    			ret.add(subcmd);
	    		}
	    		return ret;
	    	
	    	case 2:
	    		
	    		if (!sender.hasPermission("fmc.proxi." + args[0].toLowerCase())) return Collections.emptyList();
	    		
	    		switch (args[0].toLowerCase())
	    		{
	    			case "something":
	    				for (String any : anylists)
	    				{
	    					//if (!sender.hasPermission("fmc.proxi.debug." + any)) continue;    		   
	    					ret.add(any);
	    				}
	    				return ret;
	    				
	    			case "start":
	    			case "ss":
	    			case "req":
	    			case "stp":
	    				for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values())
	    		        {
	    		            ret.add(serverInfo.getName());
	    		        }
	    				return ret;
	    			case "perm":
	    				for (String args1 : Perm.args1)
	    				{
	    					ret.add(args1);
	    				}
	    				return ret;
	    			default:
	    				return Collections.emptyList();
	    		}
	    	case 3:
	    		if (!sender.hasPermission("fmc.proxi." + args[0].toLowerCase())) return Collections.emptyList();
	    		
	    		//if(args[0].toLowerCase().equalsIgnoreCase("perm"))
    			switch(args[1].toLowerCase())
	    		{
	    			case "add":
	    			case "remove":
	    				List<String> permS = Config.getConfig().getStringList("Permission.Short_Name");
	    				for (String permS1 : permS)
	    				{
	    					ret.add(permS1);
	    				}
	    				return ret;
	    		}
	    	case 4:
	    		if (!sender.hasPermission("fmc.proxi." + args[0].toLowerCase())) return Collections.emptyList();
	    		
	    		PlayerList.loadPlayers(); // プレイヤーリストをロード
	    		List<String> permS = Config.getConfig().getStringList("Permission.Short_Name");
	    		if(permS.contains(args[2].toLowerCase()))
	    		{
	    			for (String player : PlayerList.getPlayerList())
	    			{
	    				ret.add(player);
	    			}
	    		}
    			return ret;
    	}
        return Collections.emptyList();
    }
}
