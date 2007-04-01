import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualSphere extends VirtualShape
{
	private float radius;

	public VirtualSphere(Node _info)
	{
		super(_info);
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());

		boundingBox = new float[] {radius, radius, radius};
	}

	public VirtualSphere(float _radius, float[] _position)
	{
		super(_position);
		radius = _radius;
		boundingBox = new float[] {radius, radius, radius};
	}

	public float getRadius()
	{
		return radius;
	}

	public void scale( double[] factors )
	{
		radius *= factors[Constants.RADIUS];
	}
}
