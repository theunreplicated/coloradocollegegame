import java.awt.Color;
import java.io.*;
import org.w3c.dom.Element;

public class Constants
{
	public static final int SUCCESS = 0; //won't compile without this guys... Remove it when you've removed the reference in World

	// server information
	public static final int TIMEOUT = 3000; // maximum time for no communication: 3 seconds
	public static final int MAX_CONNECTIONS = 8;
	public static final int ELEMENT_ID_PADDING = (int)Math.pow(10,(Constants.MAX_CONNECTIONS+"").length());
	/* Element ids guaranteed to be unique and also to be able to
	 * generate other unique ids easily. ELEMENT_ID_PADDING is:
	 * 10^(x+1) where x = the length in digits of the number of
	 * connections allowed on the server. So, with 8 maximum
	 * connections, ELEMENT_ID_PADDING is 10. With 315 maximum
	 * connections, ELEMENT_ID_PADDING is 1000. &c. All player elements
	 * have an id of ELEMENT_ID_PADDING plus some value between
	 * 1 and the total number of connections. So, the range of
	 * IDs for players when the server allows a total of eight
	 * connections is 11-18. The range of IDs for players when
	 * the server allows a total of 315 connections is 1001-1315.
	 * All non-player elements created by the server have an ID
	 * that has ELEMENT_ID_PADDING as its last n digits. So, with
	 * eight possible connections, elements created by the server
	 * will count up as such: 110, 210, 310, 410, 510, &c. With
	 * 315 possible connections, elements created by the server
	 * will count up as such: 11000, 21000, 31000, 41000, &c.
	 * Each player can also create elements. The IDs of those
	 * elements end in the digits that make up the player's ID.
	 * So, player 15's elements will count up as such: 115, 215,
	 * 315, 415, &c.
	 * This system guarantees unique IDs for any element created
	 * by any player or by the server.
	 */

	// communication between server(s) and client(s)
	public static final String DEF_SERVER = "";
	public static final int DEF_PORT = 5600;
	public static final int MAX_PORT = 10000;
	public static final int MIN_PORT = 1500;
	public static final int DEF_SERVER_PORT = 5700;
	public static final int LOGIN = 1;
	public static final int LOGOUT = 2;
	public static final int ADD_PLAYER = 3;
	public static final int REMOVE_PLAYER = 4;
	public static final int SEND_WORLD = 5;
	public static final int MESSAGE_SIZE = 90000;

	// Startup
	public static final String DEFAULT_DATA_DIR = ".";
	public static final String ELEMENT_LIST_EXTENSION = "ccel";
	public static final String WORLD_EXTENSION = "ccw";
	public static final String ACTION_EXTENSION = "ccr";
	public static final String RULE_EXTENSION = "ccr";
	
	// Defaults (for Element and Shape)
	public static final float[] DEFAULT_POSITION = {0,0,0};
	public static final float[] DEFAULT_FACING = {0,0,0,1}; //in Quaternions!
	public static final float[] DEFAULT_SCALE = {1,1,1};
	public static final float[] DEFAULT_BOUNDS = {1.0f, 1.0f, 1.0f}; //the half-dimensions of a default bounding box in 3D
	public static final String DEFAULT_NAME = "Bruce";
	public static final int DEFAULT_COLOR = 0;
	public static final String DEFAULT_TEXTURE = null;
	public static final int DEFAULT_TEXTURE_PATTERN = 1;

	// IDs for actions that the World understands
	public static final int MOVE_TO = 101;
	public static final int ROTATE_TO = 202;
	public static final int ATTRIBUTE = 303;

	// Unit vectors for cardinal movement (in 3D)
	public static final float[] VEC_POSX = {1.0f, 0.0f, 0.0f};
	public static final float[] VEC_NEGX = {-1.0f, 0.0f, 0.0f};
	public static final float[] VEC_POSY = {0.0f, 1.0f, 0.0f};
	public static final float[] VEC_NEGY = {0.0f, -1.0f, 0.0f};
	public static final float[] VEC_POSZ = {0.0f, 0.0f, 1.0f};
	public static final float[] VEC_NEGZ = {0.0f, 0.0f, -1.0f};
	
	//Quaternion rotations for "cardinal rotation" (in 3D)--a rotation of 
	// 15 degrees either clockwise or counterclockwise around a particular axis (looking down)
	public static final float[] QUAT_CLOX = new float[] {(float)Math.sin(Math.PI/-24.0), 0, 0, (float)Math.cos(Math.PI/-24.0)};
	public static final float[] QUAT_CCLX = new float[] {(float)Math.sin(Math.PI/24.0), 0, 0, (float)Math.cos(Math.PI/24.0)};
	public static final float[] QUAT_CLOY = new float[] {0, (float)Math.sin(Math.PI/-24.0), 0, (float)Math.cos(Math.PI/-24.0)};
	public static final float[] QUAT_CCLY = new float[] {0, (float)Math.sin(Math.PI/24.0), 0, (float)Math.cos(Math.PI/24.0)};
	public static final float[] QUAT_CLOZ = new float[] {0, 0, (float)Math.sin(Math.PI/-24.0), (float)Math.cos(Math.PI/-24.0)};
	public static final float[] QUAT_CCLZ = new float[] {0, 0, (float)Math.sin(Math.PI/24.0), (float)Math.cos(Math.PI/24.0)};
	
