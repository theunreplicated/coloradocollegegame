import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualCone implements VirtualShape
{
	private float radius;
	private float height;
	private float[] center;
	private float[] rotation; //in quaternions

	public VirtualCone(Node _info)
	{
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());
		
		Element _height = (Element) info.getElementsByTagName("height").item(0);
		height = Float.parseFloat(_height.getTextContent());

		NodeList centerNodes = info.getElementsByTagName("center");
		center = new float[centerNodes.getLength()];
		for(int i = center.length-1; i>=0; i--)
		{
			center[i] = Float.parseFloat(centerNodes.item(i).getTextContent());
		}

		NodeList rotationNodes = info.getElementsByTagName("rotation");
		float[] rotEuler = new float[rotationNodes.getLength()]; //construct an array of Euler rotations
		for(int i = rotEuler.length-1; i>=0; i--)
		{
			rotEuler[i] = Float.parseFloat(rotationNodes.item(i).getTextContent());
			rotEuler[i] = (float)Math.toRadians(rotEuler[i]); //change to Radians. We like radians.
		}
		if(rotEuler.length != 0)
		{
			rotation = Quaternions.getQuatFromEuler(rotEuler); //set the rotation to be in Quaternions
		}
		else
			rotation = new float[] {0,0,0,1}; //set to a unit
	}


	public VirtualCone(float _radius, float _height, float[] _center)
	{
		radius = _radius;
		height = _height;
		center = _center;
	}

	public float getRadius()
	{
		return radius;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public float[] getCenter()
	{
		return center;
	}

	public float[] getRotation()
	{
		return rotation;
	}

	public float[][] getMinMax()
	{
		float[][] tmp = new float[center.length][2];
		
		tmp[Constants.X][Constants.MIN] = center[Constants.X]-radius;
		tmp[Constants.X][Constants.MAX] = center[Constants.X]+radius;
		tmp[Constants.Y][Constants.MIN] = center[Constants.Y]-radius;
		tmp[Constants.Y][Constants.MIN] = center[Constants.Y]+radius;
		tmp[Constants.Z][Constants.MAX] = center[Constants.Z]-height/2;
		tmp[Constants.Z][Constants.MAX] = center[Constants.Z]+height/2;

		return( tmp );
	}

	public void scale( double[] factors )
	{
		radius *= factors[Constants.RADIUS];
		height *= factors[Constants.HEIGHT];
	}
}
