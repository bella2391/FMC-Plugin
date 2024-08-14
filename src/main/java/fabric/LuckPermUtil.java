package fabric;

import java.util.Objects;

import com.google.inject.Inject;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class LuckPermUtil 
{
	private final LuckPerms luckperm;
	
	@Inject
	public LuckPermUtil(LuckPerms luckperm)
	{
		this.luckperm = luckperm;
	}
	
	public boolean hasPermission(ServerCommandSource source, String permission) 
    {
	    if (Objects.isNull(source.getEntity())) return true;
	    
	    if (!(source.getEntity() instanceof PlayerEntity)) 
	    {
	        source.sendMessage(Text.literal("Error: Command must be executed by a player."));
	        return false;
	    }
	    
        UserManager userManager = luckperm.getUserManager();
        User user = userManager.getUser(source.getPlayer().getUuid());
        
        if (Objects.nonNull(user)) 
        {
            for (Node node : user.getNodes()) 
            {
                if (node instanceof PermissionNode && ((PermissionNode) node).getPermission().equals(permission)) 
                {
                    return true;
                }
            }
        }
        
        return false;
    }
}
