import org.w3c.dom.*;
import javax.xml.parsers.*;

public class VirtualKML extends VirtualShape //A shape built from a Google SketchUp! model
{
	public VirtualKML(Node _info)
	{
		super(_info);
		Element info = (Element) _info;

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
