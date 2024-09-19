package forge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.Logger;

import com.google.inject.Inject;

public class Database {

    public static Connection conn;
    private final Config config;
    private final Logger logger;

    @Inject
    public Database(Config config, Logger logger) {
    	this.config = config;
		this.logger = logger;
    }
    
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		String host = config.getString("MySQL.Host", "");
		int port = config.getInt("MySQL.Port", 0);
		String database = config.getString("MySQL.Database", "");
		String user = config.getString("MySQL.User", "");
		String password = config.getString("MySQL.Password", "");
		if (
			(host != null && host.isEmpty()) || 
			port == 0 || 
			(database != null && database.isEmpty()) || 
			(user != null && user.isEmpty()) || 
			(password != null && password.isEmpty())
		) {
			return null;
		}
		
        if (Objects.nonNull(conn) && !conn.isClosed()) return conn;
        
        synchronized (Database.class) {
            if (Objects.nonNull(conn) && !conn.isClosed()) return conn;
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            return conn;
        }
    }
	
	public void close_resorce(ResultSet[] resultsets,Connection conn, PreparedStatement ps) {
		if (Objects.nonNull(resultsets)) {
			for (ResultSet resultSet : resultsets) {
			    if (Objects.nonNull(resultSet)) {
			    	try {
	                    resultSet.close();
	                } catch (SQLException e) {
	                    logger.error("A SQLException error occurred: " + e.getMessage());
						for (StackTraceElement element : e.getStackTrace()) {
							logger.error(element.toString());
						}
	                }
			    }
			}
		}
		
		if (Objects.nonNull(conn)) {
			try {
                ps.close();
            } catch (SQLException e) {
                logger.error("A SQLException error occurred: " + e.getMessage());
				for (StackTraceElement element : e.getStackTrace()) {
					logger.error(element.toString());
				}
            }
		}
		
		if (Objects.nonNull(ps)) {
			try {
                conn.close();
            } catch (SQLException e) {
                logger.error("A SQLException error occurred: " + e.getMessage());
				for (StackTraceElement element : e.getStackTrace()) {
					logger.error(element.toString());
				}
            }
		}
	}
}