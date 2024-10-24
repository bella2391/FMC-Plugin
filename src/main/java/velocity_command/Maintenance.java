package velocity_command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;

import discord.MessageEditorInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import velocity.DatabaseInterface;
import velocity.PlayerDisconnect;

public class Maintenance {

	public static boolean isMente;
	public static List<String> args1 = new ArrayList<>(Arrays.asList("switch","status"));
	public static List<String> args2 = new ArrayList<>(Arrays.asList("discord"));
	public static List<String> args3 = new ArrayList<>(Arrays.asList("true","false"));
	private final DatabaseInterface db;
	private final PlayerDisconnect pd;
	private final MessageEditorInterface discordME;
	private final Logger logger;
	private Component component = null;
	
	@Inject
	public Maintenance (
		Logger logger, DatabaseInterface db, PlayerDisconnect pd, 
		MessageEditorInterface discordME
	) {
		this.logger = logger;
		this.db = db;
		this.pd = pd;
		this.discordME = discordME;
	}

	public void execute(@NotNull CommandSource source, String[] args) {
		String query = "SELECT online FROM status WHERE name=?;";
		String query2 = "SELECT uuid FROM lp_user_permissions WHERE permission=?;";
		try (Connection conn = db.getConnection(); 
			Connection connLp = db.getConnection("fmc_lp");
			PreparedStatement ps = conn.prepareStatement(query);
			PreparedStatement ps2 = connLp.prepareStatement(query2)) {
			ps.setString(1, "maintenance");
			ps2.setString(1, "group.super-admin");
			try (ResultSet ismente = ps.executeQuery();
				ResultSet issuperadmin = ps2.executeQuery()) {
				List<String> superadminUUIDs = new ArrayList<>();
				while(issuperadmin.next()) {
					superadminUUIDs.add(issuperadmin.getString("uuid"));
				}
				switch (args.length) {
					case 0, 1 -> source.sendMessage(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN));
					case 2 -> {
						switch(args[1].toLowerCase()) {
							case "status" -> {
								if (ismente.next()) {
									if(ismente.getBoolean("online")) {
										component = Component.text("現在メンテナンス中です。").color(NamedTextColor.GREEN);
									} else {
										component = Component.text("現在メンテナンス中ではありません。").color(NamedTextColor.GREEN);
									}
								}
	
								source.sendMessage(component);
							}
								
							default -> source.sendMessage(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN));
						}
					}
						
					case 3 -> {
						// 以下はパーミッションが所持していることが確認されている上で、permというコマンドを使っているので、確認の必要なし
						//if(args[0].toLowerCase().equalsIgnoreCase("perm"))
						if(!(args1.contains(args[1].toLowerCase()))) {
							source.sendMessage(Component.text("第2引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
							break;
						}
						
						if(!(args2.contains(args[2].toLowerCase()))) {
							source.sendMessage(Component.text("第3引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
							break;
						}
						
						source.sendMessage(Component.text("discord通知をtrueにするかfalseにするかを決定してください。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
					}
						
					case 4 -> {
						if (!(args1.contains(args[1].toLowerCase()))) {
							source.sendMessage(Component.text("第2引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
							break;
						}
						if (!(args2.contains(args[2].toLowerCase()))) {
							source.sendMessage(Component.text("第3引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
							break;
						}
						if (!(args3.contains(args[3].toLowerCase()))) {
							source.sendMessage(Component.text("第4引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
							break;
						}
						if (!(args[3].equals("true") || args[3].equals("false"))) {
							source.sendMessage(Component.text("trueかfalseを入力してください。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp maintenance <switch|status> <discord> <true|false>").color(NamedTextColor.GREEN)));
							break;
						}

						boolean isDiscord = Boolean.parseBoolean(args[3]);
						if (ismente.next()) {
							if (ismente.getBoolean("online")) {
								Maintenance.isMente = false; // フラグをtrueに
								if (isDiscord) {
									discordME.AddEmbedSomeMessage("MenteOff");
								}
								// メンテナンスモードが有効の場合
								String query3 = "UPDATE status SET online=? WHERE name=?;";
								try (PreparedStatement ps3 = conn.prepareStatement(query3)) {
									ps3.setBoolean(1, false);
									ps3.setString(2, "maintenance");
									int rsAffected3 = ps3.executeUpdate();
									if (rsAffected3 > 0) {
										source.sendMessage(Component.text("メンテナンスモードが無効になりました。").color(NamedTextColor.GREEN));
									}
								}
							} else {
								Maintenance.isMente = true; // フラグをtrueに
								if (isDiscord) {
									discordME.AddEmbedSomeMessage("MenteOn");
								}
								// メンテナンスモードが無効の場合
								String query3 = "UPDATE status SET online=? WHERE name=?;";
								try (PreparedStatement ps3 = conn.prepareStatement(query3)) {
									ps3.setBoolean(1, true);
									ps3.setString(2, "maintenance");
									int rsAffected3 = ps3.executeUpdate();
									if (rsAffected3 > 0) {
										pd.menteDisconnect(superadminUUIDs);
									}
								}
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
            logger.error("A ClassNotFoundException | SQLException error occurred: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) 
            {
                logger.error(element.toString());
            }
        }
	}
}
