package spigot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton 
public class Database {
    private final common.Main plugin;
    
    @Inject
    public Database(common.Main plugin) {
    	this.plugin = plugin;
    }
    
    public Connection getConnection() throws SQLException, ClassNotFoundException {
		return getConnection(null);
	}
    
	public synchronized Connection getConnection(String customDatabase) throws SQLException, ClassNotFoundException {
        String host = plugin.getConfig().getString("MySQL.Host", "");
        int port = plugin.getConfig().getInt("MySQL.Port", 0);
        String user = plugin.getConfig().getString("MySQL.User", "");
        String password = plugin.getConfig().getString("MySQL.Password", "");
        if (customDatabase != null && !customDatabase.isEmpty()) {
            if ((host != null && host.isEmpty()) || 
                port == 0 || 
                (user != null && user.isEmpty()) || 
                (password != null && password.isEmpty())) {
                return null;
            }
            
            synchronized (Database.class) {
                //if (Objects.nonNull(conn2) && !conn2.isClosed()) return conn2;
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                return  DriverManager.getConnection (
                            "jdbc:mysql://" + host + ":" + 
                            port + "/" + 
                            customDatabase +
                            "?autoReconnect=true&useSSL=false", 
                            user, 
                            password
                );
            }
        } else {
            String database = plugin.getConfig().getString("MySQL.Database", "");
            if ((host != null && host.isEmpty()) || 
                port == 0 || 
                (database != null && database.isEmpty()) || 
                (user != null && user.isEmpty()) || 
                (password != null && password.isEmpty())) {
                return null;
            }
            
            synchronized (Database.class) {
                //if (Objects.nonNull(conn) && !conn.isClosed()) return conn;
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection (
                            "jdbc:mysql://" + host + ":" + 
                            port + "/" + 
                            database +
                            "?autoReconnect=true&useSSL=false", 
                            user, 
                            password
                        );
            }
        }
    }
}