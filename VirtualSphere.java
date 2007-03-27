import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualSphere implements VirtualShape
{
	private float radius;
	private float[] position;
	private float[] facing; //in quaternions

	public VirtualSphere(Node _info)
	{
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());
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


	public VirtualSphere(float _radius, float[] _position)
	{
		radius = _radius;
		position = _position;
	}

	public float getRadius()
	{
		return radius;
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
		
		for( int i = 0; i < tmp.length; i++)
		{
			tmp[i][Constants.MIN] = position[i]-radius;
			tmp[i][Constants.MAX] = position[i]+radius;
		}

		return( tmp );
	}

	public void scale( double[] factors )
	{
		radius *= factors[Constants.RADIUS];
	}
}
