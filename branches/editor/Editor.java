import javax.swing.*;
import java.awt.*;

public class Editor
{
	public static void main( String[] args )
	{
		Logger logger = new Logger(true);
		ElementFactory ef = new ElementFactory();

		SpaceCanvas canvas = new SpaceCanvas( ef , logger );
		InfoPanel infoPanel = new InfoPanel();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout( new BorderLayout() );

		mainPanel.add( canvas , BorderLayout.CENTER );
		mainPanel.add( infoPanel , BorderLayout.SOUTH );

		JFrame frame = new JFrame();
		frame.getContentPane().add( mainPanel );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack();
		frame.setVisible( true );

		ToolBox toolBox = new ToolBox(canvas,ef);
	}
}
