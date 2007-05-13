import java.awt.Component;
import java.io.File;

public class Client
{
	ClientInput clientInput;
	public Logger myLogger;
	ClientIO myIO;
	World w;

	public Client(String _server, Representation _rep, ElementFactory _ef, File _dataDir, int _port, boolean _verbose )
	{
		myLogger = new Logger( _verbose );

		ActionFactory af = new ActionFactory(_dataDir, myLogger);
		RuleFactory rf = new RuleFactory(_dataDir, af, _ef, myLogger);
		RepresentationResolver repResolver = new RepresentationResolver(_rep, myLogger);

		w = new World(_ef,myLogger);
		Resolver r = new Resolver(w, rf, af, _ef, myLogger);
		clientInput = new ClientInput(r,repResolver,af,myLogger);
		r.start();
		
		myIO = new ClientIO( r, _server, _port, myLogger );
		r.setIO(myIO);
		int id = myIO.getId();
		Action a = af.getAction("add element");
		GameElement ge = _ef.getGameElement("face");
		ge.id(id);
		ge.setPosition(new float[]{
				Constants.INITIAL_X,
				Constants.INITIAL_Y,
				Constants.INITIAL_Z
			});

		a.parameters().add(true); // yes, we want to pass this to the server
		a.parameters().add(ge);
		r.addAction(a);
		clientInput.setMe(ge);
		myIO.startListening();

		_rep.initialize(w, myLogger);
		r.setRepresentationResolver(repResolver);

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
		// Take user input.
		// Options:
		//  -p <port> 
		//  -verbose
		//  -dir <datadir>
		//  -eext <elementext>
		//  -efiles <elementfile> [elementfile [elementfile [...]]]
		int port = Constants.DEF_PORT;
		boolean verbose = false;
		String server = Constants.DEF_SERVER;
		int i;
		File dataDir = new File(Constants.DEFAULT_DATA_DIR);
		String elementExt = Constants.ELEMENT_LIST_EXTENSION;
		File[] elementFiles = null;

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
			else if(args[i].equalsIgnoreCase("-dir"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -dir option. Syntax: -dir <directory>");
					System.exit(1);
				}
				dataDir = new File(args[++i]);
			}
			else if(args[i].equalsIgnoreCase("-eext"))
			{
				if(args.length == i)
				{
					System.err.println("Bad usage of -eext option. Syntax: -eext <element file ext>");
					System.exit(1);
				}
				elementExt = args[++i];
			}
			else if(args[i].equalsIgnoreCase("-efiles"))
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
				System.out.println(" -dir <dir>\tLook for data files in directory <dir>");
				System.out.println(" -eext <ext>\tLook in data dir for Element List files that have extension <ext>");
				System.out.println(" -efiles <file> [file [file [...]]]\tOnly look for elements in the files specified");
				System.exit(0);
			}
			else
			{
				System.err.println("Bad argument (ignoring): " + args[i]);
			}
		}

		if(server.equals(""))
		{
			System.out.println("Missing: server.\nPlease see: java Client -h");
			System.exit(0);
		}

		Logger myLogger = new Logger( verbose);
		ElementFactory ef;
		if(elementFiles != null)
			ef = new ElementFactory(elementFiles, myLogger);
		else
			ef = new ElementFactory(dataDir, elementExt, myLogger);

		new Client(server, rep, ef, dataDir, port, verbose);
	}

}
