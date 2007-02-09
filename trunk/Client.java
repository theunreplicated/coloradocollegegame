import javax.swing.*;

public class Client
{
	public Logger logger;
	public int id;
	ClientIO myIO;

	public Client(Representation rep, Element[] repElements, String _server, int _port, boolean _verbose )
	{
		logger = new Logger( _verbose );

		World w = new World(repElements, logger);

		w.setRepresentation( rep );

		myIO = new ClientIO( this , w , _server, _port, logger );

		w.setIO( myIO );

	}

	public void moveSelf(int direction)
	{
		myIO.moveSelf(direction);
	}


}
