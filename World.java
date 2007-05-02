import java.util.*;

public class World
{
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

	public void setFirstElement(GameElement ge)
	{
		first = ge;
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

		return newElement;
	}

	public void removeElement(GameElement ge)
	{
		elements.remove(ge.id());
		ge.removeFromList();
	}
}
