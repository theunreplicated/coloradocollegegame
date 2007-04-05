/**
 * MadeNode.java
 *
 * @author Joel Ross
 * @version 1.0
 *
 * A class for keeping track of the layout of a maze. Each maze node represents a "room" in the maze with possible exits (north, east, south, west, up, and down).
 * The node stores which of these exits are available and which are blocked for the room it represents.
 **/

public class MazeNode
{
	//tells whether there is a wall is a specified direction
	//true => wall; false => no wall;
	public boolean north; 
	public boolean east;
	public boolean south;
	public boolean west;
	public boolean up;
	public boolean down;
	
	/**
	 * Creates a new MazeNode object with specified exits available in each of the six directions.
	 * A value of true means there is a wall, a value of false means there isn't a wall.
	 * @param _north The north exit.
	 * @param _east The east exit.
	 * @param _south The south exit.
	 * @param _west The west exit.
	 * @param _up The up exit.
	 * @param _down The down exit.
	 **/
	public MazeNode(boolean _north, boolean _east, boolean _south, boolean _west, boolean _up, boolean _down)
	{
		north = _north;
		east = _east;
		south = _south;
		west = _west;
		up = _up;
		down = _down;
	}

	/**
	 * Creates a new MazeNode object with specified exits available in each of the four cardinal directions.
	 * The up and down exits are blocked by default.
	 * A value of true means there is a wall, a value of false means there isn't a wall.
	 * @param _north The north exit.
	 * @param _east The east exit.
	 * @param _south The south exit.
	 * @param _west The west exit.
	 **/
	public MazeNode(boolean _north, boolean _east, boolean _south, boolean _west)
	{
		north = _north;
		east = _east;
		south = _south;
		west = _west;
		up = false;
		down = false;
	}
} 