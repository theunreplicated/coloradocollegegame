import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualKML implements VirtualShape //A shape built from a Google SketchUp! model
{
	private float[] position;
	private float[] facing; //in quaternions
	private float[] boundingBox;

	public VirtualKML(Node _info)
	{
		Element info = (Element) _info;
		NodeList positionNodes = info.getElementsByTagName("position");
		position = new float[positionNodes.getLength()];
		for(int i = position.length-1; i>=0; i--)
		{
			position[i] = Float.parseFloat(positionNodes.item(i).getTextContent());
		}

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

		/*
		PARSE KML HERE
		*/
		
		boundingBox = new float[] {1,1,1}; //specify the boundingBox based on ??
	
	}

	/*public VirtualKML(float[] _position)
	{
		dimensions = _dimensions;
		position = _position;
		boundingBox = new float[] {0.5f*dimensions[0], 0.5f*dimensions[1], 0.5f*dimensions[2]}; //get the half-dimensions
	}*/

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


	public void scale( double[] factors )
	{
		//fill this in
	}
}
