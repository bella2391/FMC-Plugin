package fabric;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.google.inject.Inject;

public class Database
{
    public static Connection conn;
    private final Config config;
    
    @Inject
    public Database(Config config)
    {
    	this.config = config;
    }
    
	public Connection getConnection() throws SQLException, ClassNotFoundException
	{
		if
		(
			config.getString("MySQL.Host", "").isEmpty() || 
			config.getInt("MySQL.Port", 0) == 0 || 
			config.getString("MySQL.Database", "").isEmpty() || 
			config.getString("MySQL.User", "").isEmpty() || 
			config.getString("MySQL.Password", "").isEmpty()
		)
		{
			return null;
		}
		
        if (Objects.nonNull(conn) && !conn.isClosed()) return conn;
        
        synchronized (Database.class) 
        {
            if (Objects.nonNull(conn) && !conn.isClosed()) return conn;
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection
            		(
            			"jdbc:mysql://" + config.getString("MySQL.Host") + ":" + 
            			config.getInt("MySQL.Port") + "/" + 
            			config.getString("MySQL.Database"),
            			config.getString("MySQL.User"),
            			config.getString("MySQL.Password")
            		);
            return conn;
        }
    }
	
	public void close_resorce(ResultSet[] resultsets,Connection conn, PreparedStatement ps)
	{
		if(Objects.nonNull(resultsets))
		{
			for (ResultSet resultSet : resultsets)
			{
			    if (Objects.nonNull(resultSet))
			    {
			    	try
			    	{
	                    resultSet.close();
	                }
			    	catch (SQLException e)
			    	{
	                    e.printStackTrace();
	                }
			    }
			}
		}
		
		if(Objects.nonNull(conn))
		{
			try
			{
                ps.close();
            }
			catch (SQLException e)
			{
                e.printStackTrace();
            }
		}
		
		if(Objects.nonNull(ps))
		{
			try
			{
                conn.close();
            }
			catch (SQLException e)
			{
                e.printStackTrace();
            }
		}
	}
}