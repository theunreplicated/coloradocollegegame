//VectorUtils.java
//@author Joel Ross

/***
 A static utility class for working with vectors (float precision).
 I'm currently assuming 3-dimensional vectors for optimization.
***/

public class VectorUtils
{
	public static final short X = 0; //for readable indexing (sometimes)
	public static final short Y = 1;
	public static final short Z = 2;
	
	//returns the sum of two 3D vectors
	public static float[] add(float[] v, float[] w)
	{
		return new float[] {v[X]+w[X], v[Y]+w[Y], v[Z]+w[Z]}; 
	}

	//adds a vector to every vector in an array. Batch adding.
	public static void add(float[][] m, float[] v)
	{
		for(int i=0; i<m.length; i++)
			m[i] = add(m[i],v);
	}

	//returns the difference of two 3D vectors
	public static float[] sub(float[] v, float[] w)
	{
		return new float[] {v[X]-w[X], v[Y]-w[Y], v[Z]-w[Z]};
	}

	//returns the dot product of two 3D vectors
	public static float dot(float[] v, float[] w)
	{
		return (v[X]*w[X])+(v[Y]*w[Y])+(v[Z]*w[Z]);
	}
	
	//returns the cross product of two 3D vectors
	public static float[] cross(float[] v, float[] w)
	{
		return new float[] {(v[Y]*w[Z])-(v[Z]*w[Y]), 
				    (v[Z]*w[X])-(v[X]*w[Z]), 
				    (v[X]*w[Y])-(v[Y]*w[X])};
	}

	public static float getDistSqr(float[] v, float[] w)
	{
		return (v[X]-w[X])*(v[X]-w[X]) + (v[Y]-w[Y])*(v[Y]-w[Y]) + (v[Z]-w[Z])*(v[Z]-w[Z]);
	}

	//A method that determines if two oriented bounding boxes (OBBs) in 3D intersect.
	//@param: a and b are vectors representing the half-dimensions of the boxes (x,y,z)
	//	  T is the vector representing the difference in translation in A's basis
	//	  R is the rotation matrix representing the difference in rotation in A's basis
	//Cite: Algorithm from ftp://ftp.cs.unc.edu/pub/users/manocha/PAPERS/COLLISION/sig96.pdf 
	public static boolean OBB3DIntersect(float[] a, float[] b, float[] T, float[][] R)
	{
		float R11 = Math.abs(R[0][0]);
		float R12 = Math.abs(R[0][1]);
		float R13 = Math.abs(R[0][2]);
		float R21 = Math.abs(R[1][0]);
		float R22 = Math.abs(R[1][1]);
		float R23 = Math.abs(R[1][2]);
		float R31 = Math.abs(R[2][0]);
		float R32 = Math.abs(R[2][1]);
		float R33 = Math.abs(R[2][2]);

		//Ax face
		if(Math.abs(T[X]) > a[X] + b[X]*R11 + b[Y]*R12 + b[Z]*R13)
			return false;
		//Ay face
		if(Math.abs(T[Y]) > a[Y] + b[X]*R21 + b[Y]*R22 + b[Z]*R23)
			return false;
		//Az face
		if(Math.abs(T[Z]) > a[Z] + b[X]*R31 + b[Y]*R32 + b[Z]*R33)
			return false;
		//Bx face
		if(Math.abs(T[X]*R[X][X] + T[Y]*R[Y][X] + T[Z]*R[Z][X]) > b[X] + a[X]*R11 + a[Y]*R21 + a[Z]*R31)
			return false;
		//By face
		if(Math.abs(T[X]*R[X][Y] + T[Y]*R[Y][Y] + T[Z]*R[Z][Y]) > b[Y] + a[X]*R12 + a[Y]*R22 + a[Z]*R32)
			return false;
		//Bz face
		if(Math.abs(T[X]*R[X][Z] + T[Y]*R[Y][Z] + T[Z]*R[Z][Z]) > b[Z] + a[X]*R13 + a[Y]*R23 + a[Z]*R33)
			return false;
		//Ax X Bx
		if(Math.abs(T[Z]*R[Y][X] - T[Y]*R[Z][X]) > a[Y]*R31 + a[Z]*R21 + b[Y]*R13 + b[Z]*R12)
			return false;
		//Ax X By
		if(Math.abs(T[Z]*R[Y][Y] - T[Y]*R[Z][Y]) > a[Y]*R32 + a[Z]*R22 + b[X]*R13 + b[Z]*R11)
			return false;
		//Ax X Bz
		if(Math.abs(T[Z]*R[Y][Z] - T[Y]*R[Z][Z]) > a[Y]*R33 + a[Z]*R23 + b[X]*R12 + b[Y]*R11)
			return false;
		//Ay X Bx
		if(Math.abs(T[X]*R[Z][X] - T[Z]*R[X][X]) > a[X]*R31 + a[Z]*R11 + b[Y]*R23 + b[Z]*R22)
			return false;
		//Ay X By
		if(Math.abs(T[X]*R[Z][Y] - T[Z]*R[X][Y]) > a[X]*R32 + a[Z]*R12 + b[X]*R23 + b[Z]*R21)
			return false;
		//Ay X Bz
		if(Math.abs(T[X]*R[Z][Z] - T[Z]*R[X][Z]) > a[X]*R33 + a[Z]*R13 + b[X]*R22 + b[Y]*R21)
			return false;
		//Az X Bx
		if(Math.abs(T[Y]*R[X][X] - T[X]*R[Y][X]) > a[X]*R21 + a[Y]*R11 + b[Y]*R33 + b[Z]*R32)
			return false;
		//Az X By
		if(Math.abs(T[Y]*R[X][Y] - T[X]*R[Y][Y]) > a[X]*R22 + a[Y]*R12 + b[X]*R33 + b[Z]*R31)
			return false;
		//Az X Bz
		if(Math.abs(T[Y]*R[X][Z] - T[X]*R[Y][Z]) > a[X]*R23 + a[Y]*R13 + b[X]*R32 + b[Y]*R31)
			return false;	

		return true; //if we couldn't find a separating axis, we intersect	
	}

