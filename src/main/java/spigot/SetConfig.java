package spigot;

import org.bukkit.configuration.file.FileConfiguration;

public class SetConfig
{
    public static FileConfiguration config;
    
    public SetConfig(FileConfiguration config)
    {
    	SetConfig.config = config;
    }
}
