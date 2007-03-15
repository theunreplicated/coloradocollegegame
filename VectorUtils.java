//VectorUtils.java
//@author Joel Ross

/***
 A static utility class for working with vectors (float precision).
 I'm currently assuming 3-dimensional vectors for optimization.
***/

public class VectorUtils
{
	//returns the sum of two 3D vectors
	public static float[] add(float[] v, float[] w)
	{
		return new float[] {v[0]+w[0], v[1]+w[1], v[2]+w[2]}; 
	}

	//returns the difference of two 3D vectors
	public static float[] sub(float[] v, float[] w)
	{
		return new float[] {v[0]-w[0], v[1]-w[1], v[2]-w[2]};
	}

	//returns the dot product of two 3D vectors
	public static float dot(float[] v, float[] w)
	{
		return (v[0]*w[0])+(v[1]*w[1])+(v[2]*w[2]);
	}
	
	//returns the cross product of two 3D vectors
	public static float[] cross(float[] v, float[] w)
	{
		return new float[] {(v[1]*w[2])-(w[1]*v[2]), (w[0]*v[2])-(v[0]*w[2]), (v[0]*w[1])-(w[0]*v[1])};
	}

	//A method that determines if two oriented bounding boxes (OBBs) in 3D intersect.
	//OBBs use 3D vectors of 8 elements, specified in the order of the unit box
	//	0 = { 1, 1, 1}
	//	1 = { 1, 1,-1}
	//	2 = {-1, 1,-1}
	//	3 = {-1, 1, 1}
	//	4 = { 1,-1, 1}
	//	5 = { 1,-1,-1}
	//	6 = {-1,-1,-1}
	//	7 = {-1,-1, 1}
	public static boolean OBB3DIntersect(float[][] m, float[][] e)
	{
		//define the face normals (mine and e's)
		float[] mf1 = { (m[0][0]+m[1][0]+m[4][0]+m[5][0])/4.0f, 
				(m[0][1]+m[1][1]+m[4][1]+m[5][1])/4.0f,
				(m[0][2]+m[1][2]+m[4][2]+m[5][2])/4.0f};
		float[] mf2 = {	(m[0][0]+m[1][0]+m[2][0]+m[3][0])/4.0f,
				(m[0][1]+m[1][1]+m[2][1]+m[3][1])/4.0f,
				(m[0][2]+m[1][2]+m[2][2]+m[3][2])/4.0f};
		float[] mf3 = { (m[0][0]+m[1][0]+m[5][0]+m[6][0])/4.0f,
				(m[0][1]+m[1][1]+m[5][1]+m[6][1])/4.0f,
				(m[0][2]+m[1][2]+m[5][2]+m[6][2])/4.0f};
		float[] ef1 = { (e[0][0]+e[1][0]+e[4][0]+e[5][0])/4.0f,
				(e[0][1]+e[1][1]+e[4][1]+e[5][1])/4.0f,
				(e[0][2]+e[1][2]+e[4][2]+e[5][2])/4.0f};
		float[] ef2 = {	(e[0][0]+e[1][0]+e[2][0]+e[3][0])/4.0f,
				(e[0][1]+e[1][1]+e[2][1]+e[3][1])/4.0f,
				(e[0][2]+e[1][2]+e[2][2]+e[3][2])/4.0f};
		float[] ef3 = {	(e[0][0]+e[1][0]+e[5][0]+e[6][0])/4.0f,
				(e[0][1]+e[1][1]+e[5][1]+e[6][1])/4.0f,
				(e[0][2]+e[1][2]+e[5][2]+e[6][2])/4.0f};

		float[] v;
		for(int i=0; i<8; i++)
		{
			v = sub(e[i],m[0]); //the vector we will be projecting
			
			//check mf1
			if(dot(mf1,v) > 0) //check the projection
				return false; //if we found a plane then they are not intersecting
			//check mf2
			if(dot(mf2,v) > 0)
				return false; 
			//check mf3
			if(dot(mf3,v) > 0)
				return false; 
			//check ef1
			if(dot(ef1,v) > 0)
				return false; 
			//check ef2
			if(dot(ef2,v) > 0) 
				return false; 
			//check ef3
			if(dot(ef3,v) > 0) 
				return false; 
			//check mf1 X ef1
			if(dot(cross(mf1,ef1),v) > 0)
				return false; 
			//check mf1 X ef2
			if(dot(cross(mf1,ef2),v) > 0) 
				return false; 
			//check mf1 X ef3
			if(dot(cross(mf1,ef3),v) > 0) 
				return false; 
			//check mf2 X ef1
			if(dot(cross(mf2,ef1),v) > 0) 
				return false; 
			//check mf2 X ef2
			if(dot(cross(mf2,ef2),v) > 0) 
				return false;
			//check mf2 X ef3
			if(dot(cross(mf2,ef3),v) > 0) 
				return false; 
			//check mf3 X ef1
			if(dot(cross(mf3,ef1),v) > 0) 
				return false; 
			//check mf3 X ef2
			if(dot(cross(mf3,ef2),v) > 0) 
				return false; 
			//check mf3 X ef3
			if(dot(cross(mf3,ef3),v) > 0)
				return false; 
		}
		return true; //if we got through, we intersect	
	}

	//returns a string representation of the 3D vector
	public static String toString(float[] v)
	{
		return "{"+v[0]+","+v[1]+","+v[2]+"}";
	}
	
	//returns a string representation of the vector array, independent of length
	public static String toString(float[][] a)
	{
		String s = "{" + toString(a[0]);
		
		for(int i=1; i<a.length; i++)
			s += "," + toString(a[i]);
		
		return s;
	}

	//prints out the array of vectors, independent of length
	public static void print(float[][] a)
	{
		for(int i=0; i<a.length; i++)
		{
			System.out.print("{"+a[i][0]);
			for(int j=1; j<a[i].length; j++)
			{
				System.out.print(", "+a[i][j]);
			}
			System.out.print("}\n");
		}
	}

}