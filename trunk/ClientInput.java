import javax.swing.*;
import java.awt.event.*;

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
			case KeyEvent.VK_UP:
				myIO.moveSelf(Constants.MOVE_UP);
				break;
			case KeyEvent.VK_DOWN:
				myIO.moveSelf(Constants.MOVE_DOWN);
				break;
			case KeyEvent.VK_LEFT:
				myIO.moveSelf(Constants.MOVE_LEFT);
				break;
			case KeyEvent.VK_RIGHT:
				myIO.moveSelf(Constants.MOVE_RIGHT);
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
