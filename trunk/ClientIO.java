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
	private Socket servConnectionIn;
	private Socket servConnectionOut;
	private ServerListenerThread serverListener;
	private ObjectOutputStream oos;
	private World myWorld;
	private Resolver resolver;
	private Logger myLogger;
	private ClientInput input;
	private Representation rep;

	public ClientIO(ClientInput _clientInput, Resolver _resolver, World _myWorld, String _server, int _port, Logger _logger )
	{
		myWorld = _myWorld;
		resolver = _resolver;
		myLogger = _logger;
		id = 0;
		try
		{
			myLogger.message("Connecting...\n", false);
			servConnectionIn = new Socket(InetAddress.getByName(_server), _port);
			ObjectInputStream ois = new ObjectInputStream(servConnectionIn.getInputStream());
			id = ois.readInt();

			// get the output stream
			myLogger.message("Starting temporary server to get output stream...\n", false);
			ServerSocket serve = new ServerSocket(_port+1);
			servConnectionOut = serve.accept();

			myLogger.message("Stopping temporary server with which we got an output stream...\n", false);
			serve.close();

			oos = new ObjectOutputStream(servConnectionOut.getOutputStream());

			myLogger.message("Connected as id: " + id + "\n", false);
			myWorld.setIO(this);

			GameElement ge = myWorld.addElement(new Object[] {	id,
								"R2",
								new float[] {
									Constants.INITIAL_X,
									Constants.INITIAL_Y,
									Constants.INITIAL_Z
										}
									},
								0);
			_clientInput.setMe(this,ge);

			serverListener = new ServerListenerThread(ois, myWorld);
			serverListener.start();

			this.send(new Object[] {		Constants.ADD_PLAYER,
								id,
								"R2",
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

	// A temporary function to set the Representation, until we get a
	// RepresentationResolver in place to handle user input that is based
	// on or affects the Representation.
	public void setRepresentation(Representation _rep)
	{
		rep = _rep;
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

	//move along the specified vector RELATIVE to current facing
	public void moveSelf(float[] v)
	{
		myWorld.nudgeElement(id, Quaternions.rotatePoint(v,
							myWorld.getElementFacing(id)));
	}
	
	//move along the specified vector INDEPENDENT of current facing
	public void moveSelfAbsolute(float[] v)
	{
		myWorld.nudgeElement(id, v);
	}
	
	//move along the specified vector relative to the camera's current facing
	public void moveSelfRelativeCamera(float[] v)
	{
		//change this--how does ClientIO see the Representation?
		myWorld.nudgeElement(id, v);
	}
	
	public void rotateSelf(float[] q)
	{
		myWorld.rotateElement(id, q);
	}

	public void changeAttribute(String k, Object v)
	{
		myWorld.attributeElement(id, k, v);
	}

	public ClientInput getClientInput()
	{
		return input;
	}

	private class ServerListenerThread extends Thread
	{
		private ObjectInputStream ois;
		private World myWorld;

		public ServerListenerThread(ObjectInputStream _ois, World _myWorld)
		{
			ois = _ois;
			myWorld = _myWorld;
		}
		
		public void run()
		{
			try
			{
				Object objectMessage;

				while( (objectMessage = ois.readObject()) != null)
				{
					resolver.parseOld(objectMessage);
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
