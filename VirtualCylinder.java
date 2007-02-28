import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualCylinder implements VirtualShape
{
	float radius;
	float[] center;
	float height;

	public VirtualCylinder(Node _info)
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
	}

	public VirtualCylinder(float _radius, float _height, float[] _center)
	{
		radius = _radius;
		height = _height;
		center = _center;
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