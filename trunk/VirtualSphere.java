import org.w3c.dom.*;
//import javax.xml.parsers.*;//never used

public class VirtualSphere extends VirtualShape
{
	static final long serialVersionUID = 1234035442075705846L;
	private float radius;

	public VirtualSphere(Node _info)
	{
		super(_info);
		Element info = (Element) _info;
		Element _radius = (Element) info.getElementsByTagName("radius").item(0);
		radius = Float.parseFloat(_radius.getTextContent());

		boundingBox = new float[] {radius*scale[0], radius*scale[1], radius*scale[2]};
	}

	public VirtualSphere(float _radius, float[] _position)
	{
		super(_position);
		radius = _radius;
		boundingBox = new float[] {radius*scale[0], radius*scale[1], radius*scale[2]};
	}

	public float getRadius()
	{
		return radius;
	}
}
