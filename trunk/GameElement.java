import java.util.*;
public class GameElement extends LinkedElement
{ 
	public boolean changed = true;
	private int id;
	int typeId;
	int status;
	boolean isClient;

	float[] position = new float[3]; // Absolute postition of the element
	float[][] boundingBox = null;
	VirtualShape[] shapes = null;
	HashMap attributes = null;
	String type = null;

	// Remove these once we have Element and ElementGenerator working
	public int[][] dimensions = new int[3][4]; 

	public GameElement( String _type, float[] _position, float[][] _boundingBox, VirtualShape[] _shapes, HashMap _attributes)
	{
		type = _type;
		position = _position;
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
		boundingBox = new float[original.boundingBox.length][original.boundingBox[0].length];
		System.arraycopy(original.position,0,position,0,original.position.length);
		System.arraycopy(original.shapes,0,shapes,0,original.shapes.length);
		System.arraycopy(original.boundingBox,0,boundingBox,0,original.boundingBox.length);
		if(original.attributes != null)
			attributes = (HashMap) original.attributes.clone();
		typeId = original.typeId;
		/*initialize is depracated */
		initialize(position, 10, 10);
	}

	public void toggleIsClient()
	{
		isClient = !isClient;
	}

	public boolean isClient()
	{
		return isClient;
	}

	public void setTypeId(int _id)
	{
		typeId = _id;
	}

	public int getTypeId()
	{
		return(typeId);
	}
	
	public Object attribute(String _key)
	{
		return attributes.get(_key);
	}

	public void attribute(String _key , Object _value)
	{
		attributes.put(_key,_value);
	}

	/* this takes in the index of the dimension you 
	 * want to change in the position array and the value you want to 
	 * nudge it to */
	public void nudge(int _dim, int _value)
	{
		position[_dim] += (float)_value;
	} // nudge

	public void nudge( int[] delta )
	{
		/* eventually these should come in as floats.  messaging system needs
		 * to be updated */
		for( int i = 0 ; i < delta.length; i++ )
			position[i] += (float)delta[i];
	}

	public void setPosition(int[] _position)
	{
		/* eventually these should come in as floats.  messaging system needs
		 * to be updated */
		for( int i = _position.length-1 ; i >= 0; i--)
			position[i] = (float)_position[i];
	} // setposition

	/* this takes in the index of the dimension you 
	 * want to change in the position array and the value you want to 
	 * change it to */
	public void setPosition(int _dim, int _value)
	{
		/* eventually these should come in as floats.  messaging system needs
		 * to be updated */
		position[_dim] = (float)_value;
	}
	
	public int[] getPosition()
	{
		/* eventually this should return floats.  messaging system needs
		 * to be updated */
		int[] tmp = new int[position.length];
		for(int i=0;i<position.length;i++)
			tmp[i] = (int) position[i];
		return tmp;
	}

	/* this takes in the index of the dimension you 
	 * want */
	public int getPosition(int dim)
	{
		/* eventually this should return a float.  messaging system needs
		 * to be updated */
		return (int)position[dim];
	}

	public void rotate(float[] _angles)
	{
		// So far emtpy... fill me please
		
		//If you want to be filled in, you should have an orientation variable or something
		//  that we can set when we rotate you.
	}

	public int[] getInfoArray()
	{
		/*  Should not be casting those floats as ints, better messagering system
		 *  needs to be devised! */
		return new int[] { typeId, (int)position[X], (int)position[Y] , (int)position[Z] };
	}

	public boolean isColliding(GameElement _element)
	{
		/* Joel, do you want to write this function? */
		return false; //you can never collide!  mwuahahaha!
	}

	public String isCollidingShape(GameElement _element)
	{
		/* And this one? */
		return null; //nope! Not colliding!
	}

	/*
	public boolean isRelevant(float[] _position, float _radius)
	{
		float sum = (float)Math.pow(_position[0]-position[0],2);
		for( int i = position.length-1;i > 0; i--)
			sum += (float)Math.pow(_position[i]-position[i],2);

		return ((float)Math.sqrt( sum ) - _radius - relevantRadius) < 0;
	} */

	/* For convienence */
	public final static int X=Constants.X, Y=Constants.Y, Z=Constants.Z;

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
		isClient = false;

		float _x = _position[0];
		float _y = _position[1];
		float _z = _position[2];
		position[X] = _x;
		position[Y] = _y;
		position[Z] = _z;
		
		dimensions[X][0] = -(int)(_width/2);
		dimensions[X][1] = (int)(_width/2);
		dimensions[X][2] = (int)(_width/2);
		dimensions[X][3] = -(int)(_width/2);

		dimensions[Y][0] = -(int)(_length/2);
		dimensions[Y][1] = -(int)(_length/2);
		dimensions[Y][2] = (int)(_length/2);
		dimensions[Y][3] = (int)(_length/2);

		//am not worring about third dimension yet
		dimensions[Z][0] = 0;
		dimensions[Z][1] = 0;
		dimensions[Z][2] = 0;
		dimensions[Z][3] = 0;
	}

} // class GameElement