	// Keyboard stuff
	public static final int SHIFT_KEY = 0;
	public static final int CTRL_KEY = 1;
	public static final int ALT_KEY = 2;

	// GameElement stuff
	public static final short PERSON_WIDTH = 10;
	public static final short PERSON_HEIGHT = 10;
	public static final short X = 0;
	public static final short Y = 1;
	public static final short Z = 2;
	public static final float INITIAL_X = 0;
	public static final float INITIAL_Y = 10; //for now
	public static final float INITIAL_Z = 0;
	public static final short ELEMENT_INFO_SIZE = 2;
	public static final short MIN = 0;
	public static final short MAX = 1;
	public static final short RADIUS = 0;
	public static final short HEIGHT = 1;
	public static final String UNIQUE_GE_PREFIX = "GE_";

	// SketchUp stuff
	//radii from http://www.uwgb.edu/dutchs/UsefulData/UTMFormulas.HTM
	public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0;
	public static final double WGS84_POLAR_RADIUS = 6356752.3142;
	public static final double LON_METERS_PER_DEGREE = WGS84_EQUATORIAL_RADIUS*2*Math.PI/360.0d;
	public static final double LAT_METERS_PER_DEGREE = WGS84_POLAR_RADIUS*2*Math.PI/360.0d;	

	// graphics
	public static final short UPDATE_PERIOD = 10;
	public static final short CANVAS_WIDTH = 600;
	public static final short CANVAS_HEIGHT = 600;
	public static final short TEXTURE_STRETCH = 1;
	public static final short TEXTURE_CENTER = 2;
	public static final short TEXTURE_TILE = 3;


	// resolver stuff
	public static final short DEFAULT_RELEVANT_SIZE = 20;
	public static final short DEFAULT_RULE_ARRAY_SIZE = 20;
	public static final short DEFAULT_ACTION_HASHMAP_ENTRY_SIZE = 2;
	public static final short DEFAULT_ACTION_PARAMETERS_SIZE = 2;
	public static final short DEFAULT_ACTION_LIST_SIZE = 20;
	public static final short SENTENCE_LENGTH = 4;
	
	/* Depricated
	public static byte[] toByteArray(Object _message)
	{
		byte[] a = null;
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(outStream);
			out.writeObject(_message);
			out.close();
			return outStream.toByteArray();
		}
		catch(IOException ioe)
		{
			System.err.println("Died on toByteArray: " + ioe);
			System.exit(1);
		}
		return a;
	}

	public static Object fromByteArray(byte[] _message)
	{
		Object a = null;
		try {
			ByteArrayInputStream bos = new ByteArrayInputStream(_message);
			ObjectInput dis = new ObjectInputStream(bos);
			a = dis.readObject();
			dis.close();
			return a;
		}
		catch(IOException ioe)
		{
			System.err.println("Died on fromByteArray: " + ioe);
			System.exit(1);
		}
		catch(ClassNotFoundException cnfe)
		{
				System.err.println("Bad class on fromByteArray: " + cnfe);
				System.exit(1);
		}
		return a;
	}
	*/

	public static int parseTexture(String _texture)
	{
		if(_texture.equalsIgnoreCase("stretch"))
		{
			return Constants.TEXTURE_STRETCH;
		}
		else if(_texture.equalsIgnoreCase("center"))
		{
			return Constants.TEXTURE_CENTER;
		}
		else if(_texture.equalsIgnoreCase("tile"))
		{
			return Constants.TEXTURE_TILE;
		}
		else
		{
			System.err.println("parseTexture in Constants.java received unknown texture pattern... Setting to " + Constants.DEFAULT_TEXTURE_PATTERN);
			return Constants.DEFAULT_TEXTURE_PATTERN;
		}
	}

	public static Object parseXMLwithType(Element _element)
	{
		String attributeType = _element.getAttribute("type");
		if(attributeType.equalsIgnoreCase("String"))
		{
			return _element.getTextContent();
		}
		else if(attributeType.equalsIgnoreCase("int"))
		{
			return Integer.parseInt(_element.getTextContent());
		}
		else if(attributeType.equalsIgnoreCase("float"))
		{
			return Float.parseFloat(_element.getTextContent());
		}
		else if(attributeType.equalsIgnoreCase("hex32")) //for 32bit hexadecimal
		{
			return (int)Long.parseLong(_element.getTextContent(),16);
		}
		// &c.
		return null;
	}

	//temp method, though it might be nice for the future
	public static int getColorByClientID(int cid)
	{
		switch(cid%8)
		{
			case 0:
				return (int)Long.parseLong("ffff0000",16);
			case 1:
				return (int)Long.parseLong("ff00ff00",16); 
			case 2:
				return (int)Long.parseLong("ff0000ff",16); 
			case 3:
				return (int)Long.parseLong("ffffff00",16); 
			case 4:
				return (int)Long.parseLong("ffff00ff",16); 
			case 5:
				return (int)Long.parseLong("ff00ffff",16); 
			case 6:
				return (int)Long.parseLong("ffffffff",16);
			case 7:
				return (int)Long.parseLong("ff000000",16);
			default:
				return 0;
		}
	}


}
