package bungee;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.messaging.MessagingService;

public class Luckperms
{
	public LuckPerms luckperms;
	public Main plugin;
	
	public Luckperms(Main plugin,LuckPerms luckperms)
	{
		this.plugin = plugin;
		this.luckperms = luckperms;
	}
	
	public void triggerNetworkSync()
	{
        MessagingService messagingService = this.luckperms.getMessagingService().orElse(null);

        if (messagingService != null)
        {
            messagingService.pushUpdate();
            this.plugin.getLogger().info("LuckPerms network sync triggered.");
        }
        else
        {
        	this.plugin.getLogger().severe("Failed to get LuckPerms MessagingService.");
        }
    }
}