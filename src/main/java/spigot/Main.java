package spigot;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;
import spigot_command.FMCCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Main
{
	public FileConfiguration config;
	public Connection conn = null;
	public PreparedStatement ps = null;
	public common.Main plugin;
	public SocketSwitch ssw;
	
	public Main(common.Main plugin)
	{
		this.plugin = plugin;
	}
	
	public void onEnable()
    {
		//
    }
    
    public void onDisable()
    {
    	//
    }
}
