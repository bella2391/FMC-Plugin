package fabric_command;

import java.util.Arrays;

import java.util.List;
import java.util.function.Supplier;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fabric.LuckPermUtil;
import fabric.Main;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class FMCCommand {

    private static final List<String> subcommands = Arrays.asList("reload", "potion", "medic", "fly", "test", "fv", "mcvc");
    //private final LuckPermUtil lpUtil;
    
    @Inject
    public FMCCommand()
    {
    	//
    }
    
    public int execute(CommandContext<ServerCommandSource> context, String subcommand) throws CommandSyntaxException 
    {
        ServerCommandSource source = context.getSource();
        
        try
        {
        	if (!Main.getInjector().getInstance(LuckPermUtil.class).hasPermission(source, "fmc." + subcommand)) 
            {
                source.sendMessage(Text.literal("Access denied"));
                return 1;
            }

            switch (subcommand) 
            {
                case "reload":
                    // Handle reload
                    break;
                case "potion":
                    // Handle potion
                    break;
                case "medic":
                    // Handle medic
                    break;
                case "fly":
                    // Handle fly
                    break;
                case "test":
                	source.sendMessage(Text.literal("TestCommandExecuted"));
                    break;
                case "fv":
                    // Handle fv
                    break;
                case "mcvc":
                    // Handle mcvc
                    break;
                default:
                    source.sendMessage(Text.literal("Unknown command"));
                    return 1;
            }
            
            return 0; // 正常に実行された場合は 0 を返す
        }
        catch (Exception e) 
        {
            e.printStackTrace(); // コンソールに例外を出力してデバッグ
            source.sendMessage(Text.literal("An error occurred while executing the command"));
            return 1; // 例外が発生した場合は 1 を返す
        }
    }
}
