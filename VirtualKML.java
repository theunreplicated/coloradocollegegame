import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualKML extends VirtualShape //A shape built from a Google SketchUp! model
{
	static final long serialVersionUID = -5324334570847991025L;	

	private double[][] linearRings; //an array of double[] representing the coords (every 3 elements) of the ring
					//in double precision because that's the level we're being passed from SketchUp
	
	public VirtualKML(Node _info, Document _doc)
	{
		super(_info);
		
		//create the origin
		Element originElement = ((Element)_doc.getElementsByTagName("DocumentOrigin").item(0));
		String originString = ((Element)originElement.getElementsByTagName("coordinates").item(0)).getTextContent();
		double[] origin = SketchUpUtils.parseCoordinate(originString);
		
		//LINEAR RINGS
		NodeList ringNodes = _doc.getElementsByTagName("LinearRing");
		int rnl = ringNodes.getLength();
		linearRings = new double[rnl][];
		String allCoords; //for iteration n stuff
		String[] coords;
		for(int i=0; i<rnl; i++) //run through the nodes
		{
			allCoords = ((Element)((Element)ringNodes.item(i)).getElementsByTagName("coordinates").item(0)).getTextContent();
			coords = allCoords.split(",|( )+");

			double[] poly = new double[coords.length-4];
			for(int j=1; j<coords.length-3; j++)
			{
				poly[j-1] = Double.parseDouble(coords[j]);
			}
			for(int j=0; j<poly.length; j=j+3) //could possibly combine these loops
			{
				poly[j] = (poly[j]-origin[0])*Constants.LON_METERS_PER_DEGREE; //convert from local lon to meters
				poly[j+1] = (poly[j+1]-origin[1])*Constants.LAT_METERS_PER_DEGREE; //convert from local lat to meters
				poly[j+2] = poly[j+2]-origin[2]; //already in meters
			}
			linearRings[i] = poly;
		}

		
		/***Auto-Center***/
		//initialize to the first point
		double[] max = new double[] {linearRings[0][0],linearRings[0][1],linearRings[0][2]}; //the maximum points on each axis
		double[] min = new double[] {linearRings[0][0],linearRings[0][1],linearRings[0][2]}; //the minimum points on each axis
		for(int i=0; i<linearRings.length; i++) //run through each linearRing
		{
			for(int j=0; j<linearRings[i].length; j=j+3) //for each ring, check every point (every 3rd coord)
			{
				if(linearRings[i][j] > max[0]) //if x is greater than max-X
					max[0] = linearRings[i][j];
				if(linearRings[i][j] < min[0]) //if x is less than min-X						
					min[0] = linearRings[i][j];
				
				if(linearRings[i][j+1] > max[1]) //if y is greater than max-Y
					max[1] = linearRings[i][j+1];
				if(linearRings[i][j+1] < min[1]) //if y is less than min-Y						
					min[1] = linearRings[i][j+1];

				if(linearRings[i][j+2] > max[2]) //if z is greater than max-Z
					max[2] = linearRings[i][j+2];
				if(linearRings[i][j+2] < min[2]) //if z is less than min-Z						
					min[2] = linearRings[i][j+2];
			}	
		}
		double[] center = {(max[0]+min[0])/2.0, (max[1]+min[1])/2.0, (max[2]+min[2])/2.0}; //our actual center
		for(int i=0; i<linearRings.length; i++) //run through each linearRing
		{
			for(int j=0; j<linearRings[i].length; j=j+3) //for each ring, check every point (every 3rd coord)
			{
				//recenter
				linearRings[i][j] -= center[0];
				linearRings[i][j+1] -= center[1];
				linearRings[i][j+2] -= center[2];
			}
		}


		//calculate boundingBox
		float[] bmax = {0,0,0}; //the lengths of our maximal AABB 
		for(int i=0; i<linearRings.length; i++) //run through each linearRing
		{
			for(int j=0; j<linearRings[i].length; j=j+3) //for each ring, check every point (every 3rd coord)
			{
				//if abs of any coordinate is outside current bounds, increase the bounds
				if(Math.abs(linearRings[i][j]) > bmax[0])
					bmax[0] = (float)Math.abs(linearRings[i][j]);
				if(Math.abs(linearRings[i][j+1]) > bmax[1])
					bmax[1] = (float)Math.abs(linearRings[i][j+1]);
				if(Math.abs(linearRings[i][j+2]) > bmax[2])
					bmax[2] = (float)Math.abs(linearRings[i][j+2]);
			}
		}
		boundingBox = new float[] {bmax[0]*scale[0], bmax[1]*scale[1], bmax[2]*scale[2]}; //set boundingBox to include scale!
	}

	public double[][] getLinearRings()
	{
		return linearRings;
	}
	
}
