import javax.swing.*;
import java.awt.*;

public class ToolBox extends JFrame
{
	/*
	Vars
	*/
	ElementFactory ef;
	
	/*filesaving*/
	JButton loadFileButton;
	JButton saveFileButton;
	
	/*add elements, etc*/
	JButton addElementButton;
	JButton deleteElementButton;
	
	/*change viewscale*/
	JButton zoomInButton;
	JButton zoomOutButton;
	
	/*Space stuff*/
	JButton newSpaceButton;
	JButton addSpaceButton;
	JButton deleteSpaceButton;
	
	
	/*
	constructor
	*/
	public ToolBox(SpaceCanvas _canvas, ElementFactory _ef)
	{
		SpaceCanvas canvas = _canvas;
		ef = _ef;
		
		/*
		Make the buttons
		*/
		loadFileButton = new JButton("Load File");
		saveFileButton = new JButton("Save File");
		addElementButton = new JButton("Add Element");
		deleteElementButton = new JButton("Delete Elelement");
		zoomInButton = new JButton("Zoom In");
		zoomOutButton = new JButton("Zoom Out");
		newSpaceButton = new JButton("Create New Space");
		addSpaceButton = new JButton("Add Space");
		deleteSpaceButton = new JButton("Delete Space");
	
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new GridLayout(9,1));
		
		/*
		Set up button panel
		*/
		buttonPanel.add( loadFileButton );
		buttonPanel.add( saveFileButton );
		buttonPanel.add( addElementButton );
		buttonPanel.add( deleteElementButton );
		buttonPanel.add( zoomInButton );
		buttonPanel.add( zoomOutButton );
		buttonPanel.add( newSpaceButton );
		buttonPanel.add( addSpaceButton );
		buttonPanel.add( deleteSpaceButton );
		
		loadFileButton.addActionListener( canvas );
		saveFileButton.addActionListener( canvas );
		addElementButton.addActionListener( canvas );
		deleteElementButton.addActionListener( canvas );
		zoomInButton.addActionListener( canvas );
		zoomOutButton.addActionListener( canvas );
		newSpaceButton.addActionListener( canvas );
		addSpaceButton.addActionListener( canvas );
		deleteSpaceButton.addActionListener( canvas );
	
		/*
		Putting panels together
		*/
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		this.getContentPane().add( mainPanel );
		this.pack();
		this.setVisible( true );
	}//out of constructor
}
