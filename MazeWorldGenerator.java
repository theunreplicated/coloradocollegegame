//MazeWorldGenerator.java
//@author Joel Ross

import java.util.*;

/***
 A class which contains methods to print out the XML for a world .ccw file containing a maze.
***/

public class MazeWorldGenerator
{
	final static float rt = 10;
	final static float wt = 1;

	public static void main(String[] args)
	{
		int rows = 8; //_r;
		int cols = 8; //_c;
		int flrs = 1; //_f;
		
		MazeBuilder bld = new MazeBuilder(rows,cols);
		bld.carve(); //carve a new maze
		MazeNode[][][] maze = bld.getMaze();
		
		/*MazeNode[][][] maze = new MazeNode[rows][cols][flrs];
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<cols; j++)
			{
				for(int k=0; k<flrs; k++)
				{
					maze[i][j][k] = new MazeNode(true,true,true,true,false,false);
				}
			}
		}*/
		
		ArrayList<MazeWallElement> walls = new ArrayList<MazeWallElement>();
		
		//run through the maze and start creating MazeWallElements

		//Take the layout of the maze and construct corresponding objects (MazeWallElements) for the walls
		//we currently build in the x,-z plane. Then use a different loop to center the maze
		MazeWallElement w;
		float[] p;
		float[] r;
		float[] s;
		for(int i=0; i<rows; i++) //[row]
		{
			for(int j=0; j<cols; j++) //[col]
			{
				for(int k=0; k<flrs; k++) //[floor]
				{
					//build walls of each room in the maze, adding north and east walls for all, others only on edge
					//add north wall
					if(maze[i][j][k].north)
					{
						p = new float[] {
								(j)*(rt) + (rt/2.0f),
								(k)*(rt) + 0,
								(i)*(rt) + rt};
						r = new float[] {0,0,0};
										
						w = new MazeWallElement(p,r);
						walls.add(w);
					}

					//add east wall
					if(maze[i][j][k].east)
					{
						p = new float[] {
								(j)*(rt) + rt,
								(k)*(rt) + 0,
								(i)*(rt) + (rt/2.0f)};
						r = new float[] {0,90,0};
										
						w = new MazeWallElement(p,r);
						walls.add(w);
					}

					//add south wall only if on bottom edge
					if(i==0 && maze[i][j][k].south)
					{
						p = new float[] {
								(j)*(rt) + (rt/2.0f),
								(k)*(rt) + 0,
								(i)*(rt) + 0};
						r = new float[] {0,0,0};
										
						w = new MazeWallElement(p,r);
						walls.add(w);
					}

					//add west wall	only if on back edge
					if(j==0 && maze[i][j][k].west)
					{
						p = new float[] {
								(j)*(rt) + 0,
								(k)*(rt) + 0,
								(i)*(rt) + (rt/2.0f)};
						r = new float[] {0,90,0};
										
						w = new MazeWallElement(p,r);
						walls.add(w);
					}

					//add up wall
					/*if(maze[i][j][k].up)
					{
					}

					//add down wall only if on bottom floor
					if(k==0 && maze[i][j][k].down)
					{
					}	
					*/
					
					//add posts between the walls to fill it out. 
					//northeast
					p = new float[] {
							(j)*(rt) + rt,
							(k)*(rt) + 0,
							(i)*(rt) + rt};
					r = new float[] {0,0,0};
					s = new float[] {wt,rt-wt,wt};
										
					w = new MazeWallElement(p,r,s);
					walls.add(w);
										
					//southeast only if on bottom edge
					if(i==0)
					{
						p = new float[] {
								(j)*(rt) + rt,
								(k)*(rt) + 0,
								(i)*(rt) + 0};
						r = new float[] {0,0,0};
						s = new float[] {wt,rt-wt,wt};
										
						w = new MazeWallElement(p,r,s);
						walls.add(w);
					}
					
					//northwest only if on back edge
					if(j==0)
					{
						p = new float[] {
								(j)*(rt) + 0,
								(k)*(rt) + 0,
								(i)*(rt) + rt};
						r = new float[] {0,0,0};
						s = new float[] {wt,rt-wt,wt};
										
						w = new MazeWallElement(p,r,s);
						walls.add(w);
					}
				
					//southwest only if on back edge and bottom edge
					if(i==0 && j==0)
					{
						p = new float[] {
								(j)*(rt) + 0,
								(k)*(rt) + 0,
								(i)*(rt) + 0};
						r = new float[] {0,0,0};
						s = new float[] {wt,rt-wt,wt};
										
						w = new MazeWallElement(p,r,s);
						walls.add(w);
					}
					
					
					//make 3D capable?					
		
				}
			}
		}		

		//Center the maze, since building in +x +y quadrant
		float sx = rt*cols/-2.0f;
		float sy = (rt*flrs/2.0f)-3;
		float sz = rt*rows/-2.0f;
		float[] _p;
		for(int i=0; i<walls.size(); i++)
		{
			_p = walls.get(i).p;
			walls.get(i).p = new float[] {_p[0]+sx, _p[1]+sy, _p[2]+sz};
		}

		//Print out the elements
		System.out.println("<world>\n");
		for(int i=0; i<walls.size(); i++)
			System.out.println(walls.get(i));
		//add pots of gold in the corners
		float[] pp1 = new float[] {(rt/2.0f)+sx, 0, (rt/2.0f)+sz};
		float[] pp2 = new float[] {((rt*cols)+sx-(rt/2.0f)), 0, (rt/2.0f)+sz};
		float[] pp3 = new float[] {(rt/2.0f)+sx, 0, ((rt*rows)-(rt/2.0f))+sz};
		float[] pp4 = new float[] {((rt*cols)+sx-(rt/2.0f)), 0, ((rt*rows)-(rt/2.0f))+sz};
		
		System.out.println(	"	<element type=\"pot of gold\">\n" +
					"		<position>"+pp1[0]+"</position>\n" +
					"		<position>"+pp1[1]+"</position>\n" +
					"		<position>"+pp1[2]+"</position>\n" +
					"		<scale>"+2+"</scale>\n" +
					"		<scale>"+2+"</scale>\n" +
					"   		<scale>"+2+"</scale>\n" +
					"	</element>\n");
		System.out.println(	"	<element type=\"pot of gold\">\n" +
					"		<position>"+pp2[0]+"</position>\n" +
					"		<position>"+pp2[1]+"</position>\n" +
					"		<position>"+pp2[2]+"</position>\n" +
					"		<scale>"+2+"</scale>\n" +
					"		<scale>"+2+"</scale>\n" +
					"   		<scale>"+2+"</scale>\n" +
					"	</element>\n");
		System.out.println(	"	<element type=\"pot of gold\">\n" +
					"		<position>"+pp3[0]+"</position>\n" +
					"		<position>"+pp3[1]+"</position>\n" +
					"		<position>"+pp3[2]+"</position>\n" +
					"		<scale>"+2+"</scale>\n" +
					"		<scale>"+2+"</scale>\n" +
					"   		<scale>"+2+"</scale>\n" +
					"	</element>\n");
		System.out.println(	"	<element type=\"pot of gold\">\n" +
					"		<position>"+pp4[0]+"</position>\n" +
					"		<position>"+pp4[1]+"</position>\n" +
					"		<position>"+pp4[2]+"</position>\n" +
					"		<scale>"+2+"</scale>\n" +
					"		<scale>"+2+"</scale>\n" +
					"   		<scale>"+2+"</scale>\n" +
					"	</element>\n");
		System.out.println(	"	<element type=\"pot of gold\">\n" +
					"		<position>"+pp1[0]+"</position>\n" +
					"		<position>"+pp1[1]+"</position>\n" +
					"		<position>"+pp1[2]+"</position>\n" +
					"		<scale>"+2+"</scale>\n" +
					"		<scale>"+2+"</scale>\n" +
					"   		<scale>"+2+"</scale>\n" +
					"	</element>\n");
		//close the world tag
		System.out.println("</world>\n");
	}

	public static class MazeWallElement
	{
		float[] p; //position
		float[] f; //facing
		float[] s; //scale
		
		public MazeWallElement(float[] _p, float[] _r)
		{
			p = _p;
			f = _r;
			s = new float[] {rt-wt,rt-wt,wt};
		}

		public MazeWallElement(float[] _p, float[] _r, float[] _s)
		{
			p = _p;
			f = _r;
			s = _s;
		}

		public String toString()
		{
			return	"	<element type=\"maze wall\">\n" +
				"		<position>"+p[0]+"</position>\n" +
				"		<position>"+p[1]+"</position>\n" +
				"		<position>"+p[2]+"</position>\n" +
				"		<facing>"+f[0]+"</facing>\n" +
				"		<facing>"+f[1]+"</facing>\n" +
				"		<facing>"+f[2]+"</facing>\n" +
				"		<scale>"+s[0]+"</scale>\n" +
				"		<scale>"+s[1]+"</scale>\n" +
				"   		<scale>"+s[2]+"</scale>\n" +
				"	</element>\n";
			
			
			//make String representation	
		}
	}

}