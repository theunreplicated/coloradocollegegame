import java.util.*;
import java.io.File;
import java.io.FileFilter;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ElementFactory
{
	private Logger myLogger;
	private HashMap<String, GameElement> defaultElements = new HashMap<String, GameElement>();
	private String[] defaultElementKeys;

	public ElementFactory(File folder, String ext, Logger _myLogger)
	{
		myLogger = _myLogger;
		File[] files = folder.listFiles(new EGFileFilter(ext));
		createDefaultElements(files);
	}

	public ElementFactory(File folder, Logger _myLogger)
	{
		myLogger = _myLogger;
		File[] files = folder.listFiles(new EGFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}

	public ElementFactory(Logger _myLogger)
	{
		myLogger = _myLogger;
		File folder = new File(Constants.DEFAULT_DATA_DIR);
		File[] files = folder.listFiles(new EGFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}
	public void createDefaultElements(File[] files)
	{

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			Element element, name, plural, shape, newAttribute, attributesElement;
			NodeList elements, spheres, cylinders, boxes, cones, kml, facingNodes, boundsNodes, attributesNodes;
			Node tmpNode;
			String attributeType;
			VirtualShape[] shapes;
			int i, j, k;
			float[] position;
			float[] facing;
			float[] bounds;
			HashMap attributes;
			float[][] minMax;
			float[][] tempMinMax = null;
			
			for(File file : files)
			{
				doc = db.parse(file);
				elements = doc.getElementsByTagName("element");
				for(i = elements.getLength()-1;i>=0; i--)
				{
					element = (Element) elements.item(i);

					position = Constants.DEFAULT_POSITION;
					facing = Constants.DEFAULT_FACING;
					
					attributes = new HashMap<String, Object>();
					attributesElement = (Element) element.getElementsByTagName("attributes").item(0);
					if(attributesElement != null)
					{
						// grab all of the children of attributesElement
						attributesNodes = attributesElement.getChildNodes();

						for(int a = attributesNodes.getLength()-1; a>=0; a--)
						{
							tmpNode = attributesNodes.item(a);
							if(tmpNode.getNodeType() != Node.ELEMENT_NODE) continue;
							newAttribute = (Element) tmpNode;
							attributeType = newAttribute.getAttribute("type");
							if(attributeType.equalsIgnoreCase("String"))
							{
								attributes.put(newAttribute.getTagName(), newAttribute.getTextContent());
							}
							else if(attributeType.equalsIgnoreCase("int"))
							{
								attributes.put(newAttribute.getTagName(), Integer.parseInt(newAttribute.getTextContent()));
							}
							else if(attributeType.equalsIgnoreCase("hex")) //for 32bit hexadecimal
							{
								attributes.put(newAttribute.getTagName(), (int)Long.parseLong(newAttribute.getTextContent(),16));	
							}
							// &c.
						}
					}
					
					name = (Element) element.getElementsByTagName("name").item(0);
					plural = (Element) element.getElementsByTagName("plural").item(0);
					shape = (Element) element.getElementsByTagName("shapes").item(0);
					spheres = shape.getElementsByTagName("sphere");
					cylinders = shape.getElementsByTagName("cylinder");
					boxes = shape.getElementsByTagName("box");
					cones = shape.getElementsByTagName("cone");
					kml = shape.getElementsByTagName("kml");
					shapes = new VirtualShape[spheres.getLength() + cylinders.getLength() + boxes.getLength() + cones.getLength()];
					j = shapes.length-1;

					k = cones.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualCone(cones.item(k));
					}
	
					k = boxes.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualBox(boxes.item(k));
					}

					k = cylinders.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualCylinder(cylinders.item(k));
					}

					k = spheres.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualSphere(spheres.item(k));

					}
					
					//will probably have to do more to get stuff out of kmz
					k = kml.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualKML(kml.item(k));
					}

					//determine bounds
					boundsNodes = element.getElementsByTagName("bounds");
					if(boundsNodes.getLength() == 0) //if bounds aren't specified, create our own out of the shapes
					{
						float[] max = new float[] {0,0,0}; //the lengths of our maximal AABB
						float[][] corners = new float[8][3]; //an array for the corners of a shape
						float[] sbb; //for iteration
						
						for(int s=0; s<shapes.length; s++) //run through all the shapes
						{
							sbb = shapes[s].getBoundingBox(); //fetch the boundingBox once
							//construct the corners of the AABB for the shape
							corners[0] = new float[] { sbb[0], sbb[1], sbb[2]}; //construct the corners of the AABB for the shape
							corners[1] = new float[] { sbb[0], sbb[1],-sbb[2]};
							corners[2] = new float[] {-sbb[0], sbb[1],-sbb[2]};
							corners[3] = new float[] {-sbb[0], sbb[1], sbb[2]};
							corners[4] = new float[] { sbb[0],-sbb[1], sbb[2]};
							corners[5] = new float[] { sbb[0],-sbb[1],-sbb[2]};
							corners[6] = new float[] {-sbb[0],-sbb[1],-sbb[2]};
							corners[7] = new float[] {-sbb[0],-sbb[1], sbb[2]};
							Quaternions.rotatePoints(corners, shapes[s].getFacing()); //rotate the corners into an OBB						
							VectorUtils.add(corners,shapes[s].getPosition()); //move the OBB to the shape's position

							for(int c=0; c<corners.length; c++) //run through the corners
							{
								//if abs of any coordinate is outside current bounds, increase the bounds
								if(Math.abs(corners[c][0]) > max[0])
									max[0] = Math.abs(corners[c][0]);
								if(Math.abs(corners[c][1]) > max[1])
									max[1] = Math.abs(corners[c][1]);
								if(Math.abs(corners[c][2]) > max[2])
									max[2] = Math.abs(corners[c][2]);
							}
						}
						
						bounds = max; //set bounds equal to whatever the max was					
					}
					else //if bounds were specified, use those
					{
						bounds = new float[boundsNodes.getLength()];
						for(int p = bounds.length-1; p>=0; p--)
						{
							bounds[p] = Float.parseFloat(boundsNodes.item(p).getTextContent());
						}
					}

					defaultElements.put(name.getTextContent(),
						new GameElement(name.getTextContent(), position, facing, bounds, shapes, attributes));
				}
			}
			
			// iterate through hashmap to create our array...
			defaultElementKeys = new String[] {""};
			defaultElementKeys = defaultElements.keySet().toArray(defaultElementKeys);
			for(i = 0; i < defaultElementKeys.length; i++)
			{
				defaultElements.get(defaultElementKeys[i]).setTypeId(i);
			}
		}
		catch(Exception e) // better error reporting!
		{
			myLogger.message("Failed to read input files! : " + e.getMessage() + " :\n", true);
			e.printStackTrace();
		}
	}

	public GameElement getGameElement(String _elementType)
	{
		return(new GameElement(defaultElements.get(_elementType)));
	}

	public GameElement getGameElement(int _elementTypeId)
	{
		return(getGameElement(defaultElementKeys[_elementTypeId]));
	}

	public int getType(String _type)
	{
		return(defaultElements.get(_type).getTypeId());
	}

	public String getType(int _type)
	{
		return(defaultElementKeys[_type]);
	}

	private class EGFileFilter implements FileFilter
	{
		private String ext;
		public EGFileFilter(String _ext)
		{
			ext = _ext;
		}

		public boolean accept(File pathname)
		{
			return(pathname.getName().endsWith(ext));
		}
	}
}
