import java.util.*;

public class World
{
	private IO myIO;
	private GameElement[] elements =  new GameElement[Constants.GAME_ELEMENT_INCREMENT];	
	public int nextElement = 0;
	private ElementFactory ef;
	public Logger myLogger;
	private HashMap<Integer,Integer> elementIDs = new HashMap<Integer,Integer>();

	public World(ElementFactory _ef,Logger _logger)
	{
		myLogger = _logger;
		ef = _ef;
	}

	public void setIO( IO _io )
	{
		myIO = _io;
	}
	
	public GameElement[] getElements()
	{
		return elements;
	}

	public int[] getElementInfo( int _row )
	{
		System.out.println( _row );
		return elements[elementIDs.get(_row)].getInfoArray();
	}

	public void removeElement(int[] _message, int _start)
	{
		elements[elementIDs.get(_message[_start])] = null;
	}

	public GameElement addElement(int[] _message, int _start)
	{
		int _id = _message[_start++];
		int _type = _message[_start++];
		int _x = _message[_start++];
		int _y = _message[_start++];
		int _z = _message[_start++];
		int _status = _message[_start++];
		
		if(nextElement == elements.length)
		{
			GameElement[] tmp = new GameElement[elements.length+Constants.GAME_ELEMENT_INCREMENT];
			System.arraycopy(elements,0,tmp,0,elements.length);
			elements = tmp;
		}

		elements[nextElement] = ef.getGameElement(_type);
		elementIDs.put(_id,nextElement);


		return elements[nextElement++];
	}
	
	public void nudgeElement( int _row, int _dx, int _dy, int _dz )
	{
		int id = elementIDs.get(_row);
		elements[id].nudge( _dx , _dy , _dz );
		myIO.send(new int[] {Constants.MOVE_TO, _row, elements[id].getPosition(Constants.X), elements[id].getPosition(Constants.Y), elements[id].getPosition(Constants.Z)});
		myLogger.message( "nudge position: " + _row + " (" + elements[id].getPosition(0) + "," + elements[id].getPosition(1) + "," + elements[id].getPosition(2) + ")\n" , false );
	}

	public void setPosition( int[] _message, int _start)
	{
			int id = elementIDs.get(_message[_start]);
			int _x = _message[_start+1];
			int _y = _message[_start+2];
			int _z = _message[_start+3];
			elements[id].setPosition( _x, _y, _z);
			myLogger.message( "move position: " + _message[_start] + " (" + elements[id].getPosition(0) + "," + elements[id].getPosition(1) + "," + elements[id].getPosition(2) + ")\n" , false );
	}
	
	public int parse(int[] message)
	{
			switch(message[0])
			{
				case Constants.MOVE_TO:
					setPosition( message, 1);
					break;
				case Constants.ADD_PLAYER:
					addElement( message, 1);
					break;
				case Constants.REMOVE_PLAYER:
					removeElement( message, 1 );
					myLogger.message("Removing player: " + message[1] + "\n", false);
					break;
				default:
					myLogger.message("Received unparsable message: " + message[0] + "\n", true);
		}

		return Constants.SUCCESS; //eventually every action in the world will return an int for whether or not it was a valid action
	}
}
