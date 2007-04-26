import org.w3c.dom.*;
//import javax.xml.parsers.*;//needed?
import java.io.Serializable;

public class VirtualShape implements Serializable
{
	static final long serialVersionUID = -1458411371995253014L;

	private String name;
	private float[] position;
	private float[] facing; //in quaternions
	protected float[] scale;
	protected float[] boundingBox;
	private int color;
	private String texture;
	private int texturePattern;
	
	public VirtualShape(float[] _position)
	{
		position = _position;

		//set everything else as default--you can't just leave this stuff undefined!!
		name = Constants.DEFAULT_NAME;
		facing = Constants.DEFAULT_FACING;
		scale = Constants.DEFAULT_SCALE;
		color = Constants.DEFAULT_COLOR;
		texture = Constants.DEFAULT_TEXTURE;   
		texturePattern = Constants.DEFAULT_TEXTURE_PATTERN;
	}

	public VirtualShape(Node _info)
	{
		Element info = (Element) _info;

		NodeList nameNodes = info.getElementsByTagName("name");
		if(nameNodes.getLength() != 0)
			name = nameNodes.item(0).getTextContent();
		else
			name = Constants.DEFAULT_NAME;

		NodeList positionNodes = info.getElementsByTagName("position");
		if(positionNodes.getLength() != 0)
		{
			position = new float[positionNodes.getLength()];
			for(int i = position.length-1; i>=0; i--)
				position[i] = Float.parseFloat(positionNodes.item(i).getTextContent());
		}
		else
			position = Constants.DEFAULT_POSITION;

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

		NodeList scaleNodes = info.getElementsByTagName("scale");
		if(scaleNodes.getLength() != 0)
		{
			scale = new float[scaleNodes.getLength()];
			for(int i = scale.length-1; i>=0; i--)
				scale[i] = Float.parseFloat(scaleNodes.item(i).getTextContent());
		}
		else
			scale = Constants.DEFAULT_SCALE;

		NodeList colorNodes = info.getElementsByTagName("color");
		if(colorNodes.getLength() != 0)
		{
			color = (int)Long.parseLong(colorNodes.item(0).getTextContent(),16);	
		}
		else
			color = Constants.DEFAULT_COLOR;

		NodeList textureNodes = info.getElementsByTagName("texture");
		if(textureNodes.getLength() != 0)
		{
			texture = textureNodes.item(0).getTextContent();
			NodeList texturePatternNodes = info.getElementsByTagName("texture_pattern");
			if(texturePatternNodes.getLength() != 0)
			{
				texturePattern = Constants.parseTexture(texturePatternNodes.item(0).getTextContent());
			}
			else
				texturePattern = Constants.DEFAULT_TEXTURE_PATTERN;
		}
		else
		{
			texture = Constants.DEFAULT_TEXTURE;
			texturePattern = Constants.DEFAULT_TEXTURE_PATTERN;
		}

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

	public float[] getScale()
	{ 
		return scale;
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

	public int getTexturePattern()
	{
		return texturePattern;
	}
}
