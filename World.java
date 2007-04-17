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
		ef.setWorld(this);
	}

	public void setIO( IO _io )
	{
		myIO = _io;
	}

	public HashMap<Integer,GameElement> getElementsHash()
	{
		return elements;
	}
	
	public GameElement[] getElements()
	{
		Collection<GameElement> c = elements.values();
		GameElement[] ge = c.toArray(new GameElement[]{});
		return ge;
		// return (GameElement[]) (elements.values().toArray());
	}

	public GameElement getElementById(int id)
	{
		return elements.get(id);
	}

	public GameElement getFirstElement()
	{
		return first;
	}

	public String toString()
	{
		GameElement ge = first;
		if(ge == null) { return "** WORLD IS EMPTY **\n"; }
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

	//for speed and ease of use
	public float[] getElementFacing(int _row)
	{
		return elements.get(_row).getFacing();
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
		myLogger.message("Putting element with id: " + newElement.id()+"\n",false);
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
		int _type;
		if(_message[_start] instanceof String)
		{
			_type = ef.getType((String) _message[_start++]);
		}
		else
		{
			_type = ((Integer) _message[_start++]).intValue();
		}
		float[] _pos = (float[]) _message[_start++];
		
		GameElement newElement = ef.getGameElement(_type);
		newElement.id(_id);
		newElement.setPosition(_pos);
		if(_id < Constants.ELEMENT_ID_PADDING+Constants.MAX_CONNECTIONS+1) //so that the avatars have different colors!
			newElement.attribute("color",Constants.getColorByClientID(_id));

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
		myLogger.message("Putting element with id: " + newElement.id()+"\n",false);
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
		for(GameElement newElement : newElements)
		{
			this.addElement(newElement);
		}

	}

	public boolean nudgeElement( int _row, float[] _dpos )
	{
		GameElement element = elements.get(_row);
		element.nudge( _dpos );
		
	/**/	if(hasCollisions(element)) //if we caused a collision
		{
			float[] backup = new float[_dpos.length]; //get the reversion
			for(int i=0; i<backup.length; i++)
				backup[i] = -1*_dpos[i];
			
			element.nudge(backup); //undo the move
			return false;
		}	
		else //let everybody know
		{ /**/	

			float[] position = element.getPosition();

			Object[] message = new Object[] {
				Constants.MOVE_TO,
				_row,
				position
			};


			myIO.send(message);
			myLogger.message( "nudge position: " + _row + " " + VectorUtils.toString(position)+"\n", false );
			synchronized(first)
			{
				element.changed = true;
				first.notifyAll();
			}
			return true;
	/**/	}/**/
	}

	//a nudge method without testing collisions (for MovingElement demonstration)
	//We can remove this as soon as the resolver is handling collisions
	public boolean nudgeInternal( int _row, float[] _dpos )
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
		myLogger.message( "nudge position: " + _row + " " + VectorUtils.toString(position)+"\n", false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
		return true;
	}

	public boolean rotateElement( int _row, float[] _dpos )
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
		myLogger.message( "rotate facing: " + _row + " " + VectorUtils.toString(facing)+"\n", false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
		return true;
	}

	public void attributeElement(int _row, String k, Object v)
	{
		GameElement element = elements.get(_row);
		element.attribute(k, v);

		Object[] message = new Object[] {
			Constants.ATTRIBUTE,
			_row,
			k,
			null
		};
		
		if(v instanceof GameElement)
		{
			message[3] = Constants.UNIQUE_GE_PREFIX+((GameElement) v).id(); 
		}
		else
		{
			message[3] = v;
		}
		myIO.send(message);
		myLogger.message( "set attribute: " + _row + " " + k + " -> " + v +"\n", false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
	}	

	public void setPosition( Object[] _message, int _start)
	{
		GameElement element = elements.get((Integer) _message[_start++]);
		float[] _pos = (float[]) _message[_start++];

		element.setPosition( _pos );
		myLogger.message( "move position: " + element.id() + " " + VectorUtils.toString(_pos)+"\n", false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
	}

	public void setFacing( Object[] _message, int _start)
	{
		GameElement element = elements.get((Integer) _message[_start++]);
		float[] _fac = (float[]) _message[_start++];

		element.setFacing( _fac );
		myLogger.message( "rotate facing: " + element.id() + " " + VectorUtils.toString(_fac)+"\n", false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
	}

	public void setAttribute( Object[] _message, int _start)
	{
		GameElement element = elements.get((Integer) _message[_start++]);
		String k = (String) _message[_start++];
		Object v = _message[_start++]; 

		if(v instanceof String)
		{
			if(((String) v).startsWith(Constants.UNIQUE_GE_PREFIX))
			{
				Integer id = Integer.parseInt(((String) v).substring(Constants.UNIQUE_GE_PREFIX.length()));
				v = elements.get(id);
			}
		}
		element.attribute(k,v);
		myLogger.message( "set attribute: " + element.id() + " " + k + " -> " + v +"\n", false );
		synchronized(first)
		{
			element.changed = true;
			first.notifyAll();
		}
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
				{
					System.out.println("Collision detected!! Element " + e.id() + " and  Element " + e2.id());
					//System.out.println(e);
					//System.out.println(e2);
				}
				e2 = e2.next;
			}

			e = e.next; //loop
		} while(e != first);
	}

	//a TESTING method to check the world for a collision against a certain element
	public boolean hasCollisions(GameElement e)
	{
		GameElement e2 = e.next; //for looping
		while(e2 != e)
		{
			//check collisions
			if(e.isColliding(e2))
			{
				System.out.println("Collision detected!! Element " + e.id() + " and  Element " + e2.id());
				//System.out.println(e);
				//System.out.println(e2);
				return true;
			}
			e2 = e2.next;
		}

		return false;
	}

}
