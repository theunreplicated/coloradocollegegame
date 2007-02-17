import java.io.*;
import java.net.*;

public class Server implements IO
{

	/*  declare global variables:
		threads -> 	how many clients are we connected to?  We need to be able to 
				send messages to all the clients, so we keep track of them...
		numThreads ->	pretty obvious.  number of threads in the threads array.
	*/
	private ClientThread[] threads = new ClientThread[Constants.MAX_CONNECTIONS];
	private int[] ids = new int[Constants.MAX_CONNECTIONS];
	private int nextId = 0;
	private World myWorld;
	public Logger myLogger;

	public Server( Logger _logger ,int _port)
	{
		myLogger = _logger;
		myWorld = new World( new GameElement[Constants.MAX_CONNECTIONS] , myLogger );

		for( int i = ids.length - 1; i >= 0; i-- )
			ids[i] = -1;
		ServerSocket serve = null;
		Socket connection = null;

		try
		{
			serve = new ServerSocket(_port);
		}
		catch( IOException ioe )
		{
			System.err.println( ioe.getMessage() );
		}

		myLogger.message("Starting server on port " + _port + "\n", false);

		while(true)
		{
			try
			{
				connection = serve.accept();
			}
			catch(IOException ioe)
			{
				myLogger.message("Server exception: " + ioe.getMessage() + "\n", true);
			}

			createNewThread(connection);
		}
	}

	/* 	This method creates a new thread.  we pass it off to a method
		so we can keep track of the size of the threads array and increment it or
		throw an error if we have to many threads. */
	private void createNewThread(Socket _conn)
	{
		synchronized(threads)
		{
			int i;
			for( i = ids.length - 1; i >= 0; i-- ) 
			{
				if( ids[i] < 0 )
				{
					myLogger.message( "Creating client connection thread in row " + i + "\n", false );
					threads[i] = new ClientThread(this, _conn, nextId, i);
					threads[i].start();
					ids[i] = nextId;
					nextId++;
					
					propagate( new int[] {	Constants.ADD_PLAYER , i , Constants.INITIAL_X , Constants.INITIAL_Y , Constants.INITIAL_Z, Constants.STATUS_DEFAULT } , i );

					return;
				}
				
			}

			myLogger.message("Too many connections!\n", true);
			return;
		}
	}
	
	public void removeThread( int _row )
	{
		synchronized(threads)
		{
			ids[_row] = -1;
		}
	}

	public void send( int[] _message)
	{

		synchronized(threads)
		{
			int i;
			for( i = ids.length - 1; i >= 0; i-- ) 
			{
				if( ids[i] >= 0)
				{
					threads[i].send(_message);
				}
			}
		}
	}

	public void propagate( int[] _message , int _row )
	{

		synchronized(threads)
		{
			int i;
			myWorld.parse(_message);
			for( i = ids.length - 1; i >= 0; i-- ) 
			{
				if( ids[i] >= 0 && _row != i )
				{
					threads[i].send(_message);
				}
				
			}
		}
	}

	public void sendWorld(  int _row )
	{
		synchronized(threads)
		{
			int i;
			int[] message = new int[2+Constants.ELEMENT_INFO_SIZE];
			
			for( i = ids.length - 1; i >= 0; i-- ) 
			{
				if( ids[i] >= 0 && _row != i )
				{
					message[0] = Constants.ADD_PLAYER;
					message[1] = i;
					System.arraycopy(myWorld.getElementInfo(i),0,message,2,Constants.ELEMENT_INFO_SIZE);
					threads[_row].send(message);
				try {
					// Thread.sleep(20);
				}
				catch(Exception e)
				{
					System.exit(0);
				}
				}
				
			}
		}
	}

	public static void main(String args[])
	{
		// Take user input.
		// Options:
		//  port#
		//  -verbose
		int port = Constants.DEF_PORT;
		boolean verbose = false;
		for(int i=0; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-v"))
			{
				verbose = true;
				continue;
			}
			try
			{
				port = Integer.parseInt(args[i]);
				if(port > Constants.MAX_PORT || port < Constants.MIN_PORT)
					port = Constants.DEF_PORT;
			}
			catch(NumberFormatException nfe)
			{
				port = Constants.DEF_PORT;
			}
		}

		Logger myLogger = new Logger( verbose);

		Server s = new Server( myLogger , port);
	}
}
