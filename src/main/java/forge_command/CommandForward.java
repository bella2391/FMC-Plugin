package forge_command;

import com.google.inject.Inject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import forge.SocketSwitch;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class CommandForward
{
	private final SocketSwitch ssw;
	
	@Inject
	public CommandForward(SocketSwitch ssw)
	{
		this.ssw = ssw;
	}
	
	public int execute(CommandContext<CommandSourceStack> context)
	{
		CommandSourceStack source = context.getSource();
		
	    String playerName = StringArgumentType.getString(context, "player");
	    String args = StringArgumentType.getString(context, "proxy_cmds");
	    
	    StringBuilder allcmd = new StringBuilder(); // コマンドを組み立てる
	    
	    if (source.getEntity() instanceof ServerPlayer) 
	    {
	        allcmd.append(source.getPlayer()).append(" fv ").append(playerName).append(" ").append(args); // コマンドを打ったプレイヤー名をallcmdに乗せる
	    } 
	    else 
	    {
	        allcmd.append("? fv ").append(playerName).append(" ").append(args); // コンソールから打った場合
	    }
	    
	    ssw.startSocketClient(allcmd.toString());
		
		return 0;
	}
}
