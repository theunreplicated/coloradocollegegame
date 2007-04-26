import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Guillermo Mendez-Kestler
 *
 */
public class StartUp extends JPanel implements ActionListener, ChangeListener, ItemListener
{
	private static final long serialVersionUID = 1L;
	JButton button1;
	JCheckBox chkBox;
	boolean chkBoxBool;
    JLabel inWFlabel, inEFlabel, inLogLabel, inPortLabel; 
	JSlider slider1;
	// User Input
	JTextField inWF, inEF, inLog, inPort;
	// Values of...
	JTextField sliderValue;
	
	public StartUp()
	{
		setPreferredSize(new Dimension(360,360));
		setBackground(Color.white);
		setFocusable(true); //for keyboard focus
		
		// Panels
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new BorderLayout());
		Panel worldFactoryPanel = new Panel();
		worldFactoryPanel.setLayout(new BorderLayout());
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new BorderLayout());
		Panel sliderPanel = new Panel();
		sliderPanel.setLayout(new BorderLayout());
		
		// World Factory In
		inWFlabel = new JLabel("World Factory: ");
		inWFlabel.setVisible(true);
		worldFactoryPanel.add(inWFlabel, BorderLayout.NORTH);
		
		inWF = new JTextField("Default World Factory", 20);
		inWF.setVisible(true);
		worldFactoryPanel.add(inWF, BorderLayout.SOUTH);
		
		// Buttons
		button1 = new JButton("Button");
		//panel.add(button1, layout)
		buttonPanel.add(button1, BorderLayout.NORTH);
		button1.addActionListener(this);
		
		// Check Box
		chkBox = new JCheckBox("Check Box");
		chkBox.setSelected(false);
		buttonPanel.add(chkBox, BorderLayout.SOUTH);
		
		// Sliders
		slider1 = new JSlider(0,20,2);
		slider1.setBorder(BorderFactory.createTitledBorder("Slider Label"));
		slider1.setMajorTickSpacing(5);
		slider1.setMinorTickSpacing(1);
		slider1.setPaintLabels(true);
		slider1.setPaintTicks(true);
		slider1.setPaintTrack(true);
		slider1.setSnapToTicks(true);
		sliderPanel.add(slider1, BorderLayout.WEST);
		slider1.addChangeListener(this);
		//slider value
		sliderValue = new JTextField("2", 2);
		sliderValue.setVisible(true);
		sliderPanel.add(sliderValue, BorderLayout.EAST);
		
		
		mainPanel.add(sliderPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.WEST);
		mainPanel.add(worldFactoryPanel, BorderLayout.NORTH);
		
		add(mainPanel);
	} // StartUp Contstructor
	
	public void actionPerformed(ActionEvent e) 
	{
		String inWFval = String.valueOf(inWF.getText());
		if (e.getSource()==button1)
		{
			System.out.println(inWFval);
		}
	} // actionPerformed
	
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getItemSelectable();
        if (source==chkBox)
        {
        	if (chkBoxBool==false)
        		chkBoxBool = true;
        	else
        		chkBoxBool = false;
        }
        
        if (e.getStateChange() == ItemEvent.DESELECTED)
        {
        	
        }
        //may not be needed
        else if (e.getStateChange() == ItemEvent.SELECTED)
        {
        	
        }
	} // item state changed

	public void stateChanged(ChangeEvent e) 
	{
		int sliderVal; 
		if (e.getSource()==slider1)
		{
			sliderVal = slider1.getValue();
			System.out.println(sliderVal);
			String newText=String.valueOf(sliderVal);
			sliderValue.setText(newText);
		}
	} // state changed
	
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