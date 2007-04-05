import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualBox extends VirtualShape
{
	private float[] dimensions;

	public VirtualBox(Node _info)
	{
		super(_info);
		Element info = (Element) _info;
		NodeList dimNodes = info.getElementsByTagName("dimension");
		dimensions = new float[dimNodes.getLength()];
		for(int i = dimensions.length-1; i>=0; i--)
		{
			dimensions[i] = Float.parseFloat(dimNodes.item(i).getTextContent());
		}

		boundingBox = new float[] {0.5f*dimensions[0]*scale[0], 0.5f*dimensions[1]*scale[1], 0.5f*dimensions[2]*scale[2]}; //get the half-dimensions
	}

	public VirtualBox(float[] _dimensions, float[] _position)
	{
		super(_position);
		dimensions = _dimensions;
		boundingBox = new float[] {0.5f*dimensions[0]*scale[0], 0.5f*dimensions[1]*scale[1], 0.5f*dimensions[2]*scale[2]}; //get the half-dimensions
	}

	public float getDimX()
	{
		return dimensions[0];
	}

	public float getDimY()
	{
		return dimensions[1];
	}

	public float getDimZ()
	{
		return dimensions[2];
	}
}
