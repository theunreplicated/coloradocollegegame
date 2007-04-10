import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualCone extends VirtualShape
{
	static final long serialVersionUID = 6883420106314429184L;
	private float radius;
	private float height;

	public VirtualCone(Node _info)
	{
		super(_info);
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());
		
		Element _height = (Element) info.getElementsByTagName("height").item(0);
		height = Float.parseFloat(_height.getTextContent());

		boundingBox = new float[] {radius*scale[0], 0.5f*height*scale[1], radius*scale[2]};
	}

	public VirtualCone(float _radius, float _height, float[] _position)
	{
		super(_position);
		radius = _radius;
		height = _height;
		boundingBox = new float[] {radius*scale[0], 0.5f*height*scale[1], radius*scale[2]};
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
