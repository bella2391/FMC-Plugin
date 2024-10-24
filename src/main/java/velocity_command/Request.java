package velocity_command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import discord.DiscordInterface;
import discord.EmojiManager;
import discord.MessageEditorInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import velocity.BroadCast;
import velocity.Config;
import velocity.DatabaseInterface;

public class Request {

	public static Map<String, Boolean> PlayerReqFlags = new HashMap<>();
	public Connection conn = null;
	public ResultSet minecrafts = null, reqstatus = null;
	public ResultSet[] resultsets = {minecrafts,reqstatus};
	public PreparedStatement ps = null;
	
	private final ProxyServer server;
	private final Config config;
	private final Logger logger;
	private final DatabaseInterface db;
	private final BroadCast bc;
	private final DiscordInterface discord;
	private final MessageEditorInterface discordME;
	private final EmojiManager emoji;
	private String currentServerName = null;
	
	@Inject
	public Request (
		ProxyServer server, Logger logger, 
		Config config, DatabaseInterface db, BroadCast bc,
		DiscordInterface discord, MessageEditorInterface discordME, EmojiManager emoji
	) {
		this.server = server;
		this.logger = logger;
		this.config = config;
		this.db = db;
		this.bc = bc;
		this.discord = discord;
		this.discordME = discordME;
		this.emoji = emoji;
	}

	public void execute(@NotNull CommandSource source,String[] args) {
		if (source instanceof Player player) {
			// プレイヤーがコマンドを実行した場合の処理
			String playerName = player.getUsername();

            if (args.length == 1 || Objects.isNull(args[1]) || args[1].isEmpty()) {
            	player.sendMessage(Component.text("サーバー名を入力してください。").color(NamedTextColor.RED));
            	return;
            }
            
            // プレイヤーの現在のサーバーを取得
	        player.getCurrentServer().ifPresent(serverConnection -> {
	            RegisteredServer registeredServer = serverConnection.getServer();
	            currentServerName = registeredServer.getServerInfo().getName();
	        });
            
            String targetServerName = args[1];
            boolean containsServer = false;
            for (RegisteredServer registeredServer : this.server.getAllServers()) {
                if (registeredServer.getServerInfo().getName().equalsIgnoreCase(targetServerName)) {
                    containsServer = true;
                    break;
                }
            }

            if (!containsServer) {
                player.sendMessage(Component.text("サーバー名が違います。").color(NamedTextColor.RED));
                return;
            }
            
            if (config.getString("Servers."+args[1]+".Exec_Path").isEmpty() || !config.getBoolean("Servers."+args[1]+".Entry", false)) {
            	player.sendMessage(Component.text("許可されていません。").color(NamedTextColor.RED));
            	return;
            }
            
            try {
            	conn = db.getConnection();
            	String sql = "SELECT * FROM members WHERE uuid=?;";
    			ps = conn.prepareStatement(sql);
    			ps.setString(1,player.getUniqueId().toString());
    			minecrafts = ps.executeQuery();
    			if (minecrafts.next()) {
    				if (Objects.nonNull(minecrafts.getTimestamp("sst")) && Objects.nonNull(minecrafts.getTimestamp("req"))) {
        				long now_timestamp = Instant.now().getEpochSecond();
    					
    					// /ssで発行したセッションタイムをみる
    	                Timestamp sst_timeget = minecrafts.getTimestamp("sst");
    	                long sst_timestamp = sst_timeget.getTime() / 1000L;
    					
    					long ss_sa = now_timestamp-sst_timestamp;
    					long ss_sa_minute = ss_sa/60;
    					if (ss_sa_minute>config.getInt("Interval.Session",3)) {
    						player.sendMessage(Component.text("セッションが無効です。").color(NamedTextColor.RED));
    						return;
    					}
    					
        				// /reqを実行したときに発行したセッションタイムをみる
    	                Timestamp req_timeget = minecrafts.getTimestamp("req");
    	                long req_timestamp = req_timeget.getTime() / 1000L;
    					
    					long req_sa = now_timestamp-req_timestamp;
    					long req_sa_minute = req_sa/60;
        				
        		        if (req_sa_minute<=config.getInt("Interval.Request",0)) {
        		        	player.sendMessage(Component.text("リクエストは"+config.getInt("Interval.Request",0)+"分に1回までです。").color(NamedTextColor.RED));
        		        	return;
        		        }
    				}
    			} else {
    				// MySQLサーバーにサーバーが登録されてなかった場合
    				logger.info(NamedTextColor.RED+"このサーバーは、データベースに登録されていません。");
    				player.sendMessage(Component.text("このサーバーは、データベースに登録されていません。").color(NamedTextColor.RED));
    				return;
    			}
    			
		        // /reqを実行したので、セッションタイムをreqに入れる
				sql = "UPDATE members SET req=CURRENT_TIMESTAMP WHERE uuid=?;";
				ps = conn.prepareStatement(sql);
				ps.setString(1,player.getUniqueId().toString());
				ps.executeUpdate();
		        
				Request.PlayerReqFlags.put(player.getUniqueId().toString(), true); // フラグを設定
				
				// 全サーバーにプレイヤーがサーバーを起動したことを通知
				TextComponent notifyComponent = Component.text()
						.append(Component.text(playerName+"が"+args[1]+"サーバーの起動リクエストを送信しました。").color(NamedTextColor.AQUA))
						.build();
				bc.sendExceptPlayerMessage(notifyComponent, playerName);
				
            	// discord:アドミンチャンネルへボタン送信
				emoji.createOrgetEmojiId(playerName).thenApply(success -> {
					if (success != null && !success.isEmpty()) {
						String playerEmoji = emoji.getEmojiString(playerName, success);
						discord.sendRequestButtonWithMessage(playerEmoji+playerName+"が"+args[1]+"サーバーの起動リクエストを送信しました。\n起動しますか？\n(管理者のみ実行可能です。)");
				
						player.sendMessage(Component.text("送信されました。").color(NamedTextColor.GREEN));
						
						// discordへリクエスト通知&ボタン送信
						discordME.AddEmbedSomeMessage("Request", player, args[1]);
            	
						// add log
						try {
							String asyncSql = "INSERT INTO log (name,uuid,server,req,reqserver) VALUES (?,?,?,?,?);";
							ps = conn.prepareStatement(asyncSql);
							ps.setString(1, playerName);
							ps.setString(2, player.getUniqueId().toString());
							ps.setString(3, currentServerName);
							ps.setBoolean(4, true);
							ps.setString(5, args[1]);
							ps.executeUpdate();
						} catch (SQLException e) {
							logger.error("A SQLException error occurred: " + e.getMessage());
							for (StackTraceElement element : e.getStackTrace()) {
								logger.error(element.toString());
							}
						}
						
						return true;
					} else {
						return false;
					}
				}).thenAccept(result -> {
					if (result) {
						logger.info(playerName+"が"+args[1]+"サーバーの起動リクエストを送信しました。");
					} else {
						logger.error("Start Error: Emoji is null or empty.");
					}
				}).exceptionally(ex -> {
					logger.error("Start Error: " + ex.getMessage());
					return null;
				});
            } catch (SQLException | ClassNotFoundException e) {
            	logger.error("A SQLException | ClassNotFoundException error occurred: " + e.getMessage());
				for (StackTraceElement element : e.getStackTrace()) {
					logger.error(element.toString());
				}
            } /*finally {
            	db.close_resource(resultsets, conn, ps);
            }*/
        } else {
			source.sendMessage(Component.text("このコマンドはプレイヤーのみが実行できます。").color(NamedTextColor.RED));
		}
	}
}