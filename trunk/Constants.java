import java.awt.Color;
import java.io.*;

public class Constants
{
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
	public static final int MESSAGE_SIZE = 90000;

	// Startup
	public static final String DEFAULT_DATA_DIR = ".";
	public static final String ELEMENT_LIST_EXTENSION = "ccel";
	public static final float[] DEFAULT_POSITION = {0,0,0};
	public static final float[] DEFAULT_SCALE = {0,0,0};

	// IDs for actions that the World understands
	public static final int MOVE_TO = 101;

	// Vectors for cardinal movement (in 3D)
	public static final float[] VEC_POSX = {1.0f, 0.0f, 0.0f};
	public static final float[] VEC_NEGX = {-1.0f, 0.0f, 0.0f};
	public static final float[] VEC_POSY = {0.0f, 1.0f, 0.0f};
	public static final float[] VEC_NEGY = {0.0f, -1.0f, 0.0f};
	public static final float[] VEC_POSZ = {0.0f, 0.0f, 1.0f};
	public static final float[] VEC_NEGZ = {0.0f, 0.0f, -1.0f};
	
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
	public static final short ELEMENT_INFO_SIZE = 4;
	public static final short MIN = 0;
	public static final short MAX = 1;
	public static final short RADIUS = 0;
	public static final short HEIGHT = 1;

	// graphics
	public static final short UPDATE_PERIOD = 10;
	public static final short CANVAS_WIDTH = 300;
	public static final short CANVAS_HEIGHT = 300;

	// resolver messages
	public static final short SUCCESS = 0;
	
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

	public static String toString(int[] _data)
	{
		String a = "";
		for(int i : _data)
		{
			a += i + " ";
		}
		return a;
	}
}
