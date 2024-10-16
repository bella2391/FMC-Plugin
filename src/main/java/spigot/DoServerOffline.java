package spigot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DoServerOffline {

	public Connection conn = null;
	public PreparedStatement ps = null;
	
	private final common.Main plugin;
	private final Provider<SocketSwitch> sswProvider;
	private final ServerHomeDir shd;
	private final Database db;
	
	@Inject
	public DoServerOffline (
		common.Main plugin, Provider<SocketSwitch> sswProvider, ServerHomeDir shd,
		Database db
	) {
		this.plugin = plugin;
		this.sswProvider = sswProvider;
		this.shd = shd;
		this.db = db;
	}
	
	public void UpdateDatabase() {
		try {
			// "plugins"ディレクトリの親ディレクトリを取得
            String serverName = shd.getServerName();
            
    		conn = db.getConnection();
			// サーバーをオフラインに
			if (Objects.nonNull(conn)) {
				String sql = "UPDATE status SET online=?, socketport=? WHERE name=?;";
				ps = conn.prepareStatement(sql);
				ps.setBoolean(1,false);
				ps.setInt(2, 0);
				ps.setString(3, serverName);
				ps.executeUpdate();
			}
			
			SocketSwitch ssw = sswProvider.get();
			ssw.stopSocketServer();
		} catch (SQLException | ClassNotFoundException e2) {
			plugin.getLogger().log(Level.SEVERE, "A SQLException | ClassNotFoundException error occurred: {0}", e2.getMessage());
            for (StackTraceElement element : e2.getStackTrace()) {
                plugin.getLogger().severe(element.toString());
            }
		} finally {
        	db.close_resource(null, conn, ps);
        }
	}
}
