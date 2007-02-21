import java.awt.Color;
import java.io.*;

public class Constants
{
	// ports
	public static final int DEF_PORT = 5600;
	public static final int MAX_PORT = 10000;
	public static final int MIN_PORT = 1500;
	public static final int DEF_SERVER_PORT = 5700;
	
	// number of connections and max message size
	public static final int MAX_CONNECTIONS = 8;
	public static final int MESSAGE_SIZE = 128;

	// communication between server(s) and client(s)
	public static final String DEF_SERVER = "";
	public static final short LOGIN = 1;
	public static final short LOGOUT = 2;
	public static final short ADD_PLAYER = 3;
	public static final short REMOVE_PLAYER = 4;
	public static final short CHANGE_CONNECTION = -1;

	// Startup
	public static final String DEFAULT_DATA_DIR = ".";
	public static final String ELEMENT_LIST_EXTENSION = "ccel";
	public static final int[] DEFAULT_POSITION = {0,0,0};
	public static final int[] DEFAULT_SCALE = {0,0,0};

	// IDs for actions that the World understands
	public static final int MOVE_TO = 101;

	// IDs for directions to move
	public static final short MOVE_DOWN = 1;
	public static final short MOVE_UP = 2;
	public static final short MOVE_RIGHT = 3;
	public static final short MOVE_LEFT = 4;

	// Keyboard stuff
	public static final int SHIFT_KEY = 0;
	public static final int CTRL_KEY = 1;
	public static final int ALT_KEY = 2;

	// GameElement stuff
	public static final short GAME_ELEMENT_INCREMENT = 30;
	public static final short STATUS_DEFAULT = 0;
	public static final short STATUS_FROZEN = 1;
	public static final short STATUS_IT = 2;
	public static final short PERSON_WIDTH = 10;
	public static final short PERSON_HEIGHT = 10;
	public static final short X = 0;
	public static final short Y = 1;
	public static final short Z = 2;
	public static final short INITIAL_X = 150;
	public static final short INITIAL_Y = 150;
	public static final short INITIAL_Z = 0;
	public static final short ELEMENT_INFO_SIZE = 5;

	// graphics
	public static final short UPDATE_PERIOD = 10;
	public static final short CANVAS_WIDTH = 300;
	public static final short CANVAS_HEIGHT = 300;

	// specific to freeze tag
	public static final Color[] COLORS = {Color.GREEN, Color.RED, Color.BLACK};

	// resolver messages
	public static final short SUCCESS = 0;

	public static byte[] toByteArray(int[] _message)
	{
		byte[] a = null;
		try {
			a = new byte[_message.length*4];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			for(int i = 0; i < _message.length; i++)
			{
				dos.writeInt(_message[i]);
			}
			dos.flush();
			return bos.toByteArray();
		}
		catch(IOException ioe)
		{
			System.err.println("Died on toByteArray: " + ioe.getMessage());
			System.exit(1);
		}
		return a;
	}

	public static int[] fromByteArray(byte[] _message)
	{
		int[] a = null;
		try {
			a = new int[_message.length/4];
			ByteArrayInputStream bos = new ByteArrayInputStream(_message);
			DataInputStream dos = new DataInputStream(bos);
			for(int i = 0; i < a.length; i++)
			{
				a[i] = dos.readInt();
			}
			return a;
		}
		catch(IOException ioe)
		{
			System.err.println("Died on fromByteArray: " + ioe.getMessage());
			System.exit(1);
		}
		return a;
	}
}
