import java.util.*;
import java.io.File;
import java.io.FileFilter;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ElementFactory
{
	private HashMap<String, GameElement> defaultElements = new HashMap<String, GameElement>();
	private String[] defaultElementKeys;

	public ElementFactory(File folder, String ext)
	{
		File[] files = folder.listFiles(new EGFileFilter(ext));
		createDefaultElements(files);
	}

	public ElementFactory(File folder)
	{
		File[] files = folder.listFiles(new EGFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}

	public ElementFactory()
	{
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
			Element element, name, plural, shape;
			NodeList elements, spheres, cylinders, boxes, cones;
			VirtualShape[] shapes;
			int i, j, k;
			float[] position;
			HashMap attributes;
			float[][] minMax;
			float[][] tempMinMax = null;
			
			for(File file : files)
			{
				doc = db.parse(file);
				elements = doc.getElementsByTagName("element");
				for(i = elements.getLength()-1;i>=0; i--)
				{
					position = Constants.DEFAULT_POSITION;
					attributes = new HashMap<String, Object>();

					element = (Element) elements.item(i);
					name = (Element) element.getElementsByTagName("name").item(0);
					plural = (Element) element.getElementsByTagName("plural").item(0);
					shape = (Element) element.getElementsByTagName("shapes").item(0);
					spheres = shape.getElementsByTagName("sphere");
					cylinders = shape.getElementsByTagName("cylinder");
					boxes = shape.getElementsByTagName("box");
					cones = shape.getElementsByTagName("cone");
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

					minMax = shapes[shapes.length-1].getMinMax();
					for(j = shapes.length-2; j >= 0; j--)
					{
						tempMinMax = shapes[j].getMinMax();
						for(k = tempMinMax.length-1; k >= 0; k--)
						{
							if(minMax[k][Constants.MIN] < tempMinMax[k][Constants.MIN])
							{
								minMax[k][Constants.MIN] = tempMinMax[k][Constants.MIN];
							}
							if(minMax[k][Constants.MAX] > tempMinMax[k][Constants.MAX])
							{
								minMax[k][Constants.MAX] = tempMinMax[k][Constants.MAX];
							}
						}
					}
					defaultElements.put(name.getTextContent(),
						new GameElement(name.getTextContent(), position, minMax, shapes, attributes));
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
			System.err.println("Failed to read input files! : " + e.getMessage() + " :\n");
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
