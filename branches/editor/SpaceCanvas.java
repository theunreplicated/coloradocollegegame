import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class SpaceCanvas extends JPanel implements ActionListener
{
	World world;
	GameElement[] elements;
	GameElement editing = null;
	int nextId = 0;

	public SpaceCanvas(ElementFactory _ef , Logger _logger)
	{
		world = new World(_ef,_logger);
		elements = world.getElements();

		this.setFocusable( true );
		this.setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT));
	}

	public void actionPerformed( ActionEvent _ae )
	{
		String button = ((JButton)_ae.getSource()).getText();
		System.out.println( button );

		if( button.equals( "Add Element" ) )
		{
			world.addElement( new int[] { nextId++ , 0 , nextId, nextId, 0 } , 0 );
			this.repaint();
		}
	}

	public void paintComponent( Graphics _g )
	{
		Graphics2D g = (Graphics2D)_g;
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, this.getWidth() , this.getHeight() );
		g.translate( this.getWidth()/2 , this.getHeight()/2 );

		g.setColor( Color.BLACK );

		int[] position;
		VirtualShape[] shapes;
		VirtualSphere sphere;
		VirtualCylinder cylinder;

		for( GameElement e : elements )
		{
			if( e != null )
			{
				if( e == editing )
				{
					g.setStroke(new BasicStroke(2) );
				}

				position = e.getPosition();
				shapes = e.getShapes();

				for( VirtualShape shape : shapes )
				{
					if( shape.getClass().getName().equals("VirtualSphere") )
					{
						sphere = (VirtualSphere)shape;
						g.drawOval( (int)(position[Constants.X] + sphere.center[Constants.X]) , (int)(position[Constants.Y]+sphere.center[Constants.Y]) , (int)(sphere.radius), (int)(sphere.radius) );
					}
					if( shape.getClass().getName().equals("VirtualCylinder") )
					{
						cylinder = (VirtualCylinder)shape;
						g.drawOval( (int)(position[Constants.X] + cylinder.center[Constants.X]) , (int)(position[Constants.Y]+cylinder.center[Constants.Y]) , (int)(cylinder.radius), (int)(cylinder.radius) );
					}
					else
						g.drawRect( position[Constants.X] , position[Constants.Y] , 20 , 20 );
				}

				g.setStroke(new BasicStroke(1) );
			}
		}
	}
}
