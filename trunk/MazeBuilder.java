/**
 * MadeBuilder.java
 *
 * @author Joel Ross
 * @version 1.0
 *
 * A class for building a random maze. Each instance of the maze builder begins with a three-dimensional block of rooms of specified size, none of which link to each other. 
 * The builder can then be made to randomly 'carve' into the block, specifying a path through the maze.
 *
 * Currently uses storage up to twice the maze side (I think).
 **/
 
import java.util.*;
import java.awt.*;

public class MazeBuilder
{
	//instance variables
    	int rows;
    	int cols;
    	int flrs;
    
    	ArrayList<int[]> list = new ArrayList<int[]>(); //lists for building
    	ArrayList<int[]> sublist = new ArrayList<int[]>(6);
    	Random gen = new Random(); //our random number generator

    	MazeNode[][][] maze; //holds the maze

	/**
	 * Creates a new 3D MazeBuilder object of the specified size.
	 * Multiple mazes of the same size can be created with the same MazeBuilder object
	 * @param _rows The number of rows.
	 * @param _cols The number of columns.
	 * @param _flrs The number of floors.
	 **/
    	public MazeBuilder(int _rows, int _cols, int _flrs)
   	{
		rows = _rows;
		cols = _cols;
		flrs = _flrs;
		maze = new MazeNode[rows][cols][flrs];
	}

	/**
	 * Creates a new 2D MazeBuilder object of the specified size (the number of floors defaults to 1).
	 * @param _rows The number of rows.
	 * @param _cols The number of columns.
	 **/
	public MazeBuilder(int _rows, int _cols)
	{
		this(_rows, _cols, 1);	
	}

	/**
	 * Carves a new maze out of the MazeBuilder object.
	 * This method generates a random, perfect maze (one without loops or isolated rooms--there is a single unique path from every room to every other room).
	 * Generates a maze using a Recursive Backtracker.
	 * Algorithm taken from Think Labyrinth (http://www.astrolog.org/labyrnth/algrithm.htm), though implementation is my own.
	 **/
    	public void carve()
    	{
		//clear previous maze
		maze = new MazeNode[rows][cols][flrs];
		
		//build opening. Lower right corner. Initiated with all walls
		maze[0][0][0] = new MazeNode(true,true,true,true,true,true);
		list.add(new int[] {0,0,0}); //add it to the list

		int i; //index
		//use integer version of Point3D so to avoid a lot of casting
		int[] n; //point
		int[] m; //tocarve
		while(!list.isEmpty()) //while there's something in the list
	  	{
			i = gen.nextInt(list.size()); //index in list. We currently get this index randomly
			n = list.get(i);

			sublist.clear(); //empty the sublist
			if(n[0] != rows-1 && maze[n[0]+1][n[1]][n[2]]==null) //if north is empty, add it to sublist
		    		sublist.add(new int[] {n[0]+1, n[1], n[2]});
			if(n[0] != 0 && maze[n[0]-1][n[1]][n[2]]==null) //if south is empty
		    		sublist.add(new int[] {n[0]-1, n[1], n[2]});
			if(n[1] != cols-1 && maze[n[0]][n[1]+1][n[2]]==null) //if west is empty
		    		sublist.add(new int[] {n[0], n[1]+1, n[2]});
			if(n[1] != 0 && maze[n[0]][n[1]-1][n[2]]==null) //if east is empty
		    		sublist.add(new int[] {n[0], n[1]-1, n[2]});
		    	if(n[2] != 0 && maze[n[0]][n[1]][n[2]-1]==null) //if down is empty
		    		sublist.add(new int[] {n[0], n[1], n[2]-1});
		    	if(n[2] != flrs-1 && maze[n[0]][n[1]][n[2]+1]==null) //if up is empty
		    		sublist.add(new int[] {n[0], n[1], n[2]+1});
		
			//if sublist is empty, remove n from list.
			if(sublist.isEmpty())
				list.remove(i);
			else
		    	{
				m = sublist.get(gen.nextInt(sublist.size())); //pick a random empty cell next to it
				//carve
				maze[m[0]][m[1]][m[2]] = new MazeNode(true,true,true,true,true,true); //set new room as a passage
				//make link between rooms
				if(m[0] == n[0]+1) //if north
				{
					maze[n[0]][n[1]][n[2]].north = false;
					maze[m[0]][m[1]][m[2]].south = false;
			    	}
				else if(m[1] == n[1]+1) //if east
			    	{
					maze[n[0]][n[1]][n[2]].east = false;
					maze[m[0]][m[1]][m[2]].west = false;
			    	}
				else if(m[0] == n[0]-1) //if south
			    	{
					maze[n[0]][n[1]][n[2]].south = false;
					maze[m[0]][m[1]][m[2]].north = false;
			    	}
				else if(m[1] == n[1]-1) //if west
			    	{
					maze[n[0]][n[1]][n[2]].west = false;
					maze[m[0]][m[1]][m[2]].east = false;
			    	}
				else if(m[2] == n[2]+1) //if up
				{
					maze[n[0]][n[1]][n[2]].up = false;
					maze[m[0]][m[1]][m[2]].down = false;
				}
				else if(m[2] == n[2]-1) //if down
				{
					maze[n[0]][n[1]][n[2]].down = false;
					maze[m[0]][m[1]][m[2]].up = false;
				}

				list.add(new int[] {m[0],m[1],m[2]}); //add new room to list
		    	}
		}

    		//maze[0][cols-1][flrs-1].north = false; //maze exit in UPPER corner
	}

	/**
	 * Gets the maze after it has been created.
	 * @return A double array of MazeNode objects, representing the layout of the maze.
	 **/
	public MazeNode[][][] getMaze()
    	{
		return maze;
    	}


}//MazeBuilder.java
