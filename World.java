public class World
{
	private IO myIO;
	private Representation myRepresentation = null;
	public Element[] elements;	
	public Logger myLogger;

	public World(Element[] _elements, Logger _logger)
	{
		myLogger = _logger;
		elements = _elements;
	}

	public void setRepresentation( Representation _representation )
	{
		myRepresentation = _representation;
	}

	public void setIO( IO _io )
	{
		myIO = _io;
	}
	
	public int[] getElementInfo( int _row )
	{
		return elements[_row].getInfoArray();
	}

	public void removePlayer(int _id)
	{
		elements[_id] = null;
	}

	public Element addPlayer(int _id, int _x, int _y , int _z , int _status)
	{
		if( myRepresentation != null )
			elements[_id] = myRepresentation.createPerson(_x,_y,_z,Constants.PERSON_WIDTH,Constants.PERSON_HEIGHT,_status);
		else
			elements[_id] = new Element(_x,_y,_z,Constants.PERSON_WIDTH,Constants.PERSON_HEIGHT,_status);

		return elements[_id];
	}
	
	public void nudgeElement( int _row, int _dx, int _dy, int _dz )
	{
		myLogger.message( "attempting to nudge element at " + _row + "(" + _dx + "," + _dy + "," + _dz + ")\n" , false );
		elements[_row].nudge( _dx , _dy , _dz );
		myIO.send(new int[] {Constants.MOVE_TO, _row, elements[_row].getPosition(Constants.X), elements[_row].getPosition(Constants.Y), elements[_row].getPosition(Constants.Z)});
	}

	public void setPosition( int _row, int _x, int _y , int _z )
	{
		elements[_row].setPosition( _x , _y , _z);
	}
	
	public int parse(int[] message)
	{
			switch(message[0])
			{
				case Constants.MOVE_TO:
					setPosition( message[1],  message[2],  message[3], message[4]);
					myLogger.message("Recieved move command for person " + message[1] + "\n", false);
					break;
				case Constants.ADD_PLAYER:
					addPlayer( message[1],  message[2],  message[3],  message[4], message[5]);
					myLogger.message("Recieved add command for person " + message[1] + "\n", false);
					break;
				case Constants.REMOVE_PLAYER:
					removePlayer( message[1] );
					myLogger.message("Removing player: " + message[1] + "\n", false);
					break;
				default:
					myLogger.message("Received unparsable message: " + message[0] + "\n", true);
		}

		return Constants.SUCCESS; //eventually every action in the world will return an int for whether or not it was a valid action
	}
}
