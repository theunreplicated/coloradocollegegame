import java.util.*;
import java.io.File;
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
		File[] files = folder.listFiles(new FactoryFileFilter(ext));
		createDefaultElements(files);
	}

	public ElementFactory(File folder, Logger _myLogger)
	{
		myLogger = _myLogger;
		File[] files = folder.listFiles(new FactoryFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}

	public ElementFactory(Logger _myLogger)
	{
		myLogger = _myLogger;
		File folder = new File(Constants.DEFAULT_DATA_DIR);
		File[] files = folder.listFiles(new FactoryFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}

	public ElementFactory(File[] _files, Logger _myLogger)
	{
		myLogger = _myLogger;
		createDefaultElements(_files);
	}

	private void createDefaultElements(File[] files)
	{

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			Element element, name, plural, shape, newAttribute, attributesElement;
			NodeList elements, spheres, cylinders, boxes, cones, kmz, kmzUrls, facingNodes, boundsNodes, attributesNodes;
			Node tmpNode;
			VirtualShape[] shapes;
			int i, j, k;
			int kmlCount;
			float[] position;
			float[] facing;
			float[] bounds;
			float[] scale;
			AttributesHashMap attributes;
			
			for(File file : files)
			{
				if(!file.exists())
				{
					myLogger.message("ElementFactory cannot find file: " + file + "\n", true);
					continue;
				}
				doc = db.parse(file);
				elements = doc.getElementsByTagName("element");
				for(i = elements.getLength()-1;i>=0; i--)
				{
					element = (Element) elements.item(i);

					position = Constants.DEFAULT_POSITION;
					facing = Constants.DEFAULT_FACING;
					scale = Constants.DEFAULT_SCALE;
					
					attributes = new AttributesHashMap();
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
							attributes.put(newAttribute.getTagName(), Constants.parseXMLwithType(newAttribute));
						}
					}
					
					name = (Element) element.getElementsByTagName("name").item(0);
					plural = (Element) element.getElementsByTagName("plural").item(0);
					shape = (Element) element.getElementsByTagName("shapes").item(0);
					spheres = shape.getElementsByTagName("sphere");
					cylinders = shape.getElementsByTagName("cylinder");
					boxes = shape.getElementsByTagName("box");
					cones = shape.getElementsByTagName("cone");
					kmz = shape.getElementsByTagName("kmz");
					kmlCount = 0;
					for(int c = kmz.getLength()-1; c>=0; c--)
					{
						kmzUrls = ((Element)kmz.item(c)).getElementsByTagName("url"); //better damn well be a 1:1 ratio.
						kmlCount += SketchUpUtils.countKMLFiles(kmzUrls.item(0).getTextContent());
					}
					
					shapes = new VirtualShape[spheres.getLength() + cylinders.getLength() + boxes.getLength() + cones.getLength() + kmlCount];
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

					/*SKETCHUP STUFF GOES HERE*/
					
					k = kmz.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						
						//get KML stuff out of kmz.item(k);
						//create a VirtualKML() for each KML file
						Document[] kmldocs = SketchUpUtils.decompressKMZ(
							((Element)kmz.item(k)).getElementsByTagName("url").item(0).getTextContent());
						
						for(int q = kmldocs.length-1; q>=0 && j>=0; j--, q--)
							shapes[j] = new VirtualKML(kmz.item(k),kmldocs[q]);
						
						//kmz.item(k).getTextC
						
						
						//do stuff to KMZ to get KML objects.
						
						//shapes[j] = new VirtualKML(kml.item(k));
					}

					//determine bounds
					boundsNodes = element.getElementsByTagName("bounds");
					if(boundsNodes.getLength() == 0) //if bounds aren't specified, create our own out of the shapes
					{
						float[] max = new float[] {0,0,0}; //the lengths of our maximal AABB
						float[][] corners = new float[8][3]; //an array for the corners of a shape
						float[] sbb; //for iteration
						
						for(int s=0; s<shapes.length && shapes[s] != null; s++) //run through all the shapes, double check that is defined
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
						new GameElement(name.getTextContent(), position, facing, bounds, scale, shapes, attributes));
				}
			}
			
			if(defaultElements.isEmpty())
			{
				myLogger.message("ElementFactory found no elements... You can't play a game without elements!\n", true);
				System.exit(1);
			}
			// iterate through hashmap to create our array...
			defaultElementKeys = new String[] {""};
			defaultElementKeys = defaultElements.keySet().toArray(defaultElementKeys);
			Arrays.sort(defaultElementKeys);
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

	public void setWorld(World w)
	{
		Collection<GameElement> values = defaultElements.values();
		Iterator<GameElement> it = values.iterator();
		while(it.hasNext())
		{
			GameElement ge = it.next();
			ge.getAttributes().setWorld(w);
		}
	}
}
