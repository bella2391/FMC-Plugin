package fabric;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import org.slf4j.Logger;

import com.google.inject.Inject;

public class SocketSwitch
{
    public String sendmsg;
    
    private Thread clientThread;
    private Thread socketThread;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    public final Logger logger;
    private final Config config;
    
    @Inject
	public SocketSwitch(Logger logger, Config config)
	{
		this.logger = logger;
		this.config = config;
	}
	
	//Client side
	public void startSocketClient(String sendmsg) 
	{
	    if (config.getInt("Socket.Client_Port") == 0) 
	    {
	        logger.info("Client Socket is canceled for config value not given");
	        return;
	    }
	    logger.info("Client Socket is Available");

	    clientThread = new Thread(() -> {
	        sendMessage(sendmsg);
	    });

	    clientThread.start();
	}

	private void sendMessage(String sendmsg)
	{
	    String hostname = "localhost";

	    try
	    (
	    	Socket socket = new Socket(hostname, config.getInt("Socket.Client_Port"));
	    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
	    )
	    {
	    	writer.write(sendmsg + "\n");
	    	writer.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

    public void stopSocketClient()
    {
        try
        {
            if (clientThread != null && clientThread.isAlive())
            {
                clientThread.interrupt();
                clientThread.join();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    //Server side
    public void startSocketServer()
    {
		if(Objects.isNull(config.getInt("Socket.Server_Port")) || config.getInt("Socket.Server_Port") ==0)
		{
			logger.info("Socket Server is canceled for config value not given");
			return;
		}
		logger.info("Server Socket is Available");
        socketThread = new Thread(() ->
        {
            try
            {
                serverSocket = new ServerSocket(config.getInt("Socket.Server_Port"));
                logger.info("Socket Server is listening on port " + config.getInt("Socket.Server_Port"));

                while (running)
                {
                    try
                    {
                        Socket socket = serverSocket.accept();
                        if (!running)
                        {
                            socket.close();
                            break;
                        }
                        logger.info("New client connected");
                        new SocketServerThread(socket, logger).start();
                    }
                    catch (Exception e)
                    {
                        if (running) 
                        {
                            logger.error("Error accepting client connection");
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Socket Server socket error");
                e.printStackTrace();
            } 
           finally
           {
                try
                {
                    if (serverSocket != null && !serverSocket.isClosed())
                    {
                        serverSocket.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        socketThread.start();
    }

    public void stopSocketServer()
    {
        running = false;
        try
        {
            if (serverSocket != null && !serverSocket.isClosed()) 
            {
                serverSocket.close(); // これによりaccept()が解除される
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            if (socketThread != null && socketThread.isAlive()) 
            {
                socketThread.join(1000); // 1秒以内にスレッドの終了を待つ
                if (socketThread.isAlive())
                {
                    socketThread.interrupt(); // 強制的にスレッドを停止
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
