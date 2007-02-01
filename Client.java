import javax.swing.*;

public class Client
{
	public static void main(String args[])
	{
		boolean verbose = false;
		if(args.length > 0 && args[0].substring(0,2).equalsIgnoreCase("-v"))
		{
			verbose = true;
		}

		Client myClient = new Client( verbose );
	}

	public Logger logger;
	public int id;

	public Client( boolean _verbose )
	{
		logger = new Logger( _verbose );

		Canvas2D canvas = new Canvas2D( );

		World w = new World(canvas.elements, logger);

		w.setRepresentation( canvas );

		ClientIO myIO = new ClientIO( this , w , logger );

		w.setIO( myIO );

		canvas.setListeners( myIO );

		JFrame frame = new JFrame();
		frame.getContentPane().add( canvas );
		frame.pack();
		frame.setVisible( true ) ;
	}

}
