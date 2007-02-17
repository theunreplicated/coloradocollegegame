import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualCone implements VirtualShape
{
	private float radius;
	private float[] center;
	private float height;

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
	}

	public VirtualCone(float _radius, float _height, float[] _center)
	{
		radius = _radius;
		height = _height;
		center = _center;
	}
}
