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
			serverListener = new ServerListenerThread(servConnection, myClient, myWorld);
			serverListener.start();
			myWorld.setIO(this);

			myWorld.addElement(new int[] {id, 1, Constants.INITIAL_X, Constants.INITIAL_Y, Constants.INITIAL_Z, Constants.STATUS_DEFAULT , 1}, 0).toggleIsClient();
			this.send(new int[] {Constants.ADD_PLAYER,id, 1, Constants.INITIAL_X, Constants.INITIAL_Y, Constants.INITIAL_Z, Constants.STATUS_DEFAULT});
		}
		catch(IOException ioe)
		{
			myLogger.message( "Failed to connect to server: " + ioe.getMessage() + "\n" , true );
		}
	}

	public void send( int[] _message )
	{
		try
		{
			servOut.write( Constants.toByteArray(_message) );
			servOut.write( Constants.toByteArray(new int[] { Integer.MAX_VALUE }) );
		}
		catch( IOException ioe )
		{
			myLogger.message( "Failed to send to server: " + ioe.getMessage() + "\n", true );
		}
	}

	public void moveSelf(int direction)
	{
		myWorld.nudgeElement(myClient.id, ((direction-1)/2) * ( (direction%2)*2-1),
						  ((4-direction)/2) * ( (direction%2)*2-1), 0); // add Z direction
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

				int i,messageLength,start,startRead;
				int[] intMessage;
				int[] subMessage;
				start = 0;
				startRead = 0;

				while( (messageLength=servIn.read(message,startRead,Constants.MESSAGE_SIZE - startRead)) != -1 )
				{
			/*		subMessage = Constants.fromByteArray( message );
							System.out.print("message! => start: " + startRead + "  message: " );
							for(int x = 0; x < subMessage.length; x++)
								System.out.print( subMessage[x] + " " );
							System.out.println();*/

					intMessage = Constants.fromByteArray(message);
					messageLength = (messageLength+startRead)/4;

					for(i=0; i< messageLength; i++)
					{
						if( intMessage[i] == Integer.MAX_VALUE )
						{
							subMessage = new int[i-start];
							System.arraycopy(intMessage,start,subMessage,0,i-start);
							myWorld.parse( subMessage );
							start = i+1;
						}
					}
					if( start < messageLength )
					{
						System.arraycopy(Constants.toByteArray(intMessage),start*4,message,0,(messageLength-start)*4);
						startRead = (messageLength-start)*4;
					}
					else startRead = 0;
					start = 0;
				}
			}
			catch(IOException ioe)
			{
				myLogger.message( "Error while listening for server input: " + ioe.getMessage() + "\n", true );
			}
		}

	}
}
