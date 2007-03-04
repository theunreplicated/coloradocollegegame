import java.util.*;

public class World
{
	private IO myIO;
	private GameElement first = null;
	//private GameElement[] elements =  new GameElement[Constants.GAME_ELEMENT_INCREMENT];	
	public int nextElement = 0;
	private ElementFactory ef;
	public Logger myLogger;
	private HashMap<Integer, GameElement> elements = new HashMap<Integer, GameElement>();
	//private HashMap<Integer,Integer> elementIDs = new HashMap<Integer,Integer>();

	public World(ElementFactory _ef,Logger _logger)
	{
		myLogger = _logger;
		ef = _ef;
	}

	public void setIO( IO _io )
	{
		myIO = _io;
	}
	
	/* Depricated ( from when GameElements were stored in an array )
	public GameElement[] getElements()
	{
		return elements;
	} */

	public GameElement getFirstElement()
	{
			return first;
	}

	/* Depricated ( from when GameElements were stored in an array )
	public int[] getElementInfo( int _row )
	{
		System.out.println( _row );
		return elements[elementIDs.get(_row)].getInfoArray();
	}
	*/

	public int[] getElementInfo( int _row )
	{
		return elements.get(_row).getInfoArray();
	}

	public void removeElement(int[] _message, int _start)
	{
		GameElement toRemove = elements.get(_message[_start]);
		if(toRemove == null)
		{
			myLogger.message("removeElement tried to remove null Element with id: " + _start + "...\n", true);
			return;
		}
		toRemove.removeFromList();
		elements.remove(_message[_start]);

		synchronized( first )
		{
			toRemove.changed = true;
			first.notifyAll();
		}
	}

	public GameElement addElement(int[] _message, int _start)
	{
		int _id = _message[_start++];
		int _type = _message[_start++];
		int[] _pos = new int[_message.length-_start];
		System.arraycopy(_message,_start,_pos,0,_pos.length);
		
		GameElement newElement = ef.getGameElement(_type);
		if(first == null)
		{
			first = newElement;
			first.next = first.prev = first;
		}
		else
		{
			first.insertBefore(newElement);
		}
		elements.put(_id,newElement);
		newElement.setPosition(_pos);

		synchronized( first )
		{
			first.notifyAll();
		}
		return newElement;
	}

	public void nudgeElement( int _row, int[] _dpos )
	{
		GameElement element = elements.get(_row);
		element.nudge( _dpos );
		int[] position = element.getPosition();
		int[] message = new int[position.length+2];
		message[0] = Constants.MOVE_TO;
		message[1] = _row;
		System.arraycopy(position, 0, message, 2, position.length);
		myIO.send(message);
		myLogger.message( "nudge position: " + _row + " (" + element.getPosition(0) + "," + element.getPosition(1) + "," + element.getPosition(2) + ")\n" , false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
	}

	public void setPosition( int[] _message, int _start)
	{
		GameElement element = elements.get(_message[_start++]);
		int[] _pos = new int[_message.length-_start];
		System.arraycopy(_message,_start,_pos,0,_pos.length);
		element.setPosition( _pos );
		myLogger.message( "move position: " + _message[_start] + " (" + element.getPosition(0) + "," + element.getPosition(1) + "," + element.getPosition(2) + ")\n" , false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
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