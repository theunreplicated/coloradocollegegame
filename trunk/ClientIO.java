import java.net.*;
import java.io.*;

/**
 * The Controller class is designed to take input from the user
 * in the form of keyboard and mouse sequences to interact with 
 * viewable area and world. 
 */

public class ClientIO implements IO
{
	//global variables
	public int id;
	private Socket servConnectionIn;
	private Socket servConnectionOut;
	private ServerListenerThread serverListener;
	private ObjectOutputStream oos;
	private Resolver resolver;
	private Logger myLogger;
	private ObjectInputStream ois = null;

	public ClientIO(Resolver _resolver, String _server, int _port, Logger _logger )
	{
		resolver = _resolver;
		myLogger = _logger;
		id = 0;
		try
		{
			myLogger.message("Connecting...\n", false);
			servConnectionIn = new Socket(InetAddress.getByName(_server), _port);
			ois = new ObjectInputStream(servConnectionIn.getInputStream());
			int outputPort = ois.readInt();
			myLogger.message("Output port is: " + outputPort + "\n", false);

			// get the output stream
			int wait = 0;
			while(wait < Constants.TIMEOUT && servConnectionOut == null)
			{
				try	
				{
					myLogger.message("ClientIO trying to connect to: " + servConnectionIn.getInetAddress() + ":" + (servConnectionIn.getLocalPort()+1)+"\n",false);
					servConnectionOut = new Socket(InetAddress.getByName(_server), outputPort);
				}
				catch(IOException ioe)
				{
					myLogger.message("ClientIO could not get output stream from server... waiting a bit longer\n", false);
					try
					{
						Thread.sleep(1000);
					}
					catch(InterruptedException ie)
					{
						myLogger.message("ClientIO interrupted while waiting to get output stream for server\n", true);
					}
					wait += 1000;
					servConnectionOut = null;
				}
			}
			if(servConnectionOut == null)
			{
				myLogger.message("Failed to get output stream from server... Quitting.\n",true);
				System.exit(0);
			}

			id = ois.readInt();
/*
			myLogger.message("Starting temporary server to get output stream...\n", false);
			ServerSocket serve = new ServerSocket(_port+1);
			servConnectionOut = serve.accept();

			myLogger.message("Stopping temporary server with which we got an output stream...\n", false);
			serve.close();
*/
			oos = new ObjectOutputStream(servConnectionOut.getOutputStream());

			myLogger.message("Connected as id: " + id + "\n", false);


		}
		catch(IOException ioe)
		{
			myLogger.message( "Failed to connect to server: " + ioe.getMessage() + "\n" , true );
			System.exit(0);
		}
	}

	public int getId()
	{
		return id;
	}
	public void startListening()
	{
			serverListener = new ServerListenerThread();
			serverListener.listenOnce(); // listen for the world
			serverListener.start();
	}

	public void send( Object _message )
	{
		try
		{
			oos.writeObject(_message);
			oos.flush();
		}
		catch( IOException ioe )
		{
			myLogger.message( "Failed to send to server: " + ioe + "\n", true );
			ioe.printStackTrace();
		}
	}

	private class ServerListenerThread extends Thread
	{
		public void listenOnce()
		{
			try
			{
				Object objectMessage;

				myLogger.message("Starting to listen (once)...\n", false);
				objectMessage = ois.readObject();
				myLogger.message("Recieved message (once)...\n", false);
				resolver.addAction(objectMessage);
			}
			catch(IOException ioe)
			{
				myLogger.message( "Error while listening for server input: " + ioe.getMessage() + "\n", true );
				System.exit(0);
			}
			catch(ClassNotFoundException cnfe)
			{
				myLogger.message( "Error while listening for server input (class not found): " + cnfe + "\n", true );
				System.exit(0);
			}

		}
		public void run()
		{
			try
			{
				Object objectMessage;

				myLogger.message("Starting to listen...\n", false);
				while( (objectMessage = ois.readObject()) != null)
				{
					myLogger.message("Recieved message...\n", false);
					resolver.addAction(objectMessage);
				}
			}
			catch(IOException ioe)
			{
				myLogger.message( "Error while listening for server input: " + ioe.getMessage() + "\n", true );
				System.exit(0);
			}
			catch(ClassNotFoundException cnfe)
			{
				myLogger.message( "Error while listening for server input (class not found): " + cnfe + "\n", true );
				System.exit(0);
			}
		}

	}
}
