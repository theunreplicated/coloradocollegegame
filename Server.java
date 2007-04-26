import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Server implements IO
{

	/*  declare global variables:
		threads -> 	how many clients are we connected to?  We need to be able to 
				send messages to all the clients, so we keep track of them...
		numThreads ->	pretty obvious.  number of threads in the threads array.
	*/
	private ClientThread[] threads = new ClientThread[Constants.MAX_CONNECTIONS];
	private int[] ids = new int[Constants.MAX_CONNECTIONS];
	private World myWorld;
	private Resolver resolver;
	private ActionFactory actionFactory;
	private Logger myLogger;
	
	//an array of MovingElement references so we can start/stop them all at once.
	//could change this later
	private ArrayList<MovingElement> movers = new ArrayList<MovingElement>(); 
	
	public Server(WorldFactory wf, ElementFactory ef, Logger _logger ,int _port)
	{

		myLogger = _logger;
		myWorld = new World(ef, myLogger);
		wf.fillWorld(myWorld);

		actionFactory = new ActionFactory(myLogger);
		RuleFactory rf = new RuleFactory(actionFactory,ef,myLogger);
		resolver = new Resolver(myWorld, rf, actionFactory, ef, myLogger);
		resolver.setIO(this);

		myLogger.message("\n" + myWorld.toString(), false);

		createMovingObjects(); //moved into it's own method for easy portability

		for( int i = ids.length - 1; i >= 0; i-- )
			ids[i] = Constants.CONNECTION_FREE;
		ServerSocket serve = null;
		Socket connection = null;

		try
		{
			serve = new ServerSocket(_port);
		}
		catch( IOException ioe )
		{
			myLogger.message("Failed to start server: " + ioe + "\n", true);
			System.exit(1);
		}

		myLogger.message("Starting server on port " + _port + "\n", false);

		startMovingObjects(); //moved into its own method for easy portability
	
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
				if( ids[i] == Constants.CONNECTION_FREE )
				{

					/* For an explanation of ELEMENT_ID_PADDING and id strategies
					 * in general, please see the long comment in Constants.java
					 * where ELMENT_ID_PADDING is declared.
					 */
					myLogger.message( "Creating client connection thread in row " + i + " (id is: " + ids[i] + ")\n", false );
					threads[i] = new ClientThread(this, _conn, i + 1 + Constants.ELEMENT_ID_PADDING, i, myLogger);
					threads[i].start();
					ids[i] = Constants.CONNECTION_PENDING;
					
					return;
				}
				
			}

			myLogger.message("Too many connections!\n", true);
			return;
		}
	}
	
	public void removeThread( int _row, int _id )
	{
		Action a = actionFactory.getAction("remove element");
		a.setNouns(new GameElement[]{myWorld.getElementById(_id)});
		a.parameters().add("true");
		synchronized(threads)
		{
			ids[_row] = Constants.CONNECTION_FREE;
		}
		resolver.parse(a);
	}

	public void send( Object _message)
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

	public void propagate( Object _message , int _row )
	{

		synchronized(threads)
		{
			int i;
			resolver.parse(_message);
			for( i = ids.length - 1; i >= 0; i-- ) 
			{
				if( ids[i] >= 0 && _row != i )
				{
					threads[i].send(_message);
				}
				
			}
		}
	}

	// fix to handle synchronization
	public void sendWorld(  int _row )
	{
		myLogger.message("Starting to send world to " + _row + "!\n",false);
		GameElement[] _elements = myWorld.getElements();

		Action a = actionFactory.getAction("receive world"); // tell the client to receive the world
		for(GameElement ge : _elements)
			a.parameters().add(ge);

		threads[_row].send(a);
		ids[_row] = _row + 1 + Constants.ELEMENT_ID_PADDING;
		myLogger.message("finished sending world to " + _row + "!\n",false);
	}

	//creates a bunch of MovingElements
	private void createMovingObjects()
	{
		GameElement first = myWorld.getFirstElement();
		GameElement e = first; //for looping
		Random rand = new Random();
		
		do
		{
			//do stuff to e
			if(e.attribute("moving")!=null)
			{
				int rmod = rand.nextInt(6)*5000;

				if((Integer)e.attribute("moving")==1)
				{
					//create down-moving wall
					movers.add(new MovingElement(resolver,actionFactory,e, new Object[]{
						new Object[]{ Constants.MOVE_TO, new float[]{0.0f,0.0f,10.0f}},
						new Object[]{ Constants.MOVE_TO, new float[]{0.0f,0.0f,-10.0f}}
							},5000+rmod,myLogger)); 
				}
				else if((Integer)e.attribute("moving")==2)
				{
					//create right-moving wall
					movers.add(new MovingElement(resolver,actionFactory,e, new Object[]{
						new Object[]{ Constants.MOVE_TO, new float[]{10.0f,0.0f,0.0f}},
						new Object[]{ Constants.MOVE_TO, new float[]{-10.0f,0.0f,0.0f}}
							},5000+rmod,myLogger)); 
				}
			}
			
			e = e.next;
		}while(e != first);
	}

	//starts the MovingElements moving
	private void startMovingObjects()
	{
		for(int i=0; i<movers.size(); i++)
			movers.get(i).start();
	}

	public static void main(String args[])
	{
		// Take user input.
		// Options:
		//  -p <port> 
		//  -verbose
		//  -dir <datadir>
		//  -eext <elementext>
		//  -wext <worldext>
		//  -wfiles <worldfile> [worldfile [worldfile [...]]]
		//  -efiles <elementfile> [elementfile [elementfile [...]]]
		int port = Constants.DEF_PORT;
		boolean verbose = false;
		File dataDir = new File(Constants.DEFAULT_DATA_DIR);
		String elementExt = Constants.ELEMENT_LIST_EXTENSION;
		String worldExt = Constants.WORLD_EXTENSION;
		File[] worldFiles = null;
		File[] elementFiles = null;
		for(int i=0; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-v"))
			{
				verbose = true;
				continue;
			}
			if(args[i].equalsIgnoreCase("-dir"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -dir option. Syntax: -dir <directory>");
					System.exit(1);
				}
				dataDir = new File(args[++i]);
				continue;
			}
			if(args[i].equalsIgnoreCase("-eext"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -eext option. Syntax: -eext <element file ext>");
					System.exit(1);
				}
				elementExt = args[++i];
				continue;
			}
			if(args[i].equalsIgnoreCase("-wext"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -wext option. Syntax: -wext <world file ext>");
					System.exit(1);
				}
				worldExt = args[++i];
				continue;
			}
			if(args[i].equalsIgnoreCase("-wfiles"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -wfiles option. Syntax: -wfiles <world file> [world file [world file [...]]]");
					System.exit(1);
				}
				File[] tmpFiles = new File[args.length];

				// We assume that there will be at least _one_ file.
				tmpFiles[0] = new File(args[i+1]);
				int j = 1;
				while(args.length > i+j+1 && !args[i+j+1].startsWith("-"))
				{
					tmpFiles[j] = new File(args[i+j+1]);
					j++;
				}
				worldFiles = new File[j];
				System.arraycopy(tmpFiles,0,worldFiles,0,j);
				i+=j;
				continue;
			}
			if(args[i].equalsIgnoreCase("-efiles"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -efiles option. Syntax: -efiles <element file> [element file [element file [...]]]");
					System.exit(1);
				}
				File[] tmpFiles = new File[args.length];

				// We assume that there will be at least _one_ file.
				tmpFiles[0] = new File(args[i+1]);
				int j = 1;
				while(args.length > i+j+1 && !args[i+j+1].startsWith("-"))
				{
					tmpFiles[j] = new File(args[i+j+1]);
					j++;
				}
				elementFiles = new File[j];
				System.arraycopy(tmpFiles,0,elementFiles,0,j);
				i+=j;
				continue;
			}
			if(args[i].equalsIgnoreCase("-p"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -p option. Syntax: -p <port>");
					System.exit(1);
				}
				try
				{
					port = Integer.parseInt(args[++i]);
					if(port > Constants.MAX_PORT || port < Constants.MIN_PORT)
						port = Constants.DEF_PORT;
				}
				catch(NumberFormatException nfe)
				{
					port = Constants.DEF_PORT;
				}
				continue;
			}
			if(args[i].equalsIgnoreCase("-help") || args[i].equalsIgnoreCase("-h"))
			{
				System.out.println("Syntax: java Server [options]");
				System.out.println("Options:");
				System.out.println(" -h\t\tPrint this help screen");
				System.out.println(" -v\t\tRun in verbose mode");
				System.out.println(" -p <port>\tRun on port # <port>");
				System.out.println(" -dir <dir>\tLook for data files in directory <dir>");
				System.out.println(" -eext <ext>\tLook in data dir for Element List files that have extension <ext>");
				System.out.println(" -wext <ext>\tLook in data dir for World files that have extension <ext>");
				System.out.println(" -efiles <file> [file [file [...]]]\tOnly look for elements in the files specified");
				System.out.println(" -wfiles <file> [file [file [...]]]\tBuild the world only out of the files specified");
				System.exit(0);
			}
			System.err.println("Bad argument (ignoring): " + args[i]);
		}

		Logger myLogger = new Logger( verbose);
		ElementFactory ef;
		WorldFactory wf;
		if(elementFiles != null)
			ef = new ElementFactory(elementFiles, myLogger);
		else
			ef = new ElementFactory(dataDir, elementExt, myLogger);

		if(worldFiles != null)
			wf = new WorldFactory(worldFiles, ef, myLogger);
		else
			wf = new WorldFactory(dataDir, worldExt, ef, myLogger);
		Server s = new Server(wf, ef, myLogger , port);
	}
}
