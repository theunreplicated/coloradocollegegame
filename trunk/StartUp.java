import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


/**
 * This class is designed to start either a Server or a Client with 
 * set variables passed in a GUI enviornment versus the command-line.
 *  
 * As of right now v1.5 there is no way to determine what's going on
 * with the console, or report it in some way by using StartUp 
 * 
 * @author Guillermo Mendez-Kestler
 * @version 1.5
 */
public class StartUp extends JPanel 
implements ActionListener, ItemListener
{
	// Global Variables
	private static final long serialVersionUID = -4262492391092098134L;
	// TAB NAMES
	final static String CLIENTPANEL = "CLIENT";
	final static String SERVERPANEL = "SERVER";
	final static String CLIENTHELP = "CLIENT HELP";
	final static String SERVERHELP = "SERVER HELP";
	final static String CONSOLE = "CONSOLE";
	// CLIENT HELP
	final static String CLIENTHELP0 = "Client Options:";
	final static String CLIENTHELP1 = "Print this help screen";
	final static String CLIENTHELP2 = "Run in verbose mode";
	final static String CLIENTHELP3 = "Run on server at domain <domain>";
	final static String CLIENTHELP4 = "Run on port # <port>";
	// SERVER HELP
	final static String SERVERHELP0 = "Server Options:";
	final static String SERVERHELP1 = "Print this help screen";
	final static String SERVERHELP2 = "Run in verbose mode";
	final static String SERVERHELP3 = "Run on port # <port>";
	final static String SERVERHELP4 = "Look for data files in directory <dir>";
	final static String SERVERHELP5 = 
		"Look in data dir for Element List files that have extension <ext>";
	final static String SERVERHELP6 = 
		"Look in data dir for World files that have extension <ext>";
	final static String SERVERHELP7 = 
		"Only look for elements in the files specified";
	final static String SERVERHELP8 = 
		"Build the world only out of the files specified";
	// BUTTONS, CHECK BOXES, and TEXT FILEDS
	JButton serverBtn, clientBtn;
	JCheckBox verbChkBoxC, portChkBoxC, serverChkBoxC, 
	verbChkBoxS, portChkBoxS, dirChkBoxS, eextChkBoxS, 
	wextChkBoxS, efilesChkBoxS, wfilesChkBoxS;
	JTextField inPortC, inServerC, inPortS, dirInS, eextInS, 
	wextInS, efilesInS, wfilesInS;

	// Variables needed
	boolean verboseC=false, portC=false, serverC=false, 
	verboseS=false, portS=false, dirS=false, eextS=false,
	wextS=false, efilesS=false, wfilesS=false;
	int portCNum, portSNum;
	String serverIP, dir, eext, wext, efiles, wfiles;
	String[] varsClient = {"-h","-v","-s","-p"};
	String[] varsServer = {"-h","-v","-p","-dir","-eext",
			"-wext","-efiles","-wfiles"};
	public String[] arguments = new String[17]; 

	public StartUp()
	{
		// GUI Variables
		JLabel verbLabelC, portLabelC, serverLabelC, verbLabelS, 
		portLabelS, dirLabelS, eextLabelS, wextLabelS, 
		efilesLabelS, wfilesLabelS, cNotes0, cNotes1, cNotes2, 
		cNotes3, cNotes4, cNotes01, cNotes02, cNotes03, cNotes04, 
		sNotes0, sNotes1, sNotes2, sNotes3, sNotes4, sNotes5, 
		sNotes6, sNotes7, sNotes8, sNotes01, sNotes02, sNotes03, 
		sNotes04, sNotes05, sNotes06, sNotes07, sNotes08;
		JTabbedPane tabbedPane = new JTabbedPane();
		GridBagConstraints c = new GridBagConstraints();
		// Set the default location of each item to the West
		c.anchor=GridBagConstraints.WEST;

		setPreferredSize(new Dimension(555,210));
		setBackground(Color.white);
		setFocusable(true); //for keyboard focus

		/**
		 * Creating the Panels
		 */
		// Main Panel to hold everything
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new BorderLayout());

		// Help Content Panels
		Panel clientHelp = new Panel();
		clientHelp.setLayout(new GridBagLayout());

		Panel serverHelp = new Panel();
		serverHelp.setLayout(new GridBagLayout());

		// Client Panels
		Panel clientPanelM = new Panel();
		clientPanelM.setLayout(new BorderLayout());

		Panel clientPanelR = new Panel();
		clientPanelR.setLayout(new GridBagLayout());

		Panel clientPanelS = new Panel();
		clientPanelS.setLayout(new GridBagLayout());

		// Server Panels
		Panel serverPanelM = new Panel();
		serverPanelM.setLayout(new BorderLayout());

		Panel serverPanelR = new Panel();
		serverPanelR.setLayout(new GridBagLayout());

		Panel serverPanelS = new Panel();
		serverPanelS.setLayout(new GridBagLayout());

		/**
		 * CLIENT PANEL
		 */
		// Client Server
		serverChkBoxC = new JCheckBox("");
		c.gridx = 0;
		c.gridy = 0;
		serverChkBoxC.setSelected(false);
		clientPanelR.add(serverChkBoxC, c);
		serverChkBoxC.addItemListener(this);

		serverLabelC = new JLabel("Connect to Server IP:  ");
		c.gridx = 1;
		c.gridy = 0;
		clientPanelR.add(serverLabelC, c);

		inServerC = new JTextField("", 15);
		c.gridx = 2;
		c.gridy = 0;
		clientPanelR.add(inServerC, c);

		// Client Port
		portChkBoxC= new JCheckBox("");
		c.gridx = 0;
		c.gridy = 1;
		portChkBoxC.setSelected(false);
		clientPanelR.add(portChkBoxC, c);
		portChkBoxC.addItemListener(this);

		portLabelC = new JLabel("Server Port #:");
		c.gridx = 1;
		c.gridy = 1;
		clientPanelR.add(portLabelC, c);

		inPortC = new JTextField("", 5);
		c.gridx = 2;
		c.gridy = 1;
		c.anchor=GridBagConstraints.WEST;
		clientPanelR.add(inPortC, c);

		// Client Verbose Mode
		verbChkBoxC = new JCheckBox("");
		c.gridx = 0;
		c.gridy = 2;
		verbChkBoxC.setSelected(false);
		clientPanelR.add(verbChkBoxC, c);
		verbChkBoxC.addItemListener(this);

		verbLabelC = new JLabel("Client Verbose Mode");
		c.gridx = 1;
		c.gridy = 2;
		clientPanelR.add(verbLabelC, c);

		/**
		 * SERVER PANEL
		 */
		// Server Port
		portChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=0;
		portChkBoxS.setSelected(false);
		serverPanelR.add(portChkBoxS, c);
		portChkBoxS.addItemListener(this);

		portLabelS = new JLabel("Set Server to Port #:");
		c.gridx=1;
		c.gridy=0;
		serverPanelR.add(portLabelS, c);

		inPortS = new JTextField("", 5);
		c.gridx=2;
		c.gridy=0;
		serverPanelR.add(inPortS, c);

		// Server Directory
		dirChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=1;
		dirChkBoxS.setSelected(false);
		serverPanelR.add(dirChkBoxS, c);
		dirChkBoxS.addItemListener(this);

		dirLabelS = new JLabel("Data Files in Directory:");
		c.gridx=1;
		c.gridy=1;
		serverPanelR.add(dirLabelS, c);

		dirInS = new JTextField("", 30);
		c.gridx=2;
		c.gridy=1;
		serverPanelR.add(dirInS, c);

		// Server Element List Files.<EXT>
		eextChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=2;
		eextChkBoxS.setSelected(false);
		serverPanelR.add(eextChkBoxS, c);
		eextChkBoxS.addItemListener(this);

		eextLabelS = new JLabel("Element List Files with Extension: ");
		c.gridx=1;
		c.gridy=2;
		serverPanelR.add(eextLabelS, c);

		eextInS = new JTextField("", 4);
		c.gridx=2;
		c.gridy=2;
		serverPanelR.add(eextInS, c);

		// Server World Files.<EXT>
		wextChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=3;
		wextChkBoxS.setSelected(false);
		serverPanelR.add(wextChkBoxS, c);
		wextChkBoxS.addItemListener(this);

		wextLabelS = new JLabel("World Files with Extension:");
		c.gridx=1;
		c.gridy=3;
		serverPanelR.add(wextLabelS, c);

		wextInS = new JTextField("", 4);
		c.gridx=2;
		c.gridy=3;
		serverPanelR.add(wextInS, c);

		// Server Element Files
		efilesChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=4;
		efilesChkBoxS.setSelected(false);
		serverPanelR.add(efilesChkBoxS, c);
		efilesChkBoxS.addItemListener(this);

		efilesLabelS = new JLabel("Elements in File:");
		c.gridx=1;
		c.gridy=4;
		serverPanelR.add(efilesLabelS, c);

		efilesInS = new JTextField("", 30);
		c.gridx=2;
		c.gridy=4;
		serverPanelR.add(efilesInS, c);

		// Server World Files
		wfilesChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=5;
		wfilesChkBoxS.setSelected(false);
		serverPanelR.add(wfilesChkBoxS, c);
		wfilesChkBoxS.addItemListener(this);

		wfilesLabelS = new JLabel("World in File:");
		c.gridx=1;
		c.gridy=5;
		serverPanelR.add(wfilesLabelS, c);

		wfilesInS = new JTextField("", 30);
		c.gridx=2;
		c.gridy=5;
		serverPanelR.add(wfilesInS, c);

		// Server Verbose Mode
		verbChkBoxS = new JCheckBox("");
		c.gridx=0;
		c.gridy=6;
		verbChkBoxS.setSelected(false);
		serverPanelR.add(verbChkBoxS, c);
		verbChkBoxS.addItemListener(this);

		verbLabelS = new JLabel("Server Verbose Mode");
		c.gridx=1;
		c.gridy=6;
		serverPanelR.add(verbLabelS, c);

		/**
		 * BUTTONS
		 * 
		 * Sets the grid to the bottom center for the buttons
		 */
		c.anchor=GridBagConstraints.CENTER;
		c.gridx=0;
		c.gridy=1;

		clientBtn = new JButton("Start Client");
		clientPanelS.add(clientBtn, c);
		clientBtn.addActionListener(this);
		clientBtn.setMaximumSize(new Dimension(100, 100));

		serverBtn = new JButton("Start Server");
		serverPanelS.add(serverBtn, c);
		serverBtn.addActionListener(this);

		/**
		 * HELP PANEL
		 */
		c.anchor = GridBagConstraints.WEST;
		// Client Notes
		cNotes0 = new JLabel(CLIENTHELP0);
		c.gridx = 0;
		c.gridy = 0;
		clientHelp.add(cNotes0, c);
		cNotes01 = new JLabel("-h");
		c.gridx = 0;
		c.gridy = 1;
		clientHelp.add(cNotes01, c);
		cNotes1 = new JLabel(CLIENTHELP1);
		c.gridx = 1;
		c.gridy = 1;
		clientHelp.add(cNotes1, c);
		cNotes02 = new JLabel("-v");
		c.gridx = 0;
		c.gridy = 2;
		clientHelp.add(cNotes02, c);
		cNotes2 = new JLabel(CLIENTHELP2);
		c.gridx = 1;
		c.gridy = 2;
		clientHelp.add(cNotes2, c);
		cNotes03 = new JLabel("-s <domain>    ");
		c.gridx = 0;
		c.gridy = 3;
		clientHelp.add(cNotes03, c);
		cNotes3 = new JLabel(CLIENTHELP3);
		c.gridx = 1;
		c.gridy = 3;
		clientHelp.add(cNotes3, c);
		cNotes04 = new JLabel("-p <port>");
		c.gridx = 0;
		c.gridy = 4;
		clientHelp.add(cNotes04, c);
		cNotes4 = new JLabel(CLIENTHELP4);
		c.gridx = 1;
		c.gridy = 4;
		clientHelp.add(cNotes4, c);
		c.gridx=0;
		c.gridy=0;
		clientPanelS.add(clientHelp, c);

		// Server Notes
		sNotes0 = new JLabel(SERVERHELP0);
		c.gridx = 0;
		c.gridy = 0;
		serverHelp.add(sNotes0, c);
		sNotes01 = new JLabel("-h");
		c.gridx = 0;
		c.gridy = 1;
		serverHelp.add(sNotes01, c);
		sNotes1 = new JLabel(SERVERHELP1);
		c.gridx = 1;
		c.gridy = 1;
		serverHelp.add(sNotes1, c);
		sNotes02 = new JLabel("-v");
		c.gridx = 0;
		c.gridy = 2;
		serverHelp.add(sNotes02, c);
		sNotes2 = new JLabel(SERVERHELP2);
		c.gridx = 1;
		c.gridy = 2;
		serverHelp.add(sNotes2, c);
		sNotes03 = new JLabel("-p <port>");
		c.gridx = 0;
		c.gridy = 3;
		serverHelp.add(sNotes03, c);
		sNotes3 = new JLabel(SERVERHELP3);
		c.gridx = 1;
		c.gridy = 3;
		serverHelp.add(sNotes3, c);
		sNotes04 = new JLabel("-dir <dir>");
		c.gridx = 0;
		c.gridy = 4;
		serverHelp.add(sNotes04, c);
		sNotes4 = new JLabel(SERVERHELP4);
		c.gridx = 1;
		c.gridy = 4;
		serverHelp.add(sNotes4, c);
		sNotes05 = new JLabel("-eext <ext>");
		c.gridx = 0;
		c.gridy = 5;
		serverHelp.add(sNotes05, c);
		sNotes5 = new JLabel(SERVERHELP5);
		c.gridx = 1;
		c.gridy = 5;
		serverHelp.add(sNotes5, c);
		sNotes06 = new JLabel("-wext <ext>");
		c.gridx = 0;
		c.gridy = 6;
		serverHelp.add(sNotes06, c);
		sNotes6 = new JLabel(SERVERHELP6);
		c.gridx = 1;
		c.gridy = 6;
		serverHelp.add(sNotes6, c);
		sNotes07 = new JLabel("-efiles <file> [file [file [...]]]   ");
		c.gridx = 0;
		c.gridy = 7;
		serverHelp.add(sNotes07, c);
		sNotes7 = new JLabel(SERVERHELP7);
		c.gridx = 1;
		c.gridy = 7;
		serverHelp.add(sNotes7, c);
		sNotes08 = new JLabel("-wfiles <file> [file [file [...]]]  ");
		c.gridx = 0;
		c.gridy = 8;
		serverHelp.add(sNotes08, c);
		sNotes8 = new JLabel(SERVERHELP8);
		c.gridx = 1;
		c.gridy = 8;
		serverHelp.add(sNotes8, c);
		c.gridx = 0;
		c.gridy = 1;
		serverPanelS.add(serverHelp, c);

		/**
		 * ADDING THE PANELS TO THE FRAME
		 */
		clientPanelM.add(clientPanelR, BorderLayout.CENTER);
		clientPanelM.add(clientPanelS, BorderLayout.SOUTH);

		serverPanelM.add(serverPanelR, BorderLayout.CENTER);
		serverPanelM.add(serverPanelS, BorderLayout.SOUTH);

		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		// ORDER MATTERS HERE
		tabbedPane.addTab(CLIENTPANEL, clientPanelM);
		tabbedPane.addTab(SERVERPANEL, serverPanelM);
		tabbedPane.addTab(CLIENTHELP, clientHelp);
		tabbedPane.addTab(SERVERHELP, serverHelp);

		add(mainPanel);

	} // StartUp Contstructor

	public void actionPerformed(ActionEvent e) 
	{
		Runtime runtime = Runtime.getRuntime();
		arguments[0]="java";
		arguments[1]="-Xmx512m";

		/*
		 * CLIENT BUTTON!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */

		if (e.getSource()==clientBtn)
		{
			String inPortValC = String.valueOf(inPortC.getText());
			String inServerValC = String.valueOf(inServerC.getText());

			arguments[2]="-splash:images/DukeTakesOff.gif";
			arguments[3]="Representation3D";

			if (verboseC==true)
				arguments[4]="-v";
			else if (verboseC==false)
				arguments[4]="";

			if (portC==true)
			{
				arguments[5]="-p";
				arguments[6]=inPortValC;
			}
			else if (portC==false)
			{
				arguments[5]="";
				arguments[6]="";
			}

			if (serverC==true)
			{
				arguments[7]="-s";
				arguments[8]=inServerValC;
			}
			else if (serverC==false)
			{
				arguments[7]="-s";
				arguments[8]="localhost";
			}

			// empty arguments to prevent crashes with exec
			for (int temp=9;temp<arguments.length;temp++)
			{
				arguments[temp]="";
			}
			
			System.out.println("Starting CLIENT with the following commands: ");
			for (int tmp=0;tmp<9;tmp++)
			{
				System.out.println(tmp+": "+arguments[tmp]);
			}

			// will get to the console later
			//(new Thread(new Console())).start();
			
			// TRYING TO START THE CLIENT
			try {
				runtime.exec(arguments);
				runtime.gc();
			}catch (IOException ioe) {
				System.out.println("Cant start CLIENT. \n" +
						"Error: "+ioe.getMessage());
			}
		}

		/*
		 * SERVER BUTTON!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */

		else if (e.getSource()==serverBtn)
		{
			String inPortValS = String.valueOf(inPortS.getText());
			String inDirValS = String.valueOf(dirInS.getText());
			String inEextValS = String.valueOf(eextInS.getText());
			String inWextValS = String.valueOf(wextInS.getText());
			String inEfilesValS = String.valueOf(efilesInS.getText());
			String inWfilesValS = String.valueOf(wfilesInS.getText());
			
			arguments[2]="-splash:images/server.gif";
			arguments[3]="Server";

			int i=4, a=i, b=i;

			if (verboseS==true)
				arguments[a]="-v";
			else if (verboseS==false)
				arguments[a]="";

			a=i+1;
			b=i+2;
			if (portS==true)
			{
				arguments[a]="-p";
				arguments[b]=inPortValS;
				System.out.println(inPortValS);
			}
			else if (portS==false)
			{
				arguments[a]="";
				arguments[b]="";
				System.out.println(arguments[i+1]);
				System.out.println(arguments[i+2]);
			}

			a=i+3;
			b=i+4;
			if (dirS==true)
			{
				arguments[a]="-dir";
				arguments[b]=inDirValS;
				System.out.println(inDirValS);
			}
			else if (dirS==false)
			{
				arguments[a]="";
				arguments[b]="";
			}

			a=i+5;
			b=i+6;
			if (eextS==true)
			{
				arguments[a]="-eext";
				arguments[b]=inEextValS;
			}
			else if (eextS==false)
			{
				arguments[a]="";
				arguments[b]="";
			}

			a=i+7;
			b=i+8;
			if (wextS==true)
			{
				arguments[a]="-wext";
				arguments[b]=inWextValS;
			}
			else if (wextS==false)
			{
				arguments[a]="";
				arguments[b]="";
			}

			a=i+9;
			b=i+10;
			if (efilesS==true)
			{
				arguments[a]="-efiles";
				arguments[b]=inEfilesValS;
			}
			else if (efilesS==false)
			{
				arguments[a]="";
				arguments[b]="";
			}

			a=i+11;
			b=i+12;
			if (wfilesS==true)
			{
				arguments[a]="-wfiles";
				arguments[b]=inWfilesValS;
			}
			else if (wfilesS==false)
			{
				arguments[a]="";
				arguments[b]="";
			}

			System.out.println("Starting SERVER with the following commands: ");
			for (int tmp=0;tmp<arguments.length;tmp++)
			{
				System.out.println(tmp+": "+arguments[tmp]);
			}
			
			// TRYING TO START THE SERVER
			try {
				runtime.exec(arguments);
				runtime.gc();
			} catch (IOException ioe) {
				System.out.println("Cant start SERVER. \n" +
						"Error: "+ioe.getMessage());
			}
		}
		else 
			System.out.println("NOT A VALID BUTTON COMMAND");
	} // actionPerformed

	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getItemSelectable();
		if (e.getStateChange() == ItemEvent.SELECTED)
		{
			// Client Options
			if (source==verbChkBoxC)
				verboseC = true;
			else if (source==portChkBoxC)
				portC = true;
			else if (source==serverChkBoxC)
				serverC = true;
			// Server Options
			else if (source==verbChkBoxS)
				verboseS = true;
			else if (source==portChkBoxS)
				portS = true;
			else if (source==dirChkBoxS)
				dirS = true;
			else if (source==eextChkBoxS)
				eextS = true;
			else if (source==wextChkBoxS)
				wextS = true;
			else if (source==efilesChkBoxS)
				efilesS = true;
			else if (source==wfilesChkBoxS)
				wfilesS = true;
		}

		if (e.getStateChange() == ItemEvent.DESELECTED)
		{
			// Client Options
			if (source==verbChkBoxC)
				verboseC = false;
			else if (source==portChkBoxC)
				portC = false;
			else if (source==serverChkBoxC)
				serverC = false;
			// Server Options
			else if (source==verbChkBoxS)
				verboseS = false;
			else if (source==portChkBoxS)
				portS = false;
			else if (source==dirChkBoxS)
				dirS = false;
			else if (source==eextChkBoxS)
				eextS = false;
			else if (source==wextChkBoxS)
				wextS = false;
			else if (source==efilesChkBoxS)
				efilesS = false;
			else if (source==wfilesChkBoxS)
				wfilesS = false;
		}
	} // item state changed

	public static void main(String[] args) 
	{
		StartUp theCanvas = new StartUp();

		JFrame window = new JFrame();
		window.setVisible(true);
		window.setTitle("CC Game - Start Up");
		window.setSize(1,1);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel canvasFrame = new JPanel();
		canvasFrame.add(theCanvas);
		panel.add(canvasFrame, BorderLayout.CENTER);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);
		window.pack();
		window.setResizable(false);
	}//main
}//class