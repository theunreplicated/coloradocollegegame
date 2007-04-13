import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientInput implements KeyListener, MouseListener, MouseMotionListener
{
	private ClientIO myIO;
	private Resolver resolver;
	private ActionFactory actionFactory;
	private int id;
	private Logger myLogger;
	private boolean[] modifiers = { false, false, false }; //indicate whether or not the shift,
							       //ctrl and alt modifier keys are pressed

	private Representation rep = null;
	//for mouse movement - do what exactly?
	private int mx = 0; //"old" position
	private int my = 0;
	private int dx = 0; //change in position
	private int dy = 0;

	public ClientInput(Resolver _resolver, ActionFactory _actionFactory, Logger _myLogger)
	{
		resolver = _resolver;
		actionFactory = _actionFactory;
		myLogger = _myLogger;
	}

	public void setId(ClientIO _myIO, int _id)
	{
		myIO = _myIO;
		id = _id;
	}

	public void setRepresentation(Representation _rep)
	{
		rep = _rep;
		myIO.setRepresentation(rep);
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
			
			/*Position movement*/
			case KeyEvent.VK_RIGHT:
				/*resolver.parse(new Object[]{
					new int[]{actionFactory.getType("move"),id},
					Constants.VEC_POSX}); */
				myIO.moveSelf(Constants.VEC_POSX);
				//myIO.moveSelf(new float[] {1.0f, 0.0f, 0.0f});
				break;
			case KeyEvent.VK_LEFT:
				myIO.moveSelf(Constants.VEC_NEGX);
				//myIO.moveSelf(new float[] {-1.0f, 0.0f, 0.0f});
				break;
			case KeyEvent.VK_UP:
				myIO.moveSelf(Constants.VEC_POSY);
				//myIO.moveSelf(new float[] {0.0f, 1.0f, 0.0f});
				break;
			case KeyEvent.VK_DOWN:
				myIO.moveSelf(Constants.VEC_NEGY);
				//myIO.moveSelf(new float[] {0.0f, -1.0f, 0.0f});
				break;
			case KeyEvent.VK_PAGE_UP:
				myIO.moveSelf(Constants.VEC_NEGZ);
				//myIO.moveSelf(new float[] {0.0f, 0.0f, 1.0f});
				break;
			case KeyEvent.VK_PAGE_DOWN:
				myIO.moveSelf(Constants.VEC_POSZ);
				//myIO.moveSelf(new float[] {0.0f, 0.0f, -1.0f});
				break;
			
			/*Rotation movement*/
			case KeyEvent.VK_NUMPAD2: //spin counterclockwise around x
				myIO.rotateSelf(Constants.QUAT_CLOX);
				break;
			case KeyEvent.VK_NUMPAD4: //spin counterclockwise around y
				myIO.rotateSelf(Constants.QUAT_CCLY);
				break;
			case KeyEvent.VK_NUMPAD5: //return to center
				System.out.println("Not yet implemented.");
				//WRITE THIS METHOD!!!
				//Is there anyway for ClientInput to set stuff back to default?
				break;
			case KeyEvent.VK_NUMPAD6: //spin clockwise around y
				myIO.rotateSelf(Constants.QUAT_CLOY);
				break;
			case KeyEvent.VK_NUMPAD7: //spin counterclockwise around z
				myIO.rotateSelf(Constants.QUAT_CCLZ);
				break;
			case KeyEvent.VK_NUMPAD8: //spin clockwise around x
				myIO.rotateSelf(Constants.QUAT_CCLX);
				break;
			case KeyEvent.VK_NUMPAD9: //spin clockwise around z
				myIO.rotateSelf(Constants.QUAT_CLOZ);
				break;

			/*Intuitive 3D movement (I hope), though it is planar - Joel*/
			case KeyEvent.VK_W:
				myIO.moveSelf(Constants.VEC_NEGZ);
				break;
			case KeyEvent.VK_S:
				myIO.moveSelf(Constants.VEC_POSZ);
				break;
			case KeyEvent.VK_A:
				myIO.rotateSelf(Constants.QUAT_CCLY);	
				break;
			case KeyEvent.VK_D:
				myIO.rotateSelf(Constants.QUAT_CLOY);
				break;

			/*Camera stuff*/
			case KeyEvent.VK_V:
				rep.changeView();
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
		myLogger.message("Mouse Clicked!\n", false);
	}

	//While mouseMotion would be awesome, I think it'll effectively spam the server.
	//Maybe if we get it down to Representation-only (camera adjustments and stuff).
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}
	public void mouseDragged(MouseEvent me){}

	public void mouseEntered(MouseEvent me){}
	public void mouseExited(MouseEvent me){}
	public void mouseMoved(MouseEvent me) {}
}
