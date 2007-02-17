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
		//	BufferedInputStream in = new BufferedInputStream(client.getInputStream());
	//		out = new BufferedOutputStream(client.getOutputStream());
			InputStream in = client.getInputStream();
			out = client.getOutputStream();
			out.write( row );

			serve.sendWorld(row);

			byte[] message = new byte[Constants.MESSAGE_SIZE];

			int i,messageLength,start,startRead;
			int[] intMessage;
			int[] subMessage;
			start = 0;
			startRead = 0;

			while( (messageLength=in.read(message,startRead,Constants.MESSAGE_SIZE - startRead)) != -1 )
			{
		/*		subMessage = Constants.fromByteArray( message );
						System.out.print("message! => start: " + startRead + "  message: " );
						for(int x = 0; x < subMessage.length; x++)
							System.out.print( subMessage[x] + " " );
						System.out.println();*/

				intMessage = Constants.fromByteArray(message);
				messageLength = (messageLength+startRead)/4;

				for(i=0; i< messageLength; i++)
				{
					if( intMessage[i] == Integer.MAX_VALUE )
					{
						subMessage = new int[i-start];
						System.arraycopy(intMessage,start,subMessage,0,i-start);
						serve.propagate( subMessage , row );
						start = i+1;
					}
				}
				if( start < messageLength )
				{
					System.arraycopy(Constants.toByteArray(intMessage),start*4,message,0,(messageLength-start)*4);
					startRead = (messageLength-start)*4;
				}
				else startRead = 0;
				start = 0;
			}

			serve.myLogger.message( "connection closed: " + row + "\n", false );
			
			client.close();
		}
		catch (IOException ioe)
		{
			serve.myLogger.message("Connection error on row " + row + ": " + ioe.getMessage() + "\n", true);
		}
		serve.propagate( new int[]{ Constants.REMOVE_PLAYER , row } , row );
		serve.removeThread( row );
	}

	public void send( int[] _message )
	{
		try
		{
			out.write( Constants.toByteArray(_message) );
			out.write( Constants.toByteArray(new int[] { Integer.MAX_VALUE }) );
		}
		catch( IOException ioe )
		{
			serve.myLogger.message( "Sending error on row " + row + ": " + ioe.getMessage() + "\n", true );
		}
	}

}
