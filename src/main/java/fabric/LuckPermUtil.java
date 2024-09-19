package fabric;

import java.util.Objects;
import java.util.UUID;

import com.google.inject.Inject;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class LuckPermUtil {

	private final LuckPerms luckperm;
	
	@Inject
	public LuckPermUtil(LuckPerms luckperm) {
		this.luckperm = luckperm;
	}
	
	public boolean hasPermission(ServerCommandSource source, String permission) {
	    if (Objects.isNull(source.getEntity())) return true;
	    
	    if (!(source.getEntity() instanceof PlayerEntity)) {
	        source.sendMessage(Text.literal("Error: Command must be executed by a player."));
	        return false;
	    }
	    
		ServerPlayerEntity player = source.getPlayer();
		
		if (player != null) {
			UserManager userManager = luckperm.getUserManager();
			UUID playerUUID = player.getUuid();
			User user = userManager.getUser(playerUUID);
			if (Objects.isNull(user)) {
				source.sendMessage(Text.literal("Error: User not found in LuckPerms."));
				return false;
			}
			
			// 権限チェック
			return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
		}

		return false;
    }
}
