package velocity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import discord.MessageEditorInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerDisconnect {
	
	public final Main plugin;
	private final ProxyServer server;
	private final Logger logger;
	private final DatabaseInterface db;
	private final ConsoleCommandSource console;
	private final MessageEditorInterface discordME;
	
	@Inject
	public PlayerDisconnect (
		Main plugin, Logger logger, ProxyServer server,
		DatabaseInterface db, BroadCast bc, ConsoleCommandSource console,
		RomaToKanji conv, MessageEditorInterface discordME
	) {
		this.plugin = plugin;
		this.logger = logger;
		this.server = server;
		this.db = db;
		this.console = console;
		this.discordME = discordME;
	}
	
	public void menteDisconnect(List<String> UUIDs) {
		for (Player player : server.getAllPlayers()) {
			//if(!player.hasPermission("group.super-admin"))
			if (!UUIDs.contains(player.getUniqueId().toString())) {
				playerDisconnect (
					false,
					player,
					Component.text("現在メンテナンス中です。").color(NamedTextColor.BLUE)
				);
			} else {
				player.sendMessage(Component.text("スーパーアドミン認証...PASS\n\nALL CORRECT\n\nメンテナンスモードが有効になりました。\nスーパーアドミン以外を退出させました。").color(NamedTextColor.GREEN));
			}
		}
		
		console.sendMessage(Component.text("メンテナンスモードが有効になりました。\nスーパーアドミン以外を退出させました。").color(NamedTextColor.GREEN));
	}
	
	public void playerDisconnect(Boolean bool, Player player, TextComponent component) {
		player.disconnect(component);
		if (!(bool)) return;
		String query = "UPDATE members SET ban=? WHERE uuid=?;";
		try (Connection conn = db.getConnection();
			PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setBoolean(1, true);
			ps.setString(2, player.getUniqueId().toString());
			int rsAffected = ps.executeUpdate();
			if (rsAffected > 0) {
				discordME.AddEmbedSomeMessage("Invader", player);
			}
		} catch (SQLException | ClassNotFoundException e) {
			// スタックトレースをログに出力
            logger.error("An onChat error occurred: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
		}
	}
}
