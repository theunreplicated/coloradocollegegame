import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
 * set variables passed in a GUI enviornment versus the command-line, 
 * as much as the command-line is more robust. :-D
 * 
 * @author Guillermo Mendez-Kestler
 *
 */
public class StartUp extends JPanel 
				implements ActionListener, ItemListener
{
	// Global Variables
	private static final long serialVersionUID = 1L;//generated later
	final static String CLIENTPANEL = "CLIENT";
	final static String SERVERPANEL = "SERVER";
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
	
	JButton serverBtn, clientBtn, helpBtnC, helpBtnS;
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
	public String[] arguments = new String[15]; 
	
	public StartUp()
	{
		// GUI Variables
		JLabel verbLabelC, portLabelC, serverLabelC, verbLabelS, 
			portLabelS, dirLabelS, eextLabelS, wextLabelS, 
			efilesLabelS, wfilesLabelS, cNotes0, cNotes1, cNotes2, 
			cNotes3, cNotes4, cNotes00, cNotes01, cNotes02, cNotes03, 
			cNotes04, sNotes0, sNotes1, sNotes2, sNotes3, sNotes4, 
			sNotes5, sNotes6, sNotes7, sNotes8, sNotes00, sNotes01, 
			sNotes02, sNotes03, sNotes04, sNotes05, sNotes06, 
			sNotes07, sNotes08;
		JTabbedPane tabbedPane = new JTabbedPane();
        GridBagConstraints c = new GridBagConstraints();
		
		setPreferredSize(new Dimension(625,330));
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
		//clientHelp.setLayout(new GridLayout(5,2));
		clientHelp.setLayout(new GridBagLayout());
		
		Panel serverHelp = new Panel();
		//serverHelp.setLayout(new GridLayout(10,2));
		serverHelp.setLayout(new GridBagLayout());
		
		// Client Panels
		Panel clientPanelM = new Panel();
		clientPanelM.setLayout(new BorderLayout());
		
		Panel clientPanelR = new Panel();
		clientPanelR.setLayout(new GridLayout(3,2));
		
		Panel clientPanelL = new Panel();
		clientPanelL.setLayout(new GridLayout(3,1));
		
		Panel clientPanelS = new Panel();
		clientPanelS.setLayout(new BorderLayout());
		
		// Server Panels
		Panel serverPanelM = new Panel();
		serverPanelM.setLayout(new BorderLayout());
		
		Panel serverPanelR = new Panel();
		serverPanelR.setLayout(new GridLayout(7,2));
		
		Panel serverPanelL = new Panel();
		serverPanelL.setLayout(new GridLayout(7,1));
		
		Panel serverPanelS = new Panel();
		serverPanelS.setLayout(new BorderLayout());
		
		/**
		 * CLIENT PANEL
		 */
		// Client Notes
		cNotes00 = new JLabel("");
		c.gridx = 0;
		c.gridy = 0;
		clientHelp.add(cNotes00, c);
		cNotes0 = new JLabel(CLIENTHELP0);
		c.gridx = 1;
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
		clientPanelS.add(clientHelp, BorderLayout.WEST);
		
		// Client Server
		serverChkBoxC = new JCheckBox("");
		serverChkBoxC.setSelected(false);
		clientPanelL.add(serverChkBoxC);
		serverChkBoxC.addItemListener(this);
		
		serverLabelC = new JLabel("Connect to Server IP:");
		serverLabelC.setVisible(true);
		clientPanelR.add(serverLabelC);
		
		inServerC = new JTextField("", 5);
		inServerC.setVisible(true);
		clientPanelR.add(inServerC);
		
		// Client Port
		portChkBoxC= new JCheckBox("");
		portChkBoxC.setSelected(false);
		clientPanelL.add(portChkBoxC);
		portChkBoxC.addItemListener(this);
		
		portLabelC = new JLabel("Server Port #:");
		portLabelC.setVisible(true);
		clientPanelR.add(portLabelC);
		
		inPortC = new JTextField("", 5);
		inPortC.setVisible(true);
		clientPanelR.add(inPortC);
		
		// Client Verbose Mode
		verbChkBoxC = new JCheckBox("");
		verbChkBoxC.setSelected(false);
		clientPanelL.add(verbChkBoxC);
		verbChkBoxC.addItemListener(this);
		
		verbLabelC = new JLabel("Client Verbose Mode");
		verbLabelC.setVisible(true);
		clientPanelR.add(verbLabelC);
		
		/**
		 * SERVER PANEL
		 */
		// Server Notes
		sNotes00 = new JLabel("");
		c.gridx = 0;
		c.gridy = 0;
		serverHelp.add(sNotes00, c);
		sNotes0 = new JLabel(SERVERHELP0);
		c.gridx = 1;
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
		sNotes07 = new JLabel("-efiles <file> [file [file [...]]]");
		c.gridx = 0;
		c.gridy = 7;
		serverHelp.add(sNotes07, c);
		sNotes7 = new JLabel(SERVERHELP7);
		c.gridx = 1;
		c.gridy = 7;
		serverHelp.add(sNotes7, c);
		sNotes08 = new JLabel("-wfiles <file> [file [file [...]]]");
		c.gridx = 0;
		c.gridy = 8;
		serverHelp.add(sNotes08, c);
		sNotes8 = new JLabel(SERVERHELP8);
		c.gridx = 1;
		c.gridy = 8;
		serverHelp.add(sNotes8, c);
		serverPanelS.add(serverHelp, BorderLayout.WEST);
		
		// Server Port
		portChkBoxS= new JCheckBox("");
		portChkBoxS.setSelected(false);
		serverPanelL.add(portChkBoxS);
		portChkBoxS.addItemListener(this);
		
		portLabelS = new JLabel("Set Server to Port #:");
		portLabelS.setVisible(true);
		serverPanelR.add(portLabelS);
		
		inPortS = new JTextField("", 5);
		inPortS.setVisible(true);
		serverPanelR.add(inPortS);
		
		// Server Directory
		dirChkBoxS= new JCheckBox("");
		dirChkBoxS.setSelected(false);
		serverPanelL.add(dirChkBoxS);
		dirChkBoxS.addItemListener(this);
		
		dirLabelS = new JLabel("Data Files in Directory:");
		dirLabelS.setVisible(true);
		serverPanelR.add(dirLabelS);
		
		dirInS = new JTextField("", 5);
		dirInS.setVisible(true);
		serverPanelR.add(dirInS);
		
		// Server Element List Files.<EXT>
		eextChkBoxS= new JCheckBox("");
		eextChkBoxS.setSelected(false);
		serverPanelL.add(eextChkBoxS);
		eextChkBoxS.addItemListener(this);
		
		eextLabelS = new JLabel("Element List Files with Extension: ");
		eextLabelS.setVisible(true);
		serverPanelR.add(eextLabelS);
		
		eextInS = new JTextField("", 5);
		eextInS.setVisible(true);
		serverPanelR.add(eextInS);
		
		// Server World Files.<EXT>
		wextChkBoxS= new JCheckBox("");
		wextChkBoxS.setSelected(false);
		serverPanelL.add(wextChkBoxS);
		wextChkBoxS.addItemListener(this);
		
		wextLabelS = new JLabel("World Files with Extension:");
		wextLabelS.setVisible(true);
		serverPanelR.add(wextLabelS);
		
		wextInS = new JTextField("", 5);
		wextInS.setVisible(true);
		serverPanelR.add(wextInS);
		
		// Server Element Files
		efilesChkBoxS= new JCheckBox("");
		efilesChkBoxS.setSelected(false);
		serverPanelL.add(efilesChkBoxS);
		efilesChkBoxS.addItemListener(this);
		
		efilesLabelS = new JLabel("Elements in File:");
		efilesLabelS.setVisible(true);
		serverPanelR.add(efilesLabelS);
		
		efilesInS = new JTextField("", 5);
		efilesInS.setVisible(true);
		serverPanelR.add(efilesInS);
		
		// Server World Files
		wfilesChkBoxS= new JCheckBox("");
		wfilesChkBoxS.setSelected(false);
		serverPanelL.add(wfilesChkBoxS);
		wfilesChkBoxS.addItemListener(this);
		
		wfilesLabelS = new JLabel("World in File:");
		wfilesLabelS.setVisible(true);
		serverPanelR.add(wfilesLabelS);
		
		wfilesInS = new JTextField("", 5);
		wfilesInS.setVisible(true);
		serverPanelR.add(wfilesInS);
		
		// Server Verbose Mode
		verbChkBoxS = new JCheckBox("");
		verbChkBoxS.setSelected(false);
		serverPanelL.add(verbChkBoxS);
		verbChkBoxS.addItemListener(this);
		
		verbLabelS = new JLabel("Server Verbose Mode");
		verbLabelS.setVisible(true);
		serverPanelR.add(verbLabelS);
		
		/**
		 * BUTTONS
		 */
		clientBtn = new JButton("Start Client");
		clientPanelS.add(clientBtn, BorderLayout.CENTER);
		clientBtn.addActionListener(this);
		
		serverBtn = new JButton("Start Server");
		serverPanelS.add(serverBtn, BorderLayout.CENTER);
		serverBtn.addActionListener(this);
				
		/**
		 * ADDING THE PANELS TO THE FRAME
		 */
		clientPanelM.add(clientPanelL, BorderLayout.WEST);
		clientPanelM.add(clientPanelR, BorderLayout.CENTER);
		clientPanelM.add(clientPanelS, BorderLayout.SOUTH);

		serverPanelM.add(serverPanelL, BorderLayout.WEST);
		serverPanelM.add(serverPanelR, BorderLayout.CENTER);
		serverPanelM.add(serverPanelS, BorderLayout.SOUTH);
		
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab(CLIENTPANEL, clientPanelM);
		tabbedPane.addTab(SERVERPANEL, serverPanelM);
		
		add(mainPanel);
		
	} // StartUp Contstructor
	
	public void actionPerformed(ActionEvent e) 
	{
		Runtime runtime = Runtime.getRuntime();
		if (e.getSource()==clientBtn)
		{
			String inPortValC = String.valueOf(inPortC.getText());
			String inServerValC = String.valueOf(inServerC.getText());
			
			arguments[0]="java";
			arguments[1]="-Xmx256m";
			arguments[2]="Representation3D";
			
			if (verboseC==true)
				arguments[3]="-v";
			else if (verboseC==false)
				arguments[3]="";
			
			if (portC==true)
			{
				arguments[4]="-p";
				arguments[5]=inPortValC;
			}
			else if (portC==false)
			{
				arguments[4]="";
				arguments[5]="";
			}
			
			if (serverC==true)
			{
				arguments[6]="-s";
				arguments[7]=inServerValC;
			}
			else if (serverC==false)
			{
				arguments[6]="-s";
				arguments[7]="localhost";
			}
			
			// empty arguments
			arguments[8]="";
			arguments[9]="";
			arguments[10]="";
			arguments[11]="";
			arguments[12]="";
			arguments[13]="";
			arguments[14]="";
			
			try {
				runtime.exec(arguments);
			} catch (IOException ioe) {
				System.out.println("Cant start CLIENT. \n" +
						"Error: "+ioe.getMessage());
			}
		}
		else if (e.getSource()==serverBtn)
		{
			String inPortValS = String.valueOf(inPortS.getText());
			String inDirValS = String.valueOf(dirInS.getText());
			String inEextValS = String.valueOf(eextInS.getText());
			String inWextValS = String.valueOf(wextInS.getText());
			String inEfilesValS = String.valueOf(efilesInS.getText());
			String inWfilesValS = String.valueOf(wfilesInS.getText());
			
			arguments[0]="java";
			arguments[1]="-Xmx256m";
			arguments[2]="Server";
			
			if (verboseS==true)
				arguments[3]="-v";
			else if (verboseS==false)
				arguments[3]="";
			
			if (portS==true)
			{
				arguments[4]="-p";
				arguments[5]=inPortValS;
				System.out.println(inPortValS);
			}
			else if (portS==false)
			{
				arguments[4]="";
				arguments[5]="";
			}

			if (dirS==true)
			{
				arguments[6]="-dir";
				arguments[7]=inDirValS;
				System.out.println(inDirValS);
			}
			else if (dirS==false)
			{
				arguments[6]="";
				arguments[7]="";
			}
			
			if (eextS==true)
			{
				arguments[7]="-eext";
				arguments[8]=inEextValS;
				System.out.println(inEextValS);
			}
			else if (eextS==false)
			{
				arguments[7]="";
				arguments[8]="";
			}
			
			if (wextS==true)
			{
				arguments[9]="-wext";
				arguments[10]=inWextValS;
				System.out.println(inWextValS);
			}
			else if (wextS==false)
			{
				arguments[9]="";
				arguments[10]="";
			}
			
			if (efilesS==true)
			{
				arguments[11]="-efiles";
				arguments[12]=inEfilesValS;
				System.out.println(inEfilesValS);
			}
			else if (efilesS==false)
			{
				arguments[11]="";
				arguments[12]="";
			}
			
			if (wfilesS==true)
			{
				arguments[13]="-wfiles";
				arguments[14]=inWfilesValS;
				System.out.println(inWfilesValS);
			}
			else if (wfilesS==false)
			{
				arguments[13]="";
				arguments[14]="";
			}
			
			try {
				runtime.exec(arguments);
			} catch (IOException ioe) {
				System.out.println("Cant start CLIENT. \n" +
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
		panel.add(canvasFrame, BorderLayout.WEST);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);
		window.pack();
		window.setResizable(true);
	}//main
}//class