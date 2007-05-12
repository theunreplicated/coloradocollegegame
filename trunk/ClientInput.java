import java.awt.event.*;

public class ClientInput implements KeyListener, MouseListener, MouseMotionListener
{
	private Resolver resolver;
	private ActionFactory actionFactory;
	private GameElement me;
	private Logger myLogger;
	private boolean[] modifiers = { false, false, false }; //indicate whether or not the shift,
							       //ctrl and alt modifier keys are pressed

	private RepresentationResolver repResolver = null;
	
	//for mouse movement
	private int mx = 0; //position of last event
	private int my = 0; 
	private int dx = 0; //position of new event
	private int dy = 0; 

	public ClientInput(Resolver _resolver, RepresentationResolver _repResolver, ActionFactory _actionFactory, Logger _myLogger)
	{
		resolver = _resolver;
		repResolver = _repResolver;
		actionFactory = _actionFactory;
		myLogger = _myLogger;
	}

	public void setMe(GameElement _me)
	{
		me = _me;
	}

	public void keyPressed(KeyEvent ke)
	{
		
		Action a;
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
			
			/*Position movement*/
			case KeyEvent.VK_RIGHT:
				if(modifiers[Constants.ALT_KEY])
				{
					//a = actionFactory.getAction("move camera");
					//a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					//a.parameters().add(Constants.VEC_POSX);
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.QUAT_CLOY);
					repResolver.resolve(a);
				}
				else
				{
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_POSX);
					resolver.addAction(a); 
				}
				break;
			case KeyEvent.VK_LEFT:
				if(modifiers[Constants.ALT_KEY])
				{
					//a = actionFactory.getAction("move camera");
					//a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					//a.parameters().add(Constants.VEC_NEGX);
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.QUAT_CCLY);
					repResolver.resolve(a);
				}
				else
				{

					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_NEGX);
					resolver.addAction(a); 
				}
				break;
			case KeyEvent.VK_UP:
				if(modifiers[Constants.ALT_KEY])
				{
					//a = actionFactory.getAction("move camera");
					//a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					//a.parameters().add(Constants.VEC_POSY);
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.QUAT_CCLX);
					repResolver.resolve(a);
				}
				else
				{
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_POSY);
					resolver.addAction(a); 
				}
				break;
			case KeyEvent.VK_DOWN:
				if(modifiers[Constants.ALT_KEY])
				{
					//a = actionFactory.getAction("move camera");
					//a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					//a.parameters().add(Constants.VEC_NEGY);
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.QUAT_CCLY);
					repResolver.resolve(a);
				}
				else
				{
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_NEGY);
					resolver.addAction(a); 
				}
				break;
			case KeyEvent.VK_PAGE_UP:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_NEGZ);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_PAGE_DOWN:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_POSZ);
				resolver.addAction(a); 
				break;
			
			/*Rotation movement*/
			case KeyEvent.VK_NUMPAD2: //spin counterclockwise around x
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOX);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_NUMPAD4: //spin counterclockwise around y
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLY);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_NUMPAD5: //return to center
				System.out.println("Not yet implemented.");
				//WRITE THIS METHOD!!!
				//Is there anyway for ClientInput to set stuff back to default?
				break;
			case KeyEvent.VK_NUMPAD6: //spin clockwise around y
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOY);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_NUMPAD7: //spin counterclockwise around z
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLZ);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_NUMPAD8: //spin clockwise around x
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLX);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_NUMPAD9: //spin clockwise around z
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOZ);
				resolver.addAction(a); 
				break;

			/*Intuitive (planar) 3D movement using the Resolver - Omer*/
			case KeyEvent.VK_W:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_NEGZ);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_S:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_POSZ);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_A:
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLY);
				resolver.addAction(a); 
				break;
			case KeyEvent.VK_D:
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOY);
				resolver.addAction(a); 
				break;

			/*Testing element addition: make*/
			case KeyEvent.VK_M:
				a = actionFactory.getAction("add element");
				GameElement b = new GameElement(resolver.world.getElementById(110));
				b.nudge(Constants.VEC_POSZ);
				b.id(3000);
				a.parameters().add(true);
				a.parameters().add(b);
				resolver.addAction(a);
				break;

			/*Testing delayed actions with: jump*/
			case KeyEvent.VK_J:
				a = actionFactory.getAction("jump");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(0);
				resolver.addAction(a);
				break;


			/*Camera stuff*/
			case KeyEvent.VK_V:
				a = actionFactory.getAction("change view");
				repResolver.resolve(a);
				break;

			default:
				myLogger.message("You typed: " + ke.getKeyChar() + " (" + ke.getKeyCode() + ")" + "\n", false);

		}
	}

	public void keyReleased(KeyEvent ke)
	{
		switch(ke.getKeyCode())
		{
			case KeyEvent.VK_SHIFT:
				modifiers[Constants.SHIFT_KEY] = false;
				break;
			case KeyEvent.VK_ALT:
				modifiers[Constants.ALT_KEY] = false;
				break;
			case KeyEvent.VK_CONTROL:
				modifiers[Constants.CTRL_KEY] = false;
				break;
			default:
		}
	}

	public void keyTyped(KeyEvent ke){}
	
	public void mouseClicked(MouseEvent e)
	{
		myLogger.message("Mouse Clicked!\n", false);
	}

	//While mouseMotion would be awesome, I think it'll effectively spam the server.
	//Maybe if we get it down to Representation-only (camera adjustments and stuff).
	//Or if we had some kind of stop-timer on it so it doesn't go off as often--but even that could be nasty
	public void mousePressed(MouseEvent e)
	{
		mx = e.getX();
		my = e.getY();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		//do we need to reset these?
		mx = 0;
		my = 0;
		dx = 0;
		dy = 0;
	}
	
	public void mouseDragged(MouseEvent e)
	{
		//System.out.println("mouseDragged!");
		dx = e.getX();
		dy = e.getY();
		
		//for calculating the quaternion
		double ex = Math.toRadians((dy-my)/2)/2.0; //convert degrees to rotate to radians
		double ey = Math.toRadians((dx-mx)/2)/2.0; 
		double sx = Math.sin(ex);
		double sy = Math.sin(ey);
		double cx = Math.cos(ex);
		double cy = Math.cos(ey);
		float[] q = new float[] {(float)(sx*cy), (float)(cx*sy), (float)(-1*sx*sy), (float)(cx*cy)};
		
		Action a = actionFactory.getAction("rotate camera");
		a.parameters().add(q);
		//a.parameters().add( new float[] {(float)(sx*cy), (float)(cx*sy), (float)(-1*sx*sy), (float)(cx*cy)} );
		repResolver.resolve(a);

		//prepare for the next event
		mx = dx;
		my = dy;
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e) {}
}
