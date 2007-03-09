//Quaternions.java
//@author Joel Ross

/***
 A utility class of static method for using and manipulating Quaternions
 This way we have a class of our own methods instead of relying on Java3D's
 Quaternions are represented as float[] objects, with element order {x,y,z,w}
***/

public class Quaternions
{
	public static final int X = 0; //for readable indexing
	public static final int Y = 1;
	public static final int Z = 2;
	public static final int W = 3;

	//multiply (compose) two specified Quaternions and return the result 
	public static float[] mul(float[] q1, float[] q2)
	{
		//simplified quaternion multiplication
		return new float[] {
			q1[W]*q2[X] + q1[X]*q2[W] + q1[Y]*q2[Z] - q1[Z]*q2[Y],
			q1[W]*q2[Y] + q1[Y]*q2[W] + q1[Z]*q2[X] - q1[X]*q2[Z],
			q1[W]*q2[Z] + q1[Z]*q2[W] + q1[X]*q2[Y] - q1[Y]*q2[X],
			q1[W]*q2[W] - q1[X]*q2[X] - q1[Y]*q2[Y] - q1[Z]*q2[Z]};
	}

	//normalizes the given Quaternion (sets its length to 1)
	public static void normalize(float[] q)
	{
		double len = Math.sqrt(q[X]*q[X] + q[Y]*q[Y] + q[Z]*q[Z] + q[W]*q[W]);
		q[X] = (float)(q[X]/len); 
		q[Y] = (float)(q[Y]/len); 
		q[Z] = (float)(q[Z]/len); 
		q[W] = (float)(q[W]/len);
	}

	//returns a Quaternion from an array of Euler angles (XYZ order)
	public static float[] getQuatFromEuler(float[] e)
	{
		double sx = Math.sin(e[X]/2.0);
		double sy = Math.sin(e[Y]/2.0);
		double sz = Math.sin(e[Z]/2.0);
		double cx = Math.cos(e[X]/2.0);
		double cy = Math.cos(e[Y]/2.0);
		double cz = Math.cos(e[Z]/2.0);
		
		return new float[] {
			(float)(sx*cy*cz - cx*sy*sz),
			(float)(cx*sy*cz - sx*cy*sz),
 			(float)(cx*cy*sz - sx*sy*cz),
			(float)(cx*cy*cz + sx*sy*sz)};
	}
}
