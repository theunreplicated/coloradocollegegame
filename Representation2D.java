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

	public void initialize(World w, ClientInput ci, Logger myLogger)
	{
		first = w.getFirstElement();

		// Setting the Representation - a temporary solution for camera-relative movement -- Omer.

		this.setFocusable( true );
		this.setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT));

		this.addMouseListener(ci);
		this.addKeyListener(ci);

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

	//an update method for updating a particular element's location
	public void updateLocation(GameElement ge)
	{}

	//an update method for updating a particular element's presence in the game
	public void updatePresence(GameElement ge)
	{}

	//an update method (not in use)
	public void update()
	{}

	//cycles through the views (not in use)
	public void changeView()
	{}
	
	//changes to the specified view (not in use)
	public void changeView(int to)
	{}


	public static void main(String args[])
	{
		Representation2D canvas = new Representation2D( );
		Client.initialize(args, canvas);

		JFrame frame = new JFrame();
		frame.getContentPane().add( canvas );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //for now. Need to send a nice close message	
		frame.pack();
		frame.setVisible( true ) ;



	}
}