	//prints out what the checks are for debugging purposes.
	//Collisions are working now, but hold onto this in case I missed something
	public static void printDebugInfo(float[] a, float[] b, float[] T, float[][] R)
	{
		float R11 = Math.abs(R[0][0]);
		float R12 = Math.abs(R[0][1]);
		float R13 = Math.abs(R[0][2]);
		float R21 = Math.abs(R[1][0]);
		float R22 = Math.abs(R[1][1]);
		float R23 = Math.abs(R[1][2]);
		float R31 = Math.abs(R[2][0]);
		float R32 = Math.abs(R[2][1]);
		float R33 = Math.abs(R[2][2]);		

		System.out.println("a= "+VectorUtils.toString(a));
		System.out.println("b= "+VectorUtils.toString(b));
		System.out.println("T= "+VectorUtils.toString(T));
		System.out.print("R= ");
			VectorUtils.print(R);
			System.out.println("");

		System.out.print("L=A1");
		System.out.println(" (collides:"+(Math.abs(T[X]) > a[X] + b[X]*R11 + b[Y]*R12 + b[Z]*R13)+")");

		System.out.println("   "+Math.abs(T[X])+" > "+a[X]+" + "+(b[X]*R11 + b[Y]*R12 + b[Z]*R13));	
		System.out.print("L=A2");
		System.out.println(" (collides:"+(Math.abs(T[Y]) > a[Y] + b[X]*R21 + b[Y]*R22 + b[Z]*R23)+")");

		System.out.println("   "+Math.abs(T[Y])+" > "+a[Y]+" + "+(b[X]*R21 + b[Y]*R22 + b[Z]*R23));	
		System.out.print("L=A3");
		System.out.println(" (collides:"+(Math.abs(T[Z]) > a[Z] + b[X]*R31 + b[Y]*R32 + b[Z]*R33)+")");

		System.out.println("   "+Math.abs(T[Z])+" > "+a[Z]+" + "+(b[X]*R31 + b[Y]*R32 + b[Z]*R33));	
		System.out.print("L=B1");
		System.out.println(" (collides:"+(Math.abs(T[X]*R[X][X] + T[Y]*R[Y][X] + T[Z]*R[Z][X]) > b[X] + a[X]*R11 + a[Y]*R21 + a[Z]*R31)+")");

		System.out.println("   "+Math.abs(T[X]*R[X][X] + T[Y]*R[Y][X] + T[Z]*R[Z][X])+" > "+b[X]+" + "+(a[X]*R11 + a[Y]*R21 + a[Z]*R31));	
		System.out.print("L=B2");
		System.out.println(" (collides:"+(Math.abs(T[X]*R[X][Y] + T[Y]*R[Y][Y] + T[Z]*R[Z][Y]) > b[Y] + a[X]*R12 + a[Y]*R22 + a[Z]*R32)+")");

		System.out.println("   "+Math.abs(T[X]*R[X][Y] + T[Y]*R[Y][Y] + T[Z]*R[Z][Y])+" > "+b[Y]+" + "+(a[X]*R12 + a[Y]*R22 + a[Z]*R32));	
		System.out.print("L=B3");
		System.out.println(" (collides:"+(Math.abs(T[X]*R[X][Z] + T[Y]*R[Y][Z] + T[Z]*R[Z][Z]) > b[Z] + a[X]*R13 + a[Y]*R23 + a[Z]*R33)+")");

		System.out.println("   "+Math.abs(T[X]*R[X][Z] + T[Y]*R[Y][Z] + T[Z]*R[Z][Z])+" > "+b[Z]+" + "+(a[X]*R13 + a[Y]*R23 + a[Z]*R33));	
		System.out.print("L=A1 X B1");
		System.out.println(" (collides:"+(Math.abs(T[Z]*R[Y][X] - T[Y]*R[Z][X]) > a[Y]*R31 + a[Z]*R21 + b[Y]*R13 + b[Z]*R12)+")");

		System.out.println("   "+Math.abs(T[Z]*R[Y][X] - T[Y]*R[Z][X])+" > "+(a[Y]*R31 + a[Z]*R21)+" + "+(b[Y]*R13 + b[Z]*R12));	
		System.out.print("L=A1 X B2");
		System.out.println(" (collides:"+(Math.abs(T[Z]*R[Y][Y] - T[Y]*R[Z][Y]) > a[Y]*R32 + a[Z]*R22 + b[X]*R13 + b[Z]*R11)+")");

		System.out.println("   "+Math.abs(T[Z]*R[Y][Y] - T[Y]*R[Z][Y])+" > "+(a[Y]*R32 + a[Z]*R22)+" + "+(b[X]*R13 + b[Z]*R11));	
		System.out.print("L=A1 X B3");
		System.out.println(" (collides:"+(Math.abs(T[Z]*R[Y][Z] - T[Y]*R[Z][Z]) > a[Y]*R33 + a[Z]*R23 + b[X]*R12 + b[Y]*R11)+")");

		System.out.println("   "+Math.abs(T[Z]*R[Y][Z] - T[Y]*R[Z][Z])+" > "+(a[Y]*R33 + a[Z]*R23)+" + "+(b[X]*R12 + b[Y]*R11));	
		System.out.print("L=A2 X B1");
		System.out.println(" (collides:"+(Math.abs(T[X]*R[Z][X] - T[Z]*R[X][X]) > a[X]*R31 + a[Z]*R11 + b[Y]*R23 + b[Z]*R22)+")");

		System.out.println("   "+Math.abs(T[X]*R[Z][X] - T[Z]*R[X][X])+" > "+(a[X]*R31 + a[Z]*R11)+" + "+(b[Y]*R23 + b[Z]*R22));	
		System.out.print("L=A2 X B2");
		System.out.println(" (collides:"+(Math.abs(T[X]*R[Z][Y] - T[Z]*R[X][Y]) > a[X]*R32 + a[Z]*R12 + b[X]*R23 * b[Z]*R21)+")");

		System.out.println("   "+Math.abs(T[X]*R[Z][Y] - T[Z]*R[X][Y])+" > "+(a[X]*R32 + a[Z]*R12)+" + "+(b[X]*R23 * b[Z]*R21));	
		System.out.print("L=A2 X B3");
		System.out.println(" (collides:"+(Math.abs(T[X]*R[Z][Z] - T[Z]*R[X][Z]) > a[X]*R33 + a[Z]*R13 + b[X]*R22 + b[Y]*R21)+")");

		System.out.println("   "+Math.abs(T[X]*R[Z][Z] - T[Z]*R[X][Z])+" > "+(a[X]*R33 + a[Z]*R13)+" + "+(b[X]*R22 + b[Y]*R21));	
		System.out.print("L=A3 X B1");
		System.out.println(" (collides:"+(Math.abs(T[Y]*R[X][X] - T[X]*R[Y][X]) > a[X]*R21 + a[Y]*R11 + b[Y]*R33 + b[Z]*R32)+")");

		System.out.println("   "+Math.abs(T[Y]*R[X][X] - T[X]*R[Y][X])+" > "+(a[X]*R21 + a[Y]*R11)+" + "+(b[Y]*R33 + b[Z]*R32));	
		System.out.print("L=A3 X B2");
		System.out.println(" (collides:"+(Math.abs(T[Y]*R[X][Y] - T[X]*R[Y][Y]) > a[X]*R22 + a[Y]*R12 + b[X]*R33 + b[Z]*R31)+")");

		System.out.println("   "+Math.abs(T[Y]*R[X][Y] - T[X]*R[Y][Y])+" > "+(a[X]*R22 + a[Y]*R12)+" + "+(b[X]*R33 + b[Z]*R31));	
		System.out.print("L=A3 X B3");
		System.out.println(" (collides:"+(Math.abs(T[Y]*R[X][Z] - T[X]*R[Y][Z]) > a[X]*R23 + a[Y]*R13 + b[X]*R32 + b[Y]*R31)+")");

		System.out.println("   "+Math.abs(T[Y]*R[X][Z] - T[X]*R[Y][Z])+" > "+(a[X]*R23 + a[Y]*R13)+" + "+(b[X]*R32 + b[Y]*R31)+"\n");	
	}	


