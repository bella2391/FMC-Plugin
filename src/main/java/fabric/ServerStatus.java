package fabric;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.Logger;

import com.google.inject.Inject;

import net.fabricmc.loader.api.FabricLoader;

public class ServerStatus
{
	private Connection conn = null;
	private PreparedStatement ps = null;
	private final FabricLoader fabric;
	private final Logger logger;
	private final SocketSwitch ssw;
	private final Database db;
	private final String serverName;
	
	@Inject
	public ServerStatus(FabricLoader fabric, Logger logger, SocketSwitch ssw, Database db)
	{
		this.fabric = fabric;
		this.logger = logger;
		this.ssw = ssw;
		this.db = db;
		this.serverName = fabric.getGameDir().toAbsolutePath().getParent().getFileName().toString();
	}
	
	public void doServerOnline()
	{
		try
		{
			ssw.startSocketClient(serverName+"サーバーが起動しました。");
			
			conn = db.getConnection();
			
			if(Objects.nonNull(conn) && Objects.nonNull(serverName))
			{
				// サーバーをオンラインに
				logger.info(serverName+"サーバーが起動しました。");
				
				String sql = "UPDATE mine_status SET online=? WHERE name=?;";
				ps = conn.prepareStatement(sql);
				ps.setBoolean(1,true);
				ps.setString(2, serverName);
				ps.executeUpdate();
				
				logger.info("MySQL Server is connected!");
			}
			else logger.info("MySQL Server is canceled for config value not given");
		}
		catch (SQLException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
        finally
        {
        	db.close_resorce(null, conn, ps);
        }
	}
	
	public void doServerOffline()
	{
		try
		{
    		conn = db.getConnection();
			// サーバーをオフラインに
			if(Objects.nonNull(conn))
			{
				String sql = "UPDATE mine_status SET online=? WHERE name=?;";
				ps = conn.prepareStatement(sql);
				ps.setBoolean(1,false);
				ps.setString(2, serverName);
				ps.executeUpdate();
			}
			
			ssw.stopSocketServer();
		}
		catch (SQLException | ClassNotFoundException e2)
		{
			e2.printStackTrace();
		}
        finally
        {
        	db.close_resorce(null, conn, ps);
        }
	}
}
