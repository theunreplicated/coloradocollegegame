//import javax.swing.*;//needed
import java.awt.event.*;
//import java.awt.*;//needed

public class ClientInput implements KeyListener, MouseListener, MouseMotionListener
{
	private Resolver resolver;
	private ActionFactory actionFactory;
	private GameElement me;
	private Logger myLogger;
	private boolean[] modifiers = { false, false, false }; //indicate whether or not the shift,
							       //ctrl and alt modifier keys are pressed

	private RepresentationResolver repResolver = null;
	
	//for mouse movement - do what exactly? //needed?
	/*
	private int mx = 0; //"old" position
	private int my = 0;
	private int dx = 0; //change in position
	private int dy = 0;
	//*/

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
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.VEC_POSX);
					repResolver.resolve(a);
					//rep.adjustCamera(Quaternions.rotatePoint(Constants.VEC_POSX,rep.getCameraFacing()), null);
				}
				else
				{
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_POSX);
					resolver.parse(a); 
				}
				break;
			case KeyEvent.VK_LEFT:
				if(modifiers[Constants.ALT_KEY])
				{
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.VEC_NEGX);
					repResolver.resolve(a);
					//rep.adjustCamera(Quaternions.rotatePoint(Constants.VEC_NEGX,rep.getCameraFacing()), null);
				}
				else
				{

					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_NEGX);
					resolver.parse(a); 
				}
				break;
			case KeyEvent.VK_UP:
				if(modifiers[Constants.ALT_KEY])
				{
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.VEC_POSY);
					repResolver.resolve(a);
					//rep.adjustCamera(Quaternions.rotatePoint(Constants.VEC_POSY,rep.getCameraFacing()), null);
				}
				else
				{
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_POSY);
					resolver.parse(a); 
				}
				break;
			case KeyEvent.VK_DOWN:
				if(modifiers[Constants.ALT_KEY])
				{
					a = actionFactory.getAction("rotate camera");
					a.parameters().add(Constants.VEC_NEGY);
					repResolver.resolve(a);
					//rep.adjustCamera(Quaternions.rotatePoint(Constants.VEC_NEGY,rep.getCameraFacing()), null);
				}
				else
				{
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
					a.parameters().add(Constants.VEC_NEGY);
					resolver.parse(a); 
				}
				break;
			case KeyEvent.VK_PAGE_UP:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_NEGZ);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_PAGE_DOWN:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_POSZ);
				resolver.parse(a); 
				break;
			
			/*Rotation movement*/
			case KeyEvent.VK_NUMPAD2: //spin counterclockwise around x
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOX);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_NUMPAD4: //spin counterclockwise around y
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLY);
				resolver.parse(a); 
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
				resolver.parse(a); 
				break;
			case KeyEvent.VK_NUMPAD7: //spin counterclockwise around z
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLZ);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_NUMPAD8: //spin clockwise around x
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLX);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_NUMPAD9: //spin clockwise around z
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOZ);
				resolver.parse(a); 
				break;

			/*Intuitive (planar) 3D movement using the Resolver - Omer*/
			case KeyEvent.VK_W:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_NEGZ);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_S:
				a = actionFactory.getAction("move");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.MOVE_RELATIVE_TO_FACING);
				a.parameters().add(Constants.VEC_POSZ);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_A:
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CCLY);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_D:
				a = actionFactory.getAction("rotate");
				a.setNouns(new GameElement[]{me});
				a.parameters().add(Constants.QUAT_CLOY);
				resolver.parse(a); 
				break;
			case KeyEvent.VK_M:
				a = actionFactory.getAction("add element");
				GameElement b = new GameElement(resolver.world.getElementById(110));
				b.nudge(Constants.VEC_POSZ);
				b.id(3000);
				a.parameters().add(true);
				a.parameters().add(b);
				resolver.parse(a);
				break;

			/*Camera stuff*/
			case KeyEvent.VK_V:
				a = actionFactory.getAction("change view");
				repResolver.resolve(a);
				//rep.changeView();
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
	
	public void mouseClicked(MouseEvent me)
	{
		myLogger.message("Mouse Clicked!\n", false);
	}

	//While mouseMotion would be awesome, I think it'll effectively spam the server.
	//Maybe if we get it down to Representation-only (camera adjustments and stuff).
	//Or if we had some kind of stop-timer on it so it doesn't go off as often--but even that could be nasty
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}
	public void mouseDragged(MouseEvent me){}

	public void mouseEntered(MouseEvent me){}
	public void mouseExited(MouseEvent me){}
	public void mouseMoved(MouseEvent me) {}
}
