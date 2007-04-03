import java.awt.Color;
import java.io.*;

public class Constants
{
	public static final int SUCCESS = 1; //won't compile without this guys... Remove it when you've removed the reference in World

	// server information
	public static final int MAX_CONNECTIONS = 8;

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
	
	// Defaults (for Element and Shape)
	public static final float[] DEFAULT_POSITION = {0,0,0};
	public static final float[] DEFAULT_FACING = {0,0,0,1}; //in Quaternions!
	public static final float[] DEFAULT_SCALE = {1,1,1};
	public static final float[] DEFAULT_BOUNDS = {1.0f, 1.0f, 1.0f}; //the half-dimensions of a default bounding box in 3D
	public static final String DEFAULT_NAME = "Bruce";
	public static final int DEFAULT_COLOR = 0;
	public static final String DEFAULT_TEXTURE = null;

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
	//NOTE: we may need to start calculating these directly.
	public static final float[] QUAT_CLOX = {-0.13052619f, 0.0f, 0.0f, 0.9914449f};
	public static final float[] QUAT_CCLX = {0.13052619f, 0.0f ,0.0f, 0.9914449f};
	public static final float[] QUAT_CLOY = {0.0f, -0.13052619f, 0.0f, 0.9914449f};
	public static final float[] QUAT_CCLY = {0.0f, 0.13052619f, 0.0f, 0.9914449f};
	public static final float[] QUAT_CLOZ = {0.0f, 0.0f, -0.13052619f, 0.9914449f};
	public static final float[] QUAT_CCLZ = {0.0f, 0.0f, 0.13052619f, 0.9914449f};
	
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
	public static final float INITIAL_Y = 0;
	public static final float INITIAL_Z = 0;
	public static final short ELEMENT_INFO_SIZE = 2;
	public static final short MIN = 0;
	public static final short MAX = 1;
	public static final short RADIUS = 0;
	public static final short HEIGHT = 1;
	public static final String UNIQUE_GE_PREFIX = "GE_";

	// graphics
	public static final short UPDATE_PERIOD = 10;
	public static final short CANVAS_WIDTH = 600;
	public static final short CANVAS_HEIGHT = 600;

	// resolver stuff
	public static final short DEFAULT_RULE_ARRAY_SIZE = 20;
	public static final short SENTENCE_LENGTH = 4;
	
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
			System.err.println("Died on toByteArray: " + ioe.getMessage());
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
			System.err.println("Died on fromByteArray: " + ioe.getMessage());
			System.exit(1);
		}
		catch(ClassNotFoundException cnfe)
		{
				System.err.println("Bad class on fromByteArray: " + cnfe.getMessage());
				System.exit(1);
		}
		return a;
	}
}
