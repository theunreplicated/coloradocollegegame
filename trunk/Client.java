import javax.swing.*;

public class Client
{
	ClientInput clientInput;
	public Logger myLogger;
	ClientIO myIO;
	World w;

	public Client(String _server, int _port, boolean _verbose )
	{
		myLogger = new Logger( _verbose );

		ElementFactory ef = new ElementFactory(myLogger);
		ActionFactory af = new ActionFactory(myLogger);
		RuleFactory rf = new RuleFactory(af,ef,myLogger);

		w = new World(ef,myLogger);
		Resolver r = new Resolver(w, rf, af,myLogger);
		clientInput = new ClientInput(r,af,myLogger);

		myIO = new ClientIO( clientInput , r, w , _server, _port, myLogger );
	}

	public static Client initialize(String args[])
	{
		boolean verbose = false;
		int port = Constants.DEF_PORT;
		String server = Constants.DEF_SERVER;
		int i;
		for(i = 0; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-v"))
			{
				verbose = true;
			}
			else if(args[i].equalsIgnoreCase("-s"))
			{
				server = args[++i];
			}
			else if(args[i].equalsIgnoreCase("-p"))
			{
				try
				{
					port = Integer.parseInt(args[++i]);
				}
				catch(NumberFormatException nfe)
				{
					System.err.println("Bad input on port option (-p). Port must be a positive integer.");
					port = Constants.DEF_PORT;
				}
				catch(ArrayIndexOutOfBoundsException aioobe)
				{
					System.err.println("No port given with -p command");
					port = Constants.DEF_PORT;
				}
			}
			else if(args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("-help"))
			{
				System.out.println("Syntax: java Represenation<2D|3D> [options]");
				System.out.println("Options:");
				System.out.println(" -h\t\tPrint this help screen");
				System.out.println(" -v\t\tRun in verbose mode");
				System.out.println(" -s <domain>\tRun on server at domain <domain>");
				System.out.println(" -p <port>\tRun on port # <port>");
				System.exit(0);
			}
		}

		if(server.equals(""))
		{
			System.out.println("Missing: server.\nPlease see: java Client -h");
			System.exit(0);
		}

		return ( new Client(server, port, verbose) ); 
		

	}

	/* Depricated ( from when GameElements were stored in an array )
	public GameElement[] getWorldElements()
	{
		return w.getElements();
	} */

	public GameElement getWorldElements()
	{
		return w.getFirstElement();
	}

	public ClientInput getClientInput()
	{
		return clientInput;
	}

	public Logger getLogger()
	{
		return myLogger;
	}
}
