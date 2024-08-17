package forge;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Objects;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;

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

        ServerPlayer player = (ServerPlayer) source.getEntity();
        UserManager userManager = luckperm.getUserManager();
        User user = userManager.getUser(player.getUUID());

        if (Objects.isNull(user)) 
        {
        	source.sendFailure(Component.literal("Error: User not found in LuckPerms."));
            return false;
        }

        // 権限チェック
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