	//returns the radius of the containing Sphere of a box with the given half-dimensions
	public static float getContainingSphere(float[] box)
	{
		return (float)Math.sqrt(box[X]*box[X] + box[Y]*box[Y] + box[Z]*box[Z]);
	}

	//returns a string representation of the vector (of any dimensions)
	public static String toString(float[] v)
	{
		String s = "{"+ v[0];
		for(int i=1; i<v.length; i++)
			s += ", " + v[i];
		return s+"}";
	}
	
	//returns a string representation of a vector array, independent of length
	public static String toString(float[][] a)
	{
		String s = "{" + toString(a[0]);
		
		for(int i=1; i<a.length; i++)
			s += ", " + toString(a[i]);
		
		return s+"}";
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


	/***Matrix functions I don't expect to use***/

	//returns the product of two 3x3 matrixes together (m*n)
	public static float[][] mul(float[][] m, float[][] n)
	{
		return new float[][] 
			{{	m[0][0]*n[0][0] + m[0][1]*n[1][0] + m[0][2]*n[2][0], 
				m[0][0]*n[0][1] + m[0][1]*n[1][1] + m[0][2]*n[2][1], 
				m[0][0]*n[0][2] + m[0][1]*n[1][2] + m[0][2]*n[2][2]
			 },
 			 {	m[1][0]*n[0][0] + m[1][1]*n[1][0] + m[1][2]*n[2][0], 
 			 	m[1][0]*n[0][1] + m[1][1]*n[1][1] + m[1][2]*n[2][1], 
 			 	m[1][0]*n[0][2] + m[1][1]*n[1][2] + m[1][2]*n[2][2]
 			 },
 			 {	m[2][0]*n[0][0] + m[2][1]*n[1][0] + m[2][2]*n[2][0], 
 			 	m[2][0]*n[0][1] + m[2][1]*n[1][1] + m[2][2]*n[2][1], 
 			 	m[2][0]*n[0][2] + m[2][1]*n[1][2] + m[2][2]*n[2][2]
 			 }};
	}

	//returns the vector that is the product of a 3x3 matrix and a vector
	public static float[] mul(float[][] m, float[] v)
	{
		return new float[] {m[0][0]*v[0] + m[0][1]*v[1] + m[0][2]*v[2], 
		 	 	    m[1][0]*v[0] + m[1][1]*v[1] + m[1][2]*v[2], 
			 	    m[2][0]*v[0] + m[2][1]*v[1] + m[2][2]*v[2]};	
	}
	

	//returns the inverse of the given 3x3 matrix with determinate 1 (so returns the transpose)
	public static float[][] inverse(float[][] m)
	{
		return new float[][] {{m[0][0],m[1][0],m[2][0]},
				      {m[0][1],m[1][1],m[2][1]},
				      {m[0][2],m[1][2],m[2][2]}};
	}


}