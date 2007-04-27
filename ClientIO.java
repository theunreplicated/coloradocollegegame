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
			id = ois.readInt();

			// get the output stream
			myLogger.message("Starting temporary server to get output stream...\n", false);
			ServerSocket serve = new ServerSocket(_port+1);
			servConnectionOut = serve.accept();

			myLogger.message("Stopping temporary server with which we got an output stream...\n", false);
			serve.close();

			oos = new ObjectOutputStream(servConnectionOut.getOutputStream());

			myLogger.message("Connected as id: " + id + "\n", false);


		}
		catch(IOException ioe)
		{
			myLogger.message( "Failed to connect to server: " + ioe.getMessage() + "\n" , true );
		}
	}

	public int getId()
	{
		return id;
	}
	public void startListening()
	{
			serverListener = new ServerListenerThread();
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
		public void run()
		{
			try
			{
				Object objectMessage;

				System.out.println("Starting to listen...");
				while( (objectMessage = ois.readObject()) != null)
				{
					System.out.println("Recieved message...");
					resolver.parse(objectMessage);
				}
			}
			catch(IOException ioe)
			{
				myLogger.message( "Error while listening for server input: " + ioe.getMessage() + "\n", true );
			}
			catch(ClassNotFoundException cnfe)
			{
				myLogger.message( "Error while listening for server input (class not found): " + cnfe + "\n", true );
			}
		}

	}
}
