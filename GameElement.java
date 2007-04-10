import java.io.*;
import java.util.*;
public class GameElement extends LinkedElement<GameElement> implements Serializable, Comparable
{ 
	static final long serialVersionUID = -5470967480584428990L;
	public boolean changed = true;
	private int id;
	int typeId;
	int status;

	private float[] position = new float[3]; //World-level postition of the element
	private float[] facing = new float[4]; //The element's orientation (in Quaternions!)
	private float[] scale = new float[3];
	private float[] boundingBox = new float[3];
	private float boundingRadius;

	VirtualShape[] shapes = null;
	private AttributesHashMap attributes = null;
	String type = null;

	/* For convienence */
	public final static int X=Constants.X, Y=Constants.Y, Z=Constants.Z;

	// Remove these once we have Element and ElementGenerator working
	public int[][] dimensions = new int[3][4]; 

	public GameElement( String _type, float[] _position, float[] _facing, float[] _boundingBox, float[] _scale, VirtualShape[] _shapes, AttributesHashMap _attributes)
	{
		type = _type;
		position = _position;
		facing = _facing;
		scale = _scale;
		shapes = _shapes;
		boundingBox = _boundingBox;
		boundingRadius = VectorUtils.getContainingSphere(boundingBox);
		
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
		System.arraycopy(original.scale,0,scale,0,original.scale.length);
		System.arraycopy(original.shapes,0,shapes,0,original.shapes.length);
		System.arraycopy(original.boundingBox,0,boundingBox,0,original.boundingBox.length);
		boundingRadius = VectorUtils.getContainingSphere(boundingBox);
		if(original.attributes != null)
			attributes = (AttributesHashMap) original.attributes.clone();
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
		return new Object[] {	typeId,
					new float[] 	{
						position[X],
						position[Y],
						position[Z]
							}
					};
	}

	public synchronized void setAttributes(AttributesHashMap _attributes)
	{
		attributes = _attributes; 
	}

	public synchronized AttributesHashMap getAttributes()
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


	/************* 
	 * Accessors *
	 *************/


	// The way to compare one GameElement to another is to compare their
	// types (only used right now in the ElementFactory).
	public int compareTo(Object _ge)
	{
		return(type.compareTo(((GameElement) _ge).type));
	}

	public synchronized String toString()
	{
		String s = "GameElement #" + id + ":\n" +
			" Type: " + type +" (" + typeId + ")\n" + 
		  " Position:";
		for(float f : position)
			s += " " + f;
		s += "\n Facing:";
		for(float f : facing)
			s += " " + f;
		s += "\n Scale:";
		for(float f : scale)
			s += " " + f;
		s += "\n BoundingBox:";
		for(float f: boundingBox)
			s += " " + f;
		s += " ("+boundingRadius+")";
		s += "\n Attributes:\n";
		Set<Map.Entry<String,Object>> entries = attributes.entrySet();
		Iterator<Map.Entry<String,Object>> it = entries.iterator();
		Map.Entry<String,Object> entry;
		while(it.hasNext())
		{
			entry = it.next();
			if(entry.getValue() instanceof GameElement) // to prevent infinite loops
				s += "  " + entry.getKey() + ": " + Constants.UNIQUE_GE_PREFIX + ((GameElement) entry.getValue()).id + "\n";
			else
				s += "  " + entry.getKey() + ": " + entry.getValue() + "\n";
		}
		return s;
	
	}
	
	public synchronized VirtualShape[] getShapes()
	{
		return shapes;
	}

	public synchronized float[] getPosition()
	{
		return position;
	}	

	public synchronized float[] getFacing()
	{
		return facing;
	}

	public synchronized float[] getScale()
	{
		return scale;
	}

	public synchronized float[] getBoundingBox()
	{
		return boundingBox;
	}

	public synchronized float getBoundingRadius()
	{
		return boundingRadius;
	}

	
	/************ 
	 * Mutators *
	 ************/

	//changes position by a vector
	public synchronized void nudge( float[] delta )
	{
		float[] tmp = new float[position.length];
		for( int i = 0 ; i < position.length; i++ )
			tmp[i] = position[i]+delta[i];
		position = tmp;
	}
	
	//sets position to the given
	public synchronized void setPosition(float[] _position)
	{
		position = _position;
	}

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
		facing = _facing;
	}	

	//scale by the specified factors
	public synchronized void scale(float[] delta)
	{
		for( int i = 0 ; i < scale.length; i++ )
			scale[i] *= delta[i];
	}

	//sets the scale to the specified factors
	public synchronized void setScale(float[] _scale)
	{
		scale = _scale;
	}

	//scales the boundingBox (used when creating scaling elements)
	public synchronized void scaleBoundingBox(float[] delta)
	{
		for( int i = 0 ; i < boundingBox.length; i++ )
			boundingBox[i] *= delta[i];
		boundingRadius = VectorUtils.getContainingSphere(boundingBox);
	}

	public synchronized void setBoundingBox(float[] bb)
	{
		boundingBox = bb;
		boundingRadius = VectorUtils.getContainingSphere(boundingBox);
	}


	/*********************** 
	 * Collision Detection *
	 ***********************/	

	public synchronized boolean isRelevant(GameElement _element)
	{
		return true;
	}

	//this method will run a collision detection tree using other collision methods.
	public synchronized boolean isColliding(GameElement _element)
	{
		//check bounding spheres
		if(VectorUtils.getDistSqr(position, _element.getPosition()) <= 
			(boundingRadius+_element.getBoundingRadius())*(boundingRadius+_element.getBoundingRadius()))
		{
			//uses OBBs in 3D to check
			return VectorUtils.OBB3DIntersect(boundingBox, 
						_element.getBoundingBox(), 
						VectorUtils.sub(position, _element.getPosition()),
						Quaternions.getMatrixFromQuat(facing, _element.getFacing()));
		}
		else
			return false;
	}

	//Returns an ArrayList containing String[] pairs of the names of shapes which intersect between the two elements
	//-NOTE: This hasn't been tested yet. It should work, but I'm too lazy to check.
	//	 Also, this method is going to be pretty damn slow, and should rarely be called (and never without some filtering)
	public synchronized ArrayList<String[]> isCollidingShape(GameElement _element)
	{
		ArrayList<String[]> collisions = new ArrayList<String[]>(); //a list of shape pairs that collide
		
		VirtualShape[] ushapes = _element.getShapes();
		float[] uposition = _element.getPosition();
		float[] ufacing = _element.getFacing();

		float[] sposi;
		float[] sface;
		float[] a;
		float[] T; 
		float[][] R;

		for(int i=0; i<shapes.length; i++) //for every Shape in me
		{
			sposi = shapes[i].getPosition();
			sface = shapes[i].getFacing();
			a = shapes[i].getBoundingBox();
			
			for(int j=0; j<ushapes.length; j++) //for every Shape in you
			{
				//get relative position/rotation
				T = VectorUtils.sub(	VectorUtils.add(sposi,position), 
							VectorUtils.add(ushapes[j].getPosition(), uposition));
				R = Quaternions.getMatrixFromQuat(	Quaternions.mul(sface, facing), 
									Quaternions.mul(ushapes[j].getFacing(), ufacing));	
								
				//use VectorUtils to check for OBB collisions
				if(VectorUtils.OBB3DIntersect(a,ushapes[j].getBoundingBox(), T, R)) //if collides
					collisions.add(new String[] {shapes[i].getName(), ushapes[j].getName()});
			}
		}
		
		if(collisions.size() == 0)
			return null;
		else
			return collisions;
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
