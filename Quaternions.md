Moving things back and forth is all fine and dandy, but what we really want is full degrees of freedom (DOF). We want to be able to twist and turn, to look behind us and to change direction. And for this we need rotation.

# 3D Rotation Basics #
_Warning: here thar be Linear Algebra_

At the lower levels of 3D modeling, everything is treated as a vector. Objects are really just collections of vertices specifying a Point in 3D space [x, y, z]. So if you have a cube of length 1 sitting on the origin, then it will have a vertex [.5, .5, .5].

Now suppose we want to move that cube. This is equivalent to changing the vector that represents its vertices for every vertex. So moving the cube 1 unit along the x axis will move our vertex to [1.5, .5, .5]. Translation is simply additive--moving an object just involves adding your movement vector to the position vector of each vertex.

Rotation gets a little trickier. The idea is we want some set of "rules" which we can apply to a vector P to get the new, rotated vector P'. There needs to be some kind of equation that we can apply to any P to get P'. Well we have the perfect tool for apply an Equation to a vector--Matrixes!

So if we have a 3x3 Matrix A and we multiply it by the 3x1 vector P, we're going to get a new 3x1 vector P'. Voila!

Some simple geometry (which I'm not going to write out here because I don't have my notes with me) will generate the following 2x2 matrix for rotation by an angle t
```
|cos(t)  -sin(t)| |x| = x*cos(t) - y*sin(t) = |x'|   
|sin(t)   cos(t)| |y| = x*sin(t) + y*cos(t) = |y'|
```
The coordinates [x', y'] correspond exactly to a rotation counterclockwise about the origin by the angle t.

Now if you imagine you're rotating an point on the xy plane, then the z-axis would be sticking right out of the origin. So in effect, you're rotating around the z-axis. And since you're rotating in the xy plane, your z-coordinate will remain constant. So we can extend this matrix to a 3x3 matrix
```
|cos(t)  -sin(t)  0| |x| = x*cos(t) - y*sin(t) + 0*z = |x'|  
|sin(t)   cos(t)  0| |y| = x*sin(t) + y*cos(t) + 0*z = |y'|
|0        0       1| |z| = 0        + 0        + 1*z = |z |
```
You can see that we now have a 3x3 matrix, which when multiplied by a 3x1 vector, we get a 3x1 vector that corresponds to a rotation around the z-axis. x' and y' are the same as rotating in 2 dimensions, and z = z'.

By treating the a differnet axis as the z-axis we can get rotation around the the y- and z- axes
```
Rotation around the X-axis
|1       0        0     |
|0       cos(t)  -sin(t)|  
|0       sin(t)   cos(t)|

Rotation around the Y-axis
|cos(t)  0        sin(t)|
|0       1        0     |
|-sin(t) 0        cos(t)|
```
You may notice that the signs on the sins have switched. This is because we have to look down the Y axis from the other direction.

So now we have a matrix template for a rotation around one of the main axis. We can create a rotation around ANY axis by simply multiplying the matrixes together! So if we want to rotation 30degrees around X and then 45degress around Y, we just construct the two matrixes and multiply them together to get a new 3x3 matrix that represents such a transformation. We can then use that matrix to rotate the vertices of our 3D shape!

## Problems with the Matrix ##
Sounds simple enough, right? You're probably wondering, why don't we just use these matrixes to specify a rotation?

Well there are a number of problems with using a Matrix to store a rotation. First, we're storing 9 different numbers. Second, multiplying matrixes is pretty slow--without optimization, it would take us 27 multiplications and 18 additions just to compose **two** matrixes.

There might be ways around this. We could just store a vector for the Euler rotations (the angles around the x-, y-, and z-axes). This only stores 3 numbers, but we still have to construct the matrix, which will involve a lot of sin() and cos() calls--very slow! We could also possibly optimize the matrix multiplication, but that could get hairy and may not save us much time anyway since there would be overhead. There is also the option to store 4 numbers--an axis of rotation and an angle around that rotation. But this doesn't solve other problems like Gimbal lock (where you've moved one axis onto another so lose a DOF) or rough interpolation issues.

# Quaternions! #
The solution? Quaternions!

A Quaternion is an extension of the complex numbers represented as: `w+xi+yj+zk, where i^2 = j^2 = k^2 = ijk = -1`. So a Quaternion is represented as a vector {x,y,z} and a scalar w. There are a couple of other neat algebraic properties of these numbers, which I leave to you to look up.

The beauty of the Quaternions is that the unit Quaternions correspond to rotations in three dimensions! There is a direct corrolation between a Quaternion and a rotation about a vector
```
Given a vector v and a rotation t, the Quaternion corresponding to this rotation is
  q = {w, u} where
  w = sin(t)/2
  u = cos(t)/2 * v
```

So instead of storing 9 numbers for a matrix, we store 4 numbers for a Quaternion. And instead of multiplying matrixes, we multiply Quaternions! Quaternion multiplication is defined as a set of dot and cross products, but simplifies very nicely. So we use the following function
```
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
```
We're now doing 16 multiplications and 12 additions instead of 27 multiplications and 18 additions with a matrix. Note, however, that Quaternion multiplication is not commutative.

Now if we want to rotate a vector, we treat that vector as a Quaternion (with w=0) and then multiply it by a Quaternion. However, this gives us a non-real part, so we have to multiply by the Quaternion's conjugate, `q*`. So `v' = qvq*`. This is more work than than just multiplying the vector by a matrix, but not too much. Besides, Quaternions are fun!

Quaternions mean that we store less variables and spend (hopefully) less time manipulating those variables.

# Implementation #
The game stores all rotations as Quaternions. A Quaternion is a `float[] = {x,y,z,w`}. Quaternions can be manipulated using the static Quaternions.java class. This class currently contains methods for multiplying and normalizing Quaternions, and will soon contain methods for rotating vectors. Use these methods and you'll never have to touch a Quaternion--just pass the float[.md](.md) around use the methods to multiply stuff.

GameElements store their orientation, which is then linked to J3D in the Representation3D. J3D takes the Quaternion and uses that directly as a rotation--so any conversion between matrixes and Quaternions is handled for us when using J3D. If we want to use a different graphical API, we'll have to write our own methods.

The XML files which specify elements _**DO NOT**_ use Quaternions. These rotations are specified in Euler rotations, which are then converted into Quaternions when the XML file is loaded. This way the users can specify "turn 90 degrees on the x-axis." It makes things more readable, and the XML file only needs to store a 3 fields instead of 4. Conversion from Euler rotations to Quaternions is only done once, at load time, and is very quick. Again, the method is in the Quaternions.java class.


That should explain the basics of rotation and how we implement it in our game. If you have any other questions (or feel any information is missing), let me know and I'll add it here.

