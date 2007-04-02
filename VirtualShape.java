import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualShape
{
	private String name;
	private float[] position;
	private float[] facing; //in quaternions
	protected float[] boundingBox;
	private int color;
	private String texture;
	
	public VirtualShape(float[] _position)
	{
		position = _position;
	}

	public VirtualShape(Node _info)
	{
		Element info = (Element) _info;

		NodeList nameNodes = info.getElementsByTagName("name");
		if(nameNodes.getLength() != 0)
			name = nameNodes.item(0).getTextContent();
		else
			name = "Bruce"; //what do we make the name by default?

		NodeList colorNodes = info.getElementsByTagName("color");
		if(colorNodes.getLength() != 0)
		{
			color = (int)Long.parseLong(colorNodes.item(0).getTextContent(),16);	
		}
		else
			color = 0;

		NodeList textureNodes = info.getElementsByTagName("texture");
		if(textureNodes.getLength() != 0)
			texture = textureNodes.item(0).getTextContent();
		else
			texture = null;	

		NodeList positionNodes = info.getElementsByTagName("position");
		position = new float[positionNodes.getLength()];
		for(int i = position.length-1; i>=0; i--)
			position[i] = Float.parseFloat(positionNodes.item(i).getTextContent());

		NodeList facingNodes = info.getElementsByTagName("facing");
		float[] rotEuler = new float[facingNodes.getLength()]; //construct an array of Euler facings
		for(int i = rotEuler.length-1; i>=0; i--)
		{
			rotEuler[i] = Float.parseFloat(facingNodes.item(i).getTextContent());
			rotEuler[i] = (float)Math.toRadians(rotEuler[i]); //change to Radians. We like radians.
		}
		if(rotEuler.length == 3)
			facing = Quaternions.getQuatFromEuler(rotEuler); //set the facing to be in Quaternions
		else
			facing = Constants.DEFAULT_FACING; //set to a unit
	}

	public String getName()
	{
		return name;
	}

	public float[] getPosition()
	{
		return position;
	}
	
	public float[] getFacing()
	{
		return facing;
	}

	public float[] getBoundingBox()
	{
		return boundingBox;
	}

	public int getColor()
	{
		return color;
	}

	public String getTexture()
	{
		return texture;
	}

	public void scale( double[] _factors ){ }
}
