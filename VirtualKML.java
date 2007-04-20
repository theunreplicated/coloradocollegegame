import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

public class VirtualKML extends VirtualShape //A shape built from a Google SketchUp! model
{
	static final long serialVersionUID = -5324334570847991025L;	

	private ArrayList<KMLGeometryCollection> geometries;
	
	public VirtualKML(Node _info, Document _doc)
	{
		super(_info);
		
		geometries = new ArrayList<KMLGeometryCollection>();
		
		//create the origin
		Element originElement = ((Element)_doc.getElementsByTagName("DocumentOrigin").item(0));
		String originString = ((Element)originElement.getElementsByTagName("coordinates").item(0)).getTextContent();
		double[] origin = SketchUpUtils.parseCoordinate(originString);
		
		//create and fill style hash
		HashMap<String,Node> styles = new HashMap<String,Node>();
		NodeList styleNodes = _doc.getElementsByTagName("Style");
		for(int i=0; i<styleNodes.getLength(); i++)
		{
			Node s = styleNodes.item(i);
			styles.put(((Element)s).getAttribute("id"),s);
		}
	
		//create KMLGeometryCollection objects
		NodeList placemarkNodes = _doc.getElementsByTagName("Placemark");
		for(int i=0; i<placemarkNodes.getLength(); i++)
		{
			NodeList rings = ((Element)placemarkNodes.item(i)).getElementsByTagName("LinearRing");
			if(rings.getLength() > 0) //ignore placemarks without linear rings
			{
				Node styleNode;
				Node styleURLNode = ((Element)placemarkNodes.item(i)).getElementsByTagName("styleUrl").item(0);
				if(styleURLNode != null)
				{
					String styleID = styleURLNode.getTextContent().substring(1);
					styleNode = styles.get(styleID);
				}
				else
					styleNode = null;				

				//create and store a new set of geometry
				geometries.add(new KMLGeometryCollection(rings, origin, styleNode));
			}	
		}	

		//recenter the geometries
		double[][] amm = geometries.get(0).calculateMinMax(); //initialize to first collection
		double[][] mm;
		for(int i=1; i<geometries.size(); i++) //first calculate the real center
		{
			mm = geometries.get(i).calculateMinMax();

			if(mm[0][0] < amm[0][0]) //if min-x is less than amin-x
				amm[0][0] = mm[0][0];
			if(mm[1][0] > amm[1][0]) //if max-x is greater than than amax-x
				amm[1][0] = mm[1][0];

			if(mm[0][1] < amm[0][1]) //if min-y is less than amin-y
				amm[0][1] = mm[0][1];
			if(mm[1][1] > amm[1][1]) //if max-y is greater than than amax-y
				amm[1][1] = mm[1][1];

			if(mm[0][2] < amm[0][2]) //if min-z is less than amin-z
				amm[0][2] = mm[0][2];
			if(mm[1][2] > amm[1][2]) //if max-z is greater than than amax-z
				amm[1][2] = mm[1][2];
		}
		double[] center = {(amm[0][0]+amm[1][0])/2.0, (amm[0][1]+amm[1][1])/2.0, (amm[0][2]+amm[1][2])/2.0}; //our actual center
		for(KMLGeometryCollection gc : geometries) //recenter all the geometries
			gc.center(center);

		//calculate boundingBox
		float[] bmax = geometries.get(0).calculateBounds(); //the lengths of our maximal AABB 
		float[] bounds;
		for(int i=1; i<geometries.size(); i++)
		{
			bounds = geometries.get(i).calculateBounds();
		
			//if abs of any coordinate is outside current bounds, increase the bounds
			if(Math.abs(bounds[0]) > bmax[0])
				bmax[0] = (float)Math.abs(bounds[0]);
			if(Math.abs(bounds[1]) > bmax[1])
				bmax[1] = (float)Math.abs(bounds[1]);
			if(Math.abs(bounds[2]) > bmax[2])
				bmax[2] = (float)Math.abs(bounds[2]);
		}
		boundingBox = new float[] {bmax[0]*scale[0], bmax[1]*scale[1], bmax[2]*scale[2]}; //set boundingBox to include scale!

	}

