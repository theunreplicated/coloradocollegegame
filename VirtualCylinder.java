import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualCylinder implements VirtualShape
{
	private float radius;
	private float height;
	private float[] position;
	private float[] facing; //in quaternions

	public VirtualCylinder(Node _info)
	{
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());
		
		Element _height = (Element) info.getElementsByTagName("height").item(0);
		height = Float.parseFloat(_height.getTextContent());

		NodeList positionNodes = info.getElementsByTagName("position");
		position = new float[positionNodes.getLength()];
		for(int i = position.length-1; i>=0; i--)
		{
			position[i] = Float.parseFloat(positionNodes.item(i).getTextContent());
		}

		NodeList facingNodes = info.getElementsByTagName("facing");
		float[] rotEuler = new float[facingNodes.getLength()]; //construct an array of Euler facings
		for(int i = rotEuler.length-1; i>=0; i--)
		{
			rotEuler[i] = Float.parseFloat(facingNodes.item(i).getTextContent());
			rotEuler[i] = (float)Math.toRadians(rotEuler[i]); //change to Radians. We like radians.
		}
		if(rotEuler.length == 3)
			facing = Quaternions.getQuatFromEuler(rotEuler); //set the facing to be in Quaternions
		else
			facing = Constants.DEFAULT_FACING; //set to a unit
	}

	public VirtualCylinder(float _radius, float _height, float[] _position)
	{
		radius = _radius;
		height = _height;
		position = _position;
	}

	public float getRadius()
	{
		return radius;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public float[] getPosition()
	{
		return position;
	}

	public float[] getFacing()
	{
		return facing;
	}

	public float[][] getMinMax()
	{
		float[][] tmp = new float[position.length][2];
		
		tmp[Constants.X][Constants.MIN] = position[Constants.X]-radius;
		tmp[Constants.X][Constants.MAX] = position[Constants.X]+radius;
		tmp[Constants.Y][Constants.MIN] = position[Constants.Y]-radius;
		tmp[Constants.Y][Constants.MIN] = position[Constants.Y]+radius;
		tmp[Constants.Z][Constants.MAX] = position[Constants.Z]-height/2;
		tmp[Constants.Z][Constants.MAX] = position[Constants.Z]+height/2;

		return( tmp );
	}

	public void scale( double[] factors )
	{
		radius *= factors[Constants.RADIUS];
		height *= factors[Constants.HEIGHT];
	}
}
