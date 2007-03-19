import java.util.*;
public class GameElement extends LinkedElement<GameElement>
{ 
	public boolean changed = true;
	private int id;
	int typeId;
	int status;

	private float[] position = new float[3]; //World-level postition of the element
	private float[] facing = new float[4]; //The element's orientation (in Quaternions!)
	private float[] boundingBox = new float[3];

	VirtualShape[] shapes = null;
	private HashMap attributes = null;
	String type = null;

	/* For convienence */
	public final static int X=Constants.X, Y=Constants.Y, Z=Constants.Z;

	// Remove these once we have Element and ElementGenerator working
	public int[][] dimensions = new int[3][4]; 

	public GameElement( String _type, float[] _position, float[] _facing, float[] _boundingBox, VirtualShape[] _shapes, HashMap _attributes)
	{
		type = _type;
		position = _position;
		facing = _facing;
		shapes = _shapes;
		boundingBox = _boundingBox;
		
		attributes = _attributes;
		/*initialize is depracated */
		initialize(position, 10, 10);
		System.out.println("New element: " + type + ", has: " + shapes.length + " shapes");
	}

	public GameElement( GameElement original )
	{
		type = new String(original.type);
		shapes = new VirtualShape[original.shapes.length];
		boundingBox = new float[original.boundingBox.length];
		System.arraycopy(original.position,0,position,0,original.position.length);
		System.arraycopy(original.facing,0,facing,0,original.facing.length);
		System.arraycopy(original.shapes,0,shapes,0,original.shapes.length);
		System.arraycopy(original.boundingBox,0,boundingBox,0,original.boundingBox.length);
		if(original.attributes != null)
			attributes = (HashMap) original.attributes.clone();
		typeId = original.typeId;
		/*initialize is depracated */
		initialize(position, 10, 10);
	}

	public void setTypeId(int _id)
	{
		typeId = _id;
	}

	public int getTypeId()
	{
		return(typeId);
	}
	
	public int id()
	{
		return id;
	}

	public Object[] getInfoArray()
	{
		return new Object[] { typeId, position[X], position[Y], position[Z] };
	}

	public Object attribute(String _key)
	{
		return attributes.get(_key);
	}

	public void attribute(String _key , Object _value)
	{
		attributes.put(_key,_value);
	}

	/* I'm thinking we can probably get rid of the individual dimension calls and just have
	   everything work with vectors */

	/************* 
	 * Accessors *
	 *************/

	public float[] getPosition()
	{
		return position;
	}	

	//returns the specified dimension of the position
	public float getPosition(int dim)
	{
		return position[dim];
	}

	public float[] getFacing()
	{
		return facing;
	}

	public float[] getBoundingBox()
	{
		return boundingBox;
	}
	
	/************ 
	 * Mutators *
	 ************/

	//changes position in the specified dimension by the specified value
/*	public void nudge(int _dim, float _value)
	{
		position[_dim] += _value;
	}
*/
	//changes position by a vector
	public void nudge( float[] delta )
	{
		for( int i = 0 ; i < position.length; i++ )
			position[i] += delta[i];
	}
	
	//sets position to the given
	public void setPosition(float[] _position)
	{
		for( int i = position.length-1 ; i >= 0; i--)
			position[i] = _position[i];
	}

	//sets the position in the specified dimension to the specified value
/*	public void setPosition(int _dim, float _value)
	{
		position[_dim] = _value;
	}
*/	

	//rotate by the specified Quaternion
	public void rotate(float[] q)
	{
		if(q.length != 4)
			System.out.println("Bad Quaternion length. Bad!");
		else
			facing = Quaternions.mul(facing,q);
	}

	//sets the facing to the specified Quaternion
	public void setFacing(float[] _facing)
	{
		for( int i = facing.length-1 ; i >= 0; i--)
			facing[i] = _facing[i];
	}	

	
	/*********************** 
	 * Collision Detection *
	 ***********************/	

	//this method will run a collision detection tree using other collision methods.
	public boolean isColliding(GameElement _element)
	{
		//currently just uses OBBs in 3D to check
		return VectorUtils.OBB3DIntersect(boundingBox, 
						_element.getBoundingBox(), 
						VectorUtils.sub(_element.getPosition(),position),
						Quaternions.getMatrixFromQuat(_element.getFacing(),facing));

	}

	//method is currently empty.
	public String isCollidingShape(GameElement _element) //why are you returning a String?
	{
		/* I'll get to it eventually. Shape-level collisions are not exactly high-priority */
		return null;
	}




	/*
	public boolean isRelevant(float[] _position, float _radius)
	{
		float sum = (float)Math.pow(_position[0]-position[0],2);
		for( int i = position.length-1;i > 0; i--)
			sum += (float)Math.pow(_position[i]-position[i],2);

		return ((float)Math.sqrt( sum ) - _radius - relevantRadius) < 0;
	} */


	/*  Deprecated stuff*/
	public int[][] getAbsoluteCoordinates()
	{
		for( int i = 0; i < dimensions[X].length ; i++ )
		{
			absDimensions[X][i] = (int)position[X]+dimensions[X][i];
			absDimensions[Y][i] = (int)position[Y]+dimensions[Y][i];
			absDimensions[Z][i] = (int)position[Z]+dimensions[Z][i];
		}
		
		return absDimensions;
	}
	int[][] absDimensions = new int[3][4];
	public void initialize( float[] _position , int _width, int _length )
	{
		float _x = _position[0];
		float _y = _position[1];
		float _z = _position[2];
		position[X] = _x;
		position[Y] = _y;
		position[Z] = _z;
		
		dimensions[X][0] = -(_width/2);
		dimensions[X][1] = (_width/2);
		dimensions[X][2] = (_width/2);
		dimensions[X][3] = -(_width/2);

		dimensions[Y][0] = -(_length/2);
		dimensions[Y][1] = -(_length/2);
		dimensions[Y][2] = (_length/2);
		dimensions[Y][3] = (_length/2);

		//am not worring about third dimension yet
		dimensions[Z][0] = 0;
		dimensions[Z][1] = 0;
		dimensions[Z][2] = 0;
		dimensions[Z][3] = 0;
	}

} // class GameElement
