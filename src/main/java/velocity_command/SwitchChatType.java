package velocity_command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import velocity.Config;

public class SwitchChatType
{
	private final Config config;
	private final Logger logger;
	private TextComponent component = null;
	public static List<String> args1 = new ArrayList<>(Arrays.asList("switch","status"));
	
	@Inject
	public SwitchChatType(Config config, Logger logger)
	{
		this.config = config;
		this.logger = logger;
	}

	public void execute(@NotNull CommandSource source, String[] args)
	{
		switch(args.length)
        {
        	case 0, 1 -> source.sendMessage(Component.text("usage: /fmcp chat <switch|status>").color(NamedTextColor.GREEN));
        	case 2 -> 
			{
				switch(args[1].toLowerCase())
				{
					case "status" -> 
					{
						if (config.getBoolean("Discord.ChatType", false))
						{
							component = Component.text("現在のチャットタイプは、編集Embedメッセージタイプです。").color(NamedTextColor.GREEN);
						}
						else
						{
							component = Component.text("現在のチャットタイプは、プレーンテキストメッセージタイプです。").color(NamedTextColor.GREEN);
						}
						source.sendMessage(component);
					}
						
					case "switch" -> 
					{
						Map<String, Object> DiscordConfig = config.getStringObjectMap("Discord");
						if(Objects.isNull(DiscordConfig))
						{
							source.sendMessage(Component.text("コンフィグの設定が正しくありません。").color(NamedTextColor.RED));
							return;
						}

						if (config.getBoolean("Discord.ChatType", false))
						{
							DiscordConfig.put("ChatType", false);
							component = Component.text("チャットタイプをプレーンテキストメッセージタイプに変更しました。").color(NamedTextColor.GREEN);
						}
						else
						{
							DiscordConfig.put("ChatType", true);
							component = Component.text("チャットタイプを編集Embedメッセージタイプに変更しました。").color(NamedTextColor.GREEN);
						}
						
						source.sendMessage(component);
						
						try
						{
							config.saveConfig();
						}
						catch (IOException e)
						{
							logger.error("An IOException error occurred: " + e.getMessage());
							for (StackTraceElement element : e.getStackTrace()) 
							{
								logger.error(element.toString());
							}
						}
					}
						
					default -> source.sendMessage(Component.text("usage: /fmcp chat <switch|status>").color(NamedTextColor.GREEN));
				}
            }
            default -> source.sendMessage(Component.text("usage: /fmcp chat <switch|status>").color(NamedTextColor.GREEN));
        }
	}
}