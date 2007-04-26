import java.awt.Component;

public class Client
{
	ClientInput clientInput;
	public Logger myLogger;
	ClientIO myIO;
	World w;

	public Client(String _server, Representation _rep, int _port, boolean _verbose )
	{
		myLogger = new Logger( _verbose );

		ElementFactory ef = new ElementFactory(myLogger);
		ActionFactory af = new ActionFactory(myLogger);
		RuleFactory rf = new RuleFactory(af,ef,myLogger);
		RepresentationResolver repResolver = new RepresentationResolver(_rep, myLogger);

		w = new World(ef,myLogger);
		Resolver r = new Resolver(w, rf, af, ef, myLogger);
		clientInput = new ClientInput(r,repResolver,af,myLogger);
		
		myIO = new ClientIO( r, _server, _port, myLogger );
		r.setIO(myIO);
		int id = myIO.getId();
		Action a = af.getAction("add element");
		GameElement ge = ef.getGameElement("R2");
		ge.id(id);
		ge.setPosition(new float[]{
				Constants.INITIAL_X,
				Constants.INITIAL_Y,
				Constants.INITIAL_Z
			});

		a.parameters().add(true); // yes, we want to pass this to the server
		a.parameters().add(ge);
		r.parse(a);
		myIO.startListening();
		clientInput.setMe(ge);
		r.setRepresentationResolver(repResolver);
		_rep.initialize(w, myLogger);

		//set listeners for clientInput
		Component comp = _rep.getComponent();
		if(comp != null) //in case a Representation doesn't have a Component to listen on
		{
			comp.setFocusable(true);
			comp.addKeyListener(clientInput);
			comp.addMouseListener(clientInput);
			comp.addMouseMotionListener(clientInput);		
		}
	}

	public static void initialize(String args[], Representation rep)
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

		new Client(server, rep, port, verbose);
	}

}
