import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualBox implements VirtualShape
{
	private float[] dimensions;
	private float[] position;
	private float[] facing; //in quaternions


	public VirtualBox(Node _info)
	{
		Element info = (Element) _info;
		NodeList positionNodes = info.getElementsByTagName("position");
		position = new float[positionNodes.getLength()];
		for(int i = position.length-1; i>=0; i--)
		{
			position[i] = Float.parseFloat(positionNodes.item(i).getTextContent());
		}
		NodeList dimNodes = info.getElementsByTagName("dimension");
		dimensions = new float[dimNodes.getLength()];
		for(int i = dimensions.length-1; i>=0; i--)
		{
			dimensions[i] = Float.parseFloat(dimNodes.item(i).getTextContent());
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
	}


	public VirtualBox(float[] _dimensions, float[] _position)
	{
		dimensions = _dimensions;
		position = _position;
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

	public float[] getPosition()
	{
		return position;
	}

	public float[] getFacing()
	{
		return facing;
	}

	public float[][] getMinMax()
	{
		float[][] tmp = new float[position.length][2];
		
		for( int i = 0; i < tmp.length; i++)
		{
			tmp[i][Constants.MIN] = position[i]-dimensions[i]/2;
			tmp[i][Constants.MAX] = position[i]+dimensions[i]/2;
		}

		return( tmp );
	}

	public void scale( double[] factors )
	{
		for( int i = 0; i < factors.length; i++ )
			dimensions[i] *= factors[i];
	}
}
