import java.awt.event.*;
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
	private Client myClient;
														   //pressed
	private Socket servConnection;
	private ServerListenerThread serverListener;
	private OutputStream servOut;
	private World myWorld;
	private Logger myLogger;
	private ClientInput input;

	public ClientIO(Client _myClient, World _myWorld, String _server, int _port, Logger _logger )
	{
		myClient = _myClient;
		myWorld = _myWorld;
		myLogger = _logger;
		input = new ClientInput(this,myLogger);
		id = 0;
		try
		{
			myLogger.message("Connecting...\n", false);
			servConnection = new Socket(InetAddress.getByName(_server), _port);
			servOut = servConnection.getOutputStream();
			id = servConnection.getInputStream().read();
			myClient.id = id;

			myLogger.message("Connected as id: " + id + "\n", false);
			myWorld.setIO(this);
			myWorld.addElement(new Object[] {	id,
								"face",
								new float[] {
									Constants.INITIAL_X,
									Constants.INITIAL_Y,
									Constants.INITIAL_Z
										}
									},
								0);

			serverListener = new ServerListenerThread(servConnection, myClient, myWorld);
			serverListener.start();

			this.send(new Object[] {		Constants.ADD_PLAYER,
								id,
								"face",
								new float[] {
									Constants.INITIAL_X,
									Constants.INITIAL_Y,
									Constants.INITIAL_Z
										}
								});
		}
		catch(IOException ioe)
		{
			myLogger.message( "Failed to connect to server: " + ioe.getMessage() + "\n" , true );
		}
	}

	public void send( Object _message )
	{
		try
		{
			servOut.write( Constants.toByteArray(_message) );
		}
		catch( IOException ioe )
		{
			myLogger.message( "Failed to send to server: " + ioe.getMessage() + "\n", true );
		}
	}

	//move along the specified vector
	public void moveSelf(float[] v)
	{
		myWorld.nudgeElement(myClient.id, v);
	}
	
	public void rotateSelf(float[] q)
	{
		myWorld.rotateElement(myClient.id, q);
	}

	public void changeAttribute(String k, Object v)
	{
		myWorld.attributeElement(myClient.id, k, v);
	}

	public ClientInput getClientInput()
	{
		return input;
	}

	private class ServerListenerThread extends Thread
	{
		private InputStream servIn;
		private Socket server;
		private World myWorld;
		private Client myClient;

		public ServerListenerThread(Socket _server, Client _myClient, World _myWorld)
		{
			server = _server;
			myWorld = _myWorld;
			myClient = _myClient;
		}
		
		public void run()
		{
			try
			{
				servIn = server.getInputStream();
				byte[] message = new byte[Constants.MESSAGE_SIZE];

				int i;
				Object[] objectMessage;

				while( (servIn.read(message)) != -1 )
				{
					objectMessage = (Object[]) Constants.fromByteArray(message);
					myWorld.parse(objectMessage);
				}
			}
			catch(IOException ioe)
			{
				myLogger.message( "Error while listening for server input: " + ioe.getMessage() + "\n", true );
			}
		}

	}
}
