import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

/**
 * The Canvas class is used to draw the video image of the World. 
 * Currently the screen image size is set to a default value of 400X400. 
 * The canvas is also loads the controller to listen for any keyboard
 * input or any mouse input. 
 */

public class Representation2D extends JPanel implements Representation
{
	static final long serialVersionUID = -7743175550804657967L;

    /**
     * Creates the panel to view in game objects, elements, knicknacks, players, etc. 
     */
	GameElement first;
	BufferedImage virtualImage;
	Graphics g;

	public void initialize(World w, Logger myLogger)
	{
		first = w.getFirstElement();

		this.setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT));

		virtualImage = new BufferedImage(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = virtualImage.getGraphics();

		PainterThread pt = new PainterThread(this);
		pt.start();
	}
	
	public void render()
	{
		g.setColor(Color.WHITE);
		g.fillRect(0,0,this.getWidth(),this.getHeight());

		GameElement e=first;
		int[][] tmpPoints = e.getAbsoluteCoordinates();
		g.setColor(Color.BLACK);
		g.fillPolygon( tmpPoints[Constants.X], tmpPoints[Constants.Y] , 4 );

		for(e = e.next; e != first; e = e.next)
		{
			if(e != null)
			{
				tmpPoints = e.getAbsoluteCoordinates();
				g.setColor(Color.BLACK);
				g.drawPolygon( tmpPoints[Constants.X], tmpPoints[Constants.Y] , 4 );
			}
		}
	}

	public void paint( Graphics _g )
	{
		_g.drawImage(virtualImage, 0, 0, null);

		Toolkit.getDefaultToolkit ().sync (); // helps flush on some systems
		_g.dispose ();
	}

	private class PainterThread extends Thread
	{
		long timeDiff, sleepTime;
		long beforeTime = System.currentTimeMillis ();

		private boolean running = true;
		private Representation2D myCanvas;
		
		public PainterThread(Representation2D _myCanvas)
		{
			myCanvas = _myCanvas;
		}
		
		public void run()
		{
			while(running)
			{
				myCanvas.render();
				myCanvas.repaint();

				timeDiff = System.currentTimeMillis () - beforeTime;
				sleepTime = Math.max (5L, Constants.UPDATE_PERIOD - timeDiff);
				try
				{	Thread.sleep (sleepTime); } 
				catch (InterruptedException e) {}
				beforeTime = System.currentTimeMillis ();
			}
		}

		public void killThread()
		{
			running = false;
		}
	}

	//fetches the canvas we're drawing this on
	public Component getComponent()
	{
		return this;
	}

	//an update method for updating a particular element's location
	public void updateLocation(GameElement ge)
	{}

	//an update method for updating a particular element's presence in the game
	public void updatePresence(GameElement ge)
	{}

	//cycles through the views (not in use)
	public void changeView()
	{}
	
	//changes to the specified view (not in use)
	public void changeView(int to)
	{}

	//CHANGES the position of the camera BY the specified translation and rotation
	public void adjustCamera(float[] translation, float[] rotation)
	{}

	//SETS the position of the camera TO the specified translation and rotation
	public void setCamera(float[] translation, float[] rotation)
	{}
	
	public float[] getCameraPosition()
	{return null;}
	
	public float[] getCameraFacing()
	{return null;}

	public static void main(String args[])
	{
		Representation2D me = new Representation2D( );

		Client.initialize(args, me);

		JFrame frame = new JFrame();
		frame.getContentPane().add( me );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //for now. Need to send a nice close message	
		frame.pack();
		frame.setVisible( true ) ;
	}
}
