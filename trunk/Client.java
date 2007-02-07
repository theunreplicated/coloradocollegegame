import javax.swing.*;

public class Client
{
	public static void main(String args[])
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
				port = Integer.parseInt(args[++i]);
			}
			else if(args[i].equalsIgnoreCase("-h"))
			{
				System.out.println("Syntax: java Client [options]");
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

		Client myClient = new Client( server, port, verbose );
	}

	public Logger logger;
	public int id;

	public Client( String _server, int _port, boolean _verbose )
	{
		logger = new Logger( _verbose );

		Canvas2D canvas = new Canvas2D( );

		World w = new World(canvas.elements, logger);

		w.setRepresentation( canvas );

		ClientIO myIO = new ClientIO( this , w , _server, _port, logger );

		w.setIO( myIO );

		canvas.setListeners( myIO );

		JFrame frame = new JFrame();
		frame.getContentPane().add( canvas );
		frame.pack();
		frame.setVisible( true ) ;
	}

}
