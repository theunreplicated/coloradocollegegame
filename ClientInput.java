import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientInput implements MouseListener, KeyListener
{
	private ClientIO myIO;
	private Logger myLogger;
	private boolean[] modifiers = { false, false, false }; //indicate whether or not the shift,
							       //ctrl and alt modifier keys are currently

	public ClientInput(ClientIO _cio,Logger _myLogger)
	{
		myIO = _cio;
		myLogger = _myLogger;
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
				myIO.moveSelf(Constants.VEC_POSZ);
				//myIO.moveSelf(new float[] {0.0f, 0.0f, 1.0f});
				break;
			case KeyEvent.VK_PAGE_DOWN:
				myIO.moveSelf(Constants.VEC_NEGZ);
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

			/*attribute change testing--demonstration only*/
			case KeyEvent.VK_R:
				myIO.changeAttribute("color", Color.RED.getRGB());
				break;
			case KeyEvent.VK_G:
				myIO.changeAttribute("color", Color.GREEN.getRGB());
				break;
			case KeyEvent.VK_B:
				myIO.changeAttribute("color", Color.BLUE.getRGB());
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

	public void mouseEntered(MouseEvent me){}

	public void mouseExited(MouseEvent me){}

	public void mousePressed(MouseEvent me){}

	public void mouseReleased(MouseEvent me){}
}
