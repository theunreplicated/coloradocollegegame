import java.io.*;
import java.util.*;
public class GameElement extends LinkedElement<GameElement> implements Serializable
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
	
	public void id(int _id)
	{
		id = _id;
	}
	public int id()
	{
		return id;
	}

	public synchronized Object[] getInfoArray()
	{
		return new Object[] {
								typeId,
								new float[] {
									position[X],
									position[Y],
									position[Z]
								}
							};
	}

	public synchronized void setAttributes(HashMap _attributes)
	{
		attributes = _attributes; 
	}

	public synchronized HashMap getAttributes()
	{
		return attributes;
	}

	public synchronized Object attribute(String _key)
	{
		return attributes.get(_key);
	}

	public synchronized void attribute(String _key , Object _value)
	{
		attributes.put(_key,_value);
	}

	/* I'm thinking we can probably get rid of the individual dimension calls and just have
	   everything work with vectors */

	/************* 
	 * Accessors *
	 *************/

	private synchronized void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(id);
		out.writeInt(typeId);
		out.writeObject(position);
		out.writeObject(facing);
		out.writeObject(attributes);
	}

	private synchronized void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		id = in.readInt();
		typeId = in.readInt();
		position = (float[]) in.readObject();
		facing = (float[]) in.readObject();
		attributes = (HashMap) in.readObject();
	}
	private synchronized void readObjectNoData() throws ObjectStreamException
	{
	}

	public synchronized String toString()
	{
		String s = "GameElement #" + id + ":\n" +
			" Type: " + type +" (" + typeId + ")\n" + 
		  " Position:";
		for(float f : position)
			s += " " + f;
		s += "\n Attributes:\n";
		Set<Map.Entry<String,Object>> entries = attributes.entrySet();
		Iterator<Map.Entry<String,Object>> it = entries.iterator();
		Map.Entry<String,Object> entry;
		while(it.hasNext())
		{
			entry = it.next();
			s += "  " + entry.getKey() + ": " + entry.getValue() + "\n";
		}
		return s;

	}

	public synchronized float[] getPosition()
	{
		return position;
	}	

	//returns the specified dimension of the position
	public synchronized float getPosition(int dim)
	{
		return position[dim];
	}

	public synchronized float[] getFacing()
	{
		return facing;
	}

	public synchronized float[] getBoundingBox()
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
	public synchronized void nudge( float[] delta )
	{
		for( int i = 0 ; i < position.length; i++ )
			position[i] += delta[i];
	}
	
	//sets position to the given
	public synchronized void setPosition(float[] _position)
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
	public synchronized void rotate(float[] q)
	{
		if(q.length != 4)
			System.out.println("Bad Quaternion length. Bad!");
		else
			facing = Quaternions.mul(facing,q);
	}

	//sets the facing to the specified Quaternion
	public synchronized void setFacing(float[] _facing)
	{
		for( int i = facing.length-1 ; i >= 0; i--)
			facing[i] = _facing[i];
	}	


	/*********************** 
	 * Collision Detection *
	 ***********************/	


	//this method will run a collision detection tree using other collision methods.
	public synchronized boolean isColliding(GameElement _element)
	{
		//currently just uses OBBs in 3D to check
		return VectorUtils.OBB3DIntersect(boundingBox, 
						_element.getBoundingBox(), 
						VectorUtils.sub(_element.getPosition(),position),
						Quaternions.getMatrixFromQuat(_element.getFacing(),facing));

	}

	//method is currently empty.
	public synchronized String isCollidingShape(GameElement _element) //why are you returning a String?
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
	public synchronized int[][] getAbsoluteCoordinates()
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
