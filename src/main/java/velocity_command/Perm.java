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
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import velocity.Config;
import velocity.DatabaseInterface;
import velocity.Luckperms;
import velocity.Main;


public class Perm {

	private final Config config;
	private final Logger logger;
	private final Luckperms lp;
	private final DatabaseInterface db;
	
	public Connection conn = null, connLp = null;
	public Connection[] conns = {conn, connLp};
	public ResultSet minecrafts = null, database_uuid = null, isperm = null;
	public ResultSet[] resultsets = {minecrafts,database_uuid,isperm};
	public PreparedStatement ps = null;
	public static List<String> args1 = new ArrayList<>(Arrays.asList("add","remove","list"));
	public static List<String> permS = null;
	public static List<String> permD = null;
	
	@Inject
	public Perm (
		Main plugin, ProxyServer server, Config config, 
		Logger logger, Luckperms lp, DatabaseInterface db
	) {
		this.config = config;
		this.logger = logger;
		this.lp = lp;
		this.db = db;
	}
	
	public void execute(@NotNull CommandSource source,String[] args) {
		permS = config.getList("Permission.Short_Name");
		permD = config.getList("Permission.Detail_Name");
		
		if (!(permS.size() == permD.size())) {
			source.sendMessage(Component.text("コンフィグのDetail_NameとShort_Nameの要素の数を同じにしてください。").color(NamedTextColor.RED));
			return;
		}

        try {
        	conn = db.getConnection();
        	connLp = db.getConnection("fmc_lp");
        	String sql = "SELECT * FROM members ORDER BY id DESC;";
			ps = conn.prepareStatement(sql);
			minecrafts = ps.executeQuery();
			
			boolean containsPlayer = false;
			boolean ispermindb = false;
	        
			String permD1;
			
	        switch (args.length) {
	        	case 0,1-> {
	        		source.sendMessage(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN));
				}
	        	case 2-> {
        			switch(args[1].toLowerCase()) {
	        			case "list"-> {
	        				TextComponent component = Component.text()
	        					.append(Component.text("FMC Specific Permission List")
		    			    	.color(NamedTextColor.GOLD)
		    			    	.decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED))
	        					.build();
	        				
	        				// アドミンリスト表示処理
	        				while (minecrafts.next()) {
	        					for (int i = 0; i < permD.size(); i++) {
	        						// permSのindex値をもって、permDからDetail_Nameを取得(1:1対応)
		                			//permD1 = permD.get(permS.indexOf(args[2]));
		                			
	        						sql = "SELECT * FROM lp_user_permissions WHERE uuid=? AND permission=?;";
		        					ps = connLp.prepareStatement(sql);
		        					ps.setString(1, minecrafts.getString("uuid"));
		        					ps.setString(2, permD.get(i));
		        					isperm = ps.executeQuery();
		        					
	        		        		if (isperm.next()) {
	        		        			TextComponent additionalComponent;
	        		        			additionalComponent = Component.text()
	        		        						.append(Component.text("\n"+minecrafts.getString("name"))
	        		        							.color(NamedTextColor.WHITE))
	        		        						.append(Component.text("  -"+permS.get(i))
	        		        							.color(NamedTextColor.GOLD))
	        		        						.build();
	        		        			component = component.append(additionalComponent);
	        		        			ispermindb = true;
	        		        		}
	        					}
	        	            }
	        				
	        				if (!(ispermindb)) {
	        					TextComponent additionalComponent;
	        					additionalComponent = Component.text()
	        							.append(Component.text("\nコンフィグで設定されているすべての権限が見つかりませんでした。"))
        								.color(NamedTextColor.GREEN)
        								.build();
	        					component = component.append(additionalComponent);
	        				}
	        				
	        				source.sendMessage(component);
						}
	        			default-> {
	        				source.sendMessage(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN));
						}
	        		}
				}
	        	case 3-> {
	        		// 以下はパーミッションが所持していることが確認されている上で、permというコマンドを使っているので、確認の必要なし
	        		//if(args[0].toLowerCase().equalsIgnoreCase("perm"))
	        		if (!(args1.contains(args[1].toLowerCase()))) {
        				source.sendMessage(Component.text("第2引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN)));
        				break;
        			}
	        		
	        		if (!(permS.contains(args[2].toLowerCase()))) {
	        			source.sendMessage(Component.text("第3引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN)));
        				break;
        			}
	        		
	        		source.sendMessage(Component.text("対象のプレイヤー名を入力してください。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN)));
				}
	        	case 4-> {
        			if (!(args1.contains(args[1].toLowerCase()))) {
        				source.sendMessage(Component.text("第2引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN)));
        				break;
        			}
        			
        			if (!(permS.contains(args[2].toLowerCase()))) {
        				source.sendMessage(Component.text("第3引数が不正です。\n").color(NamedTextColor.RED).append(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN)));
        				break;
        			}
        			
        			// permSのindex値をもって、permDからDetail_Nameを取得(1:1対応)
        			permD1 = permD.get(permS.indexOf(args[2]));
        			while (minecrafts.next()) {
        				sql = "SELECT * FROM lp_user_permissions WHERE uuid=? AND permission=?;";
    					ps = connLp.prepareStatement(sql);
    					ps.setString(1, minecrafts.getString("uuid"));
    					ps.setString(2, permD1);
    					isperm = ps.executeQuery();
    					
    	            	if (minecrafts.getString("name").equalsIgnoreCase(args[3])) {
    		        		containsPlayer = true;
    		        		if(isperm.next()) {
    		        			ispermindb = true;
    		        		}

    		        		break;
    		        	}
    	            }
        			
        			if (!containsPlayer) {
    		        	source.sendMessage(Component.text("サーバーに参加したことのあるプレイヤーを指定してください。").color(NamedTextColor.RED));
    		        	break;
    		        }
    		        
    				switch (args[1].toLowerCase()) {
        				case "add"-> {
        					if (ispermindb) {
        						source.sendMessage(Component.text(args[3]+"はすでにpermission: "+permD1+"を持っているため、追加できません。").color(NamedTextColor.RED));
		    		        	break;
        					}

        					SetAdmin(permD1,args[3],true,source);
        					break;
        				}
        				case "remove"-> {
        					if (!(ispermindb)) {
        						source.sendMessage(Component.text(args[3]+"はpermission: "+permD1+"を持っていないため、除去できません。").color(NamedTextColor.RED));
		    		        	break;
        					}

        					SetAdmin(permD1,args[3],false,source);
        					break;
						}
	        		}
				}
	        	default-> {
	        		source.sendMessage(Component.text("usage: /fmcp perm <add|remove|list> [Short:permission] <player>").color(NamedTextColor.GREEN));
				}
	        }
        } catch (SQLException | ClassNotFoundException e) {
        	logger.error("A SQLException | ClassNotFoundException error occurred: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
        } finally {
        	db.close_resource(resultsets, conns, ps);
        }
	}
	
	public void SetAdmin(String permission,String name,Boolean bool,CommandSource source) {
		// 上のAdminメソッドの途中なので、connは閉じない。(finallyを省く)
		try {
			conn = db.getConnection();
			connLp = db.getConnection("fmc_lp");
			String sql = "SELECT * FROM members WHERE name=? ORDER BY id DESC LIMIT 1;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			database_uuid = ps.executeQuery();
			
			if (database_uuid.next()) {
				// lp処理
				if (bool) {
					sql = "INSERT INTO lp_user_permissions "
							+ "(uuid,permission,value,server,world,expiry,contexts) VALUES (?,?,?,?,?,?,?);";
					ps = connLp.prepareStatement(sql);
					ps.setString(1,database_uuid.getString("uuid"));
					ps.setString(2,permission);
					ps.setBoolean(3,true);
					ps.setString(4,"global");
					ps.setString(5,"global");
					ps.setInt(6,0);
					ps.setString(7,"{}");
					ps.executeUpdate();
					source.sendMessage(Component.text(name+"にpermission: "+permission+"を追加しました。").color(NamedTextColor.GREEN));
				} else {
					sql = "DELETE FROM lp_user_permissions WHERE uuid=?;";
					ps = connLp.prepareStatement(sql);
					ps.setString(1,database_uuid.getString("uuid"));
					ps.executeUpdate();
					source.sendMessage(Component.text(name+"からpermission: "+permission+"を除去しました。").color(NamedTextColor.GREEN));
				}
				
				lp.triggerNetworkSync();
				source.sendMessage(Component.text("権限を更新しました。").color(NamedTextColor.GREEN));
			}
		} catch (SQLException | ClassNotFoundException e) {
        	logger.error("A SQLException | ClassNotFoundException error occurred: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
        }
	}
}