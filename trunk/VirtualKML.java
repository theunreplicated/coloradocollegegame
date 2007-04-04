import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualKML extends VirtualShape //A shape built from a Google SketchUp! model
{
	float[][] linearRings;
	
	public VirtualKML(Node _info, Document _doc)
	{
		super(_info);
		
		System.out.println("Making new VirtualKML shape!");
		
		Element originElement = (Element)_doc.getElementsByTagName("DocumentOrigin").item(0);
		String origin = ((Element)originElement.getElementsByTagName("coordinates").item(0)).getTextContent();
		
		System.out.println("Origin:"+origin);
		
		
		//NodeList ringNodes = _doc.getElementsByTagName("LinearRing");
				



		/*
		PARSE KML HERE
		*/
		



		boundingBox = new float[] {1,1,1}; //specify the boundingBox based on ??
	}


	public VirtualKML(Node _info)
	{
		super(_info);

		/*
		PARSE KML HERE
		*/


		
		boundingBox = new float[] {1,1,1}; //specify the boundingBox based on ??
	}

	public VirtualKML(float[] _position)
	{
		super(_position);
		
		//fill this in
		boundingBox = new float[] {1,1,1}; //specify the boundingBox based on ??
	}
}
