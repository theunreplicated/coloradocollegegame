import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * The Canvas class is used to draw the video image of the World. 
 * Currently the screen image size is set to a default value of 400X400. 
 * The canvas is also loads the controller to listen for any keyboard
 * input or any mouse input. 
 */
public class Canvas2D extends JPanel implements Representation, MouseListener, KeyListener
{
    /**
     * Creates the panel to view in game objects, elements, knicknacks, players, etc. 
     */
	JPanel mainPanel = new JPanel();
	GraphicalElement[] elements;
	BufferedImage virtualImage;
	Graphics g;
	Client myClient;
    	private boolean[] modifiers = { false, false, false }; //indicate whether or not the shift,
							       //ctrl and alt modifier keys are currently

	public Canvas2D()
	{
		elements = new GraphicalElement[Constants.MAX_CONNECTIONS];

		this.setFocusable( true );
		this.setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT));
		this.addMouseListener(this);
		this.addKeyListener(this);

		virtualImage = new BufferedImage(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = virtualImage.getGraphics();

		PainterThread pt = new PainterThread(this);
		pt.start();
	}
	
	//this method MUST be called. But because we have to pass a created canvas to the client, 
	//we can't create the client in the canvas constructor (I don't think)
	//well we can, but we'd need to pass the server/port/verbose parameters. I'll let someone else handle that
	public void setClient(Client _client)
	{
		myClient = _client;
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

	public GameElement createPerson(int _x, int _y, int _z, int _width, int _height, int _status)
	{
		return(new GraphicalElement(_x, _y, _z, _width, _height, _status));
	}

	public void keyPressed(KeyEvent ke)
	{
		
		switch(ke.getKeyCode())
		{
			case KeyEvent.VK_C:
				if(modifiers[Constants.CTRL_KEY])
					System.exit(0);
				break;
			case KeyEvent.VK_SHIFT:
				modifiers[Constants.SHIFT_KEY] = true;
				break;
			case KeyEvent.VK_ALT:
				modifiers[Constants.ALT_KEY] = true;
				break;
			case KeyEvent.VK_CONTROL:
				modifiers[Constants.CTRL_KEY] = true;
				break;	
			case KeyEvent.VK_UP:
				myClient.moveSelf(Constants.MOVE_UP);
				break;
			case KeyEvent.VK_DOWN:
				myClient.moveSelf(Constants.MOVE_DOWN);
				break;
			case KeyEvent.VK_LEFT:
				myClient.moveSelf(Constants.MOVE_LEFT);
				break;
			case KeyEvent.VK_RIGHT:
				myClient.moveSelf(Constants.MOVE_RIGHT);
				break;
			default:
				myClient.logger.message("You typed: " + ke.getKeyChar() + " (" + ke.getKeyCode() + ")" + "\n", false);

		}
	}

	public void keyReleased(KeyEvent ke)
	{
		switch(ke.getKeyCode())
		{
			case KeyEvent.VK_SHIFT:
				modifiers[Constants.SHIFT_KEY] = true;
				break;
			case KeyEvent.VK_ALT:
				modifiers[Constants.ALT_KEY] = true;
				break;
			case KeyEvent.VK_CONTROL:
				modifiers[Constants.CTRL_KEY] = true;
				break;
			default:
		}
	}

	public void keyTyped(KeyEvent ke){}
	
	public void mouseClicked(MouseEvent me)
	{
		myClient.logger.message("Mouse Clicked!\n", false);
	}

	public void mouseEntered(MouseEvent me){}

	public void mouseExited(MouseEvent me){}

	public void mousePressed(MouseEvent me){}

	public void mouseReleased(MouseEvent me){}


	public static void main(String args[])
	{
		boolean verbose = false;
		int port = Constants.DEF_PORT;
		String server = Constants.DEF_SERVER;
		int i;
		for(i = 0; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-v"))
			{
				verbose = true;
			}
			else if(args[i].equalsIgnoreCase("-s"))
			{
				server = args[++i];
			}
			else if(args[i].equalsIgnoreCase("-p"))
			{
				try
				{
					port = Integer.parseInt(args[++i]);
				}
				catch(NumberFormatException nfe)
				{
					System.err.println("Bad input on port option (-p). Port must be a positive integer.");
					port = Constants.DEF_PORT;
				}
				catch(ArrayIndexOutOfBoundsException aioobe)
				{
					System.err.println("No port given with -p command");
					port = Constants.DEF_PORT;
				}
			}
			else if(args[i].equalsIgnoreCase("-h"))
			{
				System.out.println("Syntax: java Client [options]");
				System.out.println("Options:");
				System.out.println(" -h\t\tPrint this help screen");
				System.out.println(" -v\t\tRun in verbose mode");
				System.out.println(" -s <domain>\tRun on server at domain <domain>");
				System.out.println(" -p <port>\tRun on port # <port>");
				System.exit(0);
			}
		}

		if(server.equals(""))
		{
			System.out.println("Missing: server.\nPlease see: java Client -h");
			System.exit(0);
		}

		Canvas2D canvas = new Canvas2D();
		Client myClient = new Client(canvas, canvas.elements, server, port, verbose); 
		canvas.setClient(myClient);
		
		JFrame frame = new JFrame();
		frame.getContentPane().add( canvas );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //for now. Need to send a nice close message	
		frame.pack();
		frame.setVisible( true ) ;

	}


}
