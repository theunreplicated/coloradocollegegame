import java.net.*;
import java.io.*;

class ClientThread extends Thread {
	private Server serve;
	private Socket clientOut;
	private Socket clientIn;
	private int id, row;
	private boolean verbose;
	private ObjectOutputStream oos;
	private Logger myLogger;

	public ClientThread(Server _server, Socket _client, int _id, int _row, Logger _myLogger)
	{
		serve  = _server;
		clientOut = _client;
		clientIn = null;
		id = _id;
		row = _row;
		myLogger = _myLogger;
	}
	
	public void run()
	{
		try
		{
			oos = new ObjectOutputStream(clientOut.getOutputStream());
			oos.writeInt( id );
			oos.flush();

			int wait = 0;
			while(wait < Constants.TIMEOUT && clientIn == null)
			{
				try	
				{
					myLogger.message("ClientThread " + row + " is trying to connect to: " + clientOut.getInetAddress() + ":" + (clientOut.getLocalPort()+1)+"\n",false);
					clientIn = new Socket(clientOut.getInetAddress(), clientOut.getLocalPort()+1);
				}
				catch(IOException ioe)
				{
					myLogger.message("ClientThread " + row + " could not get input stream from client of id " + id + "... waiting a bit longer\n", false);
					try
					{
						Thread.sleep(1000);
					}
					catch(InterruptedException ie)
					{
						myLogger.message("ClientThread " + row + " interrupted while waiting to get input stream for client of id " + id + "\n", true);
					}
					wait += 1000;
					clientIn = null;
				}
			}
			ObjectInputStream ois = new ObjectInputStream(clientIn.getInputStream());
			serve.sendWorld(row);


			Object[] objectMessage;


			while( (objectMessage = (Object[]) ois.readObject()) != null )
			{
				serve.propagate(objectMessage, row);
			}

			myLogger.message( "connection closed: " + row + "\n", false );
			
			clientOut.close();
			clientIn.close();
		}
		catch (IOException ioe)
		{
			myLogger.message("Connection error on row " + row + ": " + ioe.getMessage() + "\n", true);
		}
		catch(ClassNotFoundException cnfe)
		{
			myLogger.message( "Sending error on row " + row + " (class not found): " + cnfe + "\n", true );
		}
		catch(Exception e)
		{
			myLogger.message("Error...\n", true);
		}
		serve.propagate( new Object[]{ Constants.REMOVE_PLAYER, id } , row );
		serve.removeThread( row );
	}

	public void send( Object _message )
	{
		try
		{
			oos.writeObject(_message);
			oos.flush();
		}
		catch( IOException ioe )
		{
			myLogger.message( "Sending error on row " + row + ": " + ioe.getMessage() + "\n", true );
		}
	}

}
