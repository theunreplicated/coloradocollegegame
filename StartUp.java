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
	final static String CLIENTOPTS = "Client Options:";
	final static String DOMAIN = "Run on server at domain <domain>";
	// SERVER HELP
	final static String SERVEROPTS = "Server Options:";
	final static String WEXT = 
		"Look in data dir for World files that have extension <ext>";
	final static String WFILES = 
		"Build the world only out of the files specified";
	final static String TEXT = "Run in text mode";
	// CLIENT/SERVER SHARED 
	final static String HELP = "Print this help screen";
	final static String VERBOSE = "Run in verbose mode";
	final static String PORT = "Run on port # <port>";
	final static String DIR = "Look for data files in directory <dir>";
	final static String EEXT = 
		"Look in data dir for Element List files that have extension <ext>";
	final static String EFILES = 
		"Only look for elements in the files specified";
	
	
	// BUTTONS, CHECK BOXES, and TEXT FILEDS
	JButton serverBtn, clientBtn;
	JCheckBox verbChkBoxC, serverChkBoxC, portChkBoxC, 
		dirChkBoxC, eextChkBoxC, efilesChkBoxC,  
		verbChkBoxS, textChkBoxS, portChkBoxS, dirChkBoxS, 
		eextChkBoxS,wextChkBoxS, efilesChkBoxS, wfilesChkBoxS;
	JTextField inServerC, inPortC, dirInC, eextInC, efilesInC, 
		inPortS, dirInS, eextInS, wextInS, efilesInS, wfilesInS;

	// Variables needed
	boolean verboseC=false, serverC=false, portC=false, dirC=false, 
		eextC=false, efilesC=false, verboseS=false, textS=false, 
		portS=false, dirS=false, eextS=false, wextS=false, 
		efilesS=false, wfilesS=false;
	int portCNum, portSNum;
	String serverIP, dir, eext, wext, efiles, wfiles;
	String[] varsClient = {"-h","-v","-s","-p","-dir",
			"-eext","-efiles"};
	String[] varsServer = {"-h","-v","-t","-p","-dir",
			"-eext","-wext","-efiles","-wfiles"};
	public String[] arguments = new String[18]; 

	public StartUp()
	{
		// GUI Variables
		JLabel verbLabelC, serverLabelC, portLabelC, dirLabelC, 
			eextLabelC, efilesLabelC, verbLabelS, textLabelS, 
			portLabelS, dirLabelS, eextLabelS, wextLabelS, 
			efilesLabelS, wfilesLabelS, cNotes0, cNotes1, 
			cNotes2, cNotes3, cNotes4, cNotes5, cNotes6, 
			cNotes7, cNotes01, cNotes02, cNotes03, cNotes04, 
			cNotes05, cNotes06, cNotes07, sNotes0, sNotes1, 
			sNotes2, sNotes3, sNotes4, sNotes5, sNotes6, 
			sNotes7, sNotes8, sNotes9, sNotes01, sNotes02, 
			sNotes03, sNotes04, sNotes05, sNotes06, sNotes07, 
			sNotes08, sNotes09;
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

		// Client/Server Panels
		Panel clientPanel = new Panel();
		clientPanel.setLayout(new GridBagLayout());

		Panel serverPanel = new Panel();
		serverPanel.setLayout(new GridBagLayout());
		/**
		 * CLIENT PANEL
		 */
		// Client Server
		serverChkBoxC = new JCheckBox("");
		c.gridx = 0;
		c.gridy = 0;
		serverChkBoxC.setSelected(false);
		clientPanel.add(serverChkBoxC, c);
		serverChkBoxC.addItemListener(this);

		serverLabelC = new JLabel("Connect to Server IP:  ");
		c.gridx = 1;
		c.gridy = 0;
		clientPanel.add(serverLabelC, c);

		inServerC = new JTextField("", 15);
		c.gridx = 2;
		c.gridy = 0;
		clientPanel.add(inServerC, c);

		// Client Port
		portChkBoxC= new JCheckBox("");
		c.gridx = 0;
		c.gridy = 1;
		portChkBoxC.setSelected(false);
		clientPanel.add(portChkBoxC, c);
		portChkBoxC.addItemListener(this);

		portLabelC = new JLabel("Server Port #:");
		c.gridx = 1;
		c.gridy = 1;
		clientPanel.add(portLabelC, c);

		inPortC = new JTextField("", 5);
		c.gridx = 2;
		c.gridy = 1;
		c.anchor=GridBagConstraints.WEST;
		clientPanel.add(inPortC, c);
		
		// Client Directory
		dirChkBoxC= new JCheckBox("");
		c.gridx=0;
		c.gridy=2;
		dirChkBoxC.setSelected(false);
		clientPanel.add(dirChkBoxC, c);
		dirChkBoxC.addItemListener(this);

		dirLabelC = new JLabel("Data Files in Directory:");
		c.gridx=1;
		c.gridy=2;
		clientPanel.add(dirLabelC, c);

		dirInC = new JTextField("", 30);
		c.gridx=2;
		c.gridy=2;
		clientPanel.add(dirInC, c);

		// Client Element List Files.<EXT>
		eextChkBoxC= new JCheckBox("");
		c.gridx=0;
		c.gridy=3;
		eextChkBoxC.setSelected(false);
		clientPanel.add(eextChkBoxC, c);
		eextChkBoxC.addItemListener(this);

		eextLabelC = new JLabel("Element List Files with Extension: ");
		c.gridx=1;
		c.gridy=3;
		clientPanel.add(eextLabelC, c);

		eextInC = new JTextField("", 4);
		c.gridx=2;
		c.gridy=3;
		clientPanel.add(eextInC, c);

		// Client Element Files
		efilesChkBoxC= new JCheckBox("");
		c.gridx=0;
		c.gridy=4;
		efilesChkBoxC.setSelected(false);
		clientPanel.add(efilesChkBoxC, c);
		efilesChkBoxC.addItemListener(this);

		efilesLabelC = new JLabel("Elements in File:");
		c.gridx=1;
		c.gridy=4;
		clientPanel.add(efilesLabelC, c);

		efilesInC = new JTextField("", 30);
		c.gridx=2;
		c.gridy=4;
		clientPanel.add(efilesInC, c);

		// Client Verbose Mode
		verbChkBoxC = new JCheckBox("");
		c.gridx = 0;
		c.gridy = 5;
		verbChkBoxC.setSelected(false);
		clientPanel.add(verbChkBoxC, c);
		verbChkBoxC.addItemListener(this);

		verbLabelC = new JLabel("Client Verbose Mode");
		c.gridx = 1;
		c.gridy = 5;
		clientPanel.add(verbLabelC, c);

		/**
		 * SERVER PANEL
		 */
		// Server Port
		portChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=0;
		portChkBoxS.setSelected(false);
		serverPanel.add(portChkBoxS, c);
		portChkBoxS.addItemListener(this);

		portLabelS = new JLabel("Set Server to Port #:");
		c.gridx=1;
		c.gridy=0;
		serverPanel.add(portLabelS, c);

		inPortS = new JTextField("", 5);
		c.gridx=2;
		c.gridy=0;
		serverPanel.add(inPortS, c);

		// Server Directory
		dirChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=1;
		dirChkBoxS.setSelected(false);
		serverPanel.add(dirChkBoxS, c);
		dirChkBoxS.addItemListener(this);

		dirLabelS = new JLabel("Data Files in Directory:");
		c.gridx=1;
		c.gridy=1;
		serverPanel.add(dirLabelS, c);

		dirInS = new JTextField("", 30);
		c.gridx=2;
		c.gridy=1;
		serverPanel.add(dirInS, c);

		// Server Element List Files.<EXT>
		eextChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=2;
		eextChkBoxS.setSelected(false);
		serverPanel.add(eextChkBoxS, c);
		eextChkBoxS.addItemListener(this);

		eextLabelS = new JLabel("Element List Files with Extension: ");
		c.gridx=1;
		c.gridy=2;
		serverPanel.add(eextLabelS, c);

		eextInS = new JTextField("", 4);
		c.gridx=2;
		c.gridy=2;
		serverPanel.add(eextInS, c);

		// Server World Files.<EXT>
		wextChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=3;
		wextChkBoxS.setSelected(false);
		serverPanel.add(wextChkBoxS, c);
		wextChkBoxS.addItemListener(this);

		wextLabelS = new JLabel("World Files with Extension:");
		c.gridx=1;
		c.gridy=3;
		serverPanel.add(wextLabelS, c);

		wextInS = new JTextField("", 4);
		c.gridx=2;
		c.gridy=3;
		serverPanel.add(wextInS, c);

		// Server Element Files
		efilesChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=4;
		efilesChkBoxS.setSelected(false);
		serverPanel.add(efilesChkBoxS, c);
		efilesChkBoxS.addItemListener(this);

		efilesLabelS = new JLabel("Elements in File:");
		c.gridx=1;
		c.gridy=4;
		serverPanel.add(efilesLabelS, c);

		efilesInS = new JTextField("", 30);
		c.gridx=2;
		c.gridy=4;
		serverPanel.add(efilesInS, c);

		// Server World Files
		wfilesChkBoxS= new JCheckBox("");
		c.gridx=0;
		c.gridy=5;
		wfilesChkBoxS.setSelected(false);
		serverPanel.add(wfilesChkBoxS, c);
		wfilesChkBoxS.addItemListener(this);

		wfilesLabelS = new JLabel("World in File:");
		c.gridx=1;
		c.gridy=5;
		serverPanel.add(wfilesLabelS, c);

		wfilesInS = new JTextField("", 30);
		c.gridx=2;
		c.gridy=5;
		serverPanel.add(wfilesInS, c);

		// Server Verbose Mode
		verbChkBoxS = new JCheckBox("");
		c.gridx=0;
		c.gridy=6;
		verbChkBoxS.setSelected(false);
		serverPanel.add(verbChkBoxS, c);
		verbChkBoxS.addItemListener(this);

		verbLabelS = new JLabel("Server Verbose Mode");
		c.gridx=1;
		c.gridy=6;
		serverPanel.add(verbLabelS, c);

		// Server Text Mode
		textChkBoxS = new JCheckBox("");
		c.gridx=0;
		c.gridy=7;
		textChkBoxS.setSelected(false);
		serverPanel.add(textChkBoxS, c);
		textChkBoxS.addItemListener(this);

		textLabelS = new JLabel("Server Text Mode");
		c.gridx=1;
		c.gridy=7;
		serverPanel.add(textLabelS, c);

		/**
		 * BUTTONS
		 * 
		 * Sets the grid to the bottom center for the buttons
		 */
		c.anchor=GridBagConstraints.CENTER;

		clientBtn = new JButton("Start Client");
		c.gridx=2;
		c.gridy=5;
		clientPanel.add(clientBtn, c);
		clientBtn.addActionListener(this);

		serverBtn = new JButton("Start Server");
		c.gridx=2;
		c.gridy=7;
		serverPanel.add(serverBtn, c);
		serverBtn.addActionListener(this);

		/**
		 * HELP PANEL
		 */
		c.anchor = GridBagConstraints.WEST;
		// Client Notes
		cNotes0 = new JLabel(CLIENTOPTS);
		c.gridx = 0;
		c.gridy = 0;
		clientHelp.add(cNotes0, c);
		cNotes01 = new JLabel("-h");
		c.gridx = 0;
		c.gridy = 1;
		clientHelp.add(cNotes01, c);
		cNotes1 = new JLabel(HELP);
		c.gridx = 1;
		c.gridy = 1;
		clientHelp.add(cNotes1, c);
		cNotes02 = new JLabel("-v");
		c.gridx = 0;
		c.gridy = 2;
		clientHelp.add(cNotes02, c);
		cNotes2 = new JLabel(VERBOSE);
		c.gridx = 1;
		c.gridy = 2;
		clientHelp.add(cNotes2, c);
		cNotes03 = new JLabel("-s <domain>    ");
		c.gridx = 0;
		c.gridy = 3;
		clientHelp.add(cNotes03, c);
		cNotes3 = new JLabel(DOMAIN);
		c.gridx = 1;
		c.gridy = 3;
		clientHelp.add(cNotes3, c);
		cNotes04 = new JLabel("-p <port>");
		c.gridx = 0;
		c.gridy = 4;
		clientHelp.add(cNotes04, c);
		cNotes4 = new JLabel(PORT);
		c.gridx = 1;
		c.gridy = 4;
		clientHelp.add(cNotes4, c);
		cNotes05 = new JLabel("-dir <dir>");
		c.gridx = 0;
		c.gridy = 5;
		clientHelp.add(cNotes05, c);
		cNotes5 = new JLabel(DIR);
		c.gridx = 1;
		c.gridy = 5;
		clientHelp.add(cNotes5, c);
		cNotes06 = new JLabel("-eext <ext>");
		c.gridx = 0;
		c.gridy = 6;
		clientHelp.add(cNotes06, c);
		cNotes6 = new JLabel(EEXT);
		c.gridx = 1;
		c.gridy = 6;
		clientHelp.add(cNotes6, c);
		cNotes07 = new JLabel("-efiles <file> [file [file [...]]]   ");
		c.gridx = 0;
		c.gridy = 7;
		clientHelp.add(cNotes07, c);
		cNotes7 = new JLabel(EFILES);
		c.gridx = 1;
		c.gridy = 7;
		clientHelp.add(cNotes7, c);

		// Server Notes
		sNotes0 = new JLabel(SERVEROPTS);
		c.gridx = 0;
		c.gridy = 0;
		serverHelp.add(sNotes0, c);
		sNotes01 = new JLabel("-h");
		c.gridx = 0;
		c.gridy = 1;
		serverHelp.add(sNotes01, c);
		sNotes1 = new JLabel(HELP);
		c.gridx = 1;
		c.gridy = 1;
		serverHelp.add(sNotes1, c);
		sNotes02 = new JLabel("-v");
		c.gridx = 0;
		c.gridy = 2;
		serverHelp.add(sNotes02, c);
		sNotes2 = new JLabel(VERBOSE);
		c.gridx = 1;
		c.gridy = 2;
		serverHelp.add(sNotes2, c);
		sNotes03 = new JLabel("-t");
		c.gridx = 0;
		c.gridy = 3;
		serverHelp.add(sNotes03, c);
		sNotes3 = new JLabel(TEXT);
		c.gridx = 1;
		c.gridy = 3;
		serverHelp.add(sNotes3, c);
		sNotes04 = new JLabel("-p <port>");
		c.gridx = 0;
		c.gridy = 4;
		serverHelp.add(sNotes04, c);
		sNotes4 = new JLabel(PORT);
		c.gridx = 1;
		c.gridy = 4;
		serverHelp.add(sNotes4, c);
		sNotes05 = new JLabel("-dir <dir>");
		c.gridx = 0;
		c.gridy = 5;
		serverHelp.add(sNotes05, c);
		sNotes5 = new JLabel(DIR);
		c.gridx = 1;
		c.gridy = 5;
		serverHelp.add(sNotes5, c);
		sNotes06 = new JLabel("-eext <ext>");
		c.gridx = 0;
		c.gridy = 6;
		serverHelp.add(sNotes06, c);
		sNotes6 = new JLabel(EEXT);
		c.gridx = 1;
		c.gridy = 6;
		serverHelp.add(sNotes6, c);
		sNotes07 = new JLabel("-wext <ext>");
		c.gridx = 0;
		c.gridy = 7;
		serverHelp.add(sNotes07, c);
		sNotes7 = new JLabel(WEXT);
		c.gridx = 1;
		c.gridy = 7;
		serverHelp.add(sNotes7, c);
		sNotes08 = new JLabel("-efiles <file> [file [file [...]]]   ");
		c.gridx = 0;
		c.gridy = 8;
		serverHelp.add(sNotes08, c);
		sNotes8 = new JLabel(EFILES);
		c.gridx = 1;
		c.gridy = 8;
		serverHelp.add(sNotes8, c);
		sNotes09 = new JLabel("-wfiles <file> [file [file [...]]]  ");
		c.gridx = 0;
		c.gridy = 9;
		serverHelp.add(sNotes09, c);
		sNotes9 = new JLabel(WFILES);
		c.gridx = 1;
		c.gridy = 9;
		serverHelp.add(sNotes9, c);
		c.gridx = 0;
		c.gridy = 1;
		//serverPanelS.add(serverHelp, c);

		/**
		 * ADDING THE PANELS TO THE FRAME
		 */
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		// ORDER MATTERS HERE
		tabbedPane.addTab(CLIENTPANEL, clientPanel);
		tabbedPane.addTab(SERVERPANEL, serverPanel);
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
			String inDirValC = String.valueOf(dirInC.getText());
			String inEextValC = String.valueOf(eextInC.getText());
			String inEfilesValC = String.valueOf(efilesInC.getText());

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

			if (dirC==true)
			{
				arguments[9]="-dir";
				arguments[10]=inDirValC;
			}
			else if (dirC==false)
			{
				arguments[9]="";
				arguments[10]="";
			}

			if (eextC==true)
			{
				arguments[11]="-eext";
				arguments[12]=inEextValC;
			}
			else if (eextC==false)
			{
				arguments[11]="";
				arguments[12]="";
			}

			if (efilesC==true)
			{
				arguments[13]="-efiles";
				arguments[14]=inEfilesValC;
			}
			else if (efilesC==false)
			{
				arguments[13]="";
				arguments[14]="";
			}

			// empty arguments to prevent crashes with exec
			for (int temp=15;temp<arguments.length;temp++)
			{
				arguments[temp]="";
			}
			
			System.out.println("Starting CLIENT with the following commands: ");
			int temps=1;
			for (int tmp=4;tmp<=14;tmp++)
			{
				if (arguments[tmp]!="")
				{
					System.out.println("\t"+temps+": "+arguments[tmp]);
					temps++;
				}
			}
			if (temps==1)
				System.out.println("\tDefault Options Selected");

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
			
			arguments[3]="Server";

			if (verboseS==true)
				arguments[4]="-v";
			else if (verboseS==false)
				arguments[4]="";
			
			if (textS==true)
			{
				arguments[2]="";
				arguments[5]="-t";
			}
			else if (textS==false)
			{
				arguments[2]="-splash:images/server.gif";
				arguments[5]="";
			}

			if (portS==true)
			{
				arguments[6]="-p";
				arguments[7]=inPortValS;
			}
			else if (portS==false)
			{
				arguments[6]="";
				arguments[7]="";
			}

			if (dirS==true)
			{
				arguments[8]="-dir";
				arguments[9]=inDirValS;
			}
			else if (dirS==false)
			{
				arguments[8]="";
				arguments[9]="";
			}

			if (eextS==true)
			{
				arguments[10]="-eext";
				arguments[11]=inEextValS;
			}
			else if (eextS==false)
			{
				arguments[10]="";
				arguments[11]="";
			}

			if (wextS==true)
			{
				arguments[12]="-wext";
				arguments[13]=inWextValS;
			}
			else if (wextS==false)
			{
				arguments[12]="";
				arguments[13]="";
			}

			if (efilesS==true)
			{
				arguments[14]="-efiles";
				arguments[15]=inEfilesValS;
			}
			else if (efilesS==false)
			{
				arguments[14]="";
				arguments[15]="";
			}

			if (wfilesS==true)
			{
				arguments[16]="-wfiles";
				arguments[17]=inWfilesValS;
			}
			else if (wfilesS==false)
			{
				arguments[16]="";
				arguments[17]="";
			}

			System.out.println("Starting SERVER with the following commands: ");
			int temps=1;
			for (int tmp=4;tmp<arguments.length;tmp++)
			{
				if (arguments[tmp]!="")
				{
					System.out.println("\t"+temps+": "+arguments[tmp]);
					temps++;
				}
			}
			if (temps==1)
				System.out.println("\tDefault Options Selected");
			
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
			else if (source==serverChkBoxC)
				serverC = true;
			else if (source==portChkBoxC)
				portC = true;
			else if (source==dirChkBoxC)
				dirC = true;
			else if (source==eextChkBoxC)
				eextC = true;
			else if (source==efilesChkBoxC)
				efilesC = true;
			// Server Options
			else if (source==verbChkBoxS)
				verboseS = true;
			else if (source==textChkBoxS)
				textS = true;
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
			else if (source==serverChkBoxC)
				serverC = false;
			else if (source==portChkBoxC)
				portC = false;
			else if (source==dirChkBoxC)
				dirC = false;
			else if (source==eextChkBoxC)
				eextC = false;
			else if (source==efilesChkBoxC)
				efilesC = false;
			// Server Options
			else if (source==verbChkBoxS)
				verboseS = false;
			else if (source==textChkBoxS)
				textS = false;
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