import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualSphere implements VirtualShape
{
	private float radius;
	private float[] center;

	public VirtualSphere(Node _info)
	{
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());
		NodeList centerNodes = info.getElementsByTagName("center");
		center = new float[centerNodes.getLength()];
		for(int i = center.length-1; i>=0; i--)
		{
			center[i] = Float.parseFloat(centerNodes.item(i).getTextContent());
		}
	}

	public VirtualSphere(float _radius, float[] _center)
	{
		radius = _radius;
		center = _center;
	}

	public float getRadius()
	{
		return radius;
	}

	public float[] getCenter()
	{
		return center;
	}

	public float[][] getMinMax()
	{
		float[][] tmp = new float[center.length][2];
		
		for( int i = 0; i < tmp.length; i++)
		{
			tmp[i][Constants.MIN] = center[i]-radius;
			tmp[i][Constants.MAX] = center[i]+radius;
		}

		return( tmp );
	}

	public void scale( double[] factors )
	{
		radius *= factors[Constants.RADIUS];
	}
}
