//VectorUtils.java
//@author Joel Ross

import java.util.HashMap;
import java.util.Arrays;

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

	//returns the product of a vector and a scalar
	public static float[] mul(float s, float[] v)
	{
		return new float[] {s*v[X], s*v[Y], s*v[Z]};
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

	//returns the square of the distance between two vectors
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

	//Based on the above, a method that determines if when two oriented bounding boxes (OBBs) 
	// in 3D intersect will intersect when A is moving along a given vector.
	//@param: A is the vector representing A's position
	//	  B is the vector representing B's position
	//	  D is the vector along which we want to move
	//	  Arot is the matrix which rotates A back to normal (so A.facing^-1)
	//	  a and b are vectors representing the half-dimensions of the boxes (x,y,z)
	//	  R is the rotation matrix representing the difference in rotation IN A'S BASIS
	public static float OBB3DIntersect(float[] A, float[] B, float[] D, float[][] Arot, float[] a, float[] b, float[][] R)
	{
		//initialize variables which are used more than once
		float R11 = Math.abs(R[0][0]);
		float R12 = Math.abs(R[0][1]);
		float R13 = Math.abs(R[0][2]);
		float R21 = Math.abs(R[1][0]);
		float R22 = Math.abs(R[1][1]);
		float R23 = Math.abs(R[1][2]);
		float R31 = Math.abs(R[2][0]);
		float R32 = Math.abs(R[2][1]);
		float R33 = Math.abs(R[2][2]);

		float BmAx = B[X]-A[X];
		float BmAy = B[Y]-A[Y];
		float BmAz = B[Z]-A[Z];

		float TcoX = -(D[X]*Arot[X][X] + D[Y]*Arot[X][Y] + D[Z]*Arot[X][Z]);
		float TcoY = -(D[X]*Arot[Y][X] + D[Y]*Arot[Y][Y] + D[Z]*Arot[Y][Z]);
		float TcoZ = -(D[X]*Arot[Z][X] + D[Y]*Arot[Z][Y] + D[Z]*Arot[Z][Z]);
		float TplX = BmAx*Arot[X][X] + BmAy*Arot[X][Y] + BmAz*Arot[X][Z];
		float TplY = BmAx*Arot[Y][X] + BmAy*Arot[Y][Y] + BmAz*Arot[Y][Z];
		float TplZ = BmAx*Arot[Z][X] + BmAy*Arot[Z][Y] + BmAz*Arot[Z][Z];
		
		//T in terms of v is:
		//T[X] =  (v*TcoX + TplX) 
		//T[Y] =  (v*TcoY + TplY) 
		//T[Z] =  (v*TcoZ + TplZ) 

		float AxRight = a[X] + b[X]*R11 + b[Y]*R12 + b[Z]*R13;
		float AyRight = a[Y] + b[X]*R21 + b[Y]*R22 + b[Z]*R23;
		float AzRight = a[Z] + b[X]*R31 + b[Y]*R32 + b[Z]*R33;
		float BxRight = b[X] + a[X]*R11 + a[Y]*R21 + a[Z]*R31;
		float ByRight = b[Y] + a[X]*R12 + a[Y]*R22 + a[Z]*R32;
		float BzRight = b[Z] + a[X]*R13 + a[Y]*R23 + a[Z]*R33;
		float AxBxRight = a[Y]*R31 + a[Z]*R21 + b[Y]*R13 + b[Z]*R12;
		float AxByRight = a[Y]*R32 + a[Z]*R22 + b[X]*R13 + b[Z]*R11;
		float AxBzRight = a[Y]*R33 + a[Z]*R23 + b[X]*R12 + b[Y]*R11;
		float AyByRight = a[X]*R32 + a[Z]*R12 + b[X]*R23 + b[Z]*R21;
		float AyBxRight = a[X]*R31 + a[Z]*R11 + b[Y]*R23 + b[Z]*R22;
		float AyBzRight = a[X]*R33 + a[Z]*R13 + b[X]*R22 + b[Y]*R21;
		float AzBxRight = a[X]*R21 + a[Y]*R11 + b[Y]*R33 + b[Z]*R32;
		float AzByRight = a[X]*R22 + a[Y]*R12 + b[X]*R33 + b[Z]*R31;
		float AzBzRight = a[X]*R23 + a[Y]*R13 + b[X]*R32 + b[Y]*R31;
		float BxLeft = TplX*R[X][X] + TplY*R[Y][X] + TplZ*R[Z][X];
		float ByLeft = TplX*R[X][Y] + TplY*R[Y][Y] + TplZ*R[Z][Y];
		float BzLeft = TplX*R[X][Z] + TplY*R[Y][Z] + TplZ*R[Z][Z];
		float AxBxLeft = TplZ*R[Y][X] - TplY*R[Z][X];
		float AxByLeft = TplZ*R[Y][Y] - TplY*R[Z][Y];
		float AxBzLeft = TplZ*R[Y][Z] - TplY*R[Z][Z];
		float AyBxLeft = TplX*R[Z][X] - TplZ*R[X][X];
		float AyByLeft = TplX*R[Z][Y] - TplZ*R[X][Y];
		float AyBzLeft = TplX*R[Z][Z] - TplZ*R[X][Z];
		float AzBxLeft = TplY*R[X][X] - TplX*R[Y][X];
		float AzByLeft = TplY*R[X][Y] - TplX*R[Y][Y];
		float AzBzLeft = TplY*R[X][Z] - TplX*R[Y][Z];
		//operation count:
			//9 absolute values
			//9+3+6+27=45 set
			//3+18+81=102 mul
			//3+6+60=69 add

//Use a bitmask to store state (bits 1-15 are axes).
		int initialState = 0;
	
//INITIAL: run through the algorithm and store whether each guy is a separating axis or not
		//Ax face (1)	
		if(Math.abs(TplX) > AxRight)		initialState |= 1;
		//Ay face (2)
		if(Math.abs(TplY) > AyRight)		initialState |= 2;
		//Az face (4)
		if(Math.abs(TplZ) > AzRight)		initialState |= 4;
		//Bx face (8)
		if(Math.abs(BxLeft) > BxRight)		initialState |= 8;
		//By face (16)
		if(Math.abs(ByLeft) > ByRight)		initialState |= 16;
		//Bz face (32)
		if(Math.abs(BzLeft) > BzRight)		initialState |= 32;
		//Ax X Bx (64)
		if(Math.abs(AxBxLeft) > AxBxRight)	initialState |= 64;
		//Ax X By (128)
		if(Math.abs(AxByLeft) > AxByRight)	initialState |= 128;
		//Ax X Bz (256)
		if(Math.abs(AxBzLeft) > AxBzRight)	initialState |= 256;
		//Ay X Bx (512)
		if(Math.abs(AyBxLeft) > AyBxRight)	initialState |= 512;
		//Ay X By (1024)
		if(Math.abs(AyByLeft) > AyByRight)	initialState |= 1024;
		//Ay X Bz (2048)
		if(Math.abs(AyBzLeft) > AyBzRight)	initialState |= 2048;
		//Az X Bx (4096)
		if(Math.abs(AzBxLeft) > AzBxRight)	initialState |= 4096;
		//Az X By (8192)
		if(Math.abs(AzByLeft) > AzByRight)	initialState |= 8192;
		//Az X Bz (16384)
		if(Math.abs(AzBzLeft) > AzBzRight)	initialState |= 16384;

//For each face, get both v's (+/-|T.L|)
//if v is a positive, finite number
//	hash that value and a flag for that axis to change

		float[] vs = new float[30]; //stores the different v values
		int[] cs = new int[30]; //stores the change flags at the corresponding index

		float t;
		float v;
		int i = 0;

		//Ax face (1)
		v = (AxRight - TplX)/TcoX;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 1;
			i++;
		}			 
		v = -(AxRight + TplX)/TcoX;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 1;
			i++;
		}			 

		//Ay face (2)
		v = (AyRight - TplY)/TcoY;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 2;
			i++;
		}			 
		v = -(AyRight + TplY)/TcoY;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 2;
			i++;
		}			 

		//Az face (4)
		v = (AzRight - TplZ)/TcoZ;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 4;
			i++;
		}			 
		v = -(AzRight + TplZ)/TcoZ;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 4;
			i++;
		}			 

		//Bx face (8)
		t = TcoX*R[X][X] + TcoY*R[Y][X] + TcoZ*R[Z][X];
		v = (BxRight - BxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 8;
			i++;
		}			 
		v = -(BxRight + BxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 8;
			i++;
		}			 

		//By face (16)
		t = TcoX*R[X][Y] + TcoY*R[Y][Y] + TcoZ*R[Z][Y];
		v = (ByRight - ByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 16;
			i++;
		}			 
		v = -(ByRight + ByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 16;
			i++;
		}			 
	
		//Bz face (32)
		t = TcoX*R[X][Z] + TcoY*R[Y][Z] + TcoZ*R[Z][Z];
		v = (BzRight - BzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 32;
			i++;
		}			 
		v = -(BzRight + BzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 32;
			i++;
		}			 

		//Ax X Bx (64)
		t = TcoZ*R[Y][X] - TcoY*R[Z][X];
		v = (AxBxRight - AxBxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 64;
			i++;
		}			 
		v = -(AxBxRight + AxBxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 64;
			i++;
		}			 

		//Ax X By (128)
		t = TcoZ*R[Y][Y] - TcoY*R[Z][Y];
		v = (AxByRight - AxByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 128;
			i++;
		}			 
		v = -(AxByRight + AxByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 128;
			i++;
		}			 

		//Ax X Bz (256)
		t = TcoZ*R[Y][Z] - TcoY*R[Z][Z];
		v = (AxBzRight - AxBzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 256;
			i++;
		}			 
		v = -(AxBzRight + AxBzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 256;
			i++;
		}			 

		//Ay X Bx (512)
		t = TcoX*R[Z][X] - TcoZ*R[X][X];
		v = (AyBxRight - AyBxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 512;
			i++;
		}			 
		v = -(AyBxRight + AyBxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 512;
			i++;
		}			 

		//Ay X By (1024)
		t = TcoX*R[Z][Y] - TcoZ*R[X][Y];
		v = (AyByRight - AyByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 1024;
			i++;
		}			 
		v = -(AyByRight + AyByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 1024;
			i++;
		}			 

		//Ay X Bz (2048)
		t = TcoX*R[Z][Z] - TcoZ*R[X][Z];
		v = (AyBzRight - AyBzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 2048;
			i++;
		}			 
		v = -(AyBzRight + AyBzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 2048;
			i++;
		}			 
		
		//Az X Bx (4096)
		t = TcoY*R[X][X] - TcoX*R[Y][X];
		v = (AzBxRight - AzBxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 4096;
			i++;
		}			 
		v = -(AzBxRight + AzBxLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 4096;
			i++;
		}			 

		//Az X By (8192)
		t = TcoY*R[X][Y] - TcoX*R[Y][Y];
		v = (AzByRight - AzByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 8192;
			i++;
		}			 
		v = -(AzByRight + AzByLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 8192;
			i++;
		}			 

		//Az X Bz (16384)
		t = TcoY*R[X][Z] - TcoX*R[Y][Z];
		v = (AzBzRight - AzBzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 16384;
			i++;
		}			 
		v = -(AzBzRight + AzBzLeft)/t;
		if(!Float.isNaN(v) && !Float.isInfinite(v) && v>=0)
		{
			vs[i] = v;
			cs[i] = 16384;
			i++;
		}			 
		
		int entriesCount = i;
		
