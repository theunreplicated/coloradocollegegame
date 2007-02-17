import java.awt.*;

public class GraphicalElement extends GameElement
{
	public GraphicalElement(int _x, int _y, int _z, int _width, int _length)
	{
		super(_x, _y, _z, _width, _length);
	}

	public GraphicalElement(int _x, int _y, int _z, int _width, int _length, int _status)
	{
		super(_x, _y, _z, _width, _length, _status);
	}

	public void render(Graphics g)
	{
		int[][] tmpPoints = this.getAbsoluteCoordinates();
		g.setColor(Constants.COLORS[status]);
		if( isClient ) g.fillPolygon( tmpPoints[Constants.X], tmpPoints[Constants.Y] , 4 );
		else g.drawPolygon( tmpPoints[Constants.X], tmpPoints[Constants.Y] , 4 );
	}
}