	//returns the array of geometries
	public ArrayList<KMLGeometryCollection> getGeometryCollections()
	{
		return geometries;
	}


	//Embedded class which stores the different collections of polygons.
	//Allows us to specify color, and should hopefully clean up GameElementBranch a lot
	public class KMLGeometryCollection implements Serializable
	{
		//is there anything else we want to store? Other style stuff maybe?
		private double[] points; //an array of doubles, every 3 entries of which are a point in the geometry
		private int[] stripCounts; //an array of the number of points in each polygon.
		private int color; //the FILL color of the polygons (will have alpha of 0 if unspecified)
		
		//Currently only passes in LinearRing nodes. Could maybe have it parse them out of the collection someday
		public KMLGeometryCollection(NodeList ringNodes, double[] origin,  Node style)
		{
			/***parse geometry - currently only gets LinearRings***/
			//NodeList ringNodes = ((Element)geom).getElementsByTagName("LinearRing");
			int rnl = ringNodes.getLength();

			//construct an array of the rings
			double[][] linearRings = new double[rnl][];
			String allCoords; //for iteration and stuff
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

			//convert rings into a single array
			int len = 0; //total length
			for(int i=0; i<linearRings.length; i++) //calculate total length
				len += linearRings[i].length;

			points = new double[len];
			stripCounts = new int[rnl];

			int p=0;
			for(int i=0; i<linearRings.length; i++)
			{
				stripCounts[i] = (linearRings[i].length/3); //linearRings always have 3 points/coord

				for(int j=0; j<linearRings[i].length; j++)
				{
					points[p] = linearRings[i][j]; //fill the single array
					p++;
				}
			}

			
			/***parse style - currently only gets polygon color***/
			if(style != null)
			{			
		 		//System.out.println("non-null style!");
		 		Element polyStyle = (Element)(((Element)style).getElementsByTagName("PolyStyle").item(0));
		 		String colorString = ((Element)polyStyle.getElementsByTagName("color").item(0)).getTextContent();
				color = (int)Long.parseLong(colorString,16);
			}
			else
				color = 0; 
		}
		
		public double[] getPoints()
		{
			return points;
		}
		
		public int[] getStripCounts()
		{
			return stripCounts;
		}
		
		public int getColor()
		{
			return color;
		}	

		//centers the geometry around the specified point
		public void center(double[] center)
		{
			for(int i=0; i<points.length; i=i+3) //check every point (every 3rd coord)
			{
				//recenter
				points[i] -= center[0];
				points[i+1] -= center[1];
				points[i+2] -= center[2];
			}
		}

		//returns the minimum and maximum points on each axis (one method to save time)
		public double[][] calculateMinMax()
		{
			double[] max = {points[0],points[1],points[2]}; //initialize to first point
			double[] min = {points[0],points[1],points[2]};
			
			for(int i=3; i<points.length; i=i+3) //check every point (every 3rd coord)
			{
				if(points[i] > max[0]) //if x is greater than max-X
					max[0] = points[i];
				if(points[i] < min[0]) //if x is less than min-X						
					min[0] = points[i];
				
				if(points[i+1] > max[1]) //if y is greater than max-Y
					max[1] = points[i+1];
				if(points[i+1] < min[1]) //if y is less than min-Y						
					min[1] = points[i+1];

				if(points[i+2] > max[2]) //if z is greater than max-Z
					max[2] = points[i+2];
				if(points[i+2] < min[2]) //if z is less than min-Z						
					min[2] = points[i+2];
			}
			
			return new double[][] {min,max};
		}
	
		//returns the maximal bounds of this collection. Used in calculating boundingBox
		public float[] calculateBounds()
		{
			float[] max = {0,0,0};
			for(int i=0; i<points.length; i=i+3) //check every point (in groups of 3)
			{
				//if abs of any coordinate is outside current bounds, increase the bounds
				if(Math.abs(points[i]) > max[0])
					max[0] = (float)Math.abs(points[i]);
				if(Math.abs(points[i+1]) > max[1])
					max[1] = (float)Math.abs(points[i+1]);
				if(Math.abs(points[i+2]) > max[2])
					max[2] = (float)Math.abs(points[i+2]);
			}
			return max;
		}
	}

}
