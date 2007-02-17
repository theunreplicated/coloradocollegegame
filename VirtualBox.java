import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualBox implements VirtualShape
{
	private float[] dimensions;
	private float[] center;

	public VirtualBox(Node _info)
	{
		Element info = (Element) _info;
		NodeList centerNodes = info.getElementsByTagName("center");
		center = new float[centerNodes.getLength()];
		for(int i = center.length-1; i>=0; i--)
		{
			center[i] = Float.parseFloat(centerNodes.item(i).getTextContent());
		}
		NodeList dimNodes = info.getElementsByTagName("dimension");
		dimensions = new float[dimNodes.getLength()];
		for(int i = dimensions.length-1; i>=0; i--)
		{
			dimensions[i] = Float.parseFloat(dimNodes.item(i).getTextContent());
		}

	}

	public VirtualBox(float[] _dimensions, float[] _center)
	{
		dimensions = _dimensions;
		center = _center;
	}
}