//sort the arrays (both of them!!)
		
		//DO THIS NOW!!!


//go through each entry in the array
//	toggle the axis bit with the hashed value
//	if ==0, then we have no separating axis and we collided. Set as v (if less than 1) and return.
		int curState = initialState;
		System.out.println(Integer.toBinaryString(curState));
		for(int j=0; j<entriesCount; j++)
		{
			System.out.println(vs[j]);
			curState ^= cs[j];
			System.out.println(Integer.toBinaryString(curState));
			if(curState == 0)
			{
				v = vs[j];
				//check if we have other changes on this time stamp
				j++;
				while(vs[j] == v)
				{
					curState ^= cs[j];
					j++;
				}
				if(curState == 0)
					//return Math.min(v,1); //is necessary?
					return v;
				else
					j--; //counter the end of loop increment
			}
		}	

//if we get through all the entries, then we never had a collision state so can return 1
		return 0;

//POTENTIAL ISSUES: What about if we begin as colliding, then our next "change" is when we stop colliding because we've moved through something?

//OPTIMIZATIONS: could speed up by using our own data-structure instead of the HashMap--(like a tree)
//could also drop early/ignore if v is >1--then we know that we can move our desired distance and don't care beyond that

	}


	//Based on the above, a method that determines if when two oriented bounding boxes (OBBs) 
	// in 3D intersect will intersect when BOTH are moving along a given vectors.
	//@param: A is the vector representing A's position
	//	  B is the vector representing B's position
	//	  aD is the vector along which A wants to move
	//	  bD is the vector along which B want's to move
	//	  Arot is the matrix which rotates A back to normal (so A.facing^-1)
	//	  a and b are vectors representing the half-dimensions of the boxes (x,y,z)
	//	  R is the rotation matrix representing the difference in rotation IN A'S BASIS
	public static float OBB3DIntersect(float[] A, float[] B, float[] aD, float[] bD, float[][] Arot, float[] a, float[] b, float[][] R)
	{
		//fill this method in, someday.
		
		return 0;
	}


	//prints out what the checks are for debugging purposes.
	//Collisions are working now, but hold onto this in case I missed something
	public static void printDebugInfo(float[] a, float[] b, float[] T, float[][] R)
	{

		//for above method
		/*//debugging - print the arguments
		System.out.println("A= "+VectorUtils.toString(A));
		System.out.println("B= "+VectorUtils.toString(B));
		System.out.println("D= "+VectorUtils.toString(D));
		System.out.print("Arot= ");
			VectorUtils.print(Arot);
			System.out.println("");
		System.out.println("a= "+VectorUtils.toString(a));
		System.out.println("b= "+VectorUtils.toString(b));
		System.out.print("R= ");
			VectorUtils.print(R);
			System.out.println("");
		*/

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

	//a method that sorts a float[], and simultaneous performs the same movements on an int[]
	// (so that the relative indexes remain the same)
	public static void simultaneousSort(float[] farray, int[] iarray)
	{
		//implement using Quicksort/mergesort/something
		
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
