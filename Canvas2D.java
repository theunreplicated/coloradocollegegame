import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * The Canvas class is used to draw the video image of the World. 
 * Currently the screen image size is set to a default value of 400X400. 
 * The canvas is also loads the controller to listen for any keyboard
 * input or any mouse input. 
 */
public class Canvas2D extends JPanel implements Representation
{
    /**
     * Creates the panel to view in game objects, elements, knicknacks, players, etc. 
     */
	JPanel mainPanel = new JPanel();
	GraphicalElement[] elements;
	BufferedImage virtualImage;
	Graphics g;

	public Canvas2D( )
	{
		elements = new GraphicalElement[Constants.MAX_CONNECTIONS];

		this.setFocusable( true );
		this.setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT));

		virtualImage = new BufferedImage(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = virtualImage.getGraphics();

		PainterThread pt = new PainterThread(this);
		pt.start();
	}

    /** 
     * Creates the listener for the Controller class to accept any information from
     * in the form of keyboard input and mouse input. 
     */
	public void setListeners( ClientIO _myIO )
	{
		this.addMouseListener( _myIO );
		this.addKeyListener( _myIO );
	}

	public void render()
	{
		g.setColor(Color.WHITE);
		g.fillRect(0,0,this.getWidth(),this.getHeight());

		for(GraphicalElement e : elements)
		{
			if(e != null) e.render(g);
		}
	}

	public void paint( Graphics _g )
	{
		_g.drawImage(virtualImage, 0, 0, null);

		Toolkit.getDefaultToolkit ().sync (); // helps flush on some systems
		_g.dispose ();
	}

	private class PainterThread extends Thread
	{	long timeDiff, sleepTime;
		long beforeTime = System.currentTimeMillis ();

		private boolean running = true;
		private Canvas2D myCanvas;
		
		public PainterThread(Canvas2D _myCanvas)
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

	public Element createPerson(int _x, int _y, int _z, int _width, int _height, int _status)
	{
		return(new GraphicalElement(_x, _y, _z, _width, _height, _status));
	}
}
