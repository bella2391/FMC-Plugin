package forge;

import java.util.Objects;
import java.util.UUID;

import com.google.inject.Inject;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LuckPermUtil 
{
    private final LuckPerms luckperm;

    @Inject
    public LuckPermUtil(LuckPerms luckperm)
    {
    	this.luckperm = luckperm;
    }
    
    // 権限チェックメソッド
    public boolean hasPermission(CommandSourceStack source, String permission) 
    {
    	if(Objects.isNull(source.getEntity())) return true;
    	
        if (!(source.getEntity() instanceof ServerPlayer)) 
        {
            source.sendFailure(Component.literal("Error: Command must be executed by a player."));
            return false;
        }

        ServerPlayer player = source.getPlayer();
		
		if(player != null)
		{
			UserManager userManager = luckperm.getUserManager();
			UUID playerUUID = player.getUUID();
			User user = userManager.getUser(playerUUID);
			if(Objects.isNull(user))
			{
				source.sendFailure(Component.literal("Error: User not found in LuckPerms."));
				return false;
			}
			
			// 権限チェック
			return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
		}

		return false;
    }
}
