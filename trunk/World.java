import java.util.*;

public class World
{
	private IO myIO;
	private GameElement first = null;
	public int nextElement = 0;
	private ElementFactory ef;
	public Logger myLogger;
	private HashMap<Integer, GameElement> elements = new HashMap<Integer, GameElement>();

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
		Collection<GameElement> c = elements.values();
		GameElement[] ge = c.toArray(new GameElement[]{});
		return ge;
		// return (GameElement[]) (elements.values().toArray());
	}

	public GameElement getFirstElement()
	{
		return first;
	}

	public String toString()
	{
		GameElement ge = first;
		String output = "** CURRENT WORLD **\n";
		do
		{
			output += ge + "\n";
			ge = ge.next;
		} while(ge != first);
		return output;
	}

	public Object[] getElementInfo( int _row )
	{
		return elements.get(_row).getInfoArray();
	}

	public void removeElement(Object[] _message, int _start)
	{
		GameElement toRemove = elements.get((Integer) _message[_start]);
		if(toRemove == null)
		{
			myLogger.message("removeElement tried to remove null Element with id: " + _start + "...\n", true);
			return;
		}
		if(first == toRemove)
		{
			if(first == first.next)
			{
				first = null;
				elements.remove(_message[_start]);
				return;
			}
			else
			{
				first = first.next;
			}
		}
		elements.remove(_message[_start]);

		synchronized( first )
		{
			toRemove.removeFromList();
			toRemove.changed = true;
			first.notifyAll();
		}
	}

	public GameElement addElement(GameElement newElement)
	{
		if(first == null)
		{
			first = newElement;
			first.next = first.prev = first;
		}
		else
		{
			synchronized(first)
			{
				first.insertBefore(newElement);
			}
		}
		elements.put(newElement.id(),newElement);

		synchronized( first )
		{
			first.notifyAll();
		}
		return newElement;
	}

	public GameElement addElement(Object[] _message, int _start)
	{
		int _id = ((Integer) _message[_start++]).intValue();
		int _type = ((Integer) _message[_start++]).intValue();
		float[] _pos = (float[]) _message[_start++];
		
		GameElement newElement = ef.getGameElement(_type);
		newElement.id(_id);
		newElement.setPosition(_pos);
		if(first == null)
		{
			first = newElement;
			first.next = first.prev = first;
		}
		else
		{
			synchronized(first)
			{
				first.insertBefore(newElement);
			}
		}
		elements.put(_id,newElement);

		synchronized( first )
		{
			first.notifyAll();
		}
		return newElement;
	}

	public void addMultipleElements(Object[] _message, int _start)
	{
		GameElement[] newElements = (GameElement[]) _message[_start];
		GameElement ge;
		for(GameElement newElement : newElements)
		{
			ge = ef.getGameElement(newElement.getTypeId());
			ge.id(newElement.id());
			ge.setPosition(newElement.getPosition());
			ge.setFacing(newElement.getFacing());
			ge.setAttributes(newElement.getAttributes());
			this.addElement(ge);
		}

	}

	public void nudgeElement( int _row, float[] _dpos )
	{
		GameElement element = elements.get(_row);
		element.nudge( _dpos );
		float[] position = element.getPosition();

		Object[] message = new Object[] {
			Constants.MOVE_TO,
			_row,
			position
		};
		
		myIO.send(message);
		myLogger.message( "nudge position: " + _row + " (" + element.getPosition(0) + "," + element.getPosition(1) + "," + element.getPosition(2) + ")\n" , false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}

		checkCollisions(); //for testing
	}

	public void rotateElement( int _row, float[] _dpos )
	{
		GameElement element = elements.get(_row);
		element.rotate( _dpos );
		float[] facing = element.getFacing();

		Object[] message = new Object[] {
			Constants.ROTATE_TO,
			_row,
			facing
		};
		
		myIO.send(message);
		myLogger.message( "rotate facing: " + _row + " (" + facing[0] + "," + facing[1] + "," + facing[2] + "," + facing[3] + ")\n" , false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}

		checkCollisions(); //for testing
	}

	public void setPosition( Object[] _message, int _start)
	{
		GameElement element = elements.get((Integer) _message[_start++]);
		float[] _pos = (float[]) _message[_start++];

		element.setPosition( _pos );
		myLogger.message( "move position: " + element.id() + " (" + element.getPosition(0) + "," + element.getPosition(1) + "," + element.getPosition(2) + ")\n" , false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
	
		checkCollisions(); //for testing
	}

	public void setFacing( Object[] _message, int _start)
	{
		GameElement element = elements.get((Integer) _message[_start++]);
		float[] _fac = (float[]) _message[_start++];

		element.setFacing( _fac );
		myLogger.message( "rotate facing: " + element.id() + " (" + _fac[0] + "," + _fac[1] + "," + _fac[2] + "," + _fac[3] + ")\n" , false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
		
		checkCollisions(); //for testing
	}
	
	public int parse(Object[] message)
	{
		switch(((Integer) message[0]).intValue())
		{
			case Constants.MOVE_TO:
				setPosition( message, 1);
				break;
			case Constants.ROTATE_TO:
				setFacing( message, 1);
				break;
			case Constants.ADD_PLAYER:
				addElement( message, 1);
				break;
			case Constants.REMOVE_PLAYER:
				removeElement( message, 1 );
				myLogger.message("Removing player: " + message[1] + "\n", false);
				break;
			case Constants.SEND_WORLD:
				addMultipleElements( message, 1);
				break;
			default:
				myLogger.message("Received unparsable message: " + message[0] + "\n", true);
		}

		return Constants.SUCCESS; //eventually every action in the world will return an int for whether or not it was a valid action
	}

	//a TESTING method to check the world for a collision
	public void checkCollisions()
	{
		GameElement e = first; //for looping
		do
		{
			GameElement e2 = e.next; //for looping
			while(e2 != first)
			{
				
				//check collisions
				if(e.isColliding(e2))
					System.out.println("Collision detected!! " + e + " and " + e2);
			
				e2 = e2.next;
			}

			e = e.next; //loop
		} while(e != first);

	}
}
