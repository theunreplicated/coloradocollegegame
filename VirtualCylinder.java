import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualCylinder extends VirtualShape
{
	private float radius;
	private float height;

	public VirtualCylinder(Node _info)
	{
		super(_info);
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());
		
		Element _height = (Element) info.getElementsByTagName("height").item(0);
		height = Float.parseFloat(_height.getTextContent());

		boundingBox = new float[] {radius, 0.5f*height, radius};
	}

	public VirtualCylinder(float _radius, float _height, float[] _position)
	{
		super(_position);
		radius = _radius;
		height = _height;
		boundingBox = new float[] {radius, 0.5f*height, radius};
	}

	public float getRadius()
	{
		return radius;
	}
	
	public float getHeight()
	{
		return height;
	}
}
