import java.net.*;
import java.io.*;

class ClientThread extends Thread {
	Server serve;
	Socket client;
	int id, row;
	boolean verbose;
	OutputStream out;

	public ClientThread(Server _server, Socket _client, int _id, int _row)
	{
		serve  = _server;
		client = _client;
		id = _id;
		row = _row;
	}
	
	public void run()
	{
		try
		{
			InputStream in = client.getInputStream();
			out = client.getOutputStream();
			out.write( id );

			serve.sendWorld(row);

			byte[] message = new byte[Constants.MESSAGE_SIZE];

			int i;
			Object[] objectMessage;


			while( (in.read(message)) != -1 )
			{
				objectMessage = (Object[]) Constants.fromByteArray(message);
				serve.propagate(objectMessage, row);
			}

			serve.myLogger.message( "connection closed: " + row + "\n", false );
			
			client.close();
		}
		catch (IOException ioe)
		{
			serve.myLogger.message("Connection error on row " + row + ": " + ioe.getMessage() + "\n", true);
		}
		serve.propagate( new Object[]{ Constants.REMOVE_PLAYER, id } , row );
		serve.removeThread( row );
	}

	public void send( Object _message )
	{
		try
		{
			out.write( Constants.toByteArray(_message) );
			out.flush();
		}
		catch( IOException ioe )
		{
			serve.myLogger.message( "Sending error on row " + row + ": " + ioe.getMessage() + "\n", true );
		}
	}

}
