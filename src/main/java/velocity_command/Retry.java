package velocity_command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import velocity.DatabaseInterface;

public class Retry {

	private final Logger logger;
	private final DatabaseInterface db;
	
	private Connection conn = null;
	private PreparedStatement ps = null;

	@Inject
	public Retry(Logger logger, DatabaseInterface db) {
		this.logger = logger;
		this.db = db;
	}
	
	public void execute(@NotNull CommandSource source, String[] args) {
		if (!(source instanceof Player)) {
			source.sendMessage(Component.text("このコマンドはプレイヤーのみが実行できます。").color(NamedTextColor.RED));	
			return;
		}
		
		Player player = (Player) source;
		try {
			conn = db.getConnection();
			
			// 6桁の乱数を生成
			Random rnd = new Random();
			int ranum = 100000 + rnd.nextInt(900000);
			String ranumstr = Integer.toString(ranum);
			
			String sql = "UPDATE members SET secret2=? WHERE uuid=?;";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, ranum);
			ps.setString(2, player.getUniqueId().toString());
			ps.executeUpdate();
			
			TextComponent component = Component.text()
						.append(Component.text("認証コードを再生成しました。").color(NamedTextColor.GREEN))
						.append(Component.text("\n認証コードは ").color(NamedTextColor.WHITE))
						.append(Component.text(ranumstr).color(NamedTextColor.BLUE)
							.clickEvent(ClickEvent.copyToClipboard(ranumstr))
							.hoverEvent(HoverEvent.showText(Component.text("(クリックして)コピー"))))
						.append(Component.text(" です。").color(NamedTextColor.WHITE))
						.build();
			// BaseComponent[]に変換
						
			player.sendMessage(component);
		} catch (SQLException | ClassNotFoundException e) {
			logger.error("A SQLException | ClassNotFoundException error occurred: " + e.getMessage());
			for (StackTraceElement element : e.getStackTrace()) {
				logger.error(element.toString());
			}
		} finally {
			db.close_resource(null, conn, ps);
		}
	}
}