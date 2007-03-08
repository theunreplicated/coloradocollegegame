import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualBox implements VirtualShape
{
	private float[] dimensions;
	private float[] center;
	private float[] rotation; //in quaternions


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

		NodeList rotationNodes = info.getElementsByTagName("rotation");
		float[] rotEuler = new float[rotationNodes.getLength()]; //construct an array of Euler rotations
		for(int i = rotEuler.length-1; i>=0; i--)
		{
			rotEuler[i] = Float.parseFloat(rotationNodes.item(i).getTextContent());
			rotEuler[i] = (float)Math.toRadians(rotEuler[i]); //change to Radians. We like radians.
		}
		if(rotEuler.length != 0)
		{
			rotation = Quaternions.getQuatFromEuler(rotEuler); //set the rotation to be in Quaternions
		}
		else
			rotation = new float[] {0,0,0,1}; //set to a unit
	}


	public VirtualBox(float[] _dimensions, float[] _center)
	{
		dimensions = _dimensions;
		center = _center;
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

	public float[] getCenter()
	{
		return center;
	}

	public float[] getRotation()
	{
		return rotation;
	}

	public float[][] getMinMax()
	{
		float[][] tmp = new float[center.length][2];
		
		for( int i = 0; i < tmp.length; i++)
		{
			tmp[i][Constants.MIN] = center[i]-dimensions[i]/2;
			tmp[i][Constants.MAX] = center[i]+dimensions[i]/2;
		}

		return( tmp );
	}

	public void scale( double[] factors )
	{
		for( int i = 0; i < factors.length; i++ )
			dimensions[i] *= factors[i];
	}
}
